 document.getElementById('redirectButton').addEventListener('click', function() {
            var tagElements = document.querySelectorAll('.tags li');
            var firstTag = tagElements[0]; // Assuming you want the first tag

            if (firstTag) {
                var title = firstTag.querySelector('.tag-title').textContent;
                var description = firstTag.querySelector('.tag-description').textContent;
                var resourceType = firstTag.querySelector('.tag-resource-type').textContent;
                var redirectUrl = `/bin/articleRedirect?title=${encodeURIComponent(title)}&description=${encodeURIComponent(description)}&resourceType=${encodeURIComponent(resourceType)}`;
                window.location.href = redirectUrl;
            } else {
                console.error('No tags found to redirect.');
            }
        });