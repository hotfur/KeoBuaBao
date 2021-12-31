package com.KeoBuaBao.Utility;

/**
 * An utility for random in Java
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
public class RandomUtilis {
    /**
     * Generate a random whole number from min to max prompted by the user
     * @param min the lowest number to be random
     * @param max the highest number to be random
     * @return a random number in range min and max
     */
    public static Long getRandom(Long min, Long max) {
        // Generate random int value from min to max
        return (long) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
