/*
 * This file is part of Genesis Mod, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 Boethie
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package genesis.util;

public class Coords {
    public static final int CHUNK_BITS = 4;
    public static final int CHUNK_SIZE = 1 << CHUNK_BITS;

    public static int chunkToMinBlock(int chunkCoord) {
        return chunkCoord << CHUNK_BITS;
    }

    public static int packLocalXZ(int localX, int localZ) {
        return localZ * CHUNK_SIZE + localX;
    }

    public static int localToBlock(int local, int chunk) {
        return chunkToMinBlock(chunk) + local;
    }

    public static long pack(int x, int y) {
        return ((long) x) << 32 | (y & 0xFFFFFFFFL);
    }

    public static int unpackX(long pos) {
        return (int) (pos >>> 32);
    }

    public static int unpackY(long pos) {
        return (int) (pos & 0xFFFFFFFFL);
    }

    // TODO: add util methods for converting between local, chunk and block coords
}
