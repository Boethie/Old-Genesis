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
package genesis.world.gen;

import genesis.world.gen.biome.layer.InitialIslandGenerator;
import net.minecraft.init.Biomes;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GenesisBiomeProvider extends BiomeProvider {

    private GenesisBiomeProvider(long seed, WorldType worldTypeIn, String options) {
        super();

        GenLayer[] agenlayer = this.createGenLayers(seed, worldTypeIn);
        this.genBiomes = agenlayer[0];
        this.biomeIndexLayer = agenlayer[1];
    }

    private GenLayer[] createGenLayers(long seed, WorldType worldTypeIn) {

        GenLayer island = new InitialIslandGenerator(1, new int[]{
                // TODO: use genesis biomes instead
                Biome.REGISTRY.getIDForObject(Biomes.EXTREME_HILLS),
                Biome.REGISTRY.getIDForObject(Biomes.PLAINS),
                Biome.REGISTRY.getIDForObject(Biomes.DESERT),
                Biome.REGISTRY.getIDForObject(Biomes.FOREST),
                Biome.REGISTRY.getIDForObject(Biomes.MESA),
                Biome.REGISTRY.getIDForObject(Biomes.JUNGLE),
                Biome.REGISTRY.getIDForObject(Biomes.MUSHROOM_ISLAND)
        });

        GenLayer finalGenScale = island;
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        //finalGenScale = new GenLayerVoronoiZoom(2, finalGenScale);


        GenLayer blockScale = new GenLayerZoom(10L, finalGenScale);
        blockScale = new GenLayerZoom(10L, blockScale);

        finalGenScale.initWorldGenSeed(seed);
        blockScale.initWorldGenSeed(seed);

        return new GenLayer[]{finalGenScale, blockScale};
    }

    public GenesisBiomeProvider(WorldInfo info) {
        this(info.getSeed(), info.getTerrainType(), info.getGeneratorOptions());
    }
}
