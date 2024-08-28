package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

@Component(service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/fetchPageDetails",
                "sling.servlet.methods=GET"
        })
public class TestServlet extends SlingSafeMethodsServlet {

    @Reference
    private ResourceResolver resourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        String pagePath = request.getParameter("pagePath");
        LOGGER.info("PagePath: {}",pagePath);

        if (pagePath == null || pagePath.isEmpty()) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Page path is missing");
            return;
        }

        ResourceResolver resolver = null;
        try {
            resolver = (ResourceResolver) resourceResolver.getAttribute(pagePath);
            Resource pageResource = resolver.getResource(pagePath);
            if (pageResource == null) {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Page not found");
                return;
            }

            JSONObject json = new JSONObject();

            // Retrieve and format page properties
            ValueMap properties = pageResource.getValueMap();
            json.put("lastModifiedBy", properties.get("lastModifiedBy", ""));
            json.put("pagePath", pagePath);
            json.put("lastModifiedOn", formatDate(properties.get("lastModifiedOn", "")));
            json.put("jcr:title", properties.get("jcr:title", ""));
            json.put("pageContentPath", pagePath + "/jcr:content");
            json.put("jcr:description", properties.get("jcr:description", ""));
            json.put("cq:template", properties.get("cq:template", ""));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        } catch (JSONException e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing JSON");
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    private String formatDate(Object dateObj) {
        if (dateObj instanceof Date) {
            Date date = (Date) dateObj;
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);
        }
        return "";
    }

}

