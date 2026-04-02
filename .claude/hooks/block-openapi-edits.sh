#!/bin/bash
# Requires user approval for Edit/Write to openapi/ files

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

if [[ "$FILE_PATH" == *"openapi/"* ]] || [[ "$FILE_PATH" == *"openapi\\"* ]]; then
  jq -n '{
    hookSpecificOutput: {
      hookEventName: "PreToolUse",
      permissionDecision: "ask",
      permissionDecisionReason: "This edit targets an openapi/ file (source of truth). Approve?"
    }
  }'
  exit 0
fi

exit 0
