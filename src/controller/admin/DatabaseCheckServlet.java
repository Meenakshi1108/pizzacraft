package controller.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import database.DatabaseConnection;

/**
 * Servlet for checking database schema and table information
 */
@WebServlet("/admin/db-check")
public class DatabaseCheckServlet extends BaseServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Database Schema Check</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("h2 { margin-top: 30px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Database Schema Check</h1>");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get database metadata
            DatabaseMetaData dbmd = conn.getMetaData();
            
            // Output database information
            out.println("<h2>Database Information</h2>");
            out.println("<table>");
            out.println("<tr><th>Property</th><th>Value</th></tr>");
            out.println("<tr><td>Database Product Name</td><td>" + dbmd.getDatabaseProductName() + "</td></tr>");
            out.println("<tr><td>Database Product Version</td><td>" + dbmd.getDatabaseProductVersion() + "</td></tr>");
            out.println("<tr><td>Driver Name</td><td>" + dbmd.getDriverName() + "</td></tr>");
            out.println("<tr><td>Driver Version</td><td>" + dbmd.getDriverVersion() + "</td></tr>");
            out.println("<tr><td>URL</td><td>" + dbmd.getURL() + "</td></tr>");
            out.println("<tr><td>Username</td><td>" + dbmd.getUserName() + "</td></tr>");
            out.println("</table>");
            
            // Get tables list
            out.println("<h2>Tables</h2>");
            out.println("<table>");
            out.println("<tr><th>Table Name</th><th>Type</th></tr>");
            
            try (ResultSet tables = dbmd.getTables(conn.getCatalog(), null, "%", new String[] {"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableType = tables.getString("TABLE_TYPE");
                    out.println("<tr><td>" + tableName + "</td><td>" + tableType + "</td></tr>");
                }
            }
            
            out.println("</table>");
            
            // Check orders table structure
            out.println("<h2>Orders Table Structure</h2>");
            try (ResultSet columns = dbmd.getColumns(conn.getCatalog(), null, "orders", null)) {
                out.println("<table>");
                out.println("<tr><th>Column Name</th><th>Type</th><th>Size</th><th>Nullable</th></tr>");
                
                while (columns.next()) {
                    String name = columns.getString("COLUMN_NAME");
                    String type = columns.getString("TYPE_NAME");
                    int size = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getString("IS_NULLABLE");
                    
                    out.println("<tr><td>" + name + "</td><td>" + type + "</td><td>" + size + "</td><td>" + nullable + "</td></tr>");
                }
                
                out.println("</table>");
            }
            
            // Sample orders data
            out.println("<h2>Sample Orders Data</h2>");
            try (Statement stmt = conn.createStatement();
                 ResultSet orders = stmt.executeQuery("SELECT * FROM orders LIMIT 10")) {
                
                ResultSetMetaData rsmd = orders.getMetaData();
                int columnsCount = rsmd.getColumnCount();
                
                out.println("<table>");
                
                // Table header
                out.println("<tr>");
                for (int i = 1; i <= columnsCount; i++) {
                    out.println("<th>" + rsmd.getColumnName(i) + "</th>");
                }
                out.println("</tr>");
                
                // Table data
                while (orders.next()) {
                    out.println("<tr>");
                    for (int i = 1; i <= columnsCount; i++) {
                        out.println("<td>" + orders.getString(i) + "</td>");
                    }
                    out.println("</tr>");
                }
                
                out.println("</table>");
            }
            
            // Check users table sample data
            out.println("<h2>Sample Users Data</h2>");
            try (Statement stmt = conn.createStatement();
                 ResultSet users = stmt.executeQuery("SELECT id, username, full_name, role FROM users LIMIT 10")) {
                
                ResultSetMetaData rsmd = users.getMetaData();
                int columnsCount = rsmd.getColumnCount();
                
                out.println("<table>");
                
                // Table header
                out.println("<tr>");
                for (int i = 1; i <= columnsCount; i++) {
                    out.println("<th>" + rsmd.getColumnName(i) + "</th>");
                }
                out.println("</tr>");
                
                // Table data
                while (users.next()) {
                    out.println("<tr>");
                    for (int i = 1; i <= columnsCount; i++) {
                        out.println("<td>" + users.getString(i) + "</td>");
                    }
                    out.println("</tr>");
                }
                
                out.println("</table>");
            }
            
        } catch (Exception e) {
            out.println("<h2>Error</h2>");
            out.println("<p style='color: red;'>" + e.getMessage() + "</p>");
            
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("<p><a href='" + req.getContextPath() + "/admin/diagnostic'>Go to System Diagnostic</a></p>");
        out.println("<p><a href='" + req.getContextPath() + "/admin/orders'>Back to Orders</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
