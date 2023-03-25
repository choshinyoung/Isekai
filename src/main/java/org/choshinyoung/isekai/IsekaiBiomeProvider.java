package org.choshinyoung.isekai;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IsekaiBiomeProvider extends BiomeProvider {
    private static final ArrayList<Biome> biomes = new ArrayList<Biome>(){{
        add(Biome.PLAINS);
        add(Biome.RIVER);
    }};

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        int height = IsekaiChunkGenerator.getHeight(x, z);

        if (height - y < 10 && height < IsekaiChunkGenerator.MAX_SEA_LEVEL) {
            return Biome.RIVER;
        }

        return Biome.PLAINS;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return biomes;
    }
}
