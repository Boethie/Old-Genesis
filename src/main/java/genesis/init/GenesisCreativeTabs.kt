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
package genesis.init

import genesis.GenesisMod
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object GenesisCreativeTabs {

    val BUILDING_BLOCKS = createTabWithBlockIcon("buildingBlocks", { GenesisBlocks.GRANITE })
    val DECORATIONS = createTabWithBlockIcon("decorations", { GenesisBlocks.ARAUCARIOXYLON_SAPLING })
    val MISC = createTabWithItemIcon("misc", { GenesisItems.RED_CLAY_BALL })
    val FOOD = createTabWithItemIcon("food", { GenesisItems.ZINGIBEROPSIS_RHIZOME })
    val MATERIALS = MISC

    private fun createTabWithBlockIcon(label: String, iconSupplier: () -> Block): CreativeTabs {
        return createTabWithStackIcon(label, { ItemStack(iconSupplier()) })
    }

    private fun createTabWithItemIcon(label: String, iconSupplier: () -> Item): CreativeTabs {
        return createTabWithStackIcon(label, { ItemStack(iconSupplier()) })
    }

    private fun createTabWithStackIcon(label: String, iconSupplier: () -> ItemStack): CreativeTabs {
        return object : CreativeTabs(GenesisMod.MOD_ID + "." + label) {
            @SideOnly(Side.CLIENT)
            override fun getTabIconItem(): ItemStack {
                return iconSupplier()
            }
        }
    }
}
