package com.mysite.core.models;

import com.mysite.core.service.MyService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class MyModel {

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private MyService myService;

    private String servletResponse;

    @PostConstruct
    protected void init() {
        servletResponse = myService.callMyServlet();
    }

    public String getServletResponse() {
        return servletResponse;
    }
}
