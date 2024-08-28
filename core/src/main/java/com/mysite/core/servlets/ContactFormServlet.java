package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component(service = { Servlet.class },
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/submit/contactform"
        })
public class ContactFormServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(ContactFormServlet.class);
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_EMAIL_LOCAL_PART_LENGTH = 10;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@(gmail\\.com|hotmail\\.com|facebook\\.com|twitter\\.com)$");

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resolver = null;
        try {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String message = request.getParameter("message");

            if (name == null || email == null || message == null) {
                response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Missing form parameters");
                return;
            }
            if (name.length() < 1 || name.length() > MAX_NAME_LENGTH) {
                response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Name must be between 1 and " + MAX_NAME_LENGTH + " characters long");
                return;
            }
            if (!NAME_PATTERN.matcher(name).matches()) {
                response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Invalid name format. Name should only contain letters, spaces, hyphens, and apostrophes.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Invalid email format. Email must be one of the following domains: gmail.com, hotmail.com, facebook.com, twitter.com");
                return;
            }
            String localPart = email.substring(0, email.indexOf('@'));
            if (localPart.length() > MAX_EMAIL_LOCAL_PART_LENGTH) {
                response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "The local part of the email address (before @) must be " + MAX_EMAIL_LOCAL_PART_LENGTH + " characters or fewer.");
                return;
            }

            log.info("Received contact form submission: name={}, email={}, message={}", name, email, message);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(ResourceResolverFactory.SUBSERVICE, "contactFormService");

            resolver = resolverFactory.getServiceResourceResolver(paramMap);

            String parentPath = "/content/mysite/us/en/PageInfoPage/jcr:content";
            Resource parentResource = resolver.getResource(parentPath);

            if (parentResource != null) {
                boolean isDuplicate = false;
                for (Resource child : parentResource.getChildren()) {
                    String existingName = (String) child.getValueMap().get("name");
                 //   String existingEmail = (String) child.getValueMap().get("email");
                    if (name.equals(existingName)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (isDuplicate) {
                    response.sendError(SlingHttpServletResponse.SC_CONFLICT, "This name are already registered here ,Please update your form...");
                    return;
                }
                Map<String, Object> properties = new HashMap<>();
                properties.put("name", name);
                properties.put("email", email);
                properties.put("message", message);

                resolver.create(parentResource, name, properties);
                resolver.commit();

                response.setContentType("text/plain");
                response.getWriter().write("Contact Details\n\n");
                response.getWriter().write("Name: " + name + "\n");
                response.getWriter().write("Email: " + email + "\n");
                response.getWriter().write("Message: " + message);
            } else {
                log.error("Parent resource not found: {}", parentPath);
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Parent resource not found");
            }
        } catch (Exception e) {
            log.error("Error processing contact form submission", e);
            response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "This name are already registered here ,Please update your form...");
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }
}
