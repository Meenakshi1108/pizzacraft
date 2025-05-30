package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Pizza;
import model.ShoppingCart;
import model.Topping;
import service.PizzaService;
import service.ToppingService;
import service.ServiceException;

/**
 * Servlet for handling the shopping cart
 */
@WebServlet("/cart")
public class CartServlet extends BaseServlet {
    
    private PizzaService pizzaService;
    private ToppingService toppingService;
    
    @Override
    public void init() throws ServletException {
        pizzaService = new PizzaService();
        toppingService = new ToppingService();
    }
    
    /**
     * Handles GET requests - displays the cart
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Get the cart from the session, or create a new one if it doesn't exist
        HttpSession session = req.getSession(true);
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }
        
        req.getRequestDispatcher("/WEB-INF/views/cart/index.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests - adds an item to the cart
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Get the cart from the session, or create a new one if it doesn't exist
            HttpSession session = req.getSession(true);
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
            if (cart == null) {
                cart = new ShoppingCart();
                session.setAttribute("cart", cart);
            }
            
            // Get form parameters
            int pizzaId = Integer.parseInt(req.getParameter("pizzaId"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            
            // Get selected toppings
            String[] toppingIdsStrArray = req.getParameterValues("toppingIds");
            List<Integer> toppingIds = new ArrayList<>();
            if (toppingIdsStrArray != null) {
                for (String toppingIdStr : toppingIdsStrArray) {
                    toppingIds.add(Integer.parseInt(toppingIdStr));
                }
            }
            
            // Get pizza and toppings from the database
            Pizza pizza = pizzaService.getPizzaById(pizzaId);
            List<Topping> allToppings = toppingService.getAllToppings();
            
            // Add to cart
            cart.addItem(pizza, quantity, toppingIds, allToppings);
            
            // Set success message
            setFlashMessage(req, "success", "Added to cart successfully!");
            
            // Redirect back to the pizza detail page or the referring page
            String referer = req.getHeader("referer");
            if (referer != null && !referer.isEmpty()) {
                resp.sendRedirect(referer);
            } else {
                resp.sendRedirect(req.getContextPath() + "/pizza/" + pizzaId);
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/menu");
        } catch (ServiceException e) {
            getServletContext().log("Error adding to cart", e);
            setFlashMessage(req, "error", "An error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/menu");
        }
    }
}