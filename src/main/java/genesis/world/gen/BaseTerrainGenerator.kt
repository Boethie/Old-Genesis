package genesis.world.gen

import genesis.util.Blockstates
import genesis.util.Coords
import genesis.util.Coords.*
import genesis.util.noise.*
import genesis.util.noise.gen.SuperSimplexNoise
import net.minecraft.init.Biomes
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.chunk.ChunkPrimer

import java.util.Random

class BaseTerrainGenerator(private val world: World, private val biomeProvider: BiomeProvider, private val seed: Long) {

    private val noiseSource: BufferedNoiseProvider3D

    private val noiseBuffer: NoiseBuffer3D

    private val biomes = Array<Biome>(CHUNK_SIZE * CHUNK_SIZE, { Biomes.OCEAN })

    init {

        val rand = Random(seed)

        val low = UnbufferedNoiseProvider3D(DoubleTernaryOperator(SuperSimplexNoise(rand.nextLong())::eval),
                8, 0.00522 * INTERP_SIZE_X, 0.0, 0.00522 * INTERP_SIZE_Z).toBuffered()
                .buffered(NOISE_SIZE_X, NOISE_SIZE_Y, NOISE_SIZE_Z)

        val high = UnbufferedNoiseProvider3D(DoubleTernaryOperator(SuperSimplexNoise(rand.nextLong())::eval),
                8, 0.00522 * INTERP_SIZE_X, 0.0, 0.00522 * INTERP_SIZE_Z).toBuffered()
                .buffered(NOISE_SIZE_X, NOISE_SIZE_Y, NOISE_SIZE_Z)

        val selector = UnbufferedNoiseProvider3D(DoubleTernaryOperator(SuperSimplexNoise(rand.nextLong())::eval),
                4, 0.0167 * INTERP_SIZE_X, 0.0, 0.0167 * INTERP_SIZE_Z).toBuffered()
                .clamp(0.0, 1.0)
                .buffered(NOISE_SIZE_X, NOISE_SIZE_Y, NOISE_SIZE_Z)

        val biomeSrc = BufferedBiomeDataProvider(biomeProvider, NOISE_SIZE_X, NOISE_SIZE_Z)
        val biomeHeight = biomeSrc.biomeHeights()
        val heightVariationFactor = biomeHeight apply { _, y, _, v -> if (y * INTERP_SIZE_Y < v * HEIGHT_VAR + SEA_LEVEL) 0.25 else 1.0 }
        val biomeHeightVariation = biomeSrc.biomeHeightVariations()

        val noise = (selector * (high - low) + low).buffered(NOISE_SIZE_X, NOISE_SIZE_Y, NOISE_SIZE_Z) *
                biomeHeightVariation * heightVariationFactor + biomeHeight

        this.noiseSource = (noise * HEIGHT_VAR + SEA_LEVEL).buffered(NOISE_SIZE_X, NOISE_SIZE_Y, NOISE_SIZE_Z)
        this.noiseBuffer = NoiseBuffer3D(NOISE_SIZE_X, NOISE_SIZE_Y, NOISE_SIZE_Z)
    }

    fun generate(primer: ChunkPrimer, chunkX: Int, chunkZ: Int) {
        val density = calculateDensity(chunkX * SECTIONS_X, 0, chunkZ * SECTIONS_Z)
        val startX = chunkToMinBlock(chunkX)
        val startZ = chunkToMinBlock(chunkZ)
        interpolate(density, startX, 0, startZ, DensityConsumer({ v, x, y, z ->
            if (v - y > 0) {
                primer.setBlockState(x - startX, y, z - startZ, Blockstates.STONE)
            } else if (y < SEA_LEVEL) {
                primer.setBlockState(x - startX, y, z - startZ, Blocks.WATER.defaultState)
            }
        }))
        applyBiomeSurface(primer, chunkX, chunkZ)
    }

    private fun applyBiomeSurface(primer: ChunkPrimer, chunkX: Int, chunkZ: Int) {
        val biomes = biomeProvider.getBiomes(biomes, chunkToMinBlock(chunkX), chunkToMinBlock(chunkZ), CHUNK_SIZE, CHUNK_SIZE)

        val rand = Random(chunkX.toLong() or (chunkZ.toLong() shr 32))

        for (localX in 0 until CHUNK_SIZE) {
            for (localZ in 0 until CHUNK_SIZE) {
                biomes[packLocalXZ(localX, localZ)]
                        .genTerrainBlocks(world, rand, primer, localToBlock(localX, chunkX), localToBlock(localZ, chunkZ), 0.0)
            }
        }
    }

    private fun calculateDensity(startX: Int, startY: Int, startZ: Int): NoiseBuffer3D {
        noiseSource.fill(noiseBuffer, startX, startY, startZ)
        return noiseBuffer
    }

    private fun interpolate(density: NoiseBuffer3D, offsetX: Int, offsetY: Int, offsetZ: Int, consumer: DensityConsumer) {
        val xScale = INTERP_SIZE_X
        val yScale = INTERP_SIZE_Y
        val zScale = INTERP_SIZE_Z

        val stepX = 1.0 / xScale
        val stepY = 1.0 / yScale
        val stepZ = 1.0 / zScale

        for (sectionX in 0 until SECTIONS_X) {
            val x = sectionX * xScale + offsetX
            for (sectionZ in 0 until SECTIONS_Z) {
                val z = sectionZ * zScale + offsetZ
                for (sectionY in 0 until SECTIONS_Y) {
                    val y = sectionY * yScale + offsetY

                    val v000 = density.get(sectionX + 0, sectionY + 0, sectionZ + 0)
                    val v001 = density.get(sectionX + 0, sectionY + 0, sectionZ + 1)
                    val v010 = density.get(sectionX + 0, sectionY + 1, sectionZ + 0)
                    val v011 = density.get(sectionX + 0, sectionY + 1, sectionZ + 1)
                    val v100 = density.get(sectionX + 1, sectionY + 0, sectionZ + 0)
                    val v101 = density.get(sectionX + 1, sectionY + 0, sectionZ + 1)
                    val v110 = density.get(sectionX + 1, sectionY + 1, sectionZ + 0)
                    val v111 = density.get(sectionX + 1, sectionY + 1, sectionZ + 1)

                    var v0y0 = v000
                    var v0y1 = v001
                    var v1y0 = v100
                    var v1y1 = v101
                    val d_dy__0y0 = (v010 - v000) * stepY
                    val d_dy__0y1 = (v011 - v001) * stepY
                    val d_dy__1y0 = (v110 - v100) * stepY
                    val d_dy__1y1 = (v111 - v101) * stepY

                    for (yRel in 0 until yScale) {
                        var vxy0 = v0y0
                        var vxy1 = v0y1
                        val d_dx__xy0 = (v1y0 - v0y0) * stepX
                        val d_dx__xy1 = (v1y1 - v0y1) * stepX

                        for (xRel in 0 until xScale) {
                            val d_dz__xyz = (vxy1 - vxy0) * stepZ
                            var vxyz = vxy0

                            for (zRel in 0 until zScale) {
                                // to get gradients working, consumer usage moved to later
                                consumer.consume(vxyz, x + xRel, y + yRel, z + zRel)
                                vxyz += d_dz__xyz
                            }

                            vxy0 += d_dx__xy0
                            vxy1 += d_dx__xy1
                        }
                        v0y0 += d_dy__0y0
                        v0y1 += d_dy__0y1
                        v1y0 += d_dy__1y0
                        v1y1 += d_dy__1y1
                    }
                }
            }
        }
    }


    companion object {

        private val CHUNK_SIZE_X = Coords.CHUNK_SIZE
        private val CHUNK_SIZE_Y = 256
        private val CHUNK_SIZE_Z = Coords.CHUNK_SIZE

        private val INTERP_SIZE_X = 4
        private val INTERP_SIZE_Y = 8
        private val INTERP_SIZE_Z = 4

        private val SECTIONS_X = CHUNK_SIZE_X / INTERP_SIZE_X
        private val SECTIONS_Y = CHUNK_SIZE_Y / INTERP_SIZE_Y
        private val SECTIONS_Z = CHUNK_SIZE_Z / INTERP_SIZE_Z

        private val NOISE_SIZE_X = SECTIONS_X + 1
        private val NOISE_SIZE_Y = SECTIONS_Y + 1
        private val NOISE_SIZE_Z = SECTIONS_Z + 1

        private val SEA_LEVEL = 64.0
        private val HEIGHT_VAR = 64.0
    }
}
