package service;

import java.sql.SQLException;
import java.util.List;

import dao.PizzaDAO;
import dao.CategoryDAO;
import model.Pizza;
import model.Category;
import service.ValidationException;
/**
 * Service for handling pizza-related business logic
 */
public class PizzaService {
    
    private final PizzaDAO pizzaDAO;
    private final CategoryDAO categoryDAO;
    
    public PizzaService() {
        this.pizzaDAO = new PizzaDAO();
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Creates a new pizza
     * 
     * @param pizza The pizza to create
     * @return The created pizza with ID populated
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public Pizza createPizza(Pizza pizza) throws ValidationException, ServiceException {
        try {
            // Validate input
            validatePizza(pizza);
            
            // Verify the category exists
            if (categoryDAO.findById(pizza.getCategoryId()) == null) {
                throw new ValidationException("Category does not exist");
            }
            
            // Create the pizza in the database
            return pizzaDAO.createPizza(pizza);
        } catch (SQLException e) {
            throw new ServiceException("Error creating pizza", e);
        }
    }
    
    /**
     * Gets a pizza by its ID
     * 
     * @param id The pizza ID
     * @return The pizza if found, null otherwise
     * @throws ServiceException If a service error occurs
     */
    public Pizza getPizzaById(int id) throws ServiceException {
        try {
            return pizzaDAO.findById(id);
        } catch (SQLException e) {
            throw new ServiceException("Error getting pizza by ID", e);
        }
    }
    
    /**
     * Gets all pizzas
     * 
     * @return List of all pizzas
     * @throws ServiceException If a service error occurs
     */
    public List<Pizza> getAllPizzas() throws ServiceException {
        try {
            return pizzaDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error getting all pizzas", e);
        }
    }
    
    /**
     * Gets all pizzas in a specific category
     * 
     * @param categoryId The category ID
     * @return List of pizzas in the category
     * @throws ServiceException If a service error occurs
     */
    public List<Pizza> getPizzasByCategory(int categoryId) throws ServiceException {
        try {
            return pizzaDAO.findByCategory(categoryId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting pizzas by category", e);
        }
    }
    
    /**
     * Gets all vegetarian pizzas
     * 
     * @return List of vegetarian pizzas
     * @throws ServiceException If a service error occurs
     */
    public List<Pizza> getVegetarianPizzas() throws ServiceException {
        try {
            return pizzaDAO.findVegetarian();
        } catch (SQLException e) {
            throw new ServiceException("Error getting vegetarian pizzas", e);
        }
    }
    
    /**
     * Updates a pizza
     * 
     * @param pizza The pizza to update
     * @return true if the update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updatePizza(Pizza pizza) throws ValidationException, ServiceException {
        try {
            // Validate input
            validatePizza(pizza);
            
            // Check if the pizza exists
            if (pizzaDAO.findById(pizza.getId()) == null) {
                throw new ValidationException("Pizza not found");
            }
            
            // Verify the category exists
            if (categoryDAO.findById(pizza.getCategoryId()) == null) {
                throw new ValidationException("Category does not exist");
            }
            
            // Update the pizza
            return pizzaDAO.updatePizza(pizza);
        } catch (SQLException e) {
            throw new ServiceException("Error updating pizza", e);
        }
    }
    
    /**
     * Updates pizza availability
     * 
     * @param pizzaId The ID of the pizza
     * @param isAvailable The new availability status
     * @return true if the update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updatePizzaAvailability(int pizzaId, boolean isAvailable) 
            throws ValidationException, ServiceException {
        try {
            // Get the pizza
            Pizza pizza = pizzaDAO.findById(pizzaId);
            if (pizza == null) {
                throw new ValidationException("Pizza not found");
            }
            
            // Update availability
            pizza.setAvailable(isAvailable);
            
            // Update the pizza
            return pizzaDAO.updatePizza(pizza);
        } catch (SQLException e) {
            throw new ServiceException("Error updating pizza availability", e);
        }
    }
    
    /**
     * Deletes a pizza
     * 
     * @param id The ID of the pizza to delete
     * @return true if the deletion was successful, false otherwise
     * @throws ServiceException If a service error occurs
     */
    public boolean deletePizza(int id) throws ServiceException {
        try {
            return pizzaDAO.deletePizza(id);
        } catch (SQLException e) {
            throw new ServiceException("Error deleting pizza", e);
        }
    }
    
    /**
     * Validates a pizza
     * 
     * @param pizza The pizza to validate
     * @throws ValidationException If validation fails
     */
    private void validatePizza(Pizza pizza) throws ValidationException {
        // Check that required fields are present
        if (pizza.getName() == null || pizza.getName().trim().isEmpty()) {
            throw new ValidationException("Pizza name is required");
        }
        
        if (pizza.getPrice() == null) {
            throw new ValidationException("Pizza price is required");
        }
        
        if (pizza.getCategoryId() <= 0) {
            throw new ValidationException("Valid category ID is required");
        }
        
        // Validate price
        if (pizza.getPrice().doubleValue() <= 0) {
            throw new ValidationException("Pizza price must be greater than zero");
        }
        
        // Validate name length
        if (pizza.getName().length() > 100) {
            throw new ValidationException("Pizza name cannot exceed 100 characters");
        }
    }
}