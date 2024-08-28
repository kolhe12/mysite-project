package com.mysite.core.servlets;

import com.mysite.core.service.MyService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/testService")
public class TestServiceServlet extends SlingAllMethodsServlet {

    @Reference
    private MyService myService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String servletResponse = myService.callMyServlet();
        response.getWriter().write("Service called servlet and got response: " + servletResponse);
    }
}
