# Jaylib2 - the Ultimate Java binding for Raylib

## Alpha testing

Not production ready.  Please test, report what doesn't work and add what is missing.

## About

* Uses Java FFI
* Requires Java 22+
* Faster than JNI, Jaylib1, or any other binding

The Java FFI structs are in [com.raylib.jextract.*](https://electronstudio.github.io/jaylib2/com/raylib/jextract/package-summary.html)
The functions are in [com.raylib.jextract.raylib_h](https://electronstudio.github.io/jaylib2/com/raylib/jextract/raylib_h.html).  They are not
pleasant to use, so we are writing wrappers for them.

The wrapped structs are in [com.raylib.*](https://electronstudio.github.io/jaylib2/com/raylib/package-summary.html).

The wrapped functions are in [com.raylib.Raylib](https://electronstudio.github.io/jaylib2/com/raylib/Raylib.html).

Automatic conversion for pointer types is still TODO.

The official Raylib DLLs are included in the jar.  There isn't one for ARM Linux (Raspberry Pi) but it will attempt
to load the external file /usr/lib/libraylib.so.

## Building

    gcc raylib/parser/raylib_parser.c -o raylib_parser
    ./raylib_parser --input /usr/local/include/raylib.h --output raylib_api.json --format JSON
    ./raylib_parser --input raylib/src/raymath.h --output raymath_api.json --format JSON --define RMAPI
    ./raylib_parser --input raylib/src/rlgl.h --output rlgl_api.json --format JSON --truncate "RLGL IMPLEMENTATION"
    jextract -l :./libraylib.so --output src/main/java/ --target-package com.raylib.jextract raylib.h
    python3.12 generate.py

Edit raylib_h_1.java to change the library loader from "./librarylib.so" to Util.extractDLLforOS()

    TODO

    mvn package
    java -cp target/jaylib2-1.0-SNAPSHOT.jar uk.co.electronstudio.Bunnymark
