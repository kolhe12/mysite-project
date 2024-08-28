        document.addEventListener("DOMContentLoaded", function () {
            const form = document.getElementById('weatherForm');
            const weatherDetails = document.getElementById('weatherDetails');
         
            // Set default city and fetch weather details when the page loads
          //  const defaultCity = "Delhi"; // Set your default city here
           // document.getElementById('cityInput').value = defaultCity;
          //  getWeather(defaultCity);
         
            form.addEventListener('submit', async function (event) {
                event.preventDefault();
                const cityInput = document.getElementById('cityInput').value;
                await getWeather(cityInput);
            });
         
            document.getElementById('getLocationButton').addEventListener('click', getLocation);
         
            function getLocation() {
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(sendPositionToBackend);
                } else {
                    console.log("Geolocation is not supported by this browser.");
                }
            }
         
            async function sendPositionToBackend(position) {
                const latitude = position.coords.latitude;
                const longitude = position.coords.longitude;
         
                // Send latitude and longitude to backend
                try {
                    const response = await fetch(`/bin/getaddress?latitude=${latitude}&longitude=${longitude}`);
                    if (!response.ok) {
                        throw new Error('Error fetching city data');
                    }
                    const cityData = await response.json();
                    const city = cityData.city;
                    document.getElementById('cityInput').value = city;
                    getWeather(city);
                } catch (error) {
                    console.error('Error fetching city data:', error);
                }
            }
         
            function getWeather(city) {
                fetch(`/bin/getWeather?city=${city}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('City not found');
                        }
                        return response.json();
                    })
                    .then(data => {
                        renderWeatherDetails(data);
                    })
                    .catch(error => {
                        console.error('Error fetching weather data:', error);
                        weatherDetails.innerHTML = 'City not found';
                    });
            }
         
            function renderWeatherDetails(data) {
                const weatherHtml = `
                    <h2 class="weather-details__title" style="color:white; text-align:left; font-size: 50px;"> ${data.city}</h2>
                    <div class="weather-details__content">
                        <h1 style="color:white; text-align:left; font-size: 30px;"> ${data.currentTimeAtLocation}</h1>
                        <div class="temp"> ${data.weatherSymbol}<span class="temperature">${data.temperature}</span>
                            <span class="unit">°C</span>
                        </div>
                        <div class="weather-description"><span class="weather-details__description">${data.weatherDescription}</span> 
                        <span class="weather-details__description1" style="text-align-last:right;">${data.feelsLike}°C</span>
                        </div>
                        <div class="additional-info">
                            <div class="info-item" style="width:20%;"><h5>Pressure</h5> ${data.pressure} hPa</div>
                            <div class="info-item" style="width:20%;"><h5>Humidity</h5> ${data.humidity}%</div>
                            <div class="info-item" style="width:20%;"><h5>Wind</h5> ${data.windSpeed} km/h</div>
                            <div class="info-item" style="width:20%;"><h5>Visibility</h5> ${data.visibility} meters</div>
                        </div>
                    </div>
                `;
                weatherDetails.innerHTML = weatherHtml;
             
                // Set the background image dynamically for your component's container
                const componentContainer = document.getElementById('weather-component'); // Replace 'your-component-container-id' with the actual ID or class of your component's container
                if (data.imageUrl && componentContainer) {
                    componentContainer.style.backgroundImage = `url(${data.imageUrl})`;
                }
            }
        });
document.addEventListener("DOMContentLoaded", function () {
    // Call the function to set the background based on weather conditions
    setDefaultBackground();
});



function setDefaultBackground() {
    var weatherContainers = document.querySelectorAll('#weather-component');
    var bgImages = document.querySelectorAll('.bg-image');

    weatherContainers.forEach(function (weatherContainer, index) {
        var img = bgImages[index];
        if (img && img.dataset.backgroundimage) {
            weatherContainer.style.background = 'url("' + img.dataset.backgroundimage + '")';
            weatherContainer.style.backgroundSize = 'cover';
        } else {
            console.log("backgroundImageUrl not found or is nil");
        }
    });
}

