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
package genesis.biomegenlayer;

import static org.junit.Assert.assertEquals;

import genesis.world.gen.biome.layer.InitialIslandGenerator;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;
import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class TestInitialIslandGenerator {

    @Test
    public void testNeighbors() {
        int size = 512;

        GenLayer gen = new InitialIslandGenerator(1, new int[]{1, 2, 3, 4});

        int[] arr = gen.getInts(0, 0, size, size);

        for (int x = 1; x < size - 1; x++) {
            for (int y = 1; y < size - 1; y++) {
                int curr = arr[x + y * size];
                if (curr == 0) {
                    continue; // anything would be correct
                }
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int other = arr[x + dx + (y + dy) * size];
                        if (other == 0) {
                            continue;
                        }
                        assertEquals(curr, other);
                    }
                }
            }
        }
        IntCache.resetIntCache();
    }

    @Test
    public void testZoomedNeighbors() {
        int size = 1024;

        GenLayer gen = new GenLayerZoom(1, new InitialIslandGenerator(1, new int[]{1, 2, 3, 4}));

        int[] arr = gen.getInts(0, 0, size, size);

        for (int x = 1; x < size - 1; x++) {
            for (int y = 1; y < size - 1; y++) {
                int curr = arr[x + y * size];
                if (curr == 0) {
                    continue; // anything would be correct
                }
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int other = arr[x + dx + (y + dy) * size];
                        if (other == 0) {
                            continue;
                        }
                        assertEquals(curr, other);
                    }
                }
            }
        }
        IntCache.resetIntCache();
    }

    @Test
    public void testZoomed2Neighbors() throws IOException {
        int size = 4096;

        GenLayer island = new InitialIslandGenerator(1, new int[]{
                1, 2, 3, 4, 5, 6, 7
        });

        GenLayer finalGenScale = island;
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);


        GenLayer blockScale = new GenLayerZoom(10L, finalGenScale);
        blockScale = new GenLayerZoom(10L, blockScale);

        finalGenScale.initWorldGenSeed(42);
        blockScale.initWorldGenSeed(42);

        int[] arr = blockScale.getInts(0, 0, size, size);

        for (int x = 1; x < size - 1; x++) {
            for (int y = 1; y < size - 1; y++) {
                int curr = arr[x + y * size];
                if (curr == 0) {
                    continue; // anything would be correct
                }

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int other = arr[x + dx + (y + dy) * size];
                        if (other == 0) {
                            continue;
                        }
                        assertEquals(curr, other);
                    }
                }
            }
        }
        IntCache.resetIntCache();
    }

    @Test
    public void testAlwaysTheSame() throws IOException {
        int size = 512;

        GenLayer island = new InitialIslandGenerator(1, new int[]{
                1, 2, 3, 4, 5, 6, 7
        });

        GenLayer finalGenScale = island;
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);


        GenLayer blockScale = new GenLayerZoom(10L, finalGenScale);
        blockScale = new GenLayerZoom(10L, blockScale);

        finalGenScale.initWorldGenSeed(42);
        blockScale.initWorldGenSeed(42);

        int[] biomes = Arrays.copyOf(blockScale.getInts(0, 0, size, size), size * size);
        IntCache.resetIntCache();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int curr = biomes[x + y * size];
                int[] onePiece = blockScale.getInts(x, y, 1, 1);
                assertEquals(curr, onePiece[0]);
            }
            IntCache.resetIntCache();
        }
    }


    @Test
    public void export() throws IOException {
        int size = 2048;

        GenLayer island = new InitialIslandGenerator(1, new int[]{
                1, 2, 3, 4, 5, 6, 7
        });

        int centerX = -34180;
        int centerZ = -10049;

        GenLayer finalGenScale = island;
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);
        finalGenScale = new GenLayerZoom(2, finalGenScale);


        GenLayer blockScale = new GenLayerZoom(10L, finalGenScale);
        blockScale = new GenLayerZoom(10L, blockScale);

        finalGenScale.initWorldGenSeed(997549814875085411L);
        blockScale.initWorldGenSeed(997549814875085411L);


        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        int[] arr = finalGenScale.getInts(centerX - size /2, centerZ - size / 2, size, size);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int curr = arr[x + y * size];
                img.setRGB(x, y, curr == 0 ? 0x8080FF :
                        curr == 1 ? 0xFF0000 :
                                curr == 2 ? 0x00FF00 :
                                        curr == 3 ? 0x0000FF : 0xFFFFFF);
            }
        }
        ImageIO.write(img, "PNG", new File("/home/bartosz/IMAGE.png"));
        IntCache.resetIntCache();
    }
    //*/
}
