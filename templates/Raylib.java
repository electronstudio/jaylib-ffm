
package com.raylib;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;

import static com.raylib.jextract.raylib_h.*;

public class Raylib{

    {% for function in functions %}
    {% include 'function.java.frag' %}
    {% endfor %}

    {% for enum in enums %}
    {% include 'enum.java.frag' %}
    {% endfor %}

    public static Color LIGHTGRAY = c(200, 200, 200, 255);
    public static Color GRAY = c(130, 130, 130, 255);
    public static Color DARKGRAY = c(80, 80, 80, 255);
    public static Color YELLOW = c(253, 249, 0, 255);
    public static Color GOLD = c(255, 203, 0, 255);
    public static Color ORANGE = c(255, 161, 0, 255);
    public static Color PINK = c(255, 109, 194, 255);
    public static Color RED = c(230, 41, 55, 255);
    public static Color MAROON = c(190, 33, 55, 255);
    public static Color GREEN = c(0, 228, 48, 255);
    public static Color LIME = c(0, 158, 47, 255);
    public static Color DARKGREEN = c(0, 117, 44, 255);
    public static Color SKYBLUE = c(102, 191, 255, 255);
    public static Color BLUE = c(0, 121, 241, 255);
    public static Color DARKBLUE = c(0, 82, 172, 255);
    public static Color PURPLE = c(200, 122, 255, 255);
    public static Color VIOLET = c(135, 60, 190, 255);
    public static Color DARKPURPLE = c(112, 31, 126, 255);
    public static Color BEIGE = c(211, 176, 131, 255);
    public static Color BROWN = c(127, 106, 79, 255);
    public static Color DARKBROWN = c(76, 63, 47, 255);
    public static Color WHITE = c(255, 255, 255, 255);
    public static Color BLACK = c(0, 0, 0, 255);
    public static Color BLANK = c(0, 0, 0, 0);
    public static Color MAGENTA = c(255, 0, 255, 255);
    public static Color RAYWHITE = c(245, 245, 245, 255);

    private static Color c(int r, int g, int b, int a) {
        return new Color((byte) r, (byte) g, (byte) b, (byte) a);
    }
}
