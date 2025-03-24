document.addEventListener('DOMContentLoaded', function() {
        const editButtons = document.querySelectorAll('.edit-review-btn');
        const cancelButtons = document.querySelectorAll('.cancel-edit-btn');

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
    });