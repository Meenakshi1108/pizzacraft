package controller.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;

/**
 * Servlet for displaying user profile
 */
@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {
    
    /**
     * Handles GET requests - displays the profile page
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
}
