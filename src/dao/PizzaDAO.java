package dao;

// import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import model.Pizza;
import model.Category;

public class PizzaDAO {

    private CategoryDAO categoryDAO;  // We'll use this to fetch category details
    
    public PizzaDAO() {
        this.categoryDAO = new CategoryDAO();
    }

    /**
     * Creates a new pizza in the database
     * 
     * @param pizza The pizza to create
     * @return The created pizza with ID populated
     * @throws SQLException If a database error occurs
     */
    public Pizza createPizza(Pizza pizza) throws SQLException {
        String sql = "INSERT INTO pizzas (name, description, price, category_id, image_url, " +
                     "is_vegetarian, is_available) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set parameters in the prepared statement
            stmt.setString(1, pizza.getName());
            stmt.setString(2, pizza.getDescription());
            stmt.setBigDecimal(3, pizza.getPrice());
            stmt.setInt(4, pizza.getCategoryId());
            stmt.setString(5, pizza.getImageUrl());
            stmt.setBoolean(6, pizza.isVegetarian());
            stmt.setBoolean(7, pizza.isAvailable());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating pizza failed, no rows affected.");
            }
            
            // Get the generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pizza.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating pizza failed, no ID obtained.");
                }
            }
            
            return pizza;
        }
    }
    
    /**
     * Finds a pizza by its ID
     * 
     * @param id The pizza ID to search for
     * @return The pizza if found, null otherwise
     * @throws SQLException If a database error occurs
     */
    public Pizza findById(int id) throws SQLException {
        String sql = "SELECT * FROM pizzas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pizza pizza = mapResultSetToPizza(rs);
                    
                    // Load the category information
                    Category category = categoryDAO.findById(pizza.getCategoryId());
                    pizza.setCategory(category);
                    
                    return pizza;
                } else {
                    return null; // Pizza not found
                }
            }
        }
    }
    
    /**
     * Gets all pizzas from the database
     * 
     * @return List of all pizzas
     * @throws SQLException If a database error occurs
     */
    public List<Pizza> findAll() throws SQLException {
        String sql = "SELECT * FROM pizzas";
        List<Pizza> pizzas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pizza pizza = mapResultSetToPizza(rs);
                pizzas.add(pizza);
            }
        }
        
        // Load categories for all pizzas
        for (Pizza pizza : pizzas) {
            pizza.setCategory(categoryDAO.findById(pizza.getCategoryId()));
        }
        
        return pizzas;
    }
    
    /**
     * Gets all pizzas of a specific category
     * 
     * @param categoryId The category ID to filter by
     * @return List of pizzas in the category
     * @throws SQLException If a database error occurs
     */
    public List<Pizza> findByCategory(int categoryId) throws SQLException {
        String sql = "SELECT * FROM pizzas WHERE category_id = ?";
        List<Pizza> pizzas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pizza pizza = mapResultSetToPizza(rs);
                    pizzas.add(pizza);
                }
            }
        }
        
        // Load category for all pizzas (same category for all in this case)
        Category category = categoryDAO.findById(categoryId);
        for (Pizza pizza : pizzas) {
            pizza.setCategory(category);
        }
        
        return pizzas;
    }
    
    /**
     * Gets all vegetarian pizzas
     * 
     * @return List of vegetarian pizzas
     * @throws SQLException If a database error occurs
     */
    public List<Pizza> findVegetarian() throws SQLException {
        String sql = "SELECT * FROM pizzas WHERE is_vegetarian = TRUE";
        List<Pizza> pizzas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pizza pizza = mapResultSetToPizza(rs);
                pizzas.add(pizza);
            }
        }
        
        // Load categories for all pizzas
        for (Pizza pizza : pizzas) {
            pizza.setCategory(categoryDAO.findById(pizza.getCategoryId()));
        }
        
        return pizzas;
    }
    
    /**
     * Updates an existing pizza in the database
     * 
     * @param pizza The pizza to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updatePizza(Pizza pizza) throws SQLException {
        String sql = "UPDATE pizzas SET name = ?, description = ?, price = ?, " +
                     "category_id = ?, image_url = ?, is_vegetarian = ?, is_available = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pizza.getName());
            stmt.setString(2, pizza.getDescription());
            stmt.setBigDecimal(3, pizza.getPrice());
            stmt.setInt(4, pizza.getCategoryId());
            stmt.setString(5, pizza.getImageUrl());
            stmt.setBoolean(6, pizza.isVegetarian());
            stmt.setBoolean(7, pizza.isAvailable());
            stmt.setInt(8, pizza.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes a pizza from the database
     * 
     * @param id The ID of the pizza to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deletePizza(int id) throws SQLException {
        String sql = "DELETE FROM pizzas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Helper method to map a ResultSet row to a Pizza object
     * 
     * @param rs The ResultSet positioned at the row to map
     * @return The mapped Pizza object
     * @throws SQLException If a database error occurs
     */
    private Pizza mapResultSetToPizza(ResultSet rs) throws SQLException {
        Pizza pizza = new Pizza();
        pizza.setId(rs.getInt("id"));
        pizza.setName(rs.getString("name"));
        pizza.setDescription(rs.getString("description"));
        pizza.setPrice(rs.getBigDecimal("price"));
        pizza.setCategoryId(rs.getInt("category_id"));
        pizza.setImageUrl(rs.getString("image_url"));
        pizza.setVegetarian(rs.getBoolean("is_vegetarian"));
        pizza.setAvailable(rs.getBoolean("is_available"));
        return pizza;
    }
}