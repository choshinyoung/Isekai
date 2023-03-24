package org.choshinyoung.isekai;

import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public final class Isekai extends JavaPlugin {

    private final Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("trace").setExecutor(new TraceElevation());

        logger.info("Welcome to Isekai!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return new IsekaiChunkGenerator(this);
    }
}
