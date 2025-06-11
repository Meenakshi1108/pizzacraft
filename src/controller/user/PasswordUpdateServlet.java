package controller.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.User;
import service.ServiceException;
import service.UserService;
import service.ValidationException;

/**
 * Servlet for handling password changes
 */
@WebServlet("/profile/change-password")
public class PasswordUpdateServlet extends BaseServlet {
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    /**
     * Handles POST requests - processes the change password form submission
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if user is logged in
        if (!requireLogin(req, resp)) {
            return;
        }
        
        // Get the current user from the session
        User user = (User) req.getSession().getAttribute("user");
        
        // Get form data
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        
        try {
            // Validate input
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                throw new ValidationException("Current password is required");
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new ValidationException("New password is required");
            }
            
            if (newPassword.length() < 8) {
                throw new ValidationException("Password must be at least 8 characters long");
            }
            
            if (!newPassword.equals(confirmPassword)) {
                throw new ValidationException("Passwords do not match");
            }
            
            // Update the password
            boolean updated = userService.updatePassword(user.getId(), currentPassword, newPassword);
            
            if (updated) {
                req.setAttribute("success", "Your password has been updated successfully");
            } else {
                req.setAttribute("error", "Failed to update password");
            }
            
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
        } catch (ServiceException e) {
            req.setAttribute("error", "An error occurred: " + e.getMessage());
        }
        
        // Forward back to the profile page
        req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, resp);
    }
}