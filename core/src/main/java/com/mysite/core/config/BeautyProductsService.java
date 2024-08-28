package com.mysite.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "BeautyProducts API Configuration")
public @interface BeautyProductsService {

    @AttributeDefinition(name = "API Key", description = "API Key for the base URL for the Makeup API API")
    String apiKey() default "6c252024654657f9cd884a73a6eb46c6";

    @AttributeDefinition(name = "API URL", description = "The base URL for the Makeup API")
    String getBeautyProducts() default "http://makeup-api.herokuapp.com/api/v1/products.json";

}
