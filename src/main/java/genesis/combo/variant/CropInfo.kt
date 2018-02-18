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

package genesis.combo.variant

import genesis.init.GenesisBlocks
import genesis.init.GenesisItems
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item

enum class CropInfo(
        val cropName: String,
        private val cropF: () -> Item,
        private val seedF: () -> Item,
        private val plantF: () -> IBlockState,
        val growthStages: Int = 7,
        val growthAge: Int = 5,
        val growthMultiplier: Float = 1.0f,
        val seedDropMultiplier: Float = 1.0f,
        val isDoubleCrop: Boolean = true,
        val breakTogether: Boolean = true,
        val width: Double = 1.0,
        private val heightFunc: (Int) -> Double = { it * 0.125 })
{
    ZINGIBEROPSIS("zingiberopsis",
            { GenesisItems.ZINGIBEROPSIS_RHIZOME },
            { GenesisItems.ZINGIBEROPSIS_RHIZOME },
            { GenesisBlocks.ZINGIBEROPSIS.defaultState },
            growthMultiplier = 0.625f,
            seedDropMultiplier = 0.5f,
            width = 0.75,
            heightFunc = {
                when (it) {
                    0 -> 0.125
                    1 -> 0.25
                    2 -> 0.375
                    3 -> 0.625
                    4 -> 0.8125
                    5 -> 1.3125
                    6 -> 1.5625
                    7 -> 1.75
                    else -> 1.0
                }
            }
    );

    val seed: Item get() = seedF()
    val crop: Item get() = cropF()
    val plant: IBlockState get() = plantF()

    fun getHeight(i: Int) = heightFunc(i)
}