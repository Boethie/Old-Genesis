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

package genesis.util.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class EntityPart extends ModelRenderer {
    public float scaleX = 1;
    public float scaleY = 1;
    public float scaleZ = 1;

    private boolean showModelDef = true;
    private boolean isHiddenDef = false;
    private float offsetXDef = 0;
    private float offsetYDef = 0;
    private float offsetZDef = 0;
    private float rotationPointXDef = 0;
    private float rotationPointYDef = 0;
    private float rotationPointZDef = 0;
    private float rotateAngleXDef = 0;
    private float rotateAngleYDef = 0;
    private float rotateAngleZDef = 0;
    private float scaleXDef = 1;
    private float scaleYDef = 1;
    private float scaleZDef = 1;

    private int textureOffsetX = 0;
    private int textureOffsetY = 0;

    public EntityPart(ModelBase model) {
        super(model);

        resetState();
    }

    public EntityPart(ModelBase model, int offsetX, int offsetY) {
        this(model);

        setTextureOffset(offsetX, offsetY);
    }

    public EntityPart setDefaultState() {
        showModelDef = showModel;
        isHiddenDef = isHidden;
        offsetXDef = offsetX;
        offsetYDef = offsetY;
        offsetZDef = offsetZ;
        rotationPointXDef = rotationPointX;
        rotationPointYDef = rotationPointY;
        rotationPointZDef = rotationPointZ;
        rotateAngleXDef = rotateAngleX;
        rotateAngleYDef = rotateAngleY;
        rotateAngleZDef = rotateAngleZ;
        scaleXDef = scaleX;
        scaleYDef = scaleY;
        scaleZDef = scaleZ;

        return this;
    }

    public EntityPart setDefaultState(boolean children) {
        setDefaultState();

        if (children && childModels != null) {
            childModels
                    .stream()
                    .filter(child -> child instanceof EntityPart)
                    .forEach(child -> ((EntityPart) child).setDefaultState(true));
        }

        return this;
    }

    public void resetState() {
        showModel = showModelDef;
        isHidden = isHiddenDef;
        offsetX = offsetXDef;
        offsetY = offsetYDef;
        offsetZ = offsetZDef;
        rotationPointX = rotationPointXDef;
        rotationPointY = rotationPointYDef;
        rotationPointZ = rotationPointZDef;
        rotateAngleX = rotateAngleXDef;
        rotateAngleY = rotateAngleYDef;
        rotateAngleZ = rotateAngleZDef;
        scaleX = scaleXDef;
        scaleY = scaleYDef;
        scaleZ = scaleZDef;
    }

    public EntityPart resetState(boolean children) {
        resetState();

        if (children && childModels != null) {
            childModels
                    .stream()
                    .filter(child -> child instanceof EntityPart)
                    .forEach(child -> ((EntityPart) child).resetState(true));
        }

        return this;
    }

    public EntityPart setOffset(float x, float y, float z) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;

        return this;
    }

    public EntityPart setRotation(float x, float y, float z) {
        rotateAngleX = x;
        rotateAngleY = y;
        rotateAngleZ = z;

        return this;
    }

    public EntityPart setScale(float x, float y, float z) {
        scaleX = x;
        scaleY = y;
        scaleZ = z;

        return this;
    }

    @Override
    public void render(float scale) {
        GlStateManager.scale(scaleX, scaleY, scaleZ);
        super.render(scale);
        GlStateManager.scale(1 / scaleX, 1 / scaleY, 1 / scaleZ);
    }

    @Override
    public EntityPart setTextureOffset(int u, int v) {
        super.setTextureOffset(u, v);

        textureOffsetX = u;
        textureOffsetY = v;

        return this;
    }

    public void addElement(CustomModelElement element) {
        cubeList.add(element);
    }

    public int getTextureOffsetX() {
        return textureOffsetX;
    }

    public int getTextureOffsetY() {
        return textureOffsetY;
    }
}
