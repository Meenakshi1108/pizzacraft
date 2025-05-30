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
 * Servlet for handling user login
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    /**
     * Handles GET requests - displays the login form
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // If already logged in, redirect to home
        if (isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        
        // Forward to the login page
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests - processes the login form submission
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Get form parameters
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        try {
            // Attempt to authenticate the user
            User user = userService.authenticateUser(username, password);
            
            // If authentication succeeded, create a session and store the user
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            
            // Check if there's a redirect URL from a previous request
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                resp.sendRedirect(redirectUrl);
            } else {
                // Otherwise redirect to the home page
                resp.sendRedirect(req.getContextPath() + "/");
            }
        } catch (ValidationException e) {
            // If login failed, show error message
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (ServiceException e) {
            // Log the error and show a generic message
            getServletContext().log("Error during login", e);
            req.setAttribute("error", "An error occurred. Please try again later.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
}