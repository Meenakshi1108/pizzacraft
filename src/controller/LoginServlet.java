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
        // Get form data
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        try {
            // Authenticate user
            User user = userService.authenticateUser(username, password);
            
            if (user != null) {
                // Store user in session
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                
                // Check if there was an intended action
                String intendedAction = (String) session.getAttribute("intendedAction");
                if ("addToCart".equals(intendedAction)) {
                    // Clear the intended action
                    session.removeAttribute("intendedAction");
                    
                    // Get intended pizza ID and quantity
                    String pizzaId = (String) session.getAttribute("intendedPizzaId");
                    String quantity = (String) session.getAttribute("intendedQuantity");
                    session.removeAttribute("intendedPizzaId");
                    session.removeAttribute("intendedQuantity");
                    
                    // Redirect to add to cart with the saved parameters
                    String redirect = (String) session.getAttribute("intendedRedirect");
                    session.removeAttribute("intendedRedirect");
                    
                    if (redirect != null) {
                        resp.sendRedirect(redirect);
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/");
                    }
                } else {
                    // Get the redirect parameter if it exists
                    String redirect = req.getParameter("redirect");
                    if (redirect != null && !redirect.isEmpty()) {
                        resp.sendRedirect(redirect);
                    } else {
                        // Otherwise redirect to the home page
                        resp.sendRedirect(req.getContextPath() + "/");
                    }
                }
            } else {
                req.setAttribute("error", "Invalid username or password");
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            }
        } catch (ValidationException | ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
}