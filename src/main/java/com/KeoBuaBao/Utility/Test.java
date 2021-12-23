package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String username = "thuankent";
        String password = "123";
        String dateTime = DateUtilis.getCurrentDate();
        System.out.println(dateTime);
        String token = SecurityUtils.generateToken(username, SecurityUtils.hashPassword(password), dateTime);
        System.out.println(token);
    }
}
