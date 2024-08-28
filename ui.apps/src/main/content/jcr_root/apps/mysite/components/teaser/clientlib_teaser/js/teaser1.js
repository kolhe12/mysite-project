document.addEventListener("DOMContentLoaded", function() {
    var tab = document.getElementById("register-here-tab");
    var form = document.getElementById("registration-form");

    tab.addEventListener("click", function() {
        form.style.display = "block";
        tab.style.display = "none";
    });
});


