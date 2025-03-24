 document.addEventListener('DOMContentLoaded', function() {
            const userMenu = document.getElementById('user-menu');
            const submenu = userMenu.querySelector('.submenu');
            let timeoutId;

            userMenu.addEventListener('mouseenter', function() {
                clearTimeout(timeoutId);
                submenu.style.display = 'block';
            });

            userMenu.addEventListener('mouseleave', function() {
                timeoutId = setTimeout(function() {
                    submenu.style.display = 'none';
                }, 300); // 300ms delay before hiding
            });

            submenu.addEventListener('mouseenter', function() {
                clearTimeout(timeoutId);
            });

            submenu.addEventListener('mouseleave', function() {
                submenu.style.display = 'none';
            });
        });