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

enum class EnumCrop constructor(val growthStages: Int,
                                val growthMultiplier: Float,
                                val isDoubleCrop: Boolean,
                                val breakTogether: Boolean,
                                val growthAge: Int,
                                val crop: () -> Item,
                                val seed: () -> Item,
                                val plant: () -> IBlockState,
                                val width: Double,
                                val heightFunc: (Int) -> Double) {

    ZINGIBEROPSIS(
            7,
            0.625f,
            true,
            5,
            { GenesisItems.ZINGIBEROPSIS_RHIZOME },
            { GenesisItems.ZINGIBEROPSIS_RHIZOME },
            { GenesisBlocks.ZINGIBEROPSIS.defaultState },
            0.75,
            { integer -> when (integer) {
                0 -> 0.125
                1 -> 0.25
                2 -> 0.375
                3 -> 0.625
                4 -> 0.8125
                5 -> 1.3125
                6 -> 1.5625
                7 -> 1.75
                else -> 1.0
            } }
    );

    constructor(growthStages: Int,
                growthMultiplier: Float,
                crop: () -> Item,
                seed: () -> Item,
                plant: () -> IBlockState,
                width: Double,
                heightFunc: (Int) -> Double) : this(growthStages, growthMultiplier, false, false, 0, crop, seed, plant, width, heightFunc)

    constructor(growthStages: Int,
                growthMultiplier: Float,
                breakTogether: Boolean,
                growthAge: Int,
                crop: () -> Item,
                seed: () -> Item,
                plant: () -> IBlockState,
                width: Double,
                heightFunc: (Int) -> Double) : this(growthStages, growthMultiplier, true, breakTogether, growthAge, crop, seed, plant, width, heightFunc)
}
