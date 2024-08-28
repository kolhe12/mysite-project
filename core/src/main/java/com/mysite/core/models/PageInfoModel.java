package com.mysite.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageInfoModel {
    private static final Logger log = LoggerFactory.getLogger(PageInfoModel.class);

    @ScriptVariable
    public Page currentPage;

    @SlingObject
    Resource resource;

    private ValueMap currentPageProperties;

    @PostConstruct
    public void init() {
        if (currentPage != null) {
            currentPageProperties = currentPage.getProperties();
        }

    }

    public String getCurrentPage() {
        log.info("Current Page: {}", currentPage);
        if (currentPage != null) {
            return currentPage.getTitle();

        } else {
            return "No current page available";
        }
    }

    public Resource getResource() {
        resource.getPath();
        return resource;
    }

    public ValueMap getCurrentPageProperties() {
        return currentPageProperties;
    }
}
