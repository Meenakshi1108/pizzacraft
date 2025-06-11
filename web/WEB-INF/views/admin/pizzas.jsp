<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Manage Menu" />
</jsp:include>

<div class="container mt-4">
    <h1 class="mb-4">Manage Menu</h1>
    
    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i> ${sessionScope.success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="success" scope="session" />
    </c:if>
    
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="error" scope="session" />
    </c:if>
    
    <!-- Add New Pizza Button -->
    <div class="mb-4">
        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addPizzaModal">
            <i class="fas fa-plus me-2"></i>Add New Pizza
        </button>
    </div>
    
    <!-- Pizza List -->
    <div class="row">
        <c:choose>
            <c:when test="${empty pizzas}">
                <div class="col-12">
                    <div class="alert alert-info">No pizzas found in menu.</div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Image</th>
                                <th>Name</th>
                                <th>Price</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${pizzas}" var="pizza">
                                <tr>
                                    <td>${pizza.id}</td>
                                    <td>
                                        <img src="${pizza.imageUrl}" alt="${pizza.name}" style="width: 50px; height: 50px; object-fit: cover;" />
                                    </td>
                                    <td>${pizza.name}</td>
                                    <td>₹${pizza.price}</td>
                                    <td>
                                        <span class="badge ${pizza.available ? 'bg-success' : 'bg-danger'}">
                                            ${pizza.available ? 'Available' : 'Out of Stock'}
                                        </span>
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-primary edit-pizza" 
                                                data-id="${pizza.id}" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#editPizzaModal">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-sm btn-danger delete-pizza" 
                                                data-id="${pizza.id}"
                                                data-bs-toggle="modal" 
                                                data-bs-target="#deletePizzaModal">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Add modal forms for adding/editing/deleting pizzas -->

<jsp:include page="../common/footer.jsp" />