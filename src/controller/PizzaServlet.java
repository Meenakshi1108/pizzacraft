package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Pizza;
import model.Topping;
import service.PizzaService;
import service.ToppingService;
import service.ServiceException;

/**
 * Servlet for displaying pizza details
 */
@WebServlet("/pizza/*")
public class PizzaServlet extends BaseServlet {
    
    private PizzaService pizzaService;
    private ToppingService toppingService;
    
    @Override
    public void init() throws ServletException {
        pizzaService = new PizzaService();
        toppingService = new ToppingService();
    }
    
    /**
     * Handles GET requests - displays the details of a specific pizza
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Extract the pizza ID from the URL path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendRedirect(req.getContextPath() + "/menu");
                return;
            }
            
            int pizzaId;
            try {
                pizzaId = Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/menu");
                return;
            }
            
            // Get the pizza details
            Pizza pizza = pizzaService.getPizzaById(pizzaId);
            if (pizza == null) {
                // Pizza not found
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Get toppings for customization
            List<Topping> toppings = toppingService.getAllToppings();
            
            // Set attributes and forward to the view
            req.setAttribute("pizza", pizza);
            req.setAttribute("toppings", toppings);
            req.getRequestDispatcher("/WEB-INF/views/menu/pizza-detail.jsp").forward(req, resp);
        } catch (ServiceException e) {
            // Log the error and show an error page
            getServletContext().log("Error displaying pizza details", e);
            req.setAttribute("error", "An error occurred while loading the pizza details.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}