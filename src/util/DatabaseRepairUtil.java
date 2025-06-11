package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.DatabaseConnection;

/**
 * Utility class to repair common database issues
 */
public class DatabaseRepairUtil {
    
    /**
     * Main method for running database repair directly
     */
    public static void main(String[] args) {
        try {
            System.out.println("Starting database repair check...");
            boolean repaired = repairDatabaseSchema();
            if (repaired) {
                System.out.println("Database schema was updated successfully");
            } else {
                System.out.println("No database repairs were needed");
            }
        } catch (Exception e) {
            System.err.println("Error repairing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks and repairs the database schema if needed
     * 
     * @return true if repairs were made, false otherwise
     */
    public static boolean repairDatabaseSchema() throws SQLException {
        boolean repairsMade = false;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Check if orders table exists
            boolean ordersTableExists = false;
            try (ResultSet tables = metaData.getTables(null, null, "orders", null)) {
                ordersTableExists = tables.next();
            }
            
            if (!ordersTableExists) {
                System.out.println("Orders table does not exist. Creating schema...");
                createFullSchema(conn);
                return true;
            }
            
            // Check for and add missing columns
            repairsMade |= addColumnIfNeeded(conn, "orders", "delivery_person_id", "INT NULL");
            repairsMade |= addColumnIfNeeded(conn, "orders", "assigned_to_user_id", "INT NULL");
            repairsMade |= addColumnIfNeeded(conn, "orders", "assigned_at", "TIMESTAMP NULL");
            repairsMade |= addColumnIfNeeded(conn, "orders", "delivered_at", "TIMESTAMP NULL");
            
            // Check users table for delivery users
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM users WHERE role = 'DELIVERY' OR role = 'DELIVERY_PERSON'")) {
                
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("No delivery users found. Creating sample users...");
                    createSampleUsers(conn);
                    repairsMade = true;
                }
            }
        }
        
        return repairsMade;
    }
    
    /**
     * Adds a column to a table if it doesn't exist
     * 
     * @param conn The database connection
     * @param tableName The name of the table
     * @param columnName The name of the column to add
     * @param columnDefinition The SQL definition of the column
     * @return true if the column was added, false if it already existed
     */
    private static boolean addColumnIfNeeded(Connection conn, String tableName, 
                                            String columnName, String columnDefinition) throws SQLException {
        boolean columnExists = false;
        
        try (ResultSet columns = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            columnExists = columns.next();
        }
        
        if (!columnExists) {
            System.out.println("Adding missing column: " + tableName + "." + columnName);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Creates the full database schema
     * 
     * @param conn The database connection
     */
    private static void createFullSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Create tables
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50) NOT NULL UNIQUE," +
                "  password VARCHAR(100) NOT NULL," +
                "  full_name VARCHAR(100) NOT NULL," +
                "  email VARCHAR(100) NOT NULL UNIQUE," +
                "  phone VARCHAR(15)," +
                "  role VARCHAR(20) NOT NULL," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS orders (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  user_id INT," +
                "  total_amount DECIMAL(10,2) NOT NULL," +
                "  order_status VARCHAR(20) NOT NULL," +
                "  delivery_address TEXT NOT NULL," +
                "  contact_number VARCHAR(15) NOT NULL," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  delivery_person_id INT NULL," +
                "  assigned_to_user_id INT NULL," +
                "  assigned_at TIMESTAMP NULL," +
                "  delivered_at TIMESTAMP NULL," +
                "  FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")"
            );
            
            // Create sample users
            createSampleUsers(conn);
        }
    }
    
    /**
     * Creates sample users including admin and delivery persons
     * 
     * @param conn The database connection
     */
    private static void createSampleUsers(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Insert sample users - all with password 'password123'
            stmt.executeUpdate(
                "INSERT IGNORE INTO users (username, password, full_name, email, phone, role) VALUES" +
                "('admin', '$2a$10$v8.k0R4wNTHVJyp4eTIO5.H612cFO5J3KQ30NkZt/C0jRRXDVl0PS', 'System Admin', 'admin@example.com', '9876543210', 'ADMIN')," +
                "('delivery1', '$2a$10$v8.k0R4wNTHVJyp4eTIO5.H612cFO5J3KQ30NkZt/C0jRRXDVl0PS', 'Delivery Staff 1', 'delivery1@example.com', '8765432109', 'DELIVERY')," +
                "('delivery2', '$2a$10$v8.k0R4wNTHVJyp4eTIO5.H612cFO5J3KQ30NkZt/C0jRRXDVl0PS', 'Delivery Staff 2', 'delivery2@example.com', '7654321098', 'DELIVERY_PERSON')"
            );
        }
    }
}
