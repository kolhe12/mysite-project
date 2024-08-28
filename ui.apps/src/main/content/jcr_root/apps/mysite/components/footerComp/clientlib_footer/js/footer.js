(function($) {
    $(document).ready(function() {
        // Function to validate the form before submission
        function validateForm() {
            var linkText = $('input[name$="/linkText"]').val();
            var linkURL = $('input[name$="/linkURL"]').val();
            var disclaimer = $('textarea[name$="/disclaimer"]').val();

            if (linkText.trim() === '' || linkURL.trim() === '' || disclaimer.trim() === '') {
                alert('Please fill in all fields.');
                return false;
            }

            return true;
        }

        // Event listener for form submission
        $('.form-fields').submit(function(e) {
            if (!validateForm()) {
                e.preventDefault(); // Prevent form submission if validation fails
            }
        });
    });
})(jQuery);
