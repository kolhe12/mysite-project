package com.mysite.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Collections;

@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/test_servlet")
public class SystemServiceCreatePage extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemServiceCreatePage.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String pageTitle = request.getParameter("title");
        String pageName = request.getParameter("name");

        LOGGER.info("Received request to create page with title: {} and name: {}", pageTitle, pageName);

        if (pageTitle != null && pageName != null) {
            try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "service_createpage"))) {
                if (resourceResolver != null) {
                    LOGGER.info("Obtained resource resolver");
                    String pagePath = "/content/mysite/us/en/" + pageName;
                    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    if (pageManager != null) {
                        LOGGER.info("Obtained PageManager");
                        Page existingPage = pageManager.getPage(pagePath);
                        if (existingPage == null) {
                            LOGGER.info("Page does not exist at path: {}", pagePath);
                            String templatePath = "/conf/mysite/settings/wcm/templates/mt-template";
                            Page parentPage = pageManager.getPage("/content/mysite/us/en");
                            if (parentPage != null) {
                                LOGGER.info("Parent page exists at path: {}", parentPage.getPath());
                                Page newPage = pageManager.create(parentPage.getPath(), pageName, templatePath, pageTitle);
                                LOGGER.info("Created new page at path: {}", newPage.getPath());
                                response.getWriter().println("New page has been created under " + pagePath);
                            } else {
                                LOGGER.warn("Parent page does not exist at /content/mysite/us/en");
                                response.getWriter().println("Parent page does not exist");
                            }
                        } else {
                            LOGGER.warn("Page already exists at path: {}", pagePath);
                            response.getWriter().println("Page with the specified name already exists");
                        }
                    } else {
                        LOGGER.error("Failed to get PageManager");
                        response.getWriter().println("Failed to get PageManager");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error creating page", e);
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while creating the page");
            }
        } else {
            LOGGER.warn("Missing page title or name parameters");
            response.getWriter().println("Missing page title or name parameters");
        }
    }
}
