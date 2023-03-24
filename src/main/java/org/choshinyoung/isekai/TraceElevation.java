package org.choshinyoung.isekai;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TraceElevation implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 2) {
            return false;
        }

        int x = (int)Double.parseDouble(args[0]);
        int z = Math.abs((int)Double.parseDouble(args[1]));

        IsekaiCoordinate coordinate = IsekaiChunkGenerator.positionToIsekaiCoordinate(x, z);

        int xFloor = (int)Math.round((x - (x % IsekaiChunkGenerator.SCALE)) / IsekaiChunkGenerator.SCALE) % IsekaiChunkGenerator.DATA_SIZE;
        int zFloor = (int)Math.round((z - (z % IsekaiChunkGenerator.SCALE)) / IsekaiChunkGenerator.SCALE) % IsekaiChunkGenerator.DATA_SIZE;

        int xCeil = xFloor + 1;
        int zCeil = zFloor + 1;

        double xRelative = (x % IsekaiChunkGenerator.SCALE) / IsekaiChunkGenerator.SCALE;
        double zRelative = (z % IsekaiChunkGenerator.SCALE) / IsekaiChunkGenerator.SCALE;

        // TODO: implement for border of blocks

        int a = IsekaiChunkGenerator.getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xFloor, zFloor);
        int b = IsekaiChunkGenerator.getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xCeil, zFloor);
        int c = IsekaiChunkGenerator.getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xFloor, zCeil);
        int d = IsekaiChunkGenerator.getRawHeight((int)coordinate.latitude, (int)coordinate.longitude, xCeil, zCeil);

        double left = a * (1 - xRelative) + b * xRelative;
        double right = c * (1 - xRelative) + d * xRelative;

        Bukkit.getLogger().info("location = (" + x + ", " + z + ")");
        Bukkit.getLogger().info("coordinate = (" + coordinate.longitude + ", " + coordinate.latitude + ")");

        Bukkit.getLogger().info("floor = (" + xFloor + ", " + zFloor + ")");
        Bukkit.getLogger().info("ceil = (" + xCeil + ", " + zCeil + ")");

        Bukkit.getLogger().info("relative = (" + xRelative + ", " + zRelative + ")");

        Bukkit.getLogger().info("height(0, 0) = " + a);
        Bukkit.getLogger().info("height(1, 0) = " + b);
        Bukkit.getLogger().info("height(0, 1) = " + c);
        Bukkit.getLogger().info("height(1, 1) = " + d);

        Bukkit.getLogger().info("left = " + left);
        Bukkit.getLogger().info("right = " + right);

        Bukkit.getLogger().info("result = " + ((left * (1 - zRelative)) + right * zRelative));
        Bukkit.getLogger().info("round(result) = " + (int)Math.round((left * (1 - zRelative)) + right * zRelative));

        return true;
    }
}
