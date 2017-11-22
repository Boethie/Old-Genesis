package genesis.util.noise

import net.minecraft.init.Biomes
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeProvider

/**
 * Generates biome height and height variation as "noise"
 */
class BufferedBiomeDataProvider(private val provider: BiomeProvider, private val sizeX: Int, private val sizeZ: Int) {

    // TODO: make smooth radius a named constant
    private val biomes: Array<Biome> = Array((sizeX + 4) * (sizeZ + 4), { Biomes.OCEAN })
    private val biomeHeights: NoiseBuffer2D = NoiseBuffer2D(sizeX, sizeZ)
    private val biomeHeightVariations: NoiseBuffer2D = NoiseBuffer2D(sizeX, sizeZ)

    /**
     * Weights used for neighbor biomes for biome smoothing
     */
    private val biomeWeights: DoubleArray = DoubleArray(5 * 5, { 0.0 })

    private var currentStartX: Int = Int.MIN_VALUE
    private var currentStartZ: Int = Int.MIN_VALUE

    init {
        for (i in -2..2) {
            for (j in -2..2) {
                val f = 10.0 / Math.sqrt(i * i + j * j + 0.2)
                this.biomeWeights[i + 2 + (j + 2) * 5] = f
            }
        }
    }

    /**
     * Generates the biomes and their heights and height variation. Automatically applies biome smoothing.
     */
    private fun fillPrepare(startX: Int, startZ: Int) {

        if (currentStartX == startX && currentStartZ == startZ) {
            return
        }
        currentStartX = startX
        currentStartZ = startZ

        val countX = sizeX + 4
        val countZ = sizeX + 4
        val biomes = provider.getBiomesForGeneration(biomes, startX, startZ, countX, countZ)
        // vanilla biome smoothing
        for (x in 0 until sizeX) {
            for (z in 0 until sizeZ) {
                val centerBiome = biomes[(z + 2) + (x + 2) * countZ]

                var height = 0.0
                var heightVariation = 0.0
                var weightSum = 0.0

                for (dx in 0..4) {
                    for (dz in 0..4) {
                        var weight = this.biomeWeights[dx + dz * 5]
                        val biome = biomes[z + dz + (x + dx) * countZ]
                        if (biome.baseHeight > centerBiome.baseHeight) {
                            weight *= 0.5
                        }
                        height += biome.baseHeight * weight
                        heightVariation += biome.heightVariation * weight
                        weightSum += weight
                    }
                }
                height /= weightSum
                heightVariation /= weightSum

                biomeHeights[x, z] = biomeHeightVanilla(height)
                biomeHeightVariations[x, z] = biomeHeightVariationVanilla(heightVariation)
            }
        }
    }

    fun biomeHeights(): BufferedNoiseProvider3D = BiomeHeights(this)

    fun biomeHeightVariations(): BufferedNoiseProvider3D = BiomeHeightVariations(this)

    fun biomeHeightVariationVanilla(heightVariation: Double) = 2.4 * heightVariation + 4.0 / 15.0

    fun biomeHeightVanilla(height: Double) = height * 17.0 / 64.0 - 1.0 / 256.0

    class BiomeHeights(private val provider: BufferedBiomeDataProvider) : BufferedNoiseProvider3D() {
        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            provider.fillPrepare(startX, startZ)
        }

        override fun get(x: Int, y: Int, z: Int) = provider.biomeHeights[x, z]
    }

    class BiomeHeightVariations(private val provider: BufferedBiomeDataProvider) : BufferedNoiseProvider3D() {
        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            provider.fillPrepare(startX, startZ)
        }

        override fun get(x: Int, y: Int, z: Int) = provider.biomeHeightVariations[x, z]
    }

}