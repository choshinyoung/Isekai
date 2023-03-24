package org.choshinyoung.isekai;

public class IsekaiCoordinate {
    public static final int DIRECTION_NORTH = 1;
    public static final int DIRECTION_SOUTH = 2;
    public static final int DIRECTION_WEST = 4;
    public static final int DIRECTION_EAST = 8;

    public int direction;
    public double latitude;
    public double longitude;

    public IsekaiCoordinate(int dir, double lat, double lon) {
        direction = dir;
        latitude = lat;
        longitude = lon;
    }

    public boolean isNorth() {
        return (direction & DIRECTION_NORTH) != 0;
    }

    public boolean isSouth() {
        return (direction & DIRECTION_SOUTH) != 0;
    }

    public boolean isWest() {
        return (direction & DIRECTION_WEST) != 0;
    }

    public boolean isEast() {
        return (direction & DIRECTION_EAST) != 0;
    }
}
