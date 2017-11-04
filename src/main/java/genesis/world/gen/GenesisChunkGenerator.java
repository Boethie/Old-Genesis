/*
 * This file is part of ${name}, licensed under the MIT License (MIT).
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

import static genesis.util.Coords.CHUNK_SIZE;

import genesis.util.Blockstates;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class GenesisChunkGenerator implements IChunkGenerator {

    private World world;

    public GenesisChunkGenerator(World world) {
        this.world = world;
    }

    @Override public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();
        generateBlocks(primer, chunkX, chunkZ);
        Chunk chunk = new Chunk(this.world, primer, chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    private void generateBlocks(ChunkPrimer primer, int chunkX, int chunkZ) {
        // TODO: replace with some actual non-superflat terrain generation
        for (int localX = 0; localX < CHUNK_SIZE; localX++) {
            for (int localZ = 0; localZ < CHUNK_SIZE; localZ++) {
                primer.setBlockState(localX, 0, localZ, Blockstates.BEDROCK);
                for (int blockY = 1; blockY < 64; blockY++) {
                    IBlockState state = Blockstates.STONE;
                    if (blockY == 63) {
                        state = Blockstates.GRASS;
                    } else if (blockY >= 60) {
                        state = Blockstates.DIRT;
                    }
                    primer.setBlockState(localX, blockY, localZ, state);
                }
            }
        }


    }

    @Override public void populate(int x, int z) {

    }

    @Override public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return true;
    }

    @Override public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList();
    }

    @Nullable @Override public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
