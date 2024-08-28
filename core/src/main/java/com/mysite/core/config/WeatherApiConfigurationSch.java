package com.mysite.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Weather API Configuration")
    public @interface WeatherApiConfigurationSch {
        @AttributeDefinition(name = "Weather API URL", description = "The base URL for the weather API")
        String apiUrl() default "http://api.openweathermap.org/data/2.5/weather";
    }


