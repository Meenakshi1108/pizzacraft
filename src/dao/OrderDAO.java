package dao;

import java.math.BigDecimal;
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
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert order
            String sql = "INSERT INTO orders (user_id, delivery_address, contact_number, total_amount, order_status) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUserId());
            stmt.setString(2, order.getDeliveryAddress());
            stmt.setString(3, order.getContactNumber());
            stmt.setBigDecimal(4, order.getTotalAmount());
            stmt.setString(5, order.getOrderStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getInt(1));
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }
            
            // Insert order items
            for (OrderItem item : order.getOrderItems()) {
                item.setOrderId(order.getId());
                orderItemDAO.createOrderItem(conn, item);
            }
            
            conn.commit(); // Commit transaction
            return order;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            closeResources(conn, stmt, rs);
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
     */    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                } catch (Exception e) {
                    System.err.println("Error mapping order from ResultSet: " + e.getMessage());
                    e.printStackTrace();
                    // Continue to next order instead of breaking the whole list
                }
            }
        }
        
        // For each order, load its items
        for (Order order : orders) {
            try {
                List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
                order.setOrderItems(items);
            } catch (Exception e) {
                System.err.println("Error loading items for order #" + order.getId() + ": " + e.getMessage());
                e.printStackTrace();
                // Continue with empty items list rather than failing
                order.setOrderItems(new ArrayList<>());
            }
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
    }    /**
     * Assigns a delivery person to an order
     * 
     * @param orderId The ID of the order
     * @param deliveryPersonId The ID of the delivery person
     * @return true if the assignment was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean assignDeliveryPerson(int orderId, int deliveryPersonId) throws SQLException {
        String sql = "UPDATE orders SET assigned_to_user_id = ?, delivery_person_id = ?, assigned_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, deliveryPersonId);
            stmt.setInt(2, deliveryPersonId);
            stmt.setInt(3, orderId);
            
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
    }    /**
     * Updates an order
     * 
     * @param order The order to update
     * @return true if successful, false otherwise
     * @throws SQLException If a database error occurs
     */    public boolean updateOrder(Order order) throws SQLException {
        try {
            // First try with both columns
            String sql = "UPDATE orders SET user_id = ?, delivery_address = ?, contact_number = ?, " +
                         "total_amount = ?, order_status = ?, delivery_person_id = ?, assigned_to_user_id = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, order.getUserId());
                stmt.setString(2, order.getDeliveryAddress());
                stmt.setString(3, order.getContactNumber());
                stmt.setBigDecimal(4, order.getTotalAmount());
                stmt.setString(5, order.getOrderStatus());
                
                // Set both delivery_person_id and assigned_to_user_id to ensure consistency
                if (order.getDeliveryPersonId() != null) {
                    stmt.setInt(6, order.getDeliveryPersonId());
                } else {
                    stmt.setNull(6, java.sql.Types.INTEGER);
                }
                
                if (order.getAssignedToUserId() != null) {
                    stmt.setInt(7, order.getAssignedToUserId());
                } else {
                    stmt.setNull(7, java.sql.Types.INTEGER);
                }
                
                stmt.setInt(8, order.getId());
                
                int affectedRows = stmt.executeUpdate();
                System.out.println("DEBUG: Updated order with both columns, affected rows: " + affectedRows);
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            // If the first attempt fails, try without delivery_person_id column
            System.err.println("DEBUG: First update attempt failed: " + e.getMessage());
            System.err.println("DEBUG: Trying alternative update without delivery_person_id");
            
            String fallbackSql = "UPDATE orders SET user_id = ?, delivery_address = ?, contact_number = ?, " +
                               "total_amount = ?, order_status = ?, assigned_to_user_id = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {
                
                stmt.setInt(1, order.getUserId());
                stmt.setString(2, order.getDeliveryAddress());
                stmt.setString(3, order.getContactNumber());
                stmt.setBigDecimal(4, order.getTotalAmount());
                stmt.setString(5, order.getOrderStatus());
                
                if (order.getAssignedToUserId() != null) {
                    stmt.setInt(6, order.getAssignedToUserId());
                } else {
                    stmt.setNull(6, java.sql.Types.INTEGER);
                }
                
                stmt.setInt(7, order.getId());
                
                int affectedRows = stmt.executeUpdate();
                System.out.println("DEBUG: Updated order with fallback SQL, affected rows: " + affectedRows);
                return affectedRows > 0;
            }
        }
    }

    /**
     * Finds the most recent order for a user
     * 
     * @param userId The user ID
     * @return The most recent order, or null if none exists
     * @throws SQLException If a database error occurs
     */
    public Order findMostRecent(int userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                } else {
                    return null;
                }
            }
        }
    }
    
    /**
     * Finds the most recent orders across all users
     * 
     * @param limit Maximum number of orders to return
     * @return List of the most recent orders, limited to the specified count
     * @throws SQLException If a database error occurs
     */
    public List<Order> findRecentOrders(int limit) throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC LIMIT ?";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
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
     * Counts all orders
     * 
     * @return The total count of orders
     * @throws SQLException If a database error occurs
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }
    
    /**
     * Counts orders by status
     * 
     * @param status The status to count
     * @return The count of orders with the specified status
     * @throws SQLException If a database error occurs
     */
    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        }
    }
    
    /**
     * Calculates the total revenue from all orders
     * 
     * @return The total revenue
     * @throws SQLException If a database error occurs
     */
    public BigDecimal calculateTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE order_status != 'CANCELLED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            } else {
                return BigDecimal.ZERO;
            }
        }
    }
    
    /**
     * Calculates the revenue for today
     * 
     * @return The revenue for today
     * @throws SQLException If a database error occurs
     */
    public BigDecimal calculateTodayRevenue() throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE order_status != 'CANCELLED' " +
                     "AND DATE(created_at) = CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            } else {
                return BigDecimal.ZERO;
            }
        }
    }
    
    /**
     * Calculates the revenue for the current week
     * 
     * @return The revenue for the current week
     * @throws SQLException If a database error occurs
     */
    public BigDecimal calculateWeeklyRevenue() throws SQLException {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM orders " +
                       "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) " +
                       "AND created_at < DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 7 DAY) " +
                       "AND order_status != 'CANCELLED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }    // Update mapResultSetToOrder to include new fields - FIXED VERSION with error handling
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setContactNumber(rs.getString("contact_number"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Safely handle optional fields that might not exist in all database versions
        try {
            int assignedToUserId = rs.getInt("assigned_to_user_id");
            if (!rs.wasNull()) {
                order.setAssignedToUserId(assignedToUserId);
            }
        } catch (SQLException ignored) {
            // Column might not exist, just continue
        }
        
        try {
            order.setAssignedAt(rs.getTimestamp("assigned_at"));
        } catch (SQLException ignored) {
            // Column might not exist, just continue
        }
        
        try {
            order.setDeliveredAt(rs.getTimestamp("delivered_at"));
        } catch (SQLException ignored) {
            // Column might not exist, just continue
        }
          // Add handling for delivery_person_id column
        try {
            int deliveryPersonId = rs.getInt("delivery_person_id");
            if (!rs.wasNull()) {
                order.setDeliveryPersonId(deliveryPersonId);
            }
        } catch (SQLException ignored) {
            // Column might not exist, try using assigned_to_user_id instead
            if (order.getAssignedToUserId() != null) {
                order.setDeliveryPersonId(order.getAssignedToUserId());
                System.out.println("DEBUG: Using assignedToUserId as deliveryPersonId for order " + order.getId());
            }
        }
        
        return order;
    }
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

    /**
     * Count active deliveries assigned to a delivery person
     * 
     * @param deliveryPersonId The ID of the delivery person
     * @return The number of active deliveries assigned to the person
     * @throws SQLException If a database error occurs
     */
    public int countActiveDeliveriesByPerson(int deliveryPersonId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE " +
                     "(delivery_person_id = ? OR assigned_to_user_id = ?) AND " +
                     "order_status IN ('PLACED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, deliveryPersonId);
            stmt.setInt(2, deliveryPersonId); // Try both columns
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Error counting active deliveries: " + e.getMessage());
            return 0; // Safe default on error
        }
    }
}