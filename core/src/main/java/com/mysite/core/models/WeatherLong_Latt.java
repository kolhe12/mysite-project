package com.mysite.core.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysite.core.service.WeatherApiConfigService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherLong_Latt {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherLong_Latt.class);

    @ValueMapValue
    private String image;

    @OSGiService
    private WeatherApiConfigService weatherApiConfigService;

    @Self
    private SlingHttpServletRequest request;

    private String latitude;

    private String longitude;

    private String temperature;
    private String place;
    private String icon;
    private String iconDescription; // Added field for icon description
    private String humidity;

    @PostConstruct
    protected void init() {
        this.latitude = request.getParameter("latitude");
        this.longitude = request.getParameter("longitude");

        if (latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()) {
            fetchWeatherData(latitude, longitude);
        }
    }

    private void fetchWeatherData(String latitude, String longitude) {
        String apiKey = weatherApiConfigService.getApiKey();
        String apiUrl2 = String.format(weatherApiConfigService.getApiUrl2(), latitude, longitude, apiKey);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl2);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    parseWeatherData(result);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error fetching weather data", e);
        }
    }

    private void parseWeatherData(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonObject main = jsonObject.getAsJsonObject("main");
        JsonObject weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject();
        this.temperature = main.get("temp").getAsString();
        this.humidity = main.get("humidity").getAsString();
        this.place = jsonObject.get("name").getAsString();
        this.icon = weather.get("icon").getAsString();
        this.iconDescription = weather.get("description").getAsString(); // Extracting the description
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImage() {
        return image;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getPlace() {
        return place;
    }

    public String getIcon() {
        return icon;
    }

    public String getIconDescription() { // Getter for icon description
        return iconDescription;
    }

    public String getHumidity() {
        return humidity;
    }
}
