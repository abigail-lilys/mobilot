cmake_minimum_required(VERSION 3.20)
project(mobilot_fuse LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(GODOT_CPP_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../third_party/godot-cpp")
if(NOT EXISTS "${GODOT_CPP_DIR}/CMakeLists.txt")
    message(FATAL_ERROR "godot-cpp missing at ${GODOT_CPP_DIR}")
endif()
add_subdirectory(${GODOT_CPP_DIR} godot-cpp)

if(NOT DEFINED CMAKE_ANDROID_ARCH_ABI OR CMAKE_ANDROID_ARCH_ABI STREQUAL "")
    set(_MOBILOT_ABI "${ANDROID_ABI}")
else()
    set(_MOBILOT_ABI "${CMAKE_ANDROID_ARCH_ABI}")
endif()

set(MOBILOT_CORE_SO
    "${CMAKE_CURRENT_SOURCE_DIR}/../../android/app/src/main/jniLibs/${_MOBILOT_ABI}/libmobilot_core.so")

if(NOT EXISTS "${MOBILOT_CORE_SO}")
    message(FATAL_ERROR
        "libmobilot_core.so not found at: ${MOBILOT_CORE_SO}\n"
        "Detected ABI: '${_MOBILOT_ABI}'")
endif()

add_library(mobilot_core SHARED IMPORTED)
set_target_properties(mobilot_core PROPERTIES
    IMPORTED_LOCATION "${MOBILOT_CORE_SO}"
    IMPORTED_NO_SONAME TRUE
)

add_library(mobilot_fuse SHARED
    src/register_types.cpp
)

target_include_directories(mobilot_fuse PRIVATE src include)
target_link_libraries(mobilot_fuse PRIVATE godot-cpp mobilot_core log)
