 cmake_minimum_required(VERSION 3.5)
 project(custom_build_dir_test)

 include(${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)
 conan_basic_setup()
