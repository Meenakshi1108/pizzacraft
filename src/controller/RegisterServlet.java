package controller;

import java.io.IOException;
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
        // Get form parameters
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        
        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match");
            storeFormData(req);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        
        // Create and populate User object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // This will be hashed in the model
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        
        try {
            // Register the user
            user = userService.registerUser(user);
            
            // Create session and log in the user
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            
            // Set success message and redirect to home
            setFlashMessage(req, "success", "Registration successful! Welcome to Pizza Delivery.");
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (ValidationException e) {
            // If validation failed, show error message
            req.setAttribute("error", e.getMessage());
            storeFormData(req);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (ServiceException e) {
            // Log the error and show a generic message
            getServletContext().log("Error during registration", e);
            req.setAttribute("error", "An error occurred. Please try again later.");
            storeFormData(req);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }
    
    /**
     * Stores the form data in request attributes to redisplay after validation errors
     */
    private void storeFormData(HttpServletRequest req) {
        req.setAttribute("username", req.getParameter("username"));
        req.setAttribute("fullName", req.getParameter("fullName"));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("phone", req.getParameter("phone"));
    }
}