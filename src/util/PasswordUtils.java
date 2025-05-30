package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for securely hashing and validating passwords
 */
public class PasswordUtils {
    
    // In a real production system, you would use a library like BCrypt
    // This is a simpler implementation for learning purposes
    
    /**
     * Hash a password using SHA-256 with a random salt
     * 
     * @param password The password to hash
     * @return A string in the format "salt:hash"
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Convert to Base64 strings for storage
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Return salt:hash format
            return saltString + ":" + hashString;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify if a password matches a stored hash
     * 
     * @param password The password to check
     * @param storedHash The stored hash in "salt:hash" format
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored value to get salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            // Decode the salt and hash from Base64
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the input password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] inputHash = md.digest(password.getBytes());
            
            // Compare the hashes
            if (hash.length != inputHash.length) {
                return false;
            }
            
            // Time-constant comparison to prevent timing attacks
            int diff = 0;
            for (int i = 0; i < hash.length; i++) {
                diff |= hash[i] ^ inputHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }
    
    /**
     * Test the password utilities
     */
    public static void main(String[] args) {
        // Test password hashing and verification
        String password = "mysecretpassword";
        String hashedPassword = hashPassword(password);
        
        System.out.println("Original Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);
        
        boolean isValid = verifyPassword(password, hashedPassword);
        System.out.println("Password valid? " + isValid);
        
        boolean isInvalid = verifyPassword("wrongpassword", hashedPassword);
        System.out.println("Wrong password valid? " + isInvalid);
    }
}