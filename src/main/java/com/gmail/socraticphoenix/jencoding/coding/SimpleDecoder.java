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
package com.gmail.socraticphoenix.jencoding.coding;

import com.gmail.socraticphoenix.jencoding.charsets.AbstractCharset;

import java.io.ByteArrayOutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class SimpleDecoder extends CharsetDecoder {
    private AbstractCharset charset;
    private ByteArrayOutputStream stream;

    public SimpleDecoder(AbstractCharset cs, float averageCharsPerByte, float maxCharsPerByte) {
        super(cs, averageCharsPerByte, maxCharsPerByte);
        this.charset = cs;
        this.stream = new ByteArrayOutputStream();
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        try {
            while (in.hasRemaining()) {
                byte b = in.get();
                this.stream.write(b);
                byte[] current = this.stream.toByteArray();
                if (!this.charset.isValid(current)) {
                    return CoderResult.malformedForLength(current.length);
                } else if (this.charset.isSufficient(current)) {
                    out.put(this.charset.getDictionary().get(this.charset.getCodepoint(this.stream.toByteArray())));
                    this.stream = new ByteArrayOutputStream();
                }
            }
            return CoderResult.UNDERFLOW;
        } catch (BufferOverflowException e) {
            return CoderResult.OVERFLOW;
        } catch (BufferUnderflowException e) {
            return CoderResult.UNDERFLOW;
        }
    }

}