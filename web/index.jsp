<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Correct path with absolute reference -->
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Welcome to PizzaCraft" />
</jsp:include>

<!-- Hero Section -->
<section class="hero-section">
    <div class="hero-overlay"></div>
    <div class="container d-flex align-items-center justify-content-center h-100">
        <div class="hero-content text-center">
            <h1 class="hero-title display-4 fw-bold text-white mb-3">Authentic Indian Flavors,<br>Handcrafted Pizzas</h1>
            <p class="hero-subtitle lead text-white mb-4">Experience the perfect blend of traditional Italian bases with rich Indian spices</p>
            <a href="${pageContext.request.contextPath}/menu" class="btn btn-lg btn-danger pulse-animation">
                <i class="fas fa-pizza-slice me-2"></i> Explore Our Menu
            </a>
        </div>
    </div>
</section>

<!-- Feature Cards with Textured Background -->
<div class="features-section py-5 bg-light">
    <div class="container">
        <div class="text-center mb-5">            <h2 class="section-title">Why Choose PizzaCraft?</h2>
            <p class="section-subtitle text-muted">What makes our pizzas special</p>
        </div>
        <div class="row g-4">
            <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="feature-icon mb-3">
                            <i class="fas fa-fire-alt fa-3x text-danger"></i>
                        </div>
                        <h4 class="card-title">Fresh Ingredients</h4>
                        <p class="card-text">We use only the freshest ingredients sourced locally to create our delicious pizzas.</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="feature-icon mb-3">
                            <i class="fas fa-truck fa-3x text-primary"></i>
                        </div>
                        <h4 class="card-title">Fast Delivery</h4>
                        <p class="card-text">Our dedicated delivery team ensures your pizza arrives hot and fresh within 30 minutes.</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="feature-icon mb-3">
                            <i class="fas fa-leaf fa-3x text-success"></i>
                        </div>
                        <h4 class="card-title">Vegetarian Options</h4>
                        <p class="card-text">We offer a wide range of vegetarian pizzas with authentic Indian flavors.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Popular Pizzas Section -->
<div class="popular-section py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="section-title">Our Chef's Specials</h2>
            <p class="section-subtitle text-muted">Try our most popular creations</p>
        </div>
        <div class="row row-cols-1 row-cols-md-3 g-4">
            <div class="col">
                <div class="card h-100 shadow-sm">
                    <img src="${pageContext.request.contextPath}/images/tandooripanner.jpg" class="card-img-top" alt="Tandoori Paneer Pizza">
                    <div class="card-body">
                        <h5 class="card-title">Paneer Special</h5>
                        <p class="card-text">Fresh paneer, capsicum, onions with spicy sauce</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="price">₹299</span>
                            <a href="${pageContext.request.contextPath}/pizza/7" class="btn btn-sm btn-outline-primary">View Details</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 shadow-sm">
                    <img src="${pageContext.request.contextPath}/images/chicken-tikka.jpg" class="card-img-top" alt="Chicken Tikka Pizza">
                    <div class="card-body">
                        <h5 class="card-title">Chicken Tikka</h5>
                        <p class="card-text">Spicy chicken tikka chunks with bell peppers and onions.</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="price">₹349</span>
                            <a href="${pageContext.request.contextPath}/pizza/14" class="btn btn-sm btn-outline-primary">View Details</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 shadow-sm">
                    <img src="${pageContext.request.contextPath}/images/cheeseburst.jpeg" class="card-img-top" alt="Cheese Burst Pizza">
                    <div class="card-body">
                        <h5 class="card-title">Cheese Burst</h5>
                        <p class="card-text">Loaded with extra cheese inside the crust for the ultimate cheese lover.</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="price">₹399</span>
                            <a href="${pageContext.request.contextPath}/pizza/19" class="btn btn-sm btn-outline-primary">View Details</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/menu" class="btn btn-lg btn-primary">
                <i class="fas fa-utensils me-2"></i> View Full Menu
            </a>
        </div>
    </div>
</div>

<!-- Call to Action Section -->
<div class="cta-section py-5 bg-dark text-white">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-lg-9">
                <h3 class="mb-3 mb-lg-0">Ready to taste the best pizza in town?</h3>
                <p class="lead mb-0 d-none d-lg-block">Order now and get it delivered to your doorstep!</p>
            </div>
            <div class="col-lg-3 text-center text-lg-end">
                <a href="${pageContext.request.contextPath}/menu" class="btn btn-lg btn-danger">
                    <i class="fas fa-shopping-cart me-2"></i> Order Now
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />