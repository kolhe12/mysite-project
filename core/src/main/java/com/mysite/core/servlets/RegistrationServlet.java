package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, immediate = true,
        property = {
                "sling.servlet.paths=/bin/submit"
        })
public class RegistrationServlet extends SlingAllMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse res) throws IOException {
        try {
            String childName = req.getParameter("child-name");
            String parentName = req.getParameter("parent-name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            ResourceResolver resolver = req.getResourceResolver();

            Map<String, Object> map = new HashMap<>();
            map.put("child-name", childName);
            map.put("parent-name", parentName);
            map.put("email", email);
            map.put("phone", phone);
            map.put("address", address);

            Resource parentResource = resolver.getResource("/content/mysite/us/en/schoolsite/jcr:content/root/teaser/jcrNode/Node/Reg");
            if (parentResource != null) {
                resolver.create(parentResource, "form", map);
                resolver.commit();
                res.getWriter().write("Student Details Submitted Successfully.");
            } else {
                res.getWriter().write("Error: Parent resource not found.");
            }
        } catch (PersistenceException e) {
            res.getWriter().write("Error: " + e.getMessage());
        }
    }
}
