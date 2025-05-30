<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
</jsp:include>

<!-- Hero Section with Better Design -->
<div class="hero-section">
    <div class="hero-content">
        <h1 class="hero-title">Authentic Indian Flavors,<br>Handcrafted Pizzas</h1>
        <p class="hero-subtitle">Experience the perfect blend of traditional Italian bases with rich Indian spices</p>
        <a href="${pageContext.request.contextPath}/menu" class="btn btn-lg btn-hero pulse-animation">
            <i class="fas fa-pizza-slice"></i> Explore Our Menu
        </a>
    </div>
    <div class="hero-overlay"></div>
</div>

<!-- Feature Cards with Textured Background -->
<div class="features-section">
    <div class="container">
        <div class="row">
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-pizza-slice"></i>
                    </div>
                    <h3>Authentic Flavors</h3>
                    <p>Experience our tandoori, butter masala, and other Indian-inspired pizzas made with premium local spices.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-shipping-fast"></i>
                    </div>
                    <h3>Express Delivery</h3>
                    <p>Hot and fresh delivery within 30 minutes, bringing delicious pizzas right to your doorstep.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-mobile-alt"></i>
                    </div>
                    <h3>Easy Ordering</h3>
                    <p>Order with just a few taps on our user-friendly website and track your delivery in real-time.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Popular Pizzas Section -->
<div class="popular-section">
    <div class="container">
        <div class="section-title">
            <h2>Our Chef's Specials</h2>
            <div class="spice-divider">
                <i class="fas fa-pepper-hot"></i>
            </div>
        </div>
        <div class="row">
            <div class="col-md-4">
                <div class="pizza-card">
                    <div class="pizza-img">
                        <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" alt="Tandoori Paneer">
                        <div class="veg-badge"><i class="fas fa-circle"></i></div>
                    </div>
                    <div class="pizza-content">
                        <h3>Tandoori Paneer Pizza</h3>
                        <div class="spice-level">
                            <span>Spice Level:</span>
                            <i class="fas fa-pepper-hot"></i>
                            <i class="fas fa-pepper-hot"></i>
                            <i class="fas fa-pepper-hot"></i>
                        </div>
                        <div class="pizza-price">&#8377;329</div>
                        <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-primary">Order Now</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="pizza-card">
                    <div class="pizza-img">
                        <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" alt="Butter Chicken">
                        <div class="nonveg-badge"><i class="fas fa-circle"></i></div>
                    </div>
                    <div class="pizza-content">
                        <h3>Butter Chicken Pizza</h3>
                        <div class="spice-level">
                            <span>Spice Level:</span>
                            <i class="fas fa-pepper-hot"></i>
                            <i class="fas fa-pepper-hot"></i>
                        </div>
                        <div class="pizza-price">&#8377;369</div>
                        <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-primary">Order Now</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="pizza-card">
                    <div class="pizza-img">
                        <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" alt="Cheese Burst">
                        <div class="veg-badge"><i class="fas fa-circle"></i></div>
                    </div>
                    <div class="pizza-content">
                        <h3>Cheese Burst Pizza</h3>
                        <div class="spice-level">
                            <span>Spice Level:</span>
                            <i class="fas fa-pepper-hot"></i>
                        </div>
                        <div class="pizza-price">&#8377;349</div>
                        <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-primary">Order Now</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Call to Action Section -->
<div class="cta-section">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h3>Ready to experience authentic Indian pizzas?</h3>
                <p>Order now</p>
            </div>
            <div class="col-md-4 text-center text-md-right">
                <a href="${pageContext.request.contextPath}/register" class="btn btn-light btn-lg">Sign Up Now</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />