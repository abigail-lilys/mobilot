#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

# Termux 本地快速验证脚本（可选）。
# 注意：cargo-ndk 4.x 起，Android API 级别用 --platform，不是 -p（-p 会透传给 cargo 当 --package）。

export CARGO_NDK_ON_ANDROID=1
cargo ndk -t arm64-v8a -t x86_64 --platform 26 \
  -o android/app/src/main/jniLibs \
  build --release -p mobilot_core

echo "OK: android/app/src/main/jniLibs/{arm64-v8a,x86_64}/libmobilot_core.so"
