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
import model.Topping;

public class ToppingDAO {

    /**
     * Creates a new topping in the database
     * 
     * @param topping The topping to create
     * @return The created topping with ID populated
     * @throws SQLException If a database error occurs
     */
    public Topping createTopping(Topping topping) throws SQLException {
        String sql = "INSERT INTO toppings (name, price, is_vegetarian) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, topping.getName());
            stmt.setBigDecimal(2, topping.getPrice());
            stmt.setBoolean(3, topping.isVegetarian());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating topping failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    topping.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating topping failed, no ID obtained.");
                }
            }
            
            return topping;
        }
    }
    
    /**
     * Finds a topping by its ID
     * 
     * @param id The topping ID to search for
     * @return The topping if found, null otherwise
     * @throws SQLException If a database error occurs
     */
    public Topping findById(int id) throws SQLException {
        String sql = "SELECT * FROM toppings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTopping(rs);
                } else {
                    return null; // Topping not found
                }
            }
        }
    }
    
    /**
     * Gets all toppings from the database
     * 
     * @return List of all toppings
     * @throws SQLException If a database error occurs
     */
    public List<Topping> findAll() throws SQLException {
        String sql = "SELECT * FROM toppings";
        List<Topping> toppings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                toppings.add(mapResultSetToTopping(rs));
            }
        }
        
        return toppings;
    }
    
    /**
     * Gets all vegetarian toppings
     * 
     * @return List of vegetarian toppings
     * @throws SQLException If a database error occurs
     */
    public List<Topping> findVegetarian() throws SQLException {
        String sql = "SELECT * FROM toppings WHERE is_vegetarian = TRUE";
        List<Topping> toppings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                toppings.add(mapResultSetToTopping(rs));
            }
        }
        
        return toppings;
    }
    
    /**
     * Updates an existing topping in the database
     * 
     * @param topping The topping to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateTopping(Topping topping) throws SQLException {
        String sql = "UPDATE toppings SET name = ?, price = ?, is_vegetarian = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, topping.getName());
            stmt.setBigDecimal(2, topping.getPrice());
            stmt.setBoolean(3, topping.isVegetarian());
            stmt.setInt(4, topping.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes a topping from the database
     * 
     * @param id The ID of the topping to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteTopping(int id) throws SQLException {
        String sql = "DELETE FROM toppings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Helper method to map a ResultSet row to a Topping object
     * 
     * @param rs The ResultSet positioned at the row to map
     * @return The mapped Topping object
     * @throws SQLException If a database error occurs
     */
    private Topping mapResultSetToTopping(ResultSet rs) throws SQLException {
        Topping topping = new Topping();
        topping.setId(rs.getInt("id"));
        topping.setName(rs.getString("name"));
        topping.setPrice(rs.getBigDecimal("price"));
        topping.setVegetarian(rs.getBoolean("is_vegetarian"));
        return topping;
    }
}