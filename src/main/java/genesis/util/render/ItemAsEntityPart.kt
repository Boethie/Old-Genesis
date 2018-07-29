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

package genesis.util.render

import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.item.ItemStack

class ItemAsEntityPart(model: ModelBase) : CustomEntityPart(model) {
    var render: ItemStack = ItemStack.EMPTY
    private var default: ItemStack = ItemStack.EMPTY

    init {
        offsetX += 0.5f
        offsetZ += 0.5f
        setDefaultState()
    }

    override fun setDefaultState(): ItemAsEntityPart {
        super.setDefaultState()

        default = render

        return this
    }

    override fun resetState() {
        super.resetState()

        render = default
    }

    override fun doRender(pxSize: Float) {
        val scale = pxSize * 16
        GlStateManager.scale(scale, scale, scale)
        RenderHelper.enableStandardItemLighting()
        Minecraft.getMinecraft().renderItem.renderItem(render, TransformType.FIXED)
    }
}
