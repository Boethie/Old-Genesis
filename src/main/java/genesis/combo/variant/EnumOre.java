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

import genesis.init.GenesisBlocks;
import genesis.init.GenesisItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public enum EnumOre {
    ZIRCON(1.5F, 10.0F, 2, 5, () -> GenesisBlocks.ZIRCON_ORE, () -> GenesisItems.ZIRCON),
    GARNET(1.5F, 10.0F, 2, 5, () -> GenesisBlocks.GARNET_ORE, () -> GenesisItems.GARNET),
    AQUAMARINE(1.5F, 10.0F, 7, 3, () -> GenesisBlocks.AQUAMARINE_ORE, () -> GenesisItems.AQUAMARINE),
    MALACHITE(0.75F, 8.7F, 2, 5, 4, 8, () -> GenesisBlocks.MALACHITE_ORE, () -> GenesisItems.MALACHITE),
    PYRITE(0.75F, 8.7F, 1, 5, () -> GenesisBlocks.PYRITE_ORE, () -> GenesisItems.PYRITE);

    private final float hardness;
    private final float resistance;
    private final int dropMin;
    private final int dropMax;
    private final int expMin;
    private final int expMax;
    private final Supplier<Block> blockSupplier;
    private final Supplier<Item> itemSupplier;

    EnumOre(float hardness, float resistance,
            int expMin, int expMax,
            Supplier<Block> blockSupplier,
            Supplier<Item> itemSupplier) {
        this(hardness, resistance, expMin, expMax, 1, 1, blockSupplier, itemSupplier);
    }

    EnumOre(float hardness, float resistance,
            int expMin, int expMax,
            int dropMin, int dropMax,
            Supplier<Block> blockSupplier,
            Supplier<Item> itemSupplier) {
        this.hardness = hardness;
        this.resistance = resistance;
        this.dropMin = dropMin;
        this.dropMax = dropMax;
        this.expMin = expMin;
        this.expMax = expMax;
        this.blockSupplier = blockSupplier;
        this.itemSupplier = itemSupplier;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    public int getDropMin() {
        return dropMin;
    }

    public int getDropMax() {
        return dropMax;
    }

    public int getExpMin() {
        return expMin;
    }

    public int getExpMax() {
        return expMax;
    }

    public Block getBlock() {
        return blockSupplier.get();
    }

    public Item getItem() {
        return itemSupplier.get();
    }
}
