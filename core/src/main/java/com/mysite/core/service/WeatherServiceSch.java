package com.mysite.core.service;

import com.mysite.core.config.WeatherApiConfiguration;
import com.mysite.core.config.WeatherApiConfigurationSch;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component(service = WeatherServiceSch.class)
@Designate(ocd = WeatherApiConfigurationSch.class)
public class WeatherServiceSch {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherServiceSch.class);

    private String apiUrl;

    @Activate
    @Modified
    protected void activate(WeatherApiConfigurationSch config) {
        this.apiUrl = config.apiUrl();
    }

    public JSONObject getWeatherData(String city) {
        String url = apiUrl + "?q=" + city + "&appid=6c252024654657f9cd884a73a6eb46c6"; // Replace with your actual API key
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            InputStream content = response.getEntity().getContent();
            String json = IOUtils.toString(content, StandardCharsets.UTF_8);

            return new JSONObject(json);
        } catch (IOException | JSONException e) {
            LOG.error("Error fetching weather data: {}", e.getMessage());
            return null;
        }
    }
}



