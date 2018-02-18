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
import net.minecraft.block.Block

enum class EnumRock private constructor(val hardness: Float, val resistance: Float, private val blockSupplier: () -> Block) {

    GRANITE(1.4f, 10.0f, { GenesisBlocks.GRANITE }),
    MOSSY_GRANITE(1.4f, 10.0f, { GenesisBlocks.MOSSY_GRANITE }),
    KOMATIITE(1.25f, 10.0f, { GenesisBlocks.KOMATIITE }),
    ORTHOGNEISS(1.5f, 10.0f, { GenesisBlocks.ORTHOGNEISS }),
    PEGMATITE(1.5f, 10.0f, { GenesisBlocks.PEGMATITE }),
    CARBONADO(2.15f, 10.0f, { GenesisBlocks.CARBONADO });

    val block: Block get() = blockSupplier()
}
