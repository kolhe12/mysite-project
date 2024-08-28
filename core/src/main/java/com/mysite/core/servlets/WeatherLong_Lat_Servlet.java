package com.mysite.core.servlets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysite.core.service.WeatherApiConfigService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.servlets.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/weather"
        }
)
public class WeatherLong_Lat_Servlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherLong_Lat_Servlet.class);

    @Reference
    private WeatherApiConfigService weatherApiConfigService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");

        if (latitude == null || longitude == null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing latitude or longitude parameters");
            return;
        }

        String apiUrl2 = String.format(weatherApiConfigService.getApiUrl2(), latitude, longitude, weatherApiConfigService.getApiKey());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl2);
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
                    response.setContentType("application/json");
                    response.getWriter().write(jsonObject.toString());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error fetching weather data", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
