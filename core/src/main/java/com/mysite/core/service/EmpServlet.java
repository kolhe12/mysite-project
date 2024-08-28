package com.mysite.core.service;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/test",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET
        })
public class EmpServlet extends SlingAllMethodsServlet {

    @Reference
    private EmpService empService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, String> data = new HashMap<>();
        data.put("name", empService.getName());
        data.put("email", empService.getEmail());
        response.getWriter().write(new Gson().toJson(data));
    }
}



