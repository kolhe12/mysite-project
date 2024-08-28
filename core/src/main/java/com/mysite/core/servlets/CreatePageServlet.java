package com.mysite.core.servlets;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;


@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/createpage")
@ServiceDescription("My Demo Servlet")
public class CreatePageServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePageServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        ResourceResolver resourceResolver = request.getResourceResolver();

        try {
            if (resourceResolver != null) {
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                String pageTitle = request.getParameter("title");
                String pageName = request.getParameter("name");
                LOGGER.info("Received pageTitle: {}", pageTitle);
                LOGGER.info("Received pageName: {}", pageName);
                if (pageTitle != null && pageName != null) {
                    String pagePath = "/content/mysite/us/en/" + pageName;
                    Page existingPage = pageManager.getPage(pagePath);
                    if (existingPage == null) {
                        String templatePath = "/conf/mysite/settings/wcm/templates/page-content";
                        Page newPage = pageManager.create("/content/mysite/us/en", pageName, templatePath, pageTitle);
                        response.getWriter().println("New page has been created under " + pagePath);
                    } else {
                        response.getWriter().println("Page with the specified name already exists");
                    }
                } else {
                    response.getWriter().println("Missing page title or name parameters");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in doGet method", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }
}
