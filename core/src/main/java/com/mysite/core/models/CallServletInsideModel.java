package com.mysite.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

@Model(adaptables = SlingHttpServletRequest.class)
public class CallServletInsideModel {

    @Self
    private SlingHttpServletRequest request;

    private String servletResponse;

    @PostConstruct
    protected void init() {
        servletResponse = getDataFromRepository();
    }

    public String getServletResponse() {
        return servletResponse;
    }

    private String getDataFromRepository() {
        // Use the ResourceResolver to access the resource
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = resolver.getResource("/content/mysite/us/en/PageInfoPage");

        if (resource != null) {
            return "Data found:\n" + printResourceData(resource);
        } else {
            return "Data not found";
        }
    }

    private String printResourceData(Resource resource) {
        StringBuilder data = new StringBuilder();

        // Print the properties of the resource
        ValueMap properties = resource.getValueMap();
        Set<Map.Entry<String, Object>> entries = properties.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            data.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        // Iterate over child resources
        Iterator<Resource> children = resource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            data.append("\nChild Resource: ").append(child.getPath()).append("\n");
            data.append(printResourceData(child));
        }

        return data.toString();
    }
}
