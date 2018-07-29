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
import genesis.world.gen.feature.*
import genesis.world.gen.feature.WorldGenTreeDryophyllum.DryophyllumVariant
import net.minecraft.block.Block
import java.util.*

enum class EnumTree(private val leavesSupplier: () -> Block, private val logSupplier: () -> Block, private val saplingSupplier: () -> Block) {

    ARAUCARIOXYLON({ GenesisBlocks.ARAUCARIOXYLON_LEAVES }, { GenesisBlocks.ARAUCARIOXYLON_LOG }, { GenesisBlocks.ARAUCARIOXYLON_SAPLING }),
    DRYOPHYLLUM({ GenesisBlocks.DRYOPHYLLUM_LEAVES }, { GenesisBlocks.DRYOPHYLLUM_LOG }, { GenesisBlocks.DRYOPHYLLUM_SAPLING }),
    GINKGO({ GenesisBlocks.GINKGO_LEAVES }, { GenesisBlocks.GINKGO_LOG }, { GenesisBlocks.GINKGO_SAPLING }),
    METASEQUOIA({ GenesisBlocks.METASEQUOIA_LEAVES }, { GenesisBlocks.METASEQUOIA_LOG }, { GenesisBlocks.METASEQUOIA_SAPLING });

    val leaves: Block get() = leavesSupplier()

    val log: Block get() = logSupplier()

    val sapling: Block get() = saplingSupplier()

    fun growsIntoLargeTree() = false

    fun getTreeGenerator(random: Random): WorldGenAbstractGenesisTree? {
        return when (this) {
            ARAUCARIOXYLON -> WorldGenTreeAraucarioxylon(25, 30)
            DRYOPHYLLUM -> {
                if (random.nextInt(6) != 0) WorldGenTreeDryophyllum(DryophyllumVariant.TYPE_1, 11, 15)
                else WorldGenTreeDryophyllum(DryophyllumVariant.TYPE_2, 13, 19)
            }
            GINKGO -> WorldGenTreeGinkgo(10, 13)
            METASEQUOIA -> WorldGenTreeMetasequoia()
        }
    }
}
