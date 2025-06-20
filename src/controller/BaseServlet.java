package controller;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;

/**
 * Base servlet class with common functionality for all servlets
 */
public abstract class BaseServlet extends HttpServlet {
    
    /**
     * Checks if a user is logged in
     * 
     * @param req The HTTP request
     * @return true if a user is logged in, false otherwise
     */
    protected boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
    
    /**
     * Gets the currently logged in user
     * 
     * @param req The HTTP request
     * @return The logged in user, or null if not logged in
     */
    protected User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
    /**
     * Checks if the logged in user has the specified role
     * 
     * @param req The HTTP request
     * @param role The role to check for
     * @return true if the user has the role, false otherwise
     */
    protected boolean hasRole(HttpServletRequest req, String role) {
        User user = getLoggedInUser(req);
        return user != null && user.getRole().equals(role);
    }
    
    /**
     * Redirects to the login page if the user is not logged in
     * 
     * @param req The HTTP request
     * @param resp The HTTP response
     * @return true if the user is logged in, false if redirection occurred
     * @throws IOException If an I/O error occurs
     */
    protected boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        if (req.getSession().getAttribute("user") == null) {
            // Store original URL, but don't create infinite loops
            String originalUrl = req.getRequestURI();
            // Don't store login page as the redirect URL
            if (!originalUrl.contains("/login")) {
                req.getSession().setAttribute("redirectUrl", originalUrl);
            }
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }
    
    /**
     * Redirects to the access denied page if the user doesn't have the required role
     * 
     * @param req The HTTP request
     * @param resp The HTTP response
     * @param role The required role
     * @return true if the user has the role, false if redirection occurred
     * @throws IOException If an I/O error occurs
     */    protected boolean requireRole(HttpServletRequest req, HttpServletResponse resp, String role) throws IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            setFlashMessage(req, "error", "You must be logged in to access this page.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        
        String userRole = user.getRole();
        
        // Handle multiple possible valid roles - e.g., both DELIVERY and DELIVERY_PERSON
        if (role.equals(User.ROLE_ADMIN) && userRole.equals(User.ROLE_ADMIN)) {
            return true;
        } else if (role.equals(User.ROLE_DELIVERY) && 
                  (userRole.equals(User.ROLE_DELIVERY) || userRole.equals(User.ROLE_DELIVERY_PERSON))) {
            return true;
        } else if (role.equals(User.ROLE_CUSTOMER) && userRole.equals(User.ROLE_CUSTOMER)) {
            return true;
        } else {
            // Store a message explaining why access was denied
            setFlashMessage(req, "error", "You do not have permission to access this page. Required role: " + role + ", your role: " + userRole);
            resp.sendRedirect(req.getContextPath() + "/access-denied");
            return false;
        }
    }
    
    /**
     * Sets a flash message to be displayed on the next page
     * 
     * @param req The HTTP request
     * @param type The message type (success, error, info, warning)
     * @param message The message text
     */
    protected void setFlashMessage(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMessage", message);
    }
}