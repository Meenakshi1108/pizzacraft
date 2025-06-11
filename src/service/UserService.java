package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.UserDAO;
import dao.OrderDAO;
import model.User;
import util.PasswordUtils;
/**
 * Service for handling user-related business logic
 */
public class UserService {
    
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Registers a new user
     * 
     * @param user The user to register
     * @return The registered user
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public User registerUser(User user) throws ValidationException, ServiceException {
        try {
            // Validate user data
            validateUser(user);
            
            // Check for existing username
            User existingUsername = userDAO.findByUsername(user.getUsername());
            if (existingUsername != null) {
                throw new ValidationException("Username '" + user.getUsername() + "' is already taken");
            }
            
            // Check for existing email
            User existingEmail = userDAO.findByEmail(user.getEmail());
            if (existingEmail != null) {
                throw new ValidationException("Email '" + user.getEmail() + "' is already in use");
            }
            
            // Hash the password before storing
            String hashedPassword = hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            
            // Set default role if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("CUSTOMER");
            }
            
            // Create the user
            return userDAO.createUser(user);
        } catch (SQLException e) {
            throw new ServiceException("Error registering user", e);
        }
    }
    
    /**
     * Authenticates a user based on username and password
     * 
     * @param username The username
     * @param password The password (plain text)
     * @return The authenticated user if successful
     * @throws ValidationException If credentials are invalid
     * @throws ServiceException If a service error occurs
     */
    public User authenticateUser(String username, String password) 
            throws ValidationException, ServiceException {
        try {
            // Find the user by username
            User user = userDAO.findByUsername(username);
            
            // Check if user exists
            if (user == null) {
                throw new ValidationException("Invalid username or password");
            }
            
            // Verify password using the verifyPassword method in this class
            if (!verifyPassword(password, user.getPassword())) {
                throw new ValidationException("Invalid username or password");
            }
            
            return user;
        } catch (SQLException e) {
            throw new ServiceException("Error authenticating user", e);
        }
    }
    
    /**
     * Gets a user by their ID
     * 
     * @param id The user ID
     * @return The user if found, null otherwise
     * @throws ServiceException If a service error occurs
     */
    public User getUserById(int id) throws ServiceException {
        try {
            return userDAO.findById(id);
        } catch (SQLException e) {
            throw new ServiceException("Error getting user by ID", e);
        }
    }
    
    /**
     * Gets a user by their username
     * 
     * @param username The username
     * @return The user if found, null otherwise
     * @throws ServiceException If a service error occurs
     */
    public User getUserByUsername(String username) throws ServiceException {
        try {
            return userDAO.findByUsername(username);
        } catch (SQLException e) {
            throw new ServiceException("Error getting user by username", e);
        }
    }
    
    /**
     * Gets all users in the system
     * 
     * @return List of all users
     * @throws ServiceException If a service error occurs
     */
    public List<User> getAllUsers() throws ServiceException {
        try {
            return userDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error getting all users", e);
        }
    }
    
    /**
     * Updates a user's information
     * 
     * @param user The user to update
     * @return true if the update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updateUser(User user) throws ValidationException, ServiceException {
        try {
            // Validate input
            validateUserUpdate(user);
            
            // Check if the user exists
            User existingUser = userDAO.findById(user.getId());
            if (existingUser == null) {
                throw new ValidationException("User not found");
            }
            
            // Check if username is being changed and is already taken
            if (!existingUser.getUsername().equals(user.getUsername())) {
                User userWithUsername = userDAO.findByUsername(user.getUsername());
                if (userWithUsername != null) {
                    throw new ValidationException("Username already exists");
                }
            }
            
            // Check if email is being changed and is already taken
            if (!existingUser.getEmail().equals(user.getEmail())) {
                User userWithEmail = userDAO.findByEmail(user.getEmail());
                if (userWithEmail != null) {
                    throw new ValidationException("Email already in use");
                }
            }
            
            // Update the user
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            throw new ServiceException("Error updating user", e);
        }
    }
    
    /**
     * Updates a user's password
     * 
     * @param userId The ID of the user
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return true if the password was updated, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updatePassword(int userId, String currentPassword, String newPassword) 
            throws ValidationException, ServiceException {
        try {
            // Validate new password - ADDED LENGTH CHECK HERE TOO
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new ValidationException("New password is required");
            }
            if (newPassword.length() < 8) {
                throw new ValidationException("New password must be at least 8 characters long");
            }
            
            // Get the user
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("User not found");
            }
            
            // Verify old password
            if (!user.verifyPassword(currentPassword)) {
                throw new ValidationException("Current password is incorrect");
            }
            
            // Hash the new password
            String hashedPassword = hashPassword(newPassword);
            
            // Update the password
            return userDAO.updatePassword(userId, hashedPassword);
        } catch (SQLException e) {
            throw new ServiceException("Error updating password", e);
        }
    }
    
    /**
     * Deletes a user from the system
     * 
     * @param id The ID of the user to delete
     * @return true if the deletion was successful, false otherwise
     * @throws ServiceException If a service error occurs
     */
    public boolean deleteUser(int id) throws ServiceException {
        try {
            return userDAO.deleteUser(id);
        } catch (SQLException e) {
            throw new ServiceException("Error deleting user", e);
        }
    }
    
    /**
     * Validates a user for registration
     * 
     * @param user The user to validate
     * @throws ValidationException If validation fails
     */
    private void validateUser(User user) throws ValidationException {
        // Username validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        // Email validation
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new ValidationException("Invalid email format");
        }
        
        // Password validation - NEW CHECK FOR 8+ CHARACTERS
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (user.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        
        // Contact number validation
        if (user.getContactNumber() == null || user.getContactNumber().trim().isEmpty()) {
            throw new ValidationException("Contact number is required");
        }
    }
    
    /**
     * Validates a user for update
     * 
     * @param user The user to validate
     * @throws ValidationException If validation fails
     */
    private void validateUserUpdate(User user) throws ValidationException {
        // Similar to registration but ID is required
        if (user.getId() <= 0) {
            throw new ValidationException("User ID is required");
        }
        
        validateUser(user);
    }
    /**
     * Gets all users with a specific role
     * 
     * @param role The role to filter by
     * @return List of users with the specified role
     * @throws ServiceException If a service error occurs
     */
    public List<User> getUsersByRole(String role) throws ServiceException {
        try {
            return userDAO.findByRole(role);
        } catch (SQLException e) {
            throw new ServiceException("Error getting users by role", e);
        }
    }
    
    /**
     * Counts users by role
     * 
     * @param role The role to count
     * @return Count of users with the specified role
     * @throws ServiceException If a service error occurs
     */
    public int countUsersByRole(String role) throws ServiceException {
        try {
            return userDAO.countByRole(role);
        } catch (SQLException e) {
            throw new ServiceException("Error counting users by role", e);
        }
    }

    /**
     * Counts all users
     * 
     * @return Count of all users
     * @throws ServiceException If a service error occurs
     */
    public int countAllUsers() throws ServiceException {
        try {
            return userDAO.countAll();
        } catch (SQLException e) {
            throw new ServiceException("Error counting all users", e);
        }
    }
      /**
     * Updates a user's profile
     * 
     * @param user The updated user data
     * @return The updated user
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public User updateProfile(User user) throws ValidationException, ServiceException {
        try {
            // Validate user data
            validateUserProfile(user);
            
            // Update user in database
            boolean updated = userDAO.updateUser(user);
            
            if (updated) {
                // Return the updated user data
                return userDAO.findById(user.getId());
            } else {
                throw new ServiceException("Failed to update user profile");
            }
        } catch (SQLException e) {
            throw new ServiceException("Error updating user profile", e);
        }
    }

    /**
     * Validates user data for profile updates
     * 
     * @param user The user to validate
     * @throws ValidationException If validation fails
     */    private void validateUserProfile(User user) throws ValidationException {
        // Full name validation
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        
        // Email validation
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new ValidationException("Invalid email format");
        }
        
        // Contact number validation
        if (user.getContactNumber() == null || user.getContactNumber().trim().isEmpty()) {
            throw new ValidationException("Contact number is required");
        }
        if (!isValidContactNumber(user.getContactNumber())) {
            throw new ValidationException("Invalid contact number format");
        }
    }

    /**
     * Validates a contact number
     * 
     * @param contactNumber The contact number to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidContactNumber(String contactNumber) {
        // Check for null or empty
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove all non-digit characters for consistent validation
        String cleanNumber = contactNumber.replaceAll("\\D", "");
        
        // Check if number starts with +91 (India country code)
        if (cleanNumber.startsWith("91") && cleanNumber.length() == 12) {
            cleanNumber = cleanNumber.substring(2);
        }
        
        // Validate format - should be 10 digits for Indian numbers
        return cleanNumber.matches("^[6-9]\\d{9}$"); // Indian mobile numbers start with 6, 7, 8, or 9
    }
      /**
     * Hashes a password using PasswordUtils
     * 
     * @param password The plain text password
     * @return The hashed password
     */
    private String hashPassword(String password) {
        // Using PasswordUtils for password hashing
        return PasswordUtils.hashPassword(password);
    }

    /**
     * Verifies a password against a hash
     * 
     * @param plainPassword The plain text password
     * @param hashedPassword The hashed password
     * @return true if the password matches, false otherwise
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return PasswordUtils.verifyPassword(plainPassword, hashedPassword);
    }    /**
     * Gets all users with the role of delivery person and their current delivery counts
     * 
     * @return A list of users with delivery person role
     * @throws ServiceException If a service error occurs
     */
    public List<User> getDeliveryPersons() throws ServiceException {
        System.out.println("DEBUG: getDeliveryPersons() called");
        List<User> deliveryPersons = new ArrayList<>();
        OrderDAO orderDAO = new OrderDAO();
        
        try {
            // Try both role names to be safe
            try {
                deliveryPersons = userDAO.findByRole(User.ROLE_DELIVERY);
            } catch (Exception e) {
                System.out.println("DEBUG: Error using ROLE_DELIVERY, trying ROLE_DELIVERY_PERSON");
                deliveryPersons = userDAO.findByRole("ROLE_DELIVERY_PERSON");
            }
            
            if (deliveryPersons.isEmpty()) {
                System.out.println("DEBUG: No delivery persons found with either role");
            }
            
            // Count active deliveries for each person
            for (User user : deliveryPersons) {
                try {
                    int activeCount = orderDAO.countActiveDeliveriesByPerson(user.getId());
                    user.setActiveDeliveries(activeCount);
                } catch (Exception e) {
                    System.out.println("DEBUG: Error counting deliveries for user " + user.getId());
                    // Safe default
                    user.setActiveDeliveries(0);
                }
            }
            
            return deliveryPersons;
        } catch (SQLException e) {
            throw new ServiceException("Error retrieving delivery persons: " + e.getMessage(), e);
        }
    }
}