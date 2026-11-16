cmake_minimum_required(VERSION 3.20)
project(mobilot_fuse LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(GODOT_CPP_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../third_party/godot-cpp")
if(NOT EXISTS "${GODOT_CPP_DIR}/CMakeLists.txt")
    message(FATAL_ERROR "godot-cpp missing at ${GODOT_CPP_DIR}")
endif()

set(GODOTCPP_API_VERSION "4.6" CACHE STRING "Target Godot API version for godot-cpp")

add_subdirectory(${GODOT_CPP_DIR} godot-cpp)

if(DEFINED ANDROID_ABI AND NOT "${ANDROID_ABI}" STREQUAL "")
    set(_MOBILOT_ABI "${ANDROID_ABI}")
elseif(DEFINED CMAKE_ANDROID_ARCH_ABI AND NOT "${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "")
    set(_MOBILOT_ABI "${CMAKE_ANDROID_ARCH_ABI}")
else()
    message(FATAL_ERROR "Cannot determine current Android ABI (ANDROID_ABI / CMAKE_ANDROID_ARCH_ABI both empty)")
endif()

set(_MOBILOT_JNI_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../android/app/src/main/jniLibs")
set(MOBILOT_CORE_SO "${_MOBILOT_JNI_ROOT}/${_MOBILOT_ABI}/libmobilot_core.so")
message(STATUS "Target ABI: ${_MOBILOT_ABI}")
message(STATUS "Using mobilot_core: ${MOBILOT_CORE_SO}")

if(NOT EXISTS "${MOBILOT_CORE_SO}")
    message(FATAL_ERROR
        "libmobilot_core.so not found at: ${MOBILOT_CORE_SO}\n"
        "Contents of ${_MOBILOT_JNI_ROOT}:")
    file(GLOB _MOBILOT_ALL "${_MOBILOT_JNI_ROOT}/*")
    foreach(_f ${_MOBILOT_ALL})
        message(STATUS "  ${_f}")
    endforeach()
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
