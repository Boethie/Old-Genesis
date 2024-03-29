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
package genesis.util.noise;

/*
 * Stores 3d noise data in one dimensional array, hiding confusing index calculation.
 */
public class NoiseBuffer3D {

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    private final int strideX;
    private final int strideZ;

    // the values in the array go first bottom-up, then increasing Z, then increasing X
    private double[] data;

    public NoiseBuffer3D(int sizeX, int sizeY, int sizeZ) {
        this.data = new double[sizeX * sizeY * sizeZ];
        //noinspection SuspiciousNameCombination
        this.strideX = sizeY;
        this.strideZ = strideX * sizeZ;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public double get(int x, int y, int z) {
        return data[idx(x, y, z)];
    }

    public void set(int x, int y, int z, double value) {
        data[idx(x, y, z)] = value;
    }

    private int idx(int x, int y, int z) {
        return y + z * strideX + x * strideZ;
    }

    public void fillFrom(BufferedNoiseProvider3D noise) {
        int idx = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int y = 0; y < sizeY; y++) {
                    assert idx == idx(x, y, z);
                    this.data[idx] = noise.get(x, y, z);
                    idx++;
                }
            }
        }
    }
}
