// /apps/myproject/components/card/clientlibs/js/card.js
document.addEventListener("DOMContentLoaded", function() {
    var buttons = document.querySelectorAll(".card .btn");

    buttons.forEach(function(button) {
        button.addEventListener("click", function(event) {
            // Prevent the default behavior of the link
            event.preventDefault();
            
            // Get the link URL
            var link = button.getAttribute("href");

            // Open the link in a new tab
            window.open(link, "_blank");
        });
    });
});
