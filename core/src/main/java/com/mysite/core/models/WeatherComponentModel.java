package com.mysite.core.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysite.core.service.WeatherApiConfigService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
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

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherComponentModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherComponentModel.class);

    @OSGiService
    private WeatherApiConfigService weatherApiConfigService;

    @ValueMapValue
    private String pin;

    @ValueMapValue
    private String image;

    private String temperature;

    private String place;

    @PostConstruct
    protected void init() {
        if (pin != null && !pin.isEmpty()) {
            fetchWeatherData(pin);
        }
    }

    private void fetchWeatherData(String pin) {
        String apiKey = weatherApiConfigService.getApiKey();
        String apiUrl = String.format(weatherApiConfigService.getApiUrl(), pin, apiKey);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
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
        this.place = jsonObject.get("name").getAsString();
    }

    public String getPin() {
        return pin;
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
}
