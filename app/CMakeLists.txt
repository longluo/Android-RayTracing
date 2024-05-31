# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.10)

# Specifies the name of the project.
project(raytracer)

set(libs_include_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/include)

# Sets the path to your source file(s).
set(SRC_FILES  ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/raytracer.cpp)

# Specifies the library name and sets the library type to SHARED.
add_library(
        raytracer
        SHARED
        ${SRC_FILES}
)

# Specifies the path to your NDK libraries.
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O1")

# Specifies the libraries to link with your target library.
target_link_libraries(
        raytracer
        log
        m
        jnigraphics
)
