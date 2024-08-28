package com.mysite.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetailsModel {

    @ValueMapValue
    private String cname;

    @Inject
    @Via("resource")
    public List<Product> products;


    public String getCname() {
        return cname;
    }

    public List<Product> getProducts() {
        return products;
    }
}
