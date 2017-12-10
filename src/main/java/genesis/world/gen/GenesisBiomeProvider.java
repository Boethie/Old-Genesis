package genesis.world.gen;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.storage.WorldInfo;

public class GenesisBiomeProvider extends BiomeProvider {

    private GenesisBiomeProvider(long seed, WorldType worldTypeIn, String options) {
        super();

        GenLayer[] agenlayer = this.createGenLayers(seed, worldTypeIn);
        this.genBiomes = agenlayer[0];
        this.biomeIndexLayer = agenlayer[1];
    }

    private GenLayer[] createGenLayers(long seed, WorldType worldTypeIn) {

        GenLayer island = new GenLayerIsland(1L);

        GenLayer finalGenScale = island;
        for (int i = 0; i < 7; i++) {
            finalGenScale = new GenLayerVoronoiZoom(i + 1000L, finalGenScale);
        }
        GenLayer blockScale = new GenLayerVoronoiZoom(10L, finalGenScale);

        finalGenScale.initWorldGenSeed(seed);
        blockScale.initWorldGenSeed(seed);

        return new GenLayer[]{island, blockScale};
    }

    public GenesisBiomeProvider(WorldInfo info) {
        this(info.getSeed(), info.getTerrainType(), info.getGeneratorOptions());
    }
}
