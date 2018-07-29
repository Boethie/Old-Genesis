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
package genesis.world.gen.biome.layer;

import genesis.util.Coords;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

/**
 * Generates islands of values only from possibleDataValues. 2 different values will never touch (even by corners).
 */
public class InitialIslandGenerator extends GenLayer {

    private final int[] possibleDataValues;

    public InitialIslandGenerator(long seed, int[] possibleDataValues) {
        super(seed);
        this.possibleDataValues = possibleDataValues;
    }

    @Override public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        //int[] base = getBaseData(areaX - 2, areaY - 2, areaWidth + 4, areaHeight + 4);
        return getBaseData(areaX, areaY, areaWidth, areaHeight);//fillGaps(base, areaX, areaY, areaWidth, areaHeight);
    }

    private int[] fillGaps(int[] base, int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] filled = IntCache.getIntCache(areaWidth * areaHeight);
        final int r = 1;
        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int posX = areaX + x;
                int posY = areaY + y;
                this.initChunkSeed(posX, posY);

                boolean foundBiome = false;
                for (int dy = -r; dy <= r; ++dy) {
                    for (int dx = -r; dx <= r; ++dx) {
                        if (base[y + dy + r + (x + dx + r) * (areaWidth + 2 * r)] != 0) {
                            foundBiome = true;
                            break;
                        }
                    }
                }
                if (!foundBiome) {
                    filled[x + y * areaWidth] = getBiomeFor(posX, posY);
                }
            }
        }
        return filled;
    }

    private int[] getBaseData(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = IntCache.getIntCache(areaWidth * areaHeight);

        for (int dy = 0; dy < areaHeight; ++dy) {
            for (int dx = 0; dx < areaWidth; ++dx) {
                int x = areaX + dx;
                int y = areaY + dy;
                this.initChunkSeed(x, y);

                aint[dx + dy * areaWidth] = getValue(x, y);
            }
        }
        return aint;
    }

    // TODO: cache stuff?
    private int getValue(int x, int y) {
        if (!isLand(x, y)) {
            return 0;
        }
        // find the corner of this landmass that is first, as far negative on X axis as possible and then as far negative on Y axis as possible
        // this point is the same for all points in the landmass, and is unique for each landmass.
        // And so it can be used as "chunk seed" coordinates to specify which value to generate
        int currBestX = x;
        int currBestY = y;
        LongSet visitedCoords = new LongLinkedOpenHashSet();
        LongArrayFIFOQueue queuedCoords = new LongArrayFIFOQueue();
        queuedCoords.enqueue(Coords.pack(x, y));
        while (!queuedCoords.isEmpty()) {
            long pos = queuedCoords.dequeueLong();
            visitedCoords.add(pos);
            int posX = Coords.unpackX(pos);
            int posY = Coords.unpackY(pos);
            // check if this coord pair is better
            if (posX < currBestX) {
                // if this X is better, override both
                currBestX = posX;
                currBestY = posY;
            } else if (posY < currBestY && posX == currBestX) {
                currBestY = posY;
            }

            // scan next coords
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    long packed = Coords.pack(posX + dx, posY + dy);
                    if (!visitedCoords.contains(packed) && isLand(posX + dx, posY + dy)) {
                        queuedCoords.enqueue(packed);
                    }
                }
            }
        }
        return getBiomeFor(currBestX, currBestY);
    }

    private int getBiomeFor(int x, int y) {
        this.initChunkSeed(x, y);
        return possibleDataValues[nextInt(possibleDataValues.length)];
    }

    private boolean isLand(int x, int y) {
        this.initChunkSeed(x, y);
        return nextInt(5) == 0;
    }
}
