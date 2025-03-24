document.addEventListener('DOMContentLoaded', function() {
            const ratingInputs = document.querySelectorAll('.rating-input');
            const editButtons = document.querySelectorAll('.edit-review-btn');
            const cancelButtons = document.querySelectorAll('.cancel-edit-btn');

            function updateStars(container, value) {
                const stars = container.querySelectorAll('.star');
                stars.forEach((star, index) => {
                    if (index < value) {
                        star.classList.remove('bi-star');
                        star.classList.add('bi-star-fill');
                    } else {
                        star.classList.remove('bi-star-fill');
                        star.classList.add('bi-star');
                    }
                });
            }

            function setupStarRating(container) {
                const stars = container.querySelectorAll('.star');
                const ratingInput = container.querySelector('.rating-input');

                stars.forEach(star => {
                    star.addEventListener('click', function() {
                        const value = this.dataset.value;
                        ratingInput.value = value;
                        updateStars(container, value);
                    });
                });

                // Initialize stars
                updateStars(container, ratingInput.value);
            }

            // Setup star rating for all rating containers
            document.querySelectorAll('.rating-container').forEach(setupStarRating);

            // Update all review stars
            document.querySelectorAll('.review-content').forEach(reviewContent => {
                const ratingValue = parseInt(reviewContent.dataset.rating);
                updateStars(reviewContent, ratingValue);
            });

            editButtons.forEach(button => {
                button.addEventListener('click', function() {
                    const reviewContent = this.closest('.review-content');
                    const editForm = reviewContent.nextElementSibling;
                    reviewContent.classList.add('hidden');
                    editForm.classList.remove('hidden');
                });
            });

            cancelButtons.forEach(button => {
                button.addEventListener('click', function() {
                    const editForm = this.closest('.edit-review-form');
                    const reviewContent = editForm.previousElementSibling;
                    editForm.classList.add('hidden');
                    reviewContent.classList.remove('hidden');
                });
            });

            // Quantity incrementing and decrementing
            window.incrementQuantity = function() {
                var quantityInput = document.getElementById('quantity');
                quantityInput.value = parseInt(quantityInput.value) + 1;
            }

            window.decrementQuantity = function() {
                var quantityInput = document.getElementById('quantity');
                var currentValue = parseInt(quantityInput.value);
                if (currentValue > 1) {
                    quantityInput.value = currentValue - 1;
                }
            }
        });