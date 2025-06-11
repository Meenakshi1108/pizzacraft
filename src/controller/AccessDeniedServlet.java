package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for displaying the access denied page
 */
@WebServlet("/access-denied")
public class AccessDeniedServlet extends BaseServlet {
    
    /**
     * Handles GET requests - displays the access denied page
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Set HTTP status code to 403 Forbidden
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        // Forward to access denied JSP
        req.getRequestDispatcher("/WEB-INF/views/access-denied.jsp").forward(req, resp);
    }
}
