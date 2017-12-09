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
package genesis.init;

import genesis.GenesisMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class GenesisCreativeTabs {

    public static final CreativeTabs BUILDING_BLOCKS = createTab("buildingBlocks", () -> ItemStack.EMPTY);
    public static final CreativeTabs DECORATIONS = createTab("decorations", () -> ItemStack.EMPTY);
    public static final CreativeTabs MISC = createTab("misc", () -> ItemStack.EMPTY);
    public static final CreativeTabs MATERIALS = MISC;

    private static CreativeTabs createTab(final String label, final Supplier<ItemStack> iconItemSupplier) {
        return new CreativeTabs(GenesisMod.MOD_ID + "." + label) {
            @SideOnly(Side.CLIENT)
            @Override
            public ItemStack getTabIconItem() {
                return iconItemSupplier.get();
            }
        };
    }
}
