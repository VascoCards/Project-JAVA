package aed_lp;

public class CachePath {
    private Cache cache1,cache2;
    private double distance;
    private int time;

    public CachePath(Cache cache1, Cache cache2, double distance, int time) {
        this.cache1 = cache1;
        this.cache2 = cache2;
        this.distance = distance;
        this.time = time;
    }

    public Cache getCache1() {
        return cache1;
    }

    public Cache getCache2() {
        return cache2;
    }

    public double getDistance() {
        return distance;
    }

    public int getTime() {
        return time;
    }
}
