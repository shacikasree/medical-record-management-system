package com.medical.servlet;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt
 */
public class PasswordUtil {
    
    // BCrypt work factor (higher = more secure but slower)
    private static final int BCRYPT_ROUNDS = 12;
    
    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        String salt = BCrypt.gensalt(BCRYPT_ROUNDS);
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);
        
        System.out.println("🔐 Password hashed successfully");
        return hashedPassword;
    }
    
    /**
     * Check if a plain text password matches a hashed password
     * @param plainPassword The plain text password to check
     * @param hashedPassword The hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            System.out.println("❌ Null password provided");
            return false;
        }
        
        try {
            // Check if the stored password is a BCrypt hash
            if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$")) {
                // It's a BCrypt hash - verify it
                boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
                System.out.println("🔍 BCrypt verification: " + (matches ? "✅ SUCCESS" : "❌ FAILED"));
                return matches;
            } else {
                // Legacy plain text password - direct comparison
                boolean matches = plainPassword.equals(hashedPassword);
                System.out.println("⚠️ Plain text comparison: " + (matches ? "✅ SUCCESS" : "❌ FAILED"));
                System.out.println("⚠️ WARNING: Password should be hashed!");
                return matches;
            }
        } catch (Exception e) {
            System.out.println("❌ Password verification error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a password is already hashed
     * @param password The password to check
     * @return true if password appears to be a BCrypt hash
     */
    public static boolean isPasswordHashed(String password) {
        return password != null && 
               (password.startsWith("$2a$") || password.startsWith("$2b$")) &&
               password.length() == 60;
    }
    
    /**
     * Validate password strength
     * @param password The password to validate
     * @return true if password meets minimum requirements
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }
    
    /**
     * Get password strength message
     * @param password The password to check
     * @return A message describing password requirements
     */
    public static String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        if (password.length() < 8) {
            return "Password is acceptable but consider using 8+ characters";
        }
        return "Password is strong";
    }
}