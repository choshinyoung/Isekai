package org.choshinyoung.isekai;

import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.json.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

public class IsekaiChunkGenerator extends ChunkGenerator {
    public static final int DATA_SIZE = 3601;
    public static final double SCALE = 30.8633712858;

    public static final int DEFAULT_SEA_LEVEL = 59;
    public static final int MAX_SEA_LEVEL = DEFAULT_SEA_LEVEL + 3;

    public static Map<Pair<Integer, Integer>, int[][]> Elevation = new HashMap<>();

    private final Plugin plugin;

    public IsekaiChunkGenerator(Plugin _plugin) {
        super();

        plugin = _plugin;

        readData();
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }

    @Override
    public int getBaseHeight(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull HeightMap heightMap) {
        return Math.min(getHeight(x, Math.abs(z)) + DEFAULT_SEA_LEVEL, 1900);
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = Math.abs(chunkZ * 16 + z);

                int height = Math.min(getHeight(worldX, worldZ) + DEFAULT_SEA_LEVEL, 1900);

                for (int y = worldInfo.getMinHeight(); y <= height; y++) {
                    chunkData.setBlock(x, y, z, Material.STONE);
                }

                if (height <= MAX_SEA_LEVEL) {
                    for (int y = height; y <= MAX_SEA_LEVEL; y++) {
                        chunkData.setBlock(x, y, z, Material.WATER);
                    }
                }
            }
        }
    }

    private void readData() {
        try {
            String[] paths = new String(ByteStreams.toByteArray(Objects.requireNonNull(plugin.getResource("DEM/list.txt"))), StandardCharsets.UTF_8).split("\n");

            for (String path : paths) {
                String realPath = "DEM/" + path.trim();

                Pair<IsekaiCoordinate, int[][]> pair = readSingleData(realPath);

                IsekaiCoordinate coordinate = pair.getLeft();
                int[][] elevation = pair.getRight();

                Elevation.put(new ImmutablePair<>((int)coordinate.latitude, (int)coordinate.longitude), elevation);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, e.toString());

            throw new RuntimeException(e);
        }
    }

    private Pair<IsekaiCoordinate, int[][]> readSingleData(String path) throws IOException {
        String content = new String(ByteStreams.toByteArray(Objects.requireNonNull(plugin.getResource(path))), StandardCharsets.UTF_8);

        JSONObject json = new JSONObject(content);

        JSONArray latitudeArr = json.getJSONArray("elevation");

        int[][] result = new int[latitudeArr.length()][];

        for (int i = 0; i < latitudeArr.length(); i++) {
            JSONArray longitudeArr = latitudeArr.getJSONArray(i);

            result[i] = new int[longitudeArr.length()];

            for (int j = 0; j < longitudeArr.length(); j++) {
                result[i][j] = longitudeArr.getInt(j);
            }
        }

        return new ImmutablePair<>(getCoordinateFromData(json), result);
    }

    private IsekaiCoordinate getCoordinateFromData(JSONObject json) {
        String latitude = json.getString("latitude");
        String longitude = json.getString("longitude");

        int direction = 0;

        if (latitude.startsWith("N")) {
            direction |= IsekaiCoordinate.DIRECTION_NORTH;
        }
        else if (latitude.startsWith("S")) {
            direction |= IsekaiCoordinate.DIRECTION_SOUTH;
        }

        if (longitude.startsWith("E")) {
            direction |= IsekaiCoordinate.DIRECTION_EAST;
        }
        else if (longitude.startsWith("W")) {
            direction |= IsekaiCoordinate.DIRECTION_WEST;
        }

        return new IsekaiCoordinate(direction, Integer.parseInt(latitude.substring(1)), Integer.parseInt(longitude.substring(1)));
    }

    public static IsekaiCoordinate positionToIsekaiCoordinate(double x, double z) {
        // TODO: implement for south latitude and west longitude

        return new IsekaiCoordinate(IsekaiCoordinate.DIRECTION_NORTH & IsekaiCoordinate.DIRECTION_EAST, z / (DATA_SIZE * SCALE), x / (DATA_SIZE * SCALE));
    }

    public static int getHeight(int x, int z) {
        IsekaiCoordinate coordinate = positionToIsekaiCoordinate(x, z);

        int xFloor = (int)Math.round(((x - (x % SCALE)) / SCALE) % DATA_SIZE);
        int zFloor = (int)Math.round((z - (z % SCALE)) / SCALE) % DATA_SIZE;

        int xCeil = xFloor + 1;
        int zCeil = zFloor + 1;

        double xRelative = (x % SCALE) / SCALE;
        double zRelative = (z % SCALE) / SCALE;

        // TODO: implement for border of blocks

        int a = getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xFloor, zFloor);
        int b = getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xCeil, zFloor);
        int c = getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xFloor, zCeil);
        int d = getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xCeil, zCeil);

        double left = a * (1 - xRelative) + b * xRelative;
        double right = c * (1 - xRelative) + d * xRelative;

//        if (coordinate.latitude <= 1 && coordinate.longitude <= 1 && coordinate.latitude > 0 && coordinate.longitude > 0) {
//            plugin.getLogger().info(coordinate.longitude + ", " + coordinate.latitude + " : " + xRelative + " " + zRelative + " - " + (int)((left * (1 - zRelative)) + right * zRelative));
//        }

        return (int)Math.round((left * (1 - zRelative)) + right * zRelative);
    }

    public static int getRawHeight(int latitude, int longitude, int dataX, int dataZ) {
        if (dataX < 0) {
            longitude--;
            dataX = DATA_SIZE - dataX;
        }
        else if (dataX >= DATA_SIZE) {
            longitude++;
            dataX -= DATA_SIZE;
        }
        if (dataZ < 0) {
            latitude--;
            dataZ = DATA_SIZE - dataZ;
        }
        else if (dataZ >= DATA_SIZE) {
            latitude++;
            dataZ -= DATA_SIZE;
        }

        Pair<Integer, Integer> key = FindElevationKey(latitude, longitude);

        if (key == null) {
            return 0;
        }

        return Elevation.get(key)[dataZ][dataX];
    }

    public static Pair<Integer, Integer> FindElevationKey(int latitude, int longitude) {
        return Elevation.keySet().stream().filter(k -> k.getLeft() == Math.floor(latitude) && k.getRight() == Math.floor(longitude)).findFirst().orElse(null);
    }
}
