package com.mysite.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {

    @Inject @Optional
    private List<Link> footerLinks;

    @Inject @Optional
    private String contactUs;

    @Inject @Optional
    private String copyright;

    private boolean hasFooter;

    @PostConstruct
    protected void init() {
        hasFooter = footerLinks != null || contactUs != null || copyright != null;
    }

    public List<Link> getFooterLinks() {
        return footerLinks;
    }

    public String getContactUs() {
        return contactUs;
    }

    public String getCopyright() {
        return copyright;
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    // Inner class for footer links
    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class Link {
        @Inject @Optional
        private String url;

        @Inject @Optional
        private String text;

        public String getUrl() {
            return url;
        }

        public String getText() {
            return text;
        }
    }
}
