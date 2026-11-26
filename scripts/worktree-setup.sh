#!/usr/bin/env bash
# Prepares a git worktree: resets it to <start-sha> and copies the git-ignored
# files tests and graphify need from the main checkout.
# Usage, from the worktree root: scripts/worktree-setup.sh <start-sha>
set -euo pipefail

if [ $# -ne 1 ]; then
  echo "usage: $0 <start-sha>" >&2
  exit 1
fi

main=$(git worktree list --porcelain | head -n 1 | sed 's/^worktree //')
here=$(git rev-parse --show-toplevel)

if [ "$here" = "$main" ]; then
  echo "refusing to run in the main checkout ($main)" >&2
  exit 1
fi

cd "$here"
git reset --hard "$1"

git -C "$main" ls-files --others --ignored --exclude-standard -- '*.env' \
  ':!:**/node_modules/**' ':!:.claude/worktrees/**' |
  while read -r file; do
    if [ -f "$main/$file" ] && [ ! -L "$main/$file" ]; then
      mkdir -p "$(dirname "$file")"
      cp "$main/$file" "$file"
      echo "copied $file"
    fi
  done

if [ -d "$main/graphify-out" ]; then
  rm -rf graphify-out
  cp -r "$main/graphify-out" graphify-out
  echo "copied graphify-out/"
fi
