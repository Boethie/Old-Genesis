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

package genesis.util

import java.util.*

infix fun Double.to(x: Double): DoubleRange {
    return DoubleRange.create(this, x)
}

infix fun Float.to(x: Float): FloatRange {
    return FloatRange.create(this, x)
}

class DoubleRange(private val min: Double, private val range: Double) {
    companion object {
        @JvmStatic fun create(min: Double, max: Double): DoubleRange {
            return DoubleRange(min, max-min)
        }
    }

    fun get(random: Random) = random.nextDouble() * range + min
}

class FloatRange(private val min: Float, private val range: Float) {
    companion object {
        @JvmStatic fun create(min: Float, max: Float): FloatRange {
            return FloatRange(min, max-min)
        }
    }

    fun get(random: Random) = random.nextFloat() * range + min
}
