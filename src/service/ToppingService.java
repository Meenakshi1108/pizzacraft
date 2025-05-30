package service;

import java.sql.SQLException;
import java.util.List;

import dao.ToppingDAO;
import model.Topping;

/**
 * Service for handling topping-related business logic
 */
public class ToppingService {
    
    private final ToppingDAO toppingDAO;
    
    public ToppingService() {
        this.toppingDAO = new ToppingDAO();
    }
    
    /**
     * Creates a new topping
     * 
     * @param topping The topping to create
     * @return The created topping with ID populated
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public Topping createTopping(Topping topping) throws ValidationException, ServiceException {
        try {
            // Validate input
            validateTopping(topping);
            
            // Create the topping in the database
            return toppingDAO.createTopping(topping);
        } catch (SQLException e) {
            throw new ServiceException("Error creating topping", e);
        }
    }
    
    /**
     * Gets a topping by its ID
     * 
     * @param id The topping ID
     * @return The topping if found, null otherwise
     * @throws ServiceException If a service error occurs
     */
    public Topping getToppingById(int id) throws ServiceException {
        try {
            return toppingDAO.findById(id);
        } catch (SQLException e) {
            throw new ServiceException("Error getting topping by ID", e);
        }
    }
    
    /**
     * Gets all toppings
     * 
     * @return List of all toppings
     * @throws ServiceException If a service error occurs
     */
    public List<Topping> getAllToppings() throws ServiceException {
        try {
            return toppingDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error getting all toppings", e);
        }
    }
    
    /**
     * Gets all vegetarian toppings
     * 
     * @return List of vegetarian toppings
     * @throws ServiceException If a service error occurs
     */
    public List<Topping> getVegetarianToppings() throws ServiceException {
        try {
            return toppingDAO.findVegetarian();
        } catch (SQLException e) {
            throw new ServiceException("Error getting vegetarian toppings", e);
        }
    }
    
    /**
     * Updates a topping
     * 
     * @param topping The topping to update
     * @return true if the update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updateTopping(Topping topping) throws ValidationException, ServiceException {
        try {
            // Validate input
            validateTopping(topping);
            
            // Check if the topping exists
            if (toppingDAO.findById(topping.getId()) == null) {
                throw new ValidationException("Topping not found");
            }
            
            // Update the topping
            return toppingDAO.updateTopping(topping);
        } catch (SQLException e) {
            throw new ServiceException("Error updating topping", e);
        }
    }
    
    /**
     * Deletes a topping
     * 
     * @param id The ID of the topping to delete
     * @return true if the deletion was successful, false otherwise
     * @throws ServiceException If a service error occurs
     */
    public boolean deleteTopping(int id) throws ServiceException {
        try {
            return toppingDAO.deleteTopping(id);
        } catch (SQLException e) {
            throw new ServiceException("Error deleting topping", e);
        }
    }
    
    /**
     * Validates a topping
     * 
     * @param topping The topping to validate
     * @throws ValidationException If validation fails
     */
    private void validateTopping(Topping topping) throws ValidationException {
        // Check that required fields are present
        if (topping.getName() == null || topping.getName().trim().isEmpty()) {
            throw new ValidationException("Topping name is required");
        }
        
        if (topping.getPrice() == null) {
            throw new ValidationException("Topping price is required");
        }
        
        // Validate price
        if (topping.getPrice().doubleValue() < 0) {
            throw new ValidationException("Topping price cannot be negative");
        }
        
        // Validate name length
        if (topping.getName().length() > 50) {
            throw new ValidationException("Topping name cannot exceed 50 characters");
        }
    }
}