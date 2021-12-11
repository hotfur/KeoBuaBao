package com.KeoBuaBao.Utility;

public class RandomUtilis {
    public static Long getRandom(Long min, Long max) {
        // Generate random int value from min to max
        return (long) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
