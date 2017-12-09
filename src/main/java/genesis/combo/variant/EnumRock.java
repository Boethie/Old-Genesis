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
import net.minecraft.block.Block;

import java.util.function.Supplier;

public enum EnumRock {

    GRANITE(1.4F, 10.0F, () -> GenesisBlocks.GRANITE),
    MOSSY_GRANITE(1.4F, 10.0F, () -> GenesisBlocks.MOSSY_GRANITE),
    KOMATIITE(1.25F, 10.0F, () -> GenesisBlocks.KOMATIITE),
    ORTHOGNEISS(1.5F, 10.0F, () -> GenesisBlocks.ORTHOGNEISS),
    LIMESTONE(0.75F, 8.7F, () -> GenesisBlocks.LIMESTONE),
    PEGMATITE(1.5F, 10.0F, () -> GenesisBlocks.PEGMATITE),
    CARBONADO(2.15F, 10.0F, () -> GenesisBlocks.CARBONADO);

    private final float hardness;
    private final float resistance;
    private final Supplier<Block> blockSupplier;

    EnumRock(float hardness, float resistance, Supplier<Block> blockSupplier) {
        this.hardness = hardness;
        this.resistance = resistance;
        this.blockSupplier = blockSupplier;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    public Block getBlock() {
        return blockSupplier.get();
    }
}