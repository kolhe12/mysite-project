package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.apache.sling.api.resource.ValueMap;

@Component(service = {Servlet.class})
@SlingServletPaths(value = "/bin/myServlet")
/*@SlingServletResourceTypes(
        resourceTypes = "mysite/components/page",
        methods = HttpConstants.METHOD_GET,
        extensions = "json"
)*/
public class ServletCallinsideModel extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = resolver.getResource("/content/mysite/us/en/PageInfoPage");

        JSONObject jsonResponse = new JSONObject();
        if (resource != null) {
            jsonResponse = getResourceDataAsJson(resource);
        } else {
            jsonResponse.put("error", "Data not found");
        }

        // Set response content type to JSON
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }

    private JSONObject getResourceDataAsJson(Resource resource) {
        JSONObject jsonObject = new JSONObject();

        // Retrieve and add properties to JSON object
        ValueMap properties = resource.getValueMap();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }

        // Add child resources as nested JSON objects
        Iterator<Resource> children = resource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            jsonObject.put(child.getName(), getResourceDataAsJson(child));
        }

        return jsonObject;
    }
}
