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

package genesis.compat.waila;

import genesis.block.BlockDoubleCrop;
import genesis.init.GenesisBlocks;
import genesis.init.GenesisItems;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerGenesis implements IWailaDataProvider {
    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() == GenesisBlocks.INSTANCE.getZINGIBEROPSIS()) {
            return new ItemStack(GenesisItems.INSTANCE.getZINGIBEROPSIS_RHIZOME());
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() instanceof BlockDoubleCrop)
            currenttip.set(0, TextFormatting.WHITE + I18n.format(accessor.getBlock().getUnlocalizedName() + ".name"));
        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (config.getConfig("general.showcrop")) {
            if (block instanceof BlockDoubleCrop)
                addMaturityTooltip(currenttip, (accessor.getMetadata()&0b111) / (float) ((BlockDoubleCrop) block).getMaxAge());
        }
        return currenttip;
    }

    public static void register(IWailaRegistrar registrar) {
        IWailaDataProvider provider = new HUDHandlerGenesis();
        registrar.registerStackProvider(provider, BlockDoubleCrop.class);
        registrar.registerHeadProvider(provider, BlockDoubleCrop.class);
        registrar.registerBodyProvider(provider, BlockDoubleCrop.class);
    }

    private static void addMaturityTooltip(List<String> currentTip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            currentTip.add(LangUtil.translateG("hud.msg.growth") + " : " + LangUtil.translateG("hud.msg.growth.value", (int) growthValue));
        else
            currentTip.add(LangUtil.translateG("hud.msg.growth") + " : " + LangUtil.translateG("hud.msg.mature"));
    }
}
