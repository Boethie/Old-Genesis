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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class CropInfo {
    public static final ArrayList<CropInfo> CROPS = new ArrayList<>();

    public static CropInfo ZINGIBEROPSIS = new CropInfo.Builder("zingiberopsis")
            .setDoubleCrop(true)
            .setCrop(() -> GenesisItems.ZINGIBEROPSIS_RHIZOME)
            .setSeed(() -> GenesisItems.ZINGIBEROPSIS_RHIZOME)
            .setPlant(() -> GenesisBlocks.ZINGIBEROPSIS != null ? GenesisBlocks.ZINGIBEROPSIS.getDefaultState() : Blocks.AIR.getDefaultState())
            .setWidth(0.75)
            .setGrowthMultiplier(0.625f)
            .setSeedDropMultiplier(0.5f)
            .setHeightFunc(i -> {
                switch (i) {
                    case 0: return 0.125;
                    case 1: return 0.25;
                    case 2: return 0.375;
                    case 3: return 0.625;
                    case 4: return 0.8125;
                    case 5: return 1.3125;
                    case 6: return 1.5625;
                    case 7: return 1.75;
                    default: return 1.0;
                }
            })
            .build();

    private final String name;

    private int growthStages;
    private int growthAge;
    private float growthMultiplier;
    private float seedDropMultiplier;

    private boolean isDoubleCrop;
    private boolean breakTogether;

    private Supplier<Item> crop;
    private Supplier<Item> seed;
    private Supplier<IBlockState> plant;

    private double width;
    private Function<Integer, Double> heightFunc;

    private CropInfo(String name) {
        this.name = name;
        CROPS.add(this);
    }

    public String getName() {
        return name;
    }

    public Item getCrop() {
        return crop.get();
    }

    public Item getSeed() {
        return seed.get();
    }

    public IBlockState getPlant() {
        return plant.get();
    }

    public int getGrowthStages() {
        return growthStages;
    }

    public int getGrowthAge() {
        return growthAge;
    }

    public float getGrowthMultiplier() {
        return growthMultiplier;
    }

    public float getSeedDropMultiplier() {
        return seedDropMultiplier;
    }

    public boolean isDoubleCrop() {
        return isDoubleCrop;
    }

    public boolean getBreakTogether() {
        return breakTogether;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight(int i) {
        return heightFunc.apply(i);
    }

    private static class Builder {
        private final String name;

        private int growthStages = 7;
        private int growthAge = 5;
        private float growthMultiplier = 1.0f;
        private float seedDropMultiplier = 1.0f;

        private boolean isDoubleCrop = false;
        private boolean breakTogether = true;

        private Supplier<Item> crop = () -> Items.WHEAT_SEEDS;
        private Supplier<Item> seed = () -> Items.WHEAT;
        private Supplier<IBlockState> plant = Blocks.WHEAT::getDefaultState;

        private double width = 1.0f;
        private Function<Integer, Double> heightFunc = i -> i * 0.125;

        private Builder(String name) {
            this.name = name;
        }

        private CropInfo build() {
            CropInfo out = new CropInfo(name);
            out.growthStages = this.growthStages;
            out.growthAge = this.growthAge;
            out.growthMultiplier = this.growthMultiplier;
            out.seedDropMultiplier = this.seedDropMultiplier;
            out.isDoubleCrop = this.isDoubleCrop;
            out.breakTogether = this.breakTogether;
            out.crop = this.crop;
            out.seed = this.seed;
            out.plant = this.plant;
            out.width = this.width;
            out.heightFunc = this.heightFunc;
            return out;
        }

        private Builder setGrowthStages(int growthStages) {
            this.growthStages = growthStages;
            return this;
        }

        private Builder setGrowthAge(int growthAge) {
            this.growthAge = growthAge;
            return this;
        }

        private Builder setGrowthMultiplier(float growthMultiplier) {
            this.growthMultiplier = growthMultiplier;
            return this;
        }

        public Builder setSeedDropMultiplier(float seedDropMultiplier) {
            this.seedDropMultiplier = seedDropMultiplier;
            return this;
        }

        private Builder setDoubleCrop(boolean doubleCrop) {
            isDoubleCrop = doubleCrop;
            return this;
        }

        private Builder setBreakTogether(boolean breakTogether) {
            this.breakTogether = breakTogether;
            return this;
        }

        private Builder setCrop(Supplier<Item> crop) {
            this.crop = crop;
            return this;
        }

        private Builder setSeed(Supplier<Item> seed) {
            this.seed = seed;
            return this;
        }

        private Builder setPlant(Supplier<IBlockState> plant) {
            this.plant = plant;
            return this;
        }

        private Builder setWidth(double width) {
            this.width = width;
            return this;
        }

        private Builder setHeightFunc(Function<Integer, Double> heightFunc) {
            this.heightFunc = heightFunc;
            return this;
        }
    }
}
