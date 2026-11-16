cmake_minimum_required(VERSION 3.20)
project(mobilot_fuse LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# godot-cpp 由 CI 或本地脚本预先 clone 到 third_party/
set(GODOT_CPP_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../third_party/godot-cpp")
if(NOT EXISTS "${GODOT_CPP_DIR}/CMakeLists.txt")
    message(FATAL_ERROR "godot-cpp missing at ${GODOT_CPP_DIR}. Clone branch 4.6 first.")
endif()
add_subdirectory(${GODOT_CPP_DIR} godot-cpp)

# 预编译好的 Rust 库（由 cargo ndk 生成到 jniLibs/<abi>/）
set(MOBILOT_CORE_SO
    "${CMAKE_CURRENT_SOURCE_DIR}/../../android/app/src/main/jniLibs/${ANDROID_ABI}/libmobilot_core.so")
if(NOT EXISTS "${MOBILOT_CORE_SO}")
    message(FATAL_ERROR
        "libmobilot_core.so not found at ${MOBILOT_CORE_SO}\n"
        "Run: cargo ndk -t ${ANDROID_ABI} -p 26 -o android/app/src/main/jniLibs build --release -p mobilot_core")
endif()
add_library(mobilot_core SHARED IMPORTED)
set_target_properties(mobilot_core PROPERTIES IMPORTED_LOCATION "${MOBILOT_CORE_SO}")

add_library(mobilot_fuse SHARED
    src/register_types.cpp
)

target_include_directories(mobilot_fuse PRIVATE src include)
target_link_libraries(mobilot_fuse PRIVATE godot-cpp mobilot_core log)
