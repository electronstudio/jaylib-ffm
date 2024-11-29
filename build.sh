    #! /bin/bash
    rm -rf src/main/java/com/raylib target
    gcc raylib/parser/raylib_parser.c -o raylib_parser
    ./raylib_parser --input /usr/local/include/raylib.h --output raylib_api.json --format JSON
    ./raylib_parser --input raylib/src/raymath.h --output raymath_api.json --format JSON --define RMAPI
    ./raylib_parser --input raylib/src/rlgl.h --output rlgl_api.json --format JSON --truncate "RLGL IMPLEMENTATION"
    ~/jextract-22/bin/jextract -l :./libraylib.so --output src/main/java/ --target-package com.raylib.jextract raylib.h
    sed -i 's/".\/libraylib.so"/uk.co.electronstudio.Util.extractDLLforOS()/g' src/main/java/com/raylib/jextract/raylib_h_1.java
    python3.12 generate.py
    mvn package
    java -cp target/jaylib-ffm-5.5.0-1.jar uk.co.electronstudio.tests.Bunnymark