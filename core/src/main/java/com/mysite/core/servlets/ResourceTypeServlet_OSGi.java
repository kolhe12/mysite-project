package com.mysite.core.servlets;

import com.mysite.core.service.PracticeOSGi_service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = {Servlet.class})
@SlingServletResourceTypes(
        resourceTypes = "mysite/components/xfpage",
        methods = HttpConstants.METHOD_GET,
        extensions = "text"
)
@ServiceDescription("Simple Demo Servlet")
public class ResourceTypeServlet_OSGi extends SlingSafeMethodsServlet
{
    private static final long serialVersionUID=1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRedirectServlet.class);
    @Reference
    PracticeOSGi_service practiceOSGiService;
   protected void doGet(SlingHttpServletRequest req , SlingHttpServletResponse res) throws IOException {
       res.setContentType("text/plain");
       res.getWriter().write("Hello World "+practiceOSGiService.getName());
       LOGGER.info("Hello world..");
   }
}
