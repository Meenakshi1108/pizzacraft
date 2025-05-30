package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet for handling user logout
 */
@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {
    
    /**
     * Handles GET requests - logs out the user and redirects to home
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        setFlashMessage(req, "info", "You have been successfully logged out.");
        resp.sendRedirect(req.getContextPath() + "/");
    }
}