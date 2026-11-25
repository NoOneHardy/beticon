#!/bin/sh
# Forward the hook payload to `graphify hook-guard search` only for search
# commands: the Grep tool, or Bash commands that run grep/rg/ag/ack/find.
input=$(cat)
if command -v jq >/dev/null 2>&1; then
  tool=$(printf '%s' "$input" | jq -r '.tool_name // empty' 2>/dev/null)
  cmd=$(printf '%s' "$input" | jq -r '.tool_input.command // empty' 2>/dev/null)
else
  tool=$(printf '%s' "$input" | sed -n 's/.*"tool_name"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' | head -n1)
  cmd=$input
fi
run=0
[ "$tool" = "Grep" ] && run=1
if [ "$tool" = "Bash" ] && printf '%s' "$cmd" | grep -Eq '(^|[^[:alnum:]_.-])(grep|egrep|fgrep|rg|ag|ack|find)([^[:alnum:]_-]|$)'; then
  run=1
fi
[ "$run" = 1 ] || exit 0
printf '%s' "$input" | /home/no1hardy/.local/bin/graphify hook-guard search
