package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add("a");
        a.add("b");
        a.add("c");
        System.out.println(a);
        a.remove(0);
        System.out.println(a);
    }
}
