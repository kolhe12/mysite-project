package com.mysite.core.service;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Component(service = PageCreationService.class)
public class PageCreationService {

    private static final Logger LOG = LoggerFactory.getLogger(PageCreationService.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public void createPage() {
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "page-creator-service"));
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager != null) {
                String parentPath = "/content/mysite/us/en";
                String pageName = "content-page-" + System.currentTimeMillis();
                String templatePath = "/conf/mysite/settings/wcm/templates/base_template";
                String pageTitle = "Content Page";

                Page newPage = pageManager.create(parentPath, pageName, templatePath, pageTitle);
                resourceResolver.commit();
                LOG.info("Page created at: {}", newPage.getPath());
            }
        } catch (Exception e) {
            LOG.error("Failed to create page", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }
}

