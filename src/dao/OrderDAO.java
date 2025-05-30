package dao;

// import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import model.Order;
import model.OrderItem;

public class OrderDAO {

    private OrderItemDAO orderItemDAO;
    
    public OrderDAO() {
        this.orderItemDAO = new OrderItemDAO();
    }

    /**
     * Creates a new order in the database including all its items
     * 
     * @param order The order to create
     * @return The created order with ID populated
     * @throws SQLException If a database error occurs
     */
    public Order createOrder(Order order) throws SQLException {
        Connection conn = null;
        try {
            // Get connection and disable auto-commit for transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First, insert the main order
            String sql = "INSERT INTO orders (user_id, total_amount, order_status, " +
                         "delivery_address, contact_number) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUserId());
            stmt.setBigDecimal(2, order.getTotalAmount());
            stmt.setString(3, order.getOrderStatus());
            stmt.setString(4, order.getDeliveryAddress());
            stmt.setString(5, order.getContactNumber());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }
            
            // Get the generated order ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);
                order.setId(orderId);
                
                // Now insert all order items
                for (OrderItem item : order.getOrderItems()) {
                    item.setOrderId(orderId);
                    orderItemDAO.createOrderItem(item, conn);
                }
                
                // Everything worked, commit the transaction
                conn.commit();
                
                return order;
            } else {
                // Something went wrong, rollback
                conn.rollback();
                throw new SQLException("Creating order failed, no ID obtained.");
            }
        } catch (SQLException e) {
            // Something went wrong, rollback if connection exists
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Reset auto-commit to true and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Finds an order by its ID, including all its items
     * 
     * @param id The order ID to search for
     * @return The order if found, null otherwise
     * @throws SQLException If a database error occurs
     */
    public Order findById(int id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    
                    // Load the order items
                    List<OrderItem> items = orderItemDAO.findByOrderId(id);
                    order.setOrderItems(items);
                    
                    return order;
                } else {
                    return null; // Order not found
                }
            }
        }
    }
    
    /**
     * Gets all orders from the database
     * 
     * @return List of all orders
     * @throws SQLException If a database error occurs
     */
    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
        }
        
        // For each order, load its items
        for (Order order : orders) {
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
            order.setOrderItems(items);
        }
        
        return orders;
    }
    
    /**
     * Finds all orders for a specific user
     * 
     * @param userId The user ID to find orders for
     * @return List of orders for the user
     * @throws SQLException If a database error occurs
     */
    public List<Order> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                }
            }
        }
        
        // For each order, load its items
        for (Order order : orders) {
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
            order.setOrderItems(items);
        }
        
        return orders;
    }
    
    /**
     * Updates the status of an order
     * 
     * @param orderId The ID of the order to update
     * @param status The new status value
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET order_status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes an order from the database (including its items through cascade)
     * 
     * @param id The ID of the order to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteOrder(int id) throws SQLException {
        // In a production system, you might not want to delete orders
        // but instead mark them as canceled
        
        String sql = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Assigns a delivery person to an order
     * 
     * @param orderId The ID of the order
     * @param deliveryPersonId The ID of the delivery person
     * @return true if the assignment was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean assignDeliveryPerson(int orderId, int deliveryPersonId) throws SQLException {
        String sql = "UPDATE orders SET assigned_to_user_id = ?, assigned_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, deliveryPersonId);
            stmt.setInt(2, orderId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Marks an order as delivered
     * 
     * @param orderId The ID of the order
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean markOrderDelivered(int orderId) throws SQLException {
        String sql = "UPDATE orders SET order_status = 'DELIVERED', delivered_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Gets all orders assigned to a specific delivery person
     * 
     * @param deliveryPersonId The delivery person ID
     * @return List of orders assigned to the delivery person
     * @throws SQLException If a database error occurs
     */
    public List<Order> findByDeliveryPersonId(int deliveryPersonId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE assigned_to_user_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, deliveryPersonId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                }
            }
        }
        
        // For each order, load its items
        for (Order order : orders) {
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
            order.setOrderItems(items);
        }
        
        return orders;
    }

    // Update mapResultSetToOrder to include new fields
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setContactNumber(rs.getString("contact_number"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        
        // New fields
        int assignedToUserId = rs.getInt("assigned_to_user_id");
        if (!rs.wasNull()) {
            order.setAssignedToUserId(assignedToUserId);
        }
        order.setAssignedAt(rs.getTimestamp("assigned_at"));
        order.setDeliveredAt(rs.getTimestamp("delivered_at"));
        
        return order;
    }

}