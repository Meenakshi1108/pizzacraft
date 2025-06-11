// Function to animate the cart badge when items are added
function animateCartBadge() {
    const cartBadge = document.querySelector('.cart-badge');
    if (cartBadge) {
        cartBadge.classList.add('cart-badge-animate');
        setTimeout(() => {
            cartBadge.classList.remove('cart-badge-animate');
        }, 500);
    }
}

// Check if we just added an item to the cart
document.addEventListener('DOMContentLoaded', function() {
    // If success message contains "added to cart" then animate badge
    const successAlert = document.querySelector('.alert-success');
    if (successAlert && successAlert.textContent.includes('added to cart')) {
        animateCartBadge();
    }
});