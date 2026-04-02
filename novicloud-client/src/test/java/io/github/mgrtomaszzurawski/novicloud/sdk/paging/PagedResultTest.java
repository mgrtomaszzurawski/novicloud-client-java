/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.paging;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PagedResultTest {

    // --- test doubles ---

    private static final String SELF_URL = "http://server/api/items?content=42&start=0";
    private static final List<String> THREE_ITEMS = List.of("a", "b", "c");
    private static final List<String> TWO_ITEMS = List.of("x", "y");
    private static final List<String> ONE_ITEM = List.of("z");
    private static final String FIRST_ITEM = "a";
    private static final String SECOND_ITEM = "b";
    private static final String THIRD_ITEM = "c";
    private static final String PAGE2_FIRST_ITEM = "x";
    private static final String PAGE2_SECOND_ITEM = "y";
    private static final String PAGE2_THIRD_ITEM = "z";
    private static final String MUTATION_VALUE = "x";
    private static final int THREE_ITEMS_COUNT = 3;
    private static final int SEEK_NEGATIVE = -1;
    private static final int PAGE_1 = 1;
    private static final int PAGE_2 = 2;
    private static final int PAGE_3 = 3;
    private static final int INDEX_0 = 0;
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;
    private static final int PREVIOUS_INDEX_START = -1;
    private static final int SEEK_PAGE_2_OFFSET = 50;
    private static final int SEEK_PAGE_3_OFFSET = 100;
    private static final int HUNDRED_ITEMS_COUNT = 100;
    private static final int HUNDRED_FIFTY_ITEMS_COUNT = 150;
    private static final int SEEK_FROM_PAGE_ZERO = 0;
    private static final String START_PARAM = "start=";
    private static final char QUERY_SEPARATOR = '&';
    private static final int PARSE_DEFAULT = 0;
    private static final int NOT_FOUND = -1;
    private static final int SUBSTRING_START = 0;

    record Page(List<String> data, int size, int onPage, String selfUrl) { }

    private static PagedResult<String> singlePage(List<String> items, String selfUrl) {
        Page page = new Page(items, items.size(), items.size(), selfUrl);
        return PagedResult.of(
                () -> page,
                url -> {
                    // replace start param with items starting at that offset
                    int start = parseStart(url);
                    List<String> sub = items.subList(Math.min(start, items.size()), items.size());
                    return new Page(sub, items.size(), sub.size(), selfUrl);
                },
                Page::data,
                Page::selfUrl,
                Page::size,
                Page::onPage
        );
    }

    private static PagedResult<String> twoPages(List<String> page1Data, List<String> page2Data,
                                                 String selfUrl) {
        int total = page1Data.size() + page2Data.size();
        Page page1 = new Page(page1Data, total, page1Data.size(), selfUrl);
        Page page2 = new Page(page2Data, total, page2Data.size(), selfUrl);
        return PagedResult.of(
                () -> page1,
                url -> {
                    int start = parseStart(url);
                    if (start >= page1Data.size()) {
                        return page2;
                    }
                    return page1;
                },
                Page::data,
                Page::selfUrl,
                Page::size,
                Page::onPage
        );
    }

    private static int parseStart(String url) {
        if (url == null) {
            return PARSE_DEFAULT;
        }
        int idx = url.indexOf(START_PARAM);
        if (idx == NOT_FOUND) {
            return PARSE_DEFAULT;
        }
        String rest = url.substring(idx + START_PARAM.length());
        int end = rest.indexOf(QUERY_SEPARATOR);
        String numStr = end == NOT_FOUND ? rest : rest.substring(SUBSTRING_START, end);
        try {
            return Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            return PARSE_DEFAULT;
        }
    }

    // --- totalCount / pageSize ---

    @Test
    void totalCount_whenSinglePage_returnsSize() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when / then
        assertEquals(THREE_ITEMS_COUNT, result.totalCount());
    }

    @Test
    void pageSize_whenSinglePage_returnsOnPage() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when / then
        assertEquals(THREE_ITEMS_COUNT, result.pageSize());
    }

    // --- seek ---

    @Test
    void seek_whenNegativeValue_throwsIllegalArgument() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> result.seek(SEEK_NEGATIVE));
    }

    // --- seekFromPage ---

    @Test
    void seekFromPage_whenPage1_startsAtZero() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when
        result.seekFromPage(PAGE_1);

        // then
        assertEquals(INDEX_0, result.listIterator().nextIndex());
        assertEquals(FIRST_ITEM, result.listIterator().next());
    }

    @Test
    void seekFromPage_whenPage2_startsAt50() {
        // given - 100-item list so totalCount=100 and seek(50) is in range
        List<String> hundredItems = java.util.Collections.nCopies(HUNDRED_ITEMS_COUNT, MUTATION_VALUE);
        PagedResult<String> result = singlePage(hundredItems, SELF_URL);

        // when
        result.seekFromPage(PAGE_2);

        // then
        assertEquals(SEEK_PAGE_2_OFFSET, result.listIterator().nextIndex());
    }

    @Test
    void seekFromPage_whenPage3_startsAt100() {
        // given
        List<String> hundredFiftyItems = java.util.Collections.nCopies(HUNDRED_FIFTY_ITEMS_COUNT, MUTATION_VALUE);
        PagedResult<String> result = singlePage(hundredFiftyItems, SELF_URL);

        // when
        result.seekFromPage(PAGE_3);

        // then
        assertEquals(SEEK_PAGE_3_OFFSET, result.listIterator().nextIndex());
    }

    @Test
    void seekFromPage_whenZeroOrNegative_throwsIllegalArgument() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> result.seekFromPage(SEEK_FROM_PAGE_ZERO));
        assertThrows(IllegalArgumentException.class, () -> result.seekFromPage(SEEK_NEGATIVE));
    }

    // --- iterator() - forward only from position 0 ---

    @Test
    void iterator_whenEmptyResult_hasNoItems() {
        // given
        PagedResult<String> result = singlePage(List.of(), SELF_URL);

        // when / then
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void iterator_whenSinglePage_allItemsIterated() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when
        List<String> collected = new ArrayList<>();
        result.forEach(collected::add);

        // then
        assertEquals(THREE_ITEMS, collected);
    }

    @Test
    void iterator_whenTwoPages_allItemsIterated() {
        // given
        PagedResult<String> result = twoPages(TWO_ITEMS, ONE_ITEM, SELF_URL);

        // when
        List<String> collected = new ArrayList<>();
        result.forEach(collected::add);

        // then
        assertEquals(List.of(PAGE2_FIRST_ITEM, PAGE2_SECOND_ITEM, PAGE2_THIRD_ITEM), collected);
    }

    @Test
    void iterator_whenCalledTwice_iteratorsAreIndependent() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when
        Iterator<String> it1 = result.iterator();
        assertEquals(FIRST_ITEM, it1.next());

        Iterator<String> it2 = result.iterator();

        // then - second iterator starts fresh
        assertEquals(FIRST_ITEM, it2.next());
        assertEquals(SECOND_ITEM, it2.next());

        // first iterator not affected
        assertEquals(SECOND_ITEM, it1.next());
    }

    @Test
    void iterator_whenExhausted_throwsNoSuchElement() {
        // given
        PagedResult<String> result = singlePage(ONE_ITEM, SELF_URL);
        Iterator<String> it = result.iterator();
        it.next();

        // when / then
        assertThrows(NoSuchElementException.class, it::next);
    }

    // --- listIterator() ---

    @Test
    void listIterator_whenDefault_startsAtZero() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when
        ListIterator<String> it = result.listIterator();

        // then
        assertEquals(INDEX_0, it.nextIndex());
        assertEquals(PREVIOUS_INDEX_START, it.previousIndex());
    }

    @Test
    void listIterator_whenEmpty_hasNextIsFalse() {
        // given
        PagedResult<String> result = singlePage(List.of(), SELF_URL);

        // when / then
        assertFalse(result.listIterator().hasNext());
    }

    @Test
    void listIterator_whenAtStart_hasPreviousIsFalse() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when / then
        assertFalse(result.listIterator().hasPrevious());
    }

    @Test
    void listIterator_whenNext_returnsItemsInOrder() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);

        // when
        ListIterator<String> it = result.listIterator();

        // then
        assertEquals(FIRST_ITEM, it.next());
        assertEquals(SECOND_ITEM, it.next());
        assertEquals(THIRD_ITEM, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void listIterator_whenPreviousAfterNext_returnsLastItem() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();
        it.next(); // a
        it.next(); // b

        // when / then
        assertEquals(SECOND_ITEM, it.previous());
        assertEquals(FIRST_ITEM, it.previous());
        assertFalse(it.hasPrevious());
    }

    @Test
    void listIterator_whenNext_nextIndexTracksGlobalPosition() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();

        // when / then
        assertEquals(INDEX_0, it.nextIndex());
        it.next();
        assertEquals(INDEX_1, it.nextIndex());
        it.next();
        assertEquals(INDEX_2, it.nextIndex());
    }

    @Test
    void listIterator_whenAfterTwoNexts_previousIndexIsNextMinusOne() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();
        it.next();
        it.next();

        // when / then
        assertEquals(INDEX_1, it.previousIndex());
    }

    @Test
    void listIterator_whenNextExhausted_throwsNoSuchElement() {
        // given
        PagedResult<String> result = singlePage(ONE_ITEM, SELF_URL);
        ListIterator<String> it = result.listIterator();
        it.next();

        // when / then
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void listIterator_whenPreviousAtStart_throwsNoSuchElement() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();

        // when / then
        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void listIterator_whenTwoPages_crossesBoundaryForward() {
        // given
        PagedResult<String> result = twoPages(TWO_ITEMS, ONE_ITEM, SELF_URL);

        // when
        ListIterator<String> it = result.listIterator();

        // then
        assertEquals(PAGE2_FIRST_ITEM, it.next());
        assertEquals(PAGE2_SECOND_ITEM, it.next());
        assertEquals(PAGE2_THIRD_ITEM, it.next()); // page boundary crossing
        assertFalse(it.hasNext());
    }

    @Test
    void listIterator_whenTwoPages_crossesBoundaryBackward() {
        // given
        PagedResult<String> result = twoPages(TWO_ITEMS, ONE_ITEM, SELF_URL);
        ListIterator<String> it = result.listIterator();
        it.next(); // x
        it.next(); // y
        it.next(); // z (crosses to page 2)

        // when / then
        assertEquals(PAGE2_THIRD_ITEM, it.previous()); // back to page 2, item z
        assertEquals(PAGE2_SECOND_ITEM, it.previous()); // crosses back to page 1, item y
        assertEquals(PAGE2_FIRST_ITEM, it.previous()); // item x
        assertFalse(it.hasPrevious());
    }

    // --- seek + listIterator ---

    @Test
    void seek_whenListIteratorUsed_startsAtGivenPosition() {
        // given
        PagedResult<String> result = twoPages(TWO_ITEMS, ONE_ITEM, SELF_URL);
        result.seek(INDEX_2);

        // when
        ListIterator<String> it = result.listIterator();

        // then
        assertEquals(INDEX_2, it.nextIndex());
        assertEquals(PAGE2_THIRD_ITEM, it.next());
    }

    @Test
    void seek_whenIteratorUsed_doesNotAffectIterator() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        result.seek(INDEX_1);

        // when - iterator() always starts from 0
        Iterator<String> it = result.iterator();

        // then
        assertEquals(FIRST_ITEM, it.next());
    }

    // --- unsupported mutations ---

    @Test
    void listIterator_whenSet_throwsUnsupported() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();

        // when / then
        assertThrows(UnsupportedOperationException.class, () -> it.set(MUTATION_VALUE));
    }

    @Test
    void listIterator_whenAdd_throwsUnsupported() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();

        // when / then
        assertThrows(UnsupportedOperationException.class, () -> it.add(MUTATION_VALUE));
    }

    @Test
    void listIterator_whenRemove_throwsUnsupported() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, SELF_URL);
        ListIterator<String> it = result.listIterator();

        // when / then
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

    // --- fetchFrom ---

    @Test
    void fetchFrom_whenOffset_returnsItemsAtOffset() {
        // given
        PagedResult<String> result = twoPages(TWO_ITEMS, ONE_ITEM, SELF_URL);

        // when
        List<String> page = result.fetchFrom(INDEX_2);

        // then
        assertEquals(List.of(PAGE2_THIRD_ITEM), page);
    }

    @Test
    void fetchFrom_whenCalled_doesNotMoveSeekPosition() {
        // given
        PagedResult<String> result = twoPages(TWO_ITEMS, ONE_ITEM, SELF_URL);
        result.fetchFrom(INDEX_2);

        // when - seek position is still 0
        ListIterator<String> it = result.listIterator();

        // then
        assertEquals(PAGE2_FIRST_ITEM, it.next());
    }

    @Test
    void fetchFrom_whenNoSelfUrl_throwsIllegalState() {
        // given
        PagedResult<String> result = singlePage(THREE_ITEMS, null);
        result.totalCount(); // trigger init first

        // when / then
        assertThrows(IllegalStateException.class, () -> result.fetchFrom(INDEX_0));
    }
}
