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
import model.OrderItem;
import model.Pizza;
import model.Topping;

public class OrderItemDAO {

    private PizzaDAO pizzaDAO;
    
    public OrderItemDAO() {
        this.pizzaDAO = new PizzaDAO();
    }
    
    /**
     * Creates a new order item in the database and its associated toppings
     * 
     * @param item The order item to create
     * @param conn An existing database connection (for transaction support)
     * @return The created order item with ID populated
     * @throws SQLException If a database error occurs
     */
    public OrderItem createOrderItem(OrderItem item, Connection conn) throws SQLException {
        boolean closeConnection = false;
        
        // If no connection was provided, create one
        if (conn == null) {
            conn = DatabaseConnection.getConnection();
            closeConnection = true;
        }
        
        try {
            // First, insert the order item
            String sql = "INSERT INTO order_items (order_id, pizza_id, quantity, price) " +
                         "VALUES (?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getPizzaId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getPrice());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating order item failed, no rows affected.");
            }
            
            // Get the generated item ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int itemId = generatedKeys.getInt(1);
                    item.setId(itemId);
                    
                    // If there are toppings, insert them
                    if (item.getToppings() != null && !item.getToppings().isEmpty()) {
                        for (Topping topping : item.getToppings()) {
                            String toppingSql = "INSERT INTO order_item_toppings (order_item_id, topping_id) " +
                                               "VALUES (?, ?)";
                            
                            try (PreparedStatement toppingStmt = conn.prepareStatement(toppingSql)) {
                                toppingStmt.setInt(1, itemId);
                                toppingStmt.setInt(2, topping.getId());
                                toppingStmt.executeUpdate();
                            }
                        }
                    }
                    
                    return item;
                } else {
                    throw new SQLException("Creating order item failed, no ID obtained.");
                }
            }
        } finally {
            // Only close the connection if we created it
            if (closeConnection && conn != null) {
                conn.close();
            }
        }
    }
    
    /**
     * Alternative version of createOrderItem that creates its own connection
     * 
     * @param item The order item to create
     * @return The created order item with ID populated
     * @throws SQLException If a database error occurs
     */
    public OrderItem createOrderItem(OrderItem item) throws SQLException {
        return createOrderItem(item, null);
    }
    
    /**
     * Finds an order item by its ID
     * 
     * @param id The order item ID to search for
     * @return The order item if found, null otherwise
     * @throws SQLException If a database error occurs
     */
    public OrderItem findById(int id) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OrderItem item = mapResultSetToOrderItem(rs);
                    
                    // Load the pizza information
                    Pizza pizza = pizzaDAO.findById(item.getPizzaId());
                    item.setPizza(pizza);
                    
                    // Load toppings
                    loadToppingsForOrderItem(item);
                    
                    return item;
                } else {
                    return null; // Order item not found
                }
            }
        }
    }
    
    /**
     * Gets all order items for a specific order
     * 
     * @param orderId The order ID to find items for
     * @return List of order items for the order
     * @throws SQLException If a database error occurs
     */
    public List<OrderItem> findByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = mapResultSetToOrderItem(rs);
                    items.add(item);
                }
            }
        }
        
        // Load pizza and toppings for each item
        for (OrderItem item : items) {
            Pizza pizza = pizzaDAO.findById(item.getPizzaId());
            item.setPizza(pizza);
            loadToppingsForOrderItem(item);
        }
        
        return items;
    }
    
    /**
     * Updates an existing order item in the database
     * 
     * @param item The order item to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateOrderItem(OrderItem item) throws SQLException {
        // In most cases, you won't update order items after creation
        // This is included for completeness
        
        String sql = "UPDATE order_items SET quantity = ?, price = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, item.getQuantity());
            stmt.setBigDecimal(2, item.getPrice());
            stmt.setInt(3, item.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes an order item from the database
     * 
     * @param id The ID of the order item to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteOrderItem(int id) throws SQLException {
        // Delete associated toppings first (or rely on cascade)
        String deleteToppingsSql = "DELETE FROM order_item_toppings WHERE order_item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete toppings
                try (PreparedStatement stmt = conn.prepareStatement(deleteToppingsSql)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                // Delete the order item
                String deleteItemSql = "DELETE FROM order_items WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteItemSql)) {
                    stmt.setInt(1, id);
                    int affectedRows = stmt.executeUpdate();
                    
                    // Commit the transaction
                    conn.commit();
                    return affectedRows > 0;
                }
            } catch (SQLException e) {
                // If something goes wrong, rollback
                conn.rollback();
                throw e;
            } finally {
                // Reset auto-commit
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Helper method to load toppings for an order item
     * 
     * @param item The order item to load toppings for
     * @throws SQLException If a database error occurs
     */
    private void loadToppingsForOrderItem(OrderItem item) throws SQLException {
        String sql = "SELECT t.* FROM toppings t " +
                     "JOIN order_item_toppings oit ON t.id = oit.topping_id " +
                     "WHERE oit.order_item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, item.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<Topping> toppings = new ArrayList<>();
                while (rs.next()) {
                    Topping topping = new Topping();
                    topping.setId(rs.getInt("id"));
                    topping.setName(rs.getString("name"));
                    topping.setPrice(rs.getBigDecimal("price"));
                    topping.setVegetarian(rs.getBoolean("is_vegetarian"));
                    toppings.add(topping);
                }
                item.setToppings(toppings);
            }
        }
    }
    
    /**
     * Helper method to map a ResultSet row to an OrderItem object
     * 
     * @param rs The ResultSet positioned at the row to map
     * @return The mapped OrderItem object
     * @throws SQLException If a database error occurs
     */
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setPizzaId(rs.getInt("pizza_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPrice(rs.getBigDecimal("price"));
        return item;
    }
        // Add this to a BaseDAO class or every DAO class
    /**
     * Closes database resources safely
     */
    protected void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fix this method signature in OrderItemDAO.java
    public OrderItem createOrderItem(Connection conn, OrderItem item) throws SQLException {
        boolean closeConnection = false;
        
        // If no connection was provided, create one
        if (conn == null) {
            conn = DatabaseConnection.getConnection();
            closeConnection = true;
        }
        
        try {
            // First, insert the order item
            String sql = "INSERT INTO order_items (order_id, pizza_id, quantity, price) " +
                         "VALUES (?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getPizzaId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getPrice());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating order item failed, no rows affected.");
            }
            
            // Get the generated item ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int itemId = generatedKeys.getInt(1);
                    item.setId(itemId);
                    
                    // If there are toppings, insert them
                    if (item.getToppings() != null && !item.getToppings().isEmpty()) {
                        for (Topping topping : item.getToppings()) {
                            String toppingSql = "INSERT INTO order_item_toppings (order_item_id, topping_id) " +
                                               "VALUES (?, ?)";
                            
                            try (PreparedStatement toppingStmt = conn.prepareStatement(toppingSql)) {
                                toppingStmt.setInt(1, itemId);
                                toppingStmt.setInt(2, topping.getId());
                                toppingStmt.executeUpdate();
                            }
                        }
                    }
                    
                    return item;
                } else {
                    throw new SQLException("Creating order item failed, no ID obtained.");
                }
            }
        } finally {
            // Only close the connection if we created it
            if (closeConnection && conn != null) {
                conn.close();
            }
        }
    }
}