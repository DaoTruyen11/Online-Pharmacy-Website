document.addEventListener('DOMContentLoaded', function() {
    // Image preview for 'image' input
    const imageInput = document.getElementById('image');
    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            var preview = document.getElementById('preview');
            var noImageMessage = document.getElementById('noImageMessage');
            var file = e.target.files[0];
            var reader = new FileReader();

            reader.onload = function(e) {
                preview.src = e.target.result;
                preview.classList.remove('hidden');
                noImageMessage.classList.add('hidden');
            }

            if (file) {
                reader.readAsDataURL(file);
            } else {
                preview.src = '';
                preview.classList.add('hidden');
                noImageMessage.classList.remove('hidden');
            }
        });
    }

    // Image preview for 'profilePicture' input
    const profilePictureInput = document.getElementById('profilePicture');
    if (profilePictureInput) {
        profilePictureInput.addEventListener('change', function(e) {
            var reader = new FileReader();
            reader.onload = function(e) {
                var preview = document.getElementById('preview');
                var noImageMessage = document.getElementById('noImageMessage');
                if (preview && noImageMessage) {
                    preview.src = e.target.result;
                    preview.classList.remove('hidden');
                    noImageMessage.classList.add('hidden');
                }
            }
            if (e.target.files[0]) {
                reader.readAsDataURL(e.target.files[0]);
            }
        });
    }

    // Navigation highlight
    const navItems = document.querySelectorAll('.main-menu .nav-item a');
    const currentPath = window.location.pathname;

    navItems.forEach(item => {
        const itemPath = item.getAttribute('href');

        // Check if the current path matches the item's href
        if (currentPath === itemPath) {
            item.closest('.nav-item').classList.add('active');
        } else {
            item.closest('.nav-item').classList.remove('active');
        }
    });
});