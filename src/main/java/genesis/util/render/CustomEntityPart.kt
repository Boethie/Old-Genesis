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

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper

abstract class CustomEntityPart(model: ModelBase) : EntityPart(model) {
    init {
        resetState()
    }

    override fun setDefaultState(): CustomEntityPart {
        super.setDefaultState()

        return this
    }

    override fun setTextureOffset(x: Int, y: Int): EntityPart {
        throw UnsupportedOperationException()
    }

    override fun addBox(offset: String?, x: Float, y: Float, z: Float, w: Int, h: Int, d: Int): ModelRenderer {
        throw UnsupportedOperationException()
    }

    override fun addBox(x: Float, y: Float, z: Float, w: Int, h: Int, d: Int): ModelRenderer {
        throw UnsupportedOperationException()
    }

    override fun addBox(x: Float, y: Float, z: Float, w: Int, h: Int, d: Int, mirror: Boolean): ModelRenderer {
        throw UnsupportedOperationException()
    }

    override fun addBox(x: Float, y: Float, z: Float, w: Int, h: Int, d: Int, inset: Float) {
        throw UnsupportedOperationException()
    }

    abstract fun doRender(pxSize: Float)

    fun doChildModelRender(pxSize: Float) {
        if (childModels != null) {
            for (childModel in childModels) {
                childModel.render(pxSize)
            }
        }
    }

    override fun render(pxSize: Float) {
        if (!isHidden && showModel) {
            GlStateManager.pushMatrix()

            GlStateManager.translate(offsetX, offsetY, offsetZ)

            GlStateManager.translate(rotationPointX, rotationPointY, rotationPointZ)

            GlStateManager.rotate(rotateAngleY, 0f, 1f, 0f)
            GlStateManager.rotate(rotateAngleX, 1f, 0f, 0f)
            GlStateManager.rotate(rotateAngleZ, 0f, 0f, 1f)

            GlStateManager.translate(-rotationPointX, -rotationPointY, -rotationPointZ)

            GlStateManager.scale(scaleX, scaleY, scaleZ)

            doChildModelRender(pxSize)
            doRender(pxSize)

            GlStateManager.popMatrix()
            RenderHelper.enableStandardItemLighting()
        }
    }

    override fun renderWithRotation(pxSize: Float) {
        render(pxSize)
    }
}
