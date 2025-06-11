package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.DatabaseConnection;
import model.Order;
import model.User;

/**
 * Utility class for handling notifications throughout the system
 */
public class NotificationUtil {
    
    // In-memory storage for demo purposes (in a real app, would use WebSockets/database)
    private static final Map<Integer, Map<String, Object>> userNotifications = new HashMap<>();
    
    /**
     * Sends a notification about an order status update
     * 
     * @param order The order that was updated
     * @param messageType The type of message (success, warning, info)
     * @param message The notification message
     */
    public static void sendOrderNotification(Order order, String messageType, String message) {
        if (order == null) return;
        
        try {
            // Store notification for customer
            addNotification(order.getUserId(), messageType, message);
            
            // Store notification for admin users
            notifyAdmins("Order #" + order.getId() + ": " + message);
            
            // Log the notification in database for audit purposes
            logNotification(order.getId(), message);
            
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
        }
    }
    
    /**
     * Adds a notification for a specific user
     * 
     * @param userId The user ID to notify
     * @param messageType The type of message (success, warning, info)
     * @param message The notification message
     */
    public static void addNotification(int userId, String messageType, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("type", messageType);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        
        userNotifications.put(userId, notification);
    }
    
    /**
     * Gets notifications for a user
     * 
     * @param userId The user ID
     * @return Map of notification data
     */
    public static Map<String, Object> getUserNotification(int userId) {
        return userNotifications.get(userId);
    }
    
    /**
     * Marks a notification as read
     * 
     * @param userId The user ID
     */
    public static void markAsRead(int userId) {
        Map<String, Object> notification = userNotifications.get(userId);
        if (notification != null) {
            notification.put("read", true);
            userNotifications.put(userId, notification);
        }
    }
    
    /**
     * Notifies all admin users
     * 
     * @param message The message to send to admins
     */
    private static void notifyAdmins(String message) {
        // In a real application, you would fetch admin users from the database
        // and send them notifications via WebSockets or store in database
        System.out.println("ADMIN NOTIFICATION: " + message);
    }
    
    /**
     * Logs a notification in the database
     * 
     * @param orderId The order ID
     * @param message The notification message
     */
    private static void logNotification(int orderId, String message) throws SQLException {
        String sql = "INSERT INTO notifications (order_id, message, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        
        // Skip if the notifications table doesn't exist yet
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            stmt.setString(2, message);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Silently ignore if table doesn't exist, but log other errors
            if (!e.getMessage().contains("notifications")) {
                throw e;
            }
        }
    }
}
