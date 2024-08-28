package com.mysite.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.mysite.core.models.WeatherModel_Sch;
import com.mysite.core.service.WeatherServiceSch;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = WeatherScheduler.Config.class)
public class WeatherScheduler implements Runnable {

    @ObjectClassDefinition(name = "Weather Scheduler Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Cron Expression", description = "Cron expression to schedule the job")
        String scheduler_expression() default "0 */5 * * * ?"; // Every 5 minutes
    }

    private static final Logger LOG = LoggerFactory.getLogger(WeatherScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private WeatherServiceSch weatherService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private AtomicInteger schedulerId;

    @Activate
    protected void activate(Config config) {
        schedulerId = new AtomicInteger();
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.scheduler_expression());
        scheduleOptions.name("Weather Scheduler - " + schedulerId.incrementAndGet());
        scheduleOptions.canRunConcurrently(false);

        scheduler.schedule(this, scheduleOptions);
        LOG.info("Weather Scheduler activated with cron expression: {}", config.scheduler_expression());
    }

    @Deactivate
    protected void deactivate() {
        LOG.info("Weather Scheduler deactivated.");
    }

    @Override
    public void run() {
        try {
            LOG.info("Weather Scheduler running.");
            // Fetch weather data
            String city = "London"; // Replace with the actual city or make this dynamic
            JSONObject weatherData = weatherService.getWeatherData(city);

            if (weatherData != null) {
                // Process weather data
                ResourceResolver resourceResolver = null;
                try {
                    resourceResolver = resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "page-creator-service"));
                    WeatherModel_Sch weatherModel = new WeatherModel_Sch();
                    weatherModel.setWeatherData(weatherData); // Assuming you have a method to set weather data

                    // Create or update page based on weather data
                    createOrUpdatePage(resourceResolver, weatherModel);
                    resourceResolver.commit();
                } catch (Exception e) {
                    LOG.error("Failed to create or update page", e);
                } finally {
                    if (resourceResolver != null && resourceResolver.isLive()) {
                        resourceResolver.close();
                    }
                }
            } else {
                LOG.warn("No weather data received for city: {}", city);
            }
        } catch (Exception e) {
            LOG.error("Error executing Weather Scheduler", e);
        }
    }

    private void createOrUpdatePage(ResourceResolver resourceResolver, WeatherModel_Sch weatherModel) {
        // Implement logic to create or update a page based on weather data
        try {
            String parentPath = "/content/mysite/us/en/weather";
            String pageName = "weather-report-" + System.currentTimeMillis();
            String templatePath = "/conf/mysite/settings/wcm/templates/weather_template";
            String pageTitle = "Weather Report for " + weatherModel.getCityName();

            // Use the PageManager to create or update pages
            // Add logic to update existing pages if needed
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                Page newPage = pageManager.create(parentPath, pageName, templatePath, pageTitle);
                LOG.info("Page created at: {}", newPage.getPath());
                // Optionally add weather data to the page
            }
        } catch (Exception e) {
            LOG.error("Error creating or updating page with weather data", e);
        }
    }
}
