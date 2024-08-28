package com.mysite.core.models;

import com.mysite.core.service.WeatherService1;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json")
public class WeatherReportModel {

    private static final Logger log = LoggerFactory.getLogger(WeatherReportModel.class);

    @OSGiService
    private WeatherService1 weatherService;


    @ValueMapValue
    private String city;


    private JSONObject weatherData;

    public String getCity() {
        return city;
    }

    // Individual fields to store parsed data

    private double temperature;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private int pressure;
    private int humidity;
    private int visibility;
    private double windSpeed;
    private int windDeg;
    private int cloudsAll;
    private int sunrise;
    private int sunset;
    private String country;
    private int timezone;
    private int cityId;
   // @JsonProperty("city")
    private String cityName;
    private int cod;

    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getVisibility() {
        return visibility;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDeg() {
        return windDeg;
    }

    public int getCloudsAll() {
        return cloudsAll;
    }

    public int getSunrise() {
        return sunrise;
    }

    public int getSunset() {
        return sunset;
    }

    public String getCountry() {
        return country;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCod() {
        return cod;
    }


    public JSONObject getWeatherData() {
        if (weatherData == null && city != null) {
            weatherData = weatherService.getWeatherData("city");
            log.info("Weather Data: {}", weatherData);


            if (weatherData != null) {
                // Parse and set individual fields
                try {
                    JSONObject mainData = weatherData.getJSONObject("main");
                    JSONObject windData = weatherData.getJSONObject("wind");
                    JSONObject cloudsData = weatherData.getJSONObject("clouds");
                    JSONObject sysData = weatherData.getJSONObject("sys");

                    temperature = mainData.getDouble("temp");
                    feelsLike = mainData.getDouble("feels_like");
                    tempMin = mainData.getDouble("temp_min");
                    tempMax = mainData.getDouble("temp_max");
                    pressure = mainData.getInt("pressure");
                    humidity = mainData.getInt("humidity");
                    visibility = weatherData.getInt("visibility");
                    windSpeed = windData.getDouble("speed");
                    windDeg = windData.getInt("deg");
                    cloudsAll = cloudsData.getInt("all");
                    sunrise = sysData.getInt("sunrise");
                    sunset = sysData.getInt("sunset");
                    country = sysData.getString("country");
                    timezone = weatherData.getInt("timezone");
                    cityId = weatherData.getInt("id");
                    cityName = weatherData.getString("name");
                    cod = weatherData.getInt("cod");
                } catch (Exception e) {
                    log.error("Error parsing weather data: {}", e.getMessage());
                }
            }

        }
        return weatherData;
    }

}
