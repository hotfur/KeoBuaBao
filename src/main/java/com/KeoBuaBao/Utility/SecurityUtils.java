package com.KeoBuaBao.Utility;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class SecurityUtils {
    public static String hashPassword(String password) {
        String hashed = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] digestBytes = messageDigest.digest();
            hashed = DatatypeConverter.printHexBinary(digestBytes).toLowerCase();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return hashed;
    }

    public static String generateToken(String username, String hashedPassword, String dateTime) {
        String code = username + hashedPassword + dateTime;
        return SecurityUtils.hashPassword(code);
    }
}
