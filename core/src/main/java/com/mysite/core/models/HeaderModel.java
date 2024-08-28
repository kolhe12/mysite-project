package com.mysite.core.models;

import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

    @Inject @Named("logo")
    private String logo;

    @Inject
    private List<NavigationItem> nav;

    @Inject @Named("searchAction")
    private String searchAction;

    public String getLogo() {
        return "/content/dam/mysite/logosch.png";
    }

    public List<NavigationItem> getNav() {
        return nav;
    }

    public String getSearchAction() {
        return searchAction;
    }
}
