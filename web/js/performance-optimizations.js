/**
 * Performance optimizations for PizzaCraft
 */

// Lazy loading handler for images
document.addEventListener('DOMContentLoaded', function() {
    // Intersection Observer for lazy loading
    if ('IntersectionObserver' in window) {
        const imgObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    const src = img.getAttribute('data-src');
                    
                    if (src) {
                        img.setAttribute('src', src);
                        img.classList.remove('lazy-load');
                        observer.unobserve(img);
                    }
                }
            });
        }, {
            rootMargin: '50px 0px',
            threshold: 0.01
        });

        // Observe all images with lazy-load class
        document.querySelectorAll('.lazy-load').forEach(img => {
            imgObserver.observe(img);
        });
    } else {
        // Fallback for browsers that don't support Intersection Observer
        const loadLazyImages = () => {
            const lazyImages = document.querySelectorAll('.lazy-load');
            
            lazyImages.forEach(img => {
                const src = img.getAttribute('data-src');
                if (src) {
                    img.setAttribute('src', src);
                    img.classList.remove('lazy-load');
                }
            });
        };

        // Load images after page load
        window.addEventListener('load', loadLazyImages);
        // And also on scroll
        window.addEventListener('scroll', loadLazyImages);
    }
    
    // Quantity selector enhancement for pizza detail page
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        const decrementBtn = document.querySelector('.quantity-decrement');
        const incrementBtn = document.querySelector('.quantity-increment');
        
        if (decrementBtn && incrementBtn) {
            decrementBtn.addEventListener('click', function() {
                let value = parseInt(quantityInput.value);
                if (value > 1) {
                    quantityInput.value = value - 1;
                }
            });
            
            incrementBtn.addEventListener('click', function() {
                let value = parseInt(quantityInput.value);
                if (value < 10) {
                    quantityInput.value = value + 1;
                }
            });
        }
    }
});
