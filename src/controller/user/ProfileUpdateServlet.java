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
 * Servlet for handling user profile updates
 */
@WebServlet("/profile/update")
public class ProfileUpdateServlet extends BaseServlet {
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    /**
     * Handles GET requests - displays the profile form
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if user is logged in
        if (!requireLogin(req, resp)) {
            return;
        }
        
        // Forward to the profile page
        req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests - processes the profile form submission
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if user is logged in
        if (!requireLogin(req, resp)) {
            return;
        }
        
        // Get the current user from the session
        User currentUser = (User) req.getSession().getAttribute("user");
          // Get form data
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String contactNumber = req.getParameter("contactNumber");
        
        try {
            // Create a User object with the updated information
            User user = new User();
            user.setId(currentUser.getId());
            user.setUsername(currentUser.getUsername()); // Username cannot be changed
            user.setFullName(fullName);
            user.setEmail(email);
            user.setContactNumber(contactNumber);
            user.setRole(currentUser.getRole());
            user.setPassword(currentUser.getPassword()); // Keep the existing password
            
            // Update the profile
            User updatedUser = userService.updateProfile(user);
            
            if (updatedUser != null) {
                // Update the user in the session
                req.getSession().setAttribute("user", updatedUser);
                req.setAttribute("success", "Your profile has been updated successfully");
            } else {
                req.setAttribute("error", "Failed to update profile");
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