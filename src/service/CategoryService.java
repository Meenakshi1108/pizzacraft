
package service;

import java.sql.SQLException;
import java.util.List;

import dao.CategoryDAO;
import model.Category;
// import service.ValidationException;
/**
 * Service for handling category-related business logic
 */
public class CategoryService {
    
    private final CategoryDAO categoryDAO;
    
    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Creates a new category
     * 
     * @param category The category to create
     * @return The created category with ID populated
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public Category createCategory(Category category) throws ValidationException, ServiceException {
        try {
            // Validate input
            validateCategory(category);
            
            // Create the category in the database
            return categoryDAO.createCategory(category);
        } catch (SQLException e) {
            throw new ServiceException("Error creating category", e);
        }
    }
    
    /**
     * Gets a category by its ID
     * 
     * @param id The category ID
     * @return The category if found, null otherwise
     * @throws ServiceException If a service error occurs
     */
    public Category getCategoryById(int id) throws ServiceException {
        try {
            return categoryDAO.findById(id);
        } catch (SQLException e) {
            throw new ServiceException("Error getting category by ID", e);
        }
    }
    
    /**
     * Gets all categories
     * 
     * @return List of all categories
     * @throws ServiceException If a service error occurs
     */
    public List<Category> getAllCategories() throws ServiceException {
        try {
            return categoryDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error getting all categories", e);
        }
    }
    
    /**
     * Updates a category
     * 
     * @param category The category to update
     * @return true if the update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updateCategory(Category category) throws ValidationException, ServiceException {
        try {
            // Validate input
            validateCategory(category);
            
            // Check if the category exists
            if (categoryDAO.findById(category.getId()) == null) {
                throw new ValidationException("Category not found");
            }
            
            // Update the category
            return categoryDAO.updateCategory(category);
        } catch (SQLException e) {
            throw new ServiceException("Error updating category", e);
        }
    }
    
    /**
     * Deletes a category
     * 
     * @param id The ID of the category to delete
     * @return true if the deletion was successful, false otherwise
     * @throws ServiceException If a service error occurs
     */
    public boolean deleteCategory(int id) throws ServiceException {
        try {
            return categoryDAO.deleteCategory(id);
        } catch (SQLException e) {
            throw new ServiceException("Error deleting category", e);
        }
    }
    
    /**
     * Validates a category
     * 
     * @param category The category to validate
     * @throws ValidationException If validation fails
     */
    private void validateCategory(Category category) throws ValidationException {
        // Check that required fields are present
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("Category name is required");
        }
        
        // Validate name length
        if (category.getName().length() > 50) {
            throw new ValidationException("Category name cannot exceed 50 characters");
        }
    }
}