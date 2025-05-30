package service;

import java.sql.SQLException;
import java.util.List;

import dao.UserDAO;
import model.User;

import service.ValidationException;
/**
 * Service for handling user-related business logic
 */
public class UserService {
    
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Registers a new user in the system
     * 
     * @param user The user to register
     * @return The registered user with ID populated
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public User registerUser(User user) throws ValidationException, ServiceException {
        try {
            // Validate input
            validateUserRegistration(user);
            
            // Check if username or email already exists
            User existingByUsername = userDAO.findByUsername(user.getUsername());
            if (existingByUsername != null) {
                throw new ValidationException("Username already exists");
            }
            
            User existingByEmail = userDAO.findByEmail(user.getEmail());
            if (existingByEmail != null) {
                throw new ValidationException("Email already in use");
            }
            
            // Default role for new users is CUSTOMER
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("CUSTOMER");
            }
            
            // Create the user in the database
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
            
            // Check if user exists and password matches
            if (user == null || !user.verifyPassword(password)) {
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
            // Get the user
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("User not found");
            }
            
            // Verify the current password
            if (!user.verifyPassword(currentPassword)) {
                throw new ValidationException("Current password is incorrect");
            }
            
            // Validate the new password
            if (newPassword == null || newPassword.length() < 8) {
                throw new ValidationException("New password must be at least 8 characters");
            }
            
            // Set the new password
            user.setPassword(newPassword);
            
            // Update the user
            return userDAO.updateUser(user);
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
    private void validateUserRegistration(User user) throws ValidationException {
        // Check that required fields are present
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        
        // Validate email format (simple check)
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new ValidationException("Invalid email format");
        }
        
        // Validate username format (alphanumeric, 3-20 chars)
        if (!user.getUsername().matches("^[a-zA-Z0-9]{3,20}$")) {
            throw new ValidationException("Username must be 3-20 alphanumeric characters");
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
        
        validateUserRegistration(user);
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
}