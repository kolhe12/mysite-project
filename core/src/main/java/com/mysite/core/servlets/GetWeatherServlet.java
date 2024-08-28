package com.mysite.core.servlets;
import com.mysite.core.config.WeatherApiConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.methods=GET",
                "sling.servlet.paths=/bin/getWeather"
        }
)
@Designate(ocd = WeatherApiConfiguration.class)
public class GetWeatherServlet extends SlingAllMethodsServlet {
    private static final Logger logger = LoggerFactory.getLogger(GetWeatherServlet.class);
    private WeatherApiConfiguration config;

    @Activate
    @Modified
    protected void activate(WeatherApiConfiguration config) {
        this.config = config;
    }
    private static final String WEATHER_API_KEY = "6c252024654657f9cd884a73a6eb46c6"; // Your OpenWeatherMap API key

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String city = request.getParameter("city");
        if (city != null && !city.isEmpty()) {
            try {
                JSONObject weatherData = fetchWeatherData(city);
                response.setContentType("application/json");
                response.getWriter().write(weatherData.toString());
                logger.info("Weather data for {} retrieved successfully.", city);
            } catch (Exception e) {
                logger.error("Error processing weather data:", e);
                response.getWriter().write("Error processing weather data: " + e.getMessage());
            }
        } else {
            logger.error("City parameter is missing");
            response.getWriter().write("City parameter is missing");
        }
    }

    private JSONObject fetchWeatherData(String city) throws IOException, JSONException {
        JSONObject jsonResponse = new JSONObject();
        String encodedCity = URLEncoder.encode(city, "UTF-8");
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + WEATHER_API_KEY + "&units=metric";
        logger.info("Requesting weather data from URL: {}", url);

        // Make an HTTP request to the OpenWeatherMap API
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // Parse the response as JSON
            JSONObject responseObject = new JSONObject(responseBuilder.toString());

            // Extract the weather data you need from the response
            JSONObject main = responseObject.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double feelsLike = main.getDouble("feels_like");
            double minTemperature = main.getDouble("temp_min");
            double maxTemperature = main.getDouble("temp_max");
            int pressure = main.getInt("pressure");
            int humidity = main.getInt("humidity");
            int visibility = responseObject.getInt("visibility");
            JSONObject wind = responseObject.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            JSONArray weatherArray = responseObject.getJSONArray("weather");
            String weatherDescription = weatherArray.getJSONObject(0).getString("description");

            // Get the timezone offset
            int timezoneOffset = responseObject.getInt("timezone");

            // Calculate current time based on timezone offset
            long currentTimeMillis = System.currentTimeMillis();
            long timezoneOffsetMillis = timezoneOffset * 1000;
            long currentTimeAtLocationMillis = currentTimeMillis + timezoneOffsetMillis;

            // Format the current time (time only)
            // Format the current time (time with AM/PM)
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String currentTimeAtLocation = timeFormat.format(new Date(currentTimeAtLocationMillis));


// Put the weather data and current time in the jsonResponse object
            jsonResponse.put("city", city);
            jsonResponse.put("temperature", temperature);
            jsonResponse.put("feelsLike", feelsLike);
            jsonResponse.put("minTemperature", minTemperature);
            jsonResponse.put("maxTemperature", maxTemperature);
            jsonResponse.put("pressure", pressure);
            jsonResponse.put("humidity", humidity);
            jsonResponse.put("windSpeed", windSpeed);
            jsonResponse.put("visibility", visibility);
            jsonResponse.put("weatherDescription", weatherDescription);
            if (currentTimeAtLocation != null) {
                jsonResponse.put("currentTimeAtLocation", currentTimeAtLocation);
            } else {
                logger.error("Failed to fetch current time at location.");
            }
            String weatherSymbol=getWeatherSymbol(weatherDescription);
            jsonResponse.put("weatherSymbol",weatherSymbol);
            String imageUrl = getImageURL(weatherDescription);
            if (imageUrl != null) {
                jsonResponse.put("imageUrl", imageUrl);
            } else {
                logger.error("Failed to fetch image URL for weather description: {}", weatherDescription);
            }

            return jsonResponse;
        } else {
            // If the request fails, log an error and return an empty JSONObject
            logger.error("Failed to fetch weather data. Response code: {}", responseCode);
            return jsonResponse;
        }
    }
    private String getWeatherSymbol(String weatherDescription) {
        // Convert the weather description to lowercase for case-insensitive comparison
        String descriptionLowerCase = weatherDescription.toLowerCase();

        // Check for specific keywords in the weather description and return the corresponding weather symbol
        if (descriptionLowerCase.contains("rain")) {
            return "🌧️"; // Rain symbol
        } else if (descriptionLowerCase.contains("cloud")) {
            return "☁️"; // Cloud symbol
        } else if (descriptionLowerCase.contains("clear")) {
            return "☀️"; // Clear symbol
        } else if (descriptionLowerCase.contains("thunderstorm")) {
            return "⛈️"; // Thunderstorm symbol
        } else if (descriptionLowerCase.contains("snow")) {
            return "❄️"; // Snow symbol
        } else if (descriptionLowerCase.contains("mist") || descriptionLowerCase.contains("fog") || descriptionLowerCase.contains("haze") || descriptionLowerCase.contains("smoke")) {
            return "🌫️"; // Mist/Fog/Haze/Smoke symbol
        } else if (descriptionLowerCase.contains("sand") || descriptionLowerCase.contains("dust") || descriptionLowerCase.contains("ash")) {
            return "🌪️"; // Sand/Dust/Ash symbol
        } else if (descriptionLowerCase.contains("squall")) {
            return "🌬️"; // Squall symbol
        } else if (descriptionLowerCase.contains("tornado")) {
            return "🌪️"; // Tornado symbol
        } else {
            return "🌤️"; // Default symbol for other weather conditions
        }
    }
    public String getImageURL(String description) {
        try {
            String encodedDescription = URLEncoder.encode(description, "UTF-8");
            String unsplashAccessKey = config.unsplashAccessKey();
            String unsplashApiUrl = config.unsplashApiUrl()+ unsplashAccessKey + "&query=" + encodedDescription + "%20weather";
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(unsplashApiUrl);
                HttpResponse response = httpClient.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.has("results")) {
                        JSONArray resultsArray = jsonResponse.getJSONArray("results");
                        if (resultsArray.length() > 0) {
                            JSONObject firstResult = resultsArray.getJSONObject(0);
                            JSONObject urlsObject = firstResult.getJSONObject("urls");
                            return urlsObject.optString("regular", null);
                        } else {
                            logger.error("No results found in the response");
                        }
                    } else {
                        logger.error("No 'results' array found in the response");
                    }
                } else {
                    logger.error("HTTP request failed with status code: " + statusCode);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching image URL", e);
        }
        return null;
    }

}
