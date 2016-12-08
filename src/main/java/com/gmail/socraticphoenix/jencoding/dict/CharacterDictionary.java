/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.jencoding.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CharacterDictionary {
    public static final char[] EMPTY = new char[0];
    private int[] dict;
    private int[] sorted;
    private int[] table;

    public CharacterDictionary(int length) {
        this(new int[length]);
    }

    private CharacterDictionary(int[] dict) {
        this.dict = dict;
        int[] sorted = Arrays.copyOf(this.dict, this.dict.length);
        Arrays.sort(sorted);
        this.sorted = sorted;
        this.table = new int[dict.length];
        for (int i = 0; i < this.table.length; i++) {
            int sv = sorted[i];
            int di = -1;
            for (int j = 0; j < this.dict.length; j++) {
                if(this.dict[j] == sv) {
                    di = j;
                    break;
                }
            }
            this.table[i] = di;
        }
    }

    public int[] getDict() {
        return this.dict.clone();
    }

    public static CharacterDictionary fromResources(String location) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(CharacterDictionary.class.getResourceAsStream(location), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        int i;
        while ((i = reader.read()) != -1) {
            builder.appendCodePoint(i);
        }
        return CharacterDictionary.fromString(builder.toString());
    }

    public static CharacterDictionary fromString(String page) {
        return new CharacterDictionary(page.codePoints().toArray());
    }

    public int codepoint(int character) {
        int i = Arrays.binarySearch(this.sorted, character);
        if(i < 0) {
            return -1;
        } else {
            return this.table[i];
        }
    }

    public char[] get(int codepoint) {
        return this.contains(codepoint) ? Character.toChars(this.dict[codepoint]) : CharacterDictionary.EMPTY;
    }

    public boolean set(int codepoint, char[] val) {
        if(val.length == 2 && !Character.isSurrogatePair(val[0], val[1])) {
            throw new IllegalArgumentException("Invalid surrogate pair: [" + (int) val[0] + ", " + (int) val[1] + "]");
        } else if (val.length != 2 && val.length != 1) {
            throw new IllegalArgumentException("Invalid surrogate length: " + val.length);
        }

        if(this.contains(codepoint)) {
            this.dict[codepoint] = val.length >= 2 ? Character.toCodePoint(val[0], val[1]) : val[0];
            return true;
        } else {
            return false;
        }
    }

    public boolean encodes(int character) {
        return this.codepoint(character) != -1;
    }

    public boolean contains(int codepoint) {
        return codepoint >= 0 && codepoint < dict.length;
    }

}