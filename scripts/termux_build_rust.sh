#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

export CARGO_NDK_ON_ANDROID=1
cargo ndk -t arm64-v8a -t x86_64 -p 26 \
  -o android/app/src/main/jniLibs \
  build --release -p mobilot_core

echo "OK: android/app/src/main/jniLibs/{arm64-v8a,x86_64}/libmobilot_core.so"
