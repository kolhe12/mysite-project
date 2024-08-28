package com.mysite.core.servlets;
import com.mysite.core.models.WeatherModel_Sch;
import com.mysite.core.service.WeatherServiceSch;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes=mysite/components/weatherSchedular",
                "sling.servlet.methods=GET"
        }
)

public class WeatherSchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherSchServlet.class);

    @Reference
    private WeatherServiceSch weatherService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String city = request.getParameter("city");

        if (city == null || city.isEmpty()) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("City parameter is required.");
            return;
        }

        try {
            JSONObject weatherData = weatherService.getWeatherData(city);
            if (weatherData != null) {
                // Process and return the weather data
                response.setContentType("application/json");
                response.getWriter().write(weatherData.toString());
            } else {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Weather data not found for city: " + city);
            }
        } catch (Exception e) {
            LOG.error("Error fetching weather data", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error.");
        }
    }
}
