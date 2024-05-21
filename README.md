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

Most C types are now wrapped as Java types.  If a function returns a MemorySegment it means that type hasn't been wrapped.
This may be because there is no Java equivalent that makes sense, but if you think otherwise report it and I might
add it.

## Example project

Download ([example project](https://github.com/electronstudio/jaylib-ffm-example-project)and import it into IntelliJ or Eclipse to get up and running immediately.

## How to use with Gradle

```

dependencies {
    implementation 'io.github.electronstudio:jaylib:0.0.1'
}

```

## How to use with Maven

```

    <dependencies>
        <dependency>
            <groupId>io.github.electronstudio</groupId>
            <artifactId>jaylib-ffm</artifactId>
            <version>0.0.1</version>
        </dependency>
    </dependencies>

```

## How to use from command line

Download the latest `jaylib-ffm.jar` from [releases](https://github.com/electronstudio/jaylib-ffm/releases)

Write a demo program, e.g. Demo.java

```java
import static com.raylib.Raylib.*;
import static com.raylib.Raylib.CameraMode.CAMERA_ORBITAL;
import static com.raylib.Raylib.CameraProjection.CAMERA_PERSPECTIVE;

import com.raylib.Camera3D;
import com.raylib.Vector3;

public class Main {
    public static void main(String args[]) {
        initWindow(800, 450, "Demo");
        setTargetFPS(60);
        Camera3D camera = new Camera3D(new Vector3(18,16,18),
                new Vector3(),
                new Vector3(0,1,0),
                45, CAMERA_PERSPECTIVE);

        while (!windowShouldClose()) {
            updateCamera(camera, CAMERA_ORBITAL);
            beginDrawing();
            clearBackground(RAYWHITE);
            beginMode3D(camera);
            drawGrid(20, 1.0f);
            endMode3D();
            drawText("Hello world", 190, 200, 20, VIOLET);
            drawFPS(20, 20);
            endDrawing();
        }
        closeWindow();
    }
}
```

Compile it:

    javac -cp jaylib-ffm-5.0.0-0.jar Demo.java

Run it:

    java -cp jaylib-ffm-5.0.0-0.jar:. Demo

On MacOS you need this additional option:

    java -XstartOnFirstThread -cp jaylib-ffm-5.0.0-0.jar:. Demo

On weirdy Windows you use semi-colons:

    java -cp jaylib-ffm-5.0.0-0.jar;. Demo

## Arrays

C has a problem known as "array to pointer decay".  C will automatically convert an array type and to a pointer
type. Therefore the computer can't tell
if a function parameter is an array of many items or a pointer to a single item.
I don't know if modern C has solved this, but Raylib certainly hasn't.  The info isn't in raylib_api.json.
You just have to read the docs for each
function to find out which it is.

So sometimes you will get a Java object that may represent one struct, or may represent an array of structs.
You'll need to check the Raylib docs or the Javadoc for the function to know which it has given you.

If it's an array, you can use the getArrayElement() method to access the other structs.  To create a new array
there is allocateArray() method.

(I suppose we could provide List-like wrappers but to actually make them safe would require working out every
function that uses arrays and what parameter it uses to store the size of the array. Nice to have but a luxury
that users of C Raylib don't have, and requiring manual maintance work, so out of scope for now.)

Arrays of ints and floats are wrapped as IntBuffer and FloatBuffer.  Note if you create your own you should use
Raylib.createIntBuffer(1) to ensure you create a direct, native buffer.

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

## See also

[Megabunny](https://github.com/electronstudio/megabunny) - Raylib benchmarks

[Jaylib](https://github.com/electronstudio/jaylib) - Java JNI Raylib binding