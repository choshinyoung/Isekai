# Isekai

This Minecraft plugin generates a custom world based on a dataset extracted from SRTM elevation data.

The plugin is still a work in progress.

## Use Plugin

First, you need elevation data for your world.   
You can use [this code](https://gist.github.com/choshinyoung/f3cc51ee5f0b0e90d0e0d60fc0346a3e) to convert the GeoTIFF file to JSON data.    
You can place your converted JSON file on the [`src/main/resources/DEM/`](https://github.com/choshinyoung/Isekai/tree/master/src/main/resources/DEM) directory,
and list your files to [`list.txt`](https://github.com/choshinyoung/Isekai/blob/master/src/main/resources/DEM/list.txt).

After building the plugin that includes resources files,   
There are some settings that need to be set on the server.

- Set the world's height as its max value.
- Set world generator and biome provider by adding the following lines on `bukkit.yml`
   ```yml
   worlds:
   world:
   generator: Isekai
   biome-provider: Isekai
   ```
   
You can convert your Geographic Coordinate to Minecraft Position with the expression:
```
x = longitude * DATA_SIZE * SCALE = longitude * 111139
z = latitude * DATA_SIZE * SCALE * -1 = latitude * -111139
```

(x is flipped so hava to multiply -1 on z)