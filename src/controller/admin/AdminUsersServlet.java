package controller.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.User;
import service.ServiceException;
import service.UserService;
import service.ValidationException;

@WebServlet("/admin/users")
public class AdminUsersServlet extends BaseServlet {
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            List<User> users = userService.getAllUsers();
            req.setAttribute("users", users);
            req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting users for admin", e);
            req.setAttribute("error", "An error occurred while retrieving users.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        String action = req.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                // Create a new user
                User newUser = new User();
                newUser.setUsername(req.getParameter("username"));
                newUser.setPassword(req.getParameter("password")); // This will be hashed in UserService
                newUser.setFullName(req.getParameter("fullName"));
                newUser.setEmail(req.getParameter("email"));
                newUser.setPhone(req.getParameter("phone"));
                newUser.setRole(req.getParameter("role"));
                
                userService.registerUser(newUser);
                setFlashMessage(req, "success", "User created successfully");
                
            } else if ("update".equals(action)) {
                // Update an existing user
                int userId = Integer.parseInt(req.getParameter("id"));
                User existingUser = userService.getUserById(userId);
                
                if (existingUser != null) {
                    existingUser.setFullName(req.getParameter("fullName"));
                    existingUser.setEmail(req.getParameter("email"));
                    existingUser.setPhone(req.getParameter("phone"));
                    existingUser.setRole(req.getParameter("role"));
                    
                    userService.updateUser(existingUser);
                    setFlashMessage(req, "success", "User updated successfully");
                } else {
                    setFlashMessage(req, "error", "User not found");
                }
                
            } else if ("delete".equals(action)) {
                // Delete a user
                int userId = Integer.parseInt(req.getParameter("id"));
                userService.deleteUser(userId);
                setFlashMessage(req, "success", "User deleted successfully");
            }
            
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
        } catch (ServiceException e) {
            getServletContext().log("Error managing users", e);
            setFlashMessage(req, "error", "An error occurred while processing your request.");
        } catch (NumberFormatException e) {
            setFlashMessage(req, "error", "Invalid user ID");
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}