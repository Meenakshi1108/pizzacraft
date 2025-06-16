package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Handles database connections
 */
public class DatabaseConnection {
    
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static boolean initialized = false;
      private static boolean schemaVerified = false;
    
    static {
        initializeConnection();
    }
    
    private static void initializeConnection() {
        try {
            // Load configuration properties
            Properties props = new Properties();
            boolean configLoaded = false;
            
            System.out.println("DEBUG: Initializing database connection");
            
            // Try to load from classpath first
            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
                if (is != null) {
                    props.load(is);
                    configLoaded = true;
                    System.out.println("Loaded database config from classpath");
                }
            } catch (IOException e) {
                System.out.println("Could not load config from classpath: " + e.getMessage());
            }
            
            // Try from WEB-INF/classes
            if (!configLoaded) {
                String catalinaBase = System.getProperty("catalina.base");
                if (catalinaBase != null) {
                    String configPath = catalinaBase + "/webapps/PizzaDeliverySystem/WEB-INF/classes/config.properties";
                    try (FileInputStream fis = new FileInputStream(configPath)) {
                        props.load(fis);
                        configLoaded = true;
                        System.out.println("Loaded database config from: " + configPath);
                    } catch (IOException e) {
                        System.out.println("Could not load config from Tomcat: " + e.getMessage());
                    }
                }
            }
            
            // Last resort: try from config directory
            if (!configLoaded) {
                try (FileInputStream fis = new FileInputStream("config/config.properties")) {
                    props.load(fis);
                    configLoaded = true;
                    System.out.println("Loaded database config from config directory");
                } catch (IOException e) {
                    System.out.println("Could not load config from config directory: " + e.getMessage());
                }
            }
            
            if (!configLoaded) {
                throw new RuntimeException("Could not load database configuration from any location");
            }
            
            // Load JDBC driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC driver loaded successfully");
            
            // Set connection properties
            URL = props.getProperty("db.url") + 
                  "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
            
            initialized = true;
            
            // Test connection
            try (Connection testConn = getConnection()) {
                System.out.println("Database connection test successful");
            }
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Gets a database connection
     * 
     * @return A database connection
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initializeConnection();
        }
        
        // If schema has not been verified yet, verify it
        if (!schemaVerified) {
            try {
                verifyDatabaseSchema();
                schemaVerified = true;
            } catch (Exception e) {
                System.err.println("Warning: Could not verify database schema: " + e.getMessage());
                e.printStackTrace();
                // Continue without failing - we'll let the application try to run
            }
        }
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * Verifies and fixes the database schema if needed
     */
    private static void verifyDatabaseSchema() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Check if the orders table has the delivery_person_id column
            boolean needsSchemaFix = false;
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM information_schema.columns " +
                     "WHERE table_schema = 'pizza_delivery' AND " +
                     "table_name = 'orders' AND column_name = 'delivery_person_id'")) {
                
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        needsSchemaFix = true;
                    }
                }
            }
            
            if (needsSchemaFix) {
                System.out.println("Database schema needs to be fixed. Applying schema updates...");
                
                // Execute the add column statement
                try (java.sql.Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivery_person_id INT NULL");
                    stmt.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS assigned_to_user_id INT NULL");
                    stmt.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS assigned_at TIMESTAMP NULL");
                    stmt.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivered_at TIMESTAMP NULL");
                    
                    // Add the foreign key constraints if possible
                    try {
                        stmt.execute("ALTER TABLE orders ADD CONSTRAINT fk_orders_delivery_person " +
                                    "FOREIGN KEY (delivery_person_id) REFERENCES users(id)");
                    } catch (SQLException e) {
                        System.out.println("Note: Could not add foreign key for delivery_person_id: " + e.getMessage());
                    }
                    
                    try {
                        stmt.execute("ALTER TABLE orders ADD CONSTRAINT fk_orders_assigned_user " +
                                    "FOREIGN KEY (assigned_to_user_id) REFERENCES users(id)");
                    } catch (SQLException e) {
                        System.out.println("Note: Could not add foreign key for assigned_to_user_id: " + e.getMessage());
                    }
                    
                    System.out.println("Database schema updated successfully");
                }
            } else {
                System.out.println("Database schema is up to date");
            }
        } catch (SQLException e) {
            System.err.println("Error verifying database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shuts down the database connections
     */
    public static void shutdown() {
        // Code to close any connection pools or lingering connections
        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            // Log exception
        }
    }
}