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
import java.util.Arrays;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageInfo {
    private static final Logger log = LoggerFactory.getLogger(PageInfo.class);

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private Resource resource;

    private ValueMap currentPageProperties;
    private List<String> tags;

    @PostConstruct
    public void init() {
        if (currentPage != null) {
            currentPageProperties = currentPage.getProperties();
            String[] tagsArray = currentPageProperties.get("cq:tags", new String[0]);
            tags = Arrays.asList(tagsArray);
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
        log.info("Resource: {}", resource);
        return resource;
    }

    public ValueMap getCurrentPageProperties() {
        log.info("Current_Page_Properties: {}", currentPageProperties);
        return currentPageProperties;
    }

    public List<String> getTags() {
        log.info("Tags: {}", tags);
        return tags;
    }
}
