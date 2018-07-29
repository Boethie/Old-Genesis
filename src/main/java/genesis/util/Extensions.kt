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

import net.minecraft.util.math.BlockPos

public fun BlockPos.MutableBlockPos.shift(x: Int, y: Int, z: Int): BlockPos.MutableBlockPos {
    this.setPos(this.x + x, this.y + y, this.z + z)
    return this
}

public fun BlockPos.southEast(): BlockPos = BlockPos(this.x + 1, this.y, this.z + 1)
public fun BlockPos.southWest(): BlockPos = BlockPos(this.x - 1, this.y, this.z + 1)
public fun BlockPos.northEast(): BlockPos = BlockPos(this.x + 1, this.y, this.z - 1)
public fun BlockPos.northWest(): BlockPos = BlockPos(this.x - 1, this.y, this.z - 1)