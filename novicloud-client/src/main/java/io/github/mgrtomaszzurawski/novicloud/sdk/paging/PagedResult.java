/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.paging;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * An {@link Iterable} over paginated NoviCloud API results that exposes response
 * metadata and supports random access via {@link #seek(int)}.
 *
 * <p>Returned by every
 * {@code list()} method on resource clients:
 *
 * <pre>{@code
 * PagedResult<Towar> result = client.towary().list(query);
 *
 * // Metadata (triggers first page fetch):
 * int total = result.totalCount();   // response.size
 * int perPage = result.pageSize();   // response.on_page (1-50)
 *
 * // Backward-compatible forward iteration:
 * for (Towar t : result) { ... }
 *
 * // Random access with bidirectional iterator:
 * result.seek(432);
 * ListIterator<Towar> it = result.listIterator();
 * it.next();        // record 432
 * it.previous();    // record 432 again
 *
 * // One-shot page fetch (does not move iterator position):
 * List<Towar> page = result.fetchFrom(1500);
 * }</pre>
 *
 * <p>This class is not thread-safe. Use a single thread per instance.
 *
 * @param <T> the element type (SDK record, e.g. {@code Towar}, {@code Dokument})
 * @since 1.0.0
 */
public final class PagedResult<T> implements Iterable<T> {

    private static final String PARAM_START = "start=";
    private static final int START_POSITION = 0;
    /** Server-enforced maximum records per page. Used by {@link #seekFromPage(int)}. */
    public static final int SERVER_PAGE_SIZE = 50;
    private static final int MIN_PAGE_NUMBER = 1;
    private static final String ERR_SEEK_NEGATIVE = "seek position must be non-negative: ";
    private static final String ERR_PAGE_NUMBER = "page number must be >= 1: ";
    private static final String ERR_NO_SELF_LINK = "Cannot perform random access: server did not return a self link";
    private static final String REGEX_START_PARAM = "([?&])start=\\d+";
    private static final String REGEX_START_REPLACE = "$1start=";
    private static final String QUERY_SEPARATOR = "?";
    private static final String PARAM_SEPARATOR = "&";
    private static final String ERR_NULL_INIT_FETCHER = "initFetcher must not be null";
    private static final String ERR_NULL_PAGE_FETCHER = "pageFetcher must not be null";
    private static final int PREVIOUS_OFFSET = 1;
    private static final int UNINITIALIZED_PAGE_START = -1;
    /** Sentinel: extractor returns this when the server did not include size/onPage. */
    public static final int UNKNOWN = -1;

    /**
     * Metadata and first-page data captured on initialization.
     */
    private record PageInfo<T>(List<T> firstPageData, int totalCount, int pageSize, String selfUrl) { }

    private final Supplier<PageInfo<T>> initFetcher;
    private final Function<String, List<T>> pageFetcher;

    private PageInfo<T> pageInfo;
    private int seekPosition = START_POSITION;

    private PagedResult(Supplier<PageInfo<T>> initFetcher, Function<String, List<T>> pageFetcher) {
        this.initFetcher = Objects.requireNonNull(initFetcher, ERR_NULL_INIT_FETCHER);
        this.pageFetcher = Objects.requireNonNull(pageFetcher, ERR_NULL_PAGE_FETCHER);
    }

    /**
     * Creates a {@code PagedResult} from the given lambdas.
     *
     * <p>The type parameter {@code P} (page response type) is fully captured inside the factory
     * method and is not visible in the returned type. Callers receive {@code PagedResult<T>}.
     *
     * @param <T>               element type (SDK record)
     * @param <P>               page response type (an internal page-envelope class; never visible to callers)
     * @param firstPage         fetches the first page using the query already captured in the closure
     * @param urlFetch          fetches any page by its absolute URL (used for pagination and random access)
     * @param dataExtractor     extracts and maps items from a page response into SDK records (handles null)
     * @param selfLinkExtractor extracts the {@code links.self} URL string from a page response;
     *                          may return {@code null} if not present
     * @param sizeExtractor     extracts the total record count ({@code size} field);
     *                          return {@link #UNKNOWN} if the server did not include it
     * @param onPageExtractor   extracts the number of records on this page ({@code on_page} field);
     *                          return {@link #UNKNOWN} if the server did not include it
     * @return a new {@code PagedResult}
     */
    public static <T, P> PagedResult<T> of(
            Supplier<P> firstPage,
            Function<String, P> urlFetch,
            Function<P, List<T>> dataExtractor,
            Function<P, String> selfLinkExtractor,
            ToIntFunction<P> sizeExtractor,
            ToIntFunction<P> onPageExtractor)
    {
        Supplier<PageInfo<T>> initFetcher = () -> {
            P page = firstPage.get();
            List<T> data = Objects.requireNonNullElseGet(dataExtractor.apply(page), List::of);
            int size = sizeExtractor.applyAsInt(page);
            int onPage = onPageExtractor.applyAsInt(page);
            int totalCount = size >= START_POSITION ? size : data.size();
            int pageSize = onPage >= START_POSITION ? onPage : data.size();
            String selfUrl = selfLinkExtractor.apply(page);
            return new PageInfo<>(data, totalCount, pageSize, selfUrl);
        };
        Function<String, List<T>> pageFetcher = url -> {
            P page = urlFetch.apply(url);
            return Objects.requireNonNullElseGet(dataExtractor.apply(page), List::of);
        };
        return new PagedResult<>(initFetcher, pageFetcher);
    }

    /**
     * Returns the total number of records matching the query ({@code response.size}).
     * Triggers the first page fetch if not yet initialized.
     *
     * @return total record count
     */
    public int totalCount() {
        ensureInitialized();
        return pageInfo.totalCount();
    }

    /**
     * Returns the number of records on the first page ({@code response.on_page}, 1-50,
     * server-controlled). Triggers the first page fetch if not yet initialized.
     *
     * @return records per page
     */
    public int pageSize() {
        ensureInitialized();
        return pageInfo.pageSize();
    }

    /**
     * Positions the next {@link #listIterator()} at the given global record offset.
     * Does not trigger any HTTP request. The offset is zero-based.
     *
     * @param n zero-based record offset (must be &gt;= 0)
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public void seek(int n) {
        if (n < START_POSITION) {
            throw new IllegalArgumentException(ERR_SEEK_NEGATIVE + n);
        }
        this.seekPosition = n;
    }

    /**
     * Positions the next {@link #listIterator()} at the first record of the given page (1-based).
     * Equivalent to {@code seek((pageNumber - 1) * 50)}.
     *
     * <p>Uses the fixed server page size of {@value #SERVER_PAGE_SIZE} as the stride, not the
     * {@link #pageSize()} of the first response. {@code pageSize()} can be smaller than 50 when
     * the total record count is below 50, which would produce wrong offsets for pages 2+.
     *
     * <pre>{@code
     * result.seekFromPage(1);  // seek(0)  - first record
     * result.seekFromPage(2);  // seek(50) - record 51
     * result.seekFromPage(3);  // seek(100)
     * }</pre>
     *
     * @param pageNumber 1-based page number (must be &gt;= 1)
     * @throws IllegalArgumentException if {@code pageNumber} is less than 1
     */
    public void seekFromPage(int pageNumber) {
        if (pageNumber < MIN_PAGE_NUMBER) {
            throw new IllegalArgumentException(ERR_PAGE_NUMBER + pageNumber);
        }
        seek((pageNumber - PREVIOUS_OFFSET) * SERVER_PAGE_SIZE);
    }

    /**
     * Returns records starting at the given global offset.
     * The server returns at most one page (up to 50 records).
     * This call does not affect the iterator position set by {@link #seek(int)}.
     *
     * @param start zero-based record offset
     * @return records starting at {@code start}; never {@code null}
     * @throws IllegalStateException if the server did not return a self link
     */
    public List<T> fetchFrom(int start) {
        return pageFetcher.apply(buildUrl(start));
    }

    /**
     * Returns a forward-only iterator starting from record 0.
     * Each call creates an independent iterator. The position set by {@link #seek(int)}
     * does not affect this method.
     *
     * @return a new forward iterator from position 0
     */
    @Override
    public Iterator<T> iterator() {
        return new PagedListIterator(START_POSITION);
    }

    /**
     * Returns a bidirectional {@link ListIterator} positioned at the offset set by
     * the last {@link #seek(int)} call (default: 0).
     *
     * <p>Page boundary crossings cost one HTTP request each, in both directions.
     * {@link ListIterator#set(Object)}, {@link ListIterator#add(Object)}, and
     * {@link ListIterator#remove()} throw {@link UnsupportedOperationException}.
     *
     * @return a bidirectional iterator from the seeked position
     */
    public ListIterator<T> listIterator() {
        return new PagedListIterator(seekPosition);
    }

    private void ensureInitialized() {
        if (pageInfo == null) {
            pageInfo = initFetcher.get();
        }
    }

    private String buildUrl(int start) {
        ensureInitialized();
        String selfUrl = pageInfo.selfUrl();
        if (selfUrl == null) {
            throw new IllegalStateException(ERR_NO_SELF_LINK);
        }
        if (selfUrl.contains(PARAM_START)) {
            return selfUrl.replaceAll(REGEX_START_PARAM, REGEX_START_REPLACE + start);
        }
        return selfUrl + (selfUrl.contains(QUERY_SEPARATOR) ? PARAM_SEPARATOR : QUERY_SEPARATOR) + PARAM_START + start;
    }

    private final class PagedListIterator implements ListIterator<T> {

        private int cursor;
        private List<T> page;
        private int pageStart;

        PagedListIterator(int initialPosition) {
            this.cursor = initialPosition;
            this.pageStart = UNINITIALIZED_PAGE_START;
        }

        @Override
        public boolean hasNext() {
            ensureInitialized();
            return cursor < pageInfo.totalCount();
        }

        @Override
        public boolean hasPrevious() {
            return cursor > START_POSITION;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            ensurePageForCursor();
            if (cursor >= pageStart + page.size()) {
                pageStart = pageStart + page.size();
                page = pageFetcher.apply(buildUrl(pageStart));
            }
            return page.get(cursor++ - pageStart);
        }

        @Override
        public T previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            ensurePageForCursor();
            if (cursor <= pageStart) {
                int prevStart = Math.max(START_POSITION, pageStart - SERVER_PAGE_SIZE);
                page = pageFetcher.apply(buildUrl(prevStart));
                pageStart = prevStart;
            }
            return page.get(--cursor - pageStart);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - PREVIOUS_OFFSET;
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void ensurePageForCursor() {
            if (page != null) {
                return;
            }
            if (cursor == START_POSITION && pageInfo != null) {
                page = pageInfo.firstPageData();
                pageStart = START_POSITION;
            } else {
                page = pageFetcher.apply(buildUrl(cursor));
                pageStart = cursor;
            }
        }
    }
}
