package com.almondtools.util.text;

public class CharUtils {

    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

}