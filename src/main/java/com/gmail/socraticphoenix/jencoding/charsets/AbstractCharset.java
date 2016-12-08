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
package com.gmail.socraticphoenix.jencoding.charsets;

import com.gmail.socraticphoenix.jencoding.coding.SimpleDecoder;
import com.gmail.socraticphoenix.jencoding.coding.SimpleEncoder;
import com.gmail.socraticphoenix.jencoding.dict.CharacterDictionary;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public abstract class AbstractCharset extends Charset {
    private CharacterDictionary dictionary;
    private float averageCharsPerByte;
    private float maxCharsPerByte;

    private float averageBytesPerChar;
    private float maxBytesPerChar;
    private byte[] replacement;

    public AbstractCharset(String canonicalName, String[] aliases, CharacterDictionary dictionary, float averageCharsPerByte, float maxCharsPerByte, float averageBytesPerChar, float maxBytesPerChar, int replacement) {
        super(canonicalName, aliases);
        this.dictionary = dictionary;
        this.averageCharsPerByte = averageCharsPerByte;
        this.maxCharsPerByte = maxCharsPerByte;
        this.averageBytesPerChar = averageBytesPerChar;
        this.maxBytesPerChar = maxBytesPerChar;
        this.replacement = this.encodeCodepoint(this.dictionary.codepoint(replacement));
    }

    public abstract boolean isSufficient(byte[] arr);

    public abstract boolean isValid(byte[] arr);

    public abstract int getCodepoint(byte[] arr);

    public abstract byte[] encodeCodepoint(int point);

    public CharacterDictionary getDictionary() {
        return this.dictionary;
    }

    @Override
    public boolean contains(Charset cs) {
        return cs == this;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new SimpleDecoder(this, this.averageCharsPerByte, this.maxCharsPerByte);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new SimpleEncoder(this, this.averageBytesPerChar, this.maxBytesPerChar, this.replacement);
    }

}