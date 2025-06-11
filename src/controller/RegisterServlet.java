package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;
import service.UserService;
import service.ValidationException;
import service.ServiceException;

/**
 * Servlet for handling user registration
 */
@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    /**
     * Handles GET requests - displays the registration form
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // If already logged in, redirect to home
        if (isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        
        // Forward to the registration page
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests - processes the registration form submission
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Get form data
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String contactNumber = req.getParameter("contactNumber");
        
        // Store form data for redisplay
        storeFormData(req);
        
        // Validate form data
        if (username == null || username.trim().isEmpty()) {
            req.setAttribute("error", "Username is required");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Email is required");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Password is required");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        // NEW VALIDATION: Check password length
        if (password.length() < 8) {
            req.setAttribute("error", "Password must be at least 8 characters long");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            req.setAttribute("error", "Contact number is required");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        try {
            // Create user object
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setContactNumber(contactNumber);
            user.setRole("CUSTOMER");
            
            // Register the user
            User registeredUser = userService.registerUser(user);
            
            if (registeredUser != null) {
                // Set success message and redirect to login
                req.getSession().setAttribute("success", "Registration successful! You can now log in.");
                resp.sendRedirect(req.getContextPath() + "/login");
            } else {
                req.setAttribute("error", "Registration failed. Please try again.");
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            }
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (ServiceException e) {
            // Enhanced error handling for duplicate user information
            String errorMessage = e.getMessage();
            
            if (e.getCause() != null && e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                String sqlMessage = sqlEx.getMessage().toLowerCase();
                
                // Check for common duplicate key error patterns
                if (sqlMessage.contains("duplicate") || sqlMessage.contains("unique constraint")) {
                    if (sqlMessage.contains("username") || 
                        sqlMessage.contains("user_username_key") || 
                        sqlMessage.contains("users.username")) {
                        errorMessage = "Username '" + username + "' is already taken";
                    } else if (sqlMessage.contains("email") || 
                               sqlMessage.contains("user_email_key") || 
                               sqlMessage.contains("users.email")) {
                        errorMessage = "Email '" + email + "' is already in use";
                    } else {
                        errorMessage = "A user with this information already exists";
                    }
                }
            }
            
            req.setAttribute("error", errorMessage);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }
    
    /**
     * Stores the form data in request attributes to redisplay after validation errors
     */
    private void storeFormData(HttpServletRequest req) {
        req.setAttribute("username", req.getParameter("username"));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("contactNumber", req.getParameter("contactNumber"));
    }
}