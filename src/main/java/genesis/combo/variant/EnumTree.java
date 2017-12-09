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
package genesis.combo.variant;

import genesis.world.gen.feature.WorldGenAbstractGenesisTree;
import genesis.world.gen.feature.WorldGenTreeAraucarioxylon;
import genesis.world.gen.feature.WorldGenTreeDryophyllum;
import genesis.world.gen.feature.WorldGenTreeDryophyllum.DryophyllumVariant;
import genesis.world.gen.feature.WorldGenTreeGinkgo;
import net.minecraft.block.Block;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

import static genesis.init.GenesisBlocks.*;

public enum EnumTree {

    ARAUCARIOXYLON(() -> ARAUCARIOXYLON_LEAVES, () -> ARAUCARIOXYLON_LOG, () -> ARAUCARIOXYLON_SAPLING),
    DRYOPHYLLUM(() -> DRYOPHYLLUM_LEAVES, () -> DRYOPHYLLUM_LOG, () -> DRYOPHYLLUM_SAPLING),
    FICUS(() -> FICUS_LEAVES, () -> FICUS_LOG, () -> FICUS_SAPLING),
    GINKGO(() -> GINKGO_LEAVES, () -> GINKGO_LOG, () -> GINKGO_SAPLING),
    METASEQUOIA(() -> METASEQUOIA_LEAVES, () -> METASEQUOIA_LOG, () -> METASEQUOIA_SAPLING);

    private final Supplier<Block> leavesSupplier;
    private final Supplier<Block> logSupplier;
    private final Supplier<Block> saplingSupplier;

    EnumTree(Supplier<Block> leavesSupplier, Supplier<Block> logSupplier, Supplier<Block> saplingSupplier) {
        this.leavesSupplier = leavesSupplier;
        this.logSupplier = logSupplier;
        this.saplingSupplier = saplingSupplier;
    }

    public Block getLeaves() {
        return leavesSupplier.get();
    }

    public Block getLog() {
        return logSupplier.get();
    }

    public Block getSapling() {
        return saplingSupplier.get();
    }

    public boolean growsIntoLargeTree() {
        return false;
    }

    @Nullable
    public WorldGenAbstractGenesisTree getTreeGenerator(Random random) {
        switch (this) {
            case ARAUCARIOXYLON:
                return new WorldGenTreeAraucarioxylon(25, 30);
            case DRYOPHYLLUM:
                return (random.nextInt(6) != 0 ?
                        new WorldGenTreeDryophyllum(DryophyllumVariant.TYPE_1, 11, 15) :
                        new WorldGenTreeDryophyllum(DryophyllumVariant.TYPE_2, 13, 19)
                );
            case GINKGO:
                return new WorldGenTreeGinkgo(10, 13);
        }
        return null;
    }
}
