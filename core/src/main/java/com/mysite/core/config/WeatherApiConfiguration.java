package com.mysite.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Weather API Configuration")
public @interface WeatherApiConfiguration {

    @AttributeDefinition(name = "API Key", description = "API Key for the Weather API")
    String apiKey() default "6c252024654657f9cd884a73a6eb46c6";

    @AttributeDefinition(name = "API URL", description = "URL for the Weather API")
    String apiUrl() default "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    @AttributeDefinition(name="API URL", description = "Based on Longitude and Lattitude")
    String apiUrl2() default "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric";

    @AttributeDefinition(name = "Base URL", description = "Base URL for OpenWeatherMap API")
    String baseUrl() default "https://api.openweathermap.org/data/2.5/weather?q=";


    @AttributeDefinition(name = "Unsplash Access Key", description = "Access key for Unsplash API")
    String unsplashAccessKey() default "z0SSos04pyw7jNCEyK-Sf_33UBsCZ3SJE0dzcia-XSo";

    @AttributeDefinition(name = "Unsplash API URL", description = "URL for Unsplash API")
    String unsplashApiUrl() default "https://api.unsplash.com/search/photos?client_id=";

}
