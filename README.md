# Jaylib2 - the Ultimate Java binding for Raylib

## Alpha testing

Not production ready.  Please test, report what doesn't work and add what is missing.

## About

* Uses Java FFI
* Requires Java 22+
* Faster than JNI, Jaylib1, or any other binding

The Java FFI structs are in com.raylib.jextract.* The functions are in com.raylib.jextract.raylib_h.  They are not
pleasant to use, so we are writing wrappers for them.

The wrapped structs are in com.raylib.*.  They are missing automatic conversion for more complex types.

The wrapped functions aren't done yet.

The official Raylib DLLs are included in the jar.  There isn't one for ARM Linux (Raspberry Pi) but it will attempt
to load the external file /usr/lib/libraylib.so.

## Building


    python3.12 generate.py
    jextract -l :./libraylib.so --output src/main/java/ --target-package com.raylib.jextract /usr/local/include/raylib.h

Edit raylib_h.java to change the library loader from "./librarylib.so" to Util.extractDLLforOS()

    TODO

    mvn package
    java -cp target/jaylib2-1.0-SNAPSHOT.jar uk.co.electronstudio.Bunnymark
