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
package genesis.util.noise.gen;

public class NoiseValueSchemes {

    //2D Value+Derivatives of order 1 (with Cross derivatives)
    public static class D2C1 {
        public static final int F = 0;
        public static final int Fx = 1;
        public static final int Fy = 2;
        public static final int Fxy = 3;
        public static final int STEP = 4;

        public static void add(double[] dest, int dest_i, double[] src, int src_i) {
            dest[dest_i+F] += src[src_i+F]; dest[dest_i+Fx] += src[src_i+Fx]; dest[dest_i+Fy] += src[src_i+Fy]; dest[dest_i+Fxy] += src[src_i+Fxy];
        }

        public static void totalScale(double[] dest, int dest_i, double xScale, double yScale, double fScale) {
            dest[dest_i+F] *= fScale; dest[dest_i+Fx] *= fScale * xScale; dest[dest_i+Fy] *= fScale * yScale; dest[dest_i+Fxy] *= fScale * xScale * yScale;
        }

        public static void valueScale(double[] dest, int dest_i, double fScale) {
            dest[dest_i+F] *= fScale; dest[dest_i+Fx] *= fScale; dest[dest_i+Fy] *= fScale; dest[dest_i+Fxy] *= fScale;
        }

        public static void octaveDown(double[] dest, int dest_i) {
            dest[dest_i+F] /= 2; dest[dest_i+Fxy] *= 2;
        }
    }

    //3D Value+Derivatives of order 1 (with Cross derivatives)
    public static class D3C1 {
        public static final int F = 0;
        public static final int Fx = 1;
        public static final int Fy = 2;
        public static final int Fz = 3;
        public static final int Fxy = 4;
        public static final int Fxz = 5;
        public static final int Fyz = 6;
        public static final int Fxyz = 7;
        public static final int STEP = 8;

        public static void add(double[] dest, int dest_i, double[] src, int src_i) {
            dest[dest_i+F] += src[src_i+F]; dest[dest_i+Fx] += src[src_i+Fx]; dest[dest_i+Fy] += src[src_i+Fy]; dest[dest_i+Fz] += src[src_i+Fz];
            dest[dest_i+Fxy] += src[src_i+Fxy]; dest[dest_i+Fxz] += src[src_i+Fxz]; dest[dest_i+Fyz] += src[src_i+Fyz]; dest[dest_i+Fxyz] += src[src_i+Fxyz];
        }

        public static void totalScale(double[] dest, int dest_i, double xScale, double yScale, double zScale, double fScale) {
            dest[dest_i+F] *= fScale; dest[dest_i+Fx] *= fScale * xScale; dest[dest_i+Fy] *= fScale * yScale; dest[dest_i+Fz] *= fScale * zScale;
            dest[dest_i+Fxy] *= fScale * xScale * yScale; dest[dest_i+Fxz] *= fScale * xScale * zScale; dest[dest_i+Fyz] *= fScale * yScale * zScale;
            dest[dest_i+Fxyz] *= fScale * xScale * yScale * zScale;
        }

        public static void valueScale(double[] dest, int dest_i, double fScale) {
            dest[dest_i+F] *= fScale; dest[dest_i+Fx] *= fScale; dest[dest_i+Fy] *= fScale; dest[dest_i+Fz] *= fScale;
            dest[dest_i+Fxy] *= fScale; dest[dest_i+Fxz] *= fScale; dest[dest_i+Fyz] *= fScale;
            dest[dest_i+Fxyz] *= fScale;
        }

        public static void octaveDown(double[] dest, int dest_i) {
            dest[dest_i+F] /= 2;
            dest[dest_i+Fxy] *= 2; dest[dest_i+Fxz] *= 2; dest[dest_i+Fyz] *= 2;
            dest[dest_i+Fxyz] *= 4;
        }
    }

    //3D Value+Derivatives of order 1
    public static class D3S1 {
        public static final int F = 0;
        public static final int Fx = 1;
        public static final int Fy = 2;
        public static final int Fz = 3;
        public static final int STEP = 4;

        public static void add(double[] dest, int dest_i, double[] src, int src_i) {
            dest[dest_i+F] += src[src_i+F]; dest[dest_i+Fx] += src[src_i+Fx]; dest[dest_i+Fy] += src[src_i+Fy]; dest[dest_i+Fz] += src[src_i+Fz];
        }

        public static void totalScale(double[] dest, int dest_i, double xScale, double yScale, double zScale, double fScale) {
            dest[dest_i+F] *= fScale; dest[dest_i+Fx] *= fScale * xScale; dest[dest_i+Fy] *= fScale * yScale; dest[dest_i+Fz] *= fScale * zScale;
        }

        public static void valueScale(double[] dest, int dest_i, double fScale) {
            dest[dest_i+F] *= fScale; dest[dest_i+Fx] *= fScale; dest[dest_i+Fy] *= fScale; dest[dest_i+Fz] *= fScale;
        }

        public static void octaveDown(double[] dest, int dest_i) {
            dest[dest_i+F] /= 2;
        }
    }

}