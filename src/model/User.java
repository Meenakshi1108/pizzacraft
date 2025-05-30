package model;

import java.sql.Timestamp;
import util.PasswordUtils;

/**
 * Represents a user (customer or staff) in the system
 */
public class User {
    private int id;
    private String username;
    private String password;  // This will now store the hashed password
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Timestamp createdAt;
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_DELIVERY = "DELIVERY_PERSON";

    
    // Default constructor
    public User() {
    }
    
    // Constructor with all fields (except password)
    public User(int id, String username, String fullName, String email, 
                String phone, String role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    // Only returns the hashed password, never expose the actual password
    public String getPassword() {
        return password;
    }
    
    // Hash the password before storing it
    public void setPassword(String plainTextPassword) {
        this.password = PasswordUtils.hashPassword(plainTextPassword);
    }
    
    // Direct setter for already-hashed password (e.g., when loading from database)
    public void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }
    
    // Verify if a password matches
    public boolean verifyPassword(String plainTextPassword) {
        return PasswordUtils.verifyPassword(plainTextPassword, this.password);
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}