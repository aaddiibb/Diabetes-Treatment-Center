package com.example.diabetes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    private static final SecureRandom RNG = new SecureRandom();

    // Store as: saltBase64:hashBase64
    public static String hashForStorage(String password) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] hash = sha256(concat(salt, password.getBytes(StandardCharsets.UTF_8)));
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(String password, String stored) {
        if (stored == null || !stored.contains(":")) return false;
        String[] parts = stored.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expected = Base64.getDecoder().decode(parts[1]);
        byte[] actual = sha256(concat(salt, password.getBytes(StandardCharsets.UTF_8)));
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
}
