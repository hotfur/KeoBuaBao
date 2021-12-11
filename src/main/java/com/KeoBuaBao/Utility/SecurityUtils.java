package com.KeoBuaBao.Utility;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {
    public static String hashPassword(String password) {
        String hashed = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digested = md.digest();
            hashed = DatatypeConverter.printHexBinary(digested).toLowerCase();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashed;
    }
}
