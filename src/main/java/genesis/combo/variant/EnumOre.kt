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
import net.minecraft.block.Block
import net.minecraft.item.Item

enum class EnumOre(
        val hardness: Float = 3.0f,
        val resistance: Float = 5.0f,
        val expMin: Int = 2,
        val expMax: Int = 5,
        val expFurnace: Float = 0.2f,
        val dropMin: Int = 1,
        val dropMax: Int = 1,
        private val blockSupplier: () -> Block,
        private val itemSupplier: () -> Item)
{
    ZIRCON(blockSupplier =  { GenesisBlocks.ZIRCON_ORE }, itemSupplier = { GenesisItems.ZIRCON }),
    GARNET(blockSupplier = { GenesisBlocks.GARNET_ORE }, itemSupplier =  { GenesisItems.GARNET }),
    SIDERITE(expMin = 1, blockSupplier =  { GenesisBlocks.SIDERITE_ORE }, itemSupplier = { GenesisItems.SIDERITE }),
    TOURMALINE(blockSupplier = { GenesisBlocks.TOURMALINE_ORE }, itemSupplier =  { GenesisItems.TOURMALINE }),
    AQUAMARINE(blockSupplier = { GenesisBlocks.AQUAMARINE_ORE }, itemSupplier =  { GenesisItems.AQUAMARINE }),
    PYRITE(expMin = 1, blockSupplier =  { GenesisBlocks.PYRITE_ORE }, itemSupplier = { GenesisItems.PYRITE }),
    AZURITE(dropMin = 4, dropMax = 8, blockSupplier =  { GenesisBlocks.AZURITE_ORE }, itemSupplier = { GenesisItems.AZURITE });

    val block: Block get() = blockSupplier()

    val item: Item get() = itemSupplier()
}
