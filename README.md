# Jaylib-FFM - Java FFM binding for Raylib 5.0, RLGL, Raymath

Jaylib-FFM is a [Java FFM](https://docs.oracle.com/en/java/javase/22/core/foreign-function-and-memory-api.html) ('Project Panama')
binding for Raylib.  This makes it faster than any binding that uses older tech, even JNI as used by Jaylib.
However it does require Java 22 or newer to use.

The core of the library is generated using Jextract.  This has the advantage over JavaCPP that it is a first party tool and far
easier to understand.  The structs are in [com.raylib.jextract.*](https://electronstudio.github.io/jaylib-ffm/com/raylib/jextract/package-summary.html)
The functions are in [com.raylib.jextract.raylib_h](https://electronstudio.github.io/jaylib-ffm/com/raylib/jextract/raylib_h.html).

Unfortunately, compared to JavaCPP, the generated API is quite basic.  You need to be familiar with FFM memory management
and it doesn't enforce type safety.

Therefore we also generate high level wrappers ourselves based on raylib_api.json.  These should be nice and easy to
use.

The wrapped structs are in [com.raylib.*](https://electronstudio.github.io/jaylib-ffm/com/raylib/package-summary.html).

The wrapped functions are in [com.raylib.Raylib](https://electronstudio.github.io/jaylib-ffm/com/raylib/Raylib.html).

The official Raylib DLLs are included in the jar.  There isn't one for ARM Linux (Raspberry Pi) but it will attempt
to load the external file /usr/lib/libraylib.so.

## Alpha testing

Not production ready.  Please test, report what doesn't work and add what is missing.

Automatic conversion for pointer types is still TODO.  Raygui won't be added unless there is demand.

## About


## Building

    gcc raylib/parser/raylib_parser.c -o raylib_parser
    ./raylib_parser --input /usr/local/include/raylib.h --output raylib_api.json --format JSON
    ./raylib_parser --input raylib/src/raymath.h --output raymath_api.json --format JSON --define RMAPI
    ./raylib_parser --input raylib/src/rlgl.h --output rlgl_api.json --format JSON --truncate "RLGL IMPLEMENTATION"
    jextract -l :./libraylib.so --output src/main/java/ --target-package com.raylib.jextract raylib.h
    sed -i 's/".\/libraylib.so"/uk.co.electronstudio.Util.extractDLLforOS()/g' src/main/java/com/raylib/jextract/raylib_h_1.java
    python3.12 generate.py
    mvn package
    java -cp target/jaylib2-ffm-0.0.1.jar uk.co.electronstudio.tests.Bunnymark
