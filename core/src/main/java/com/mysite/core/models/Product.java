package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Product {

    @ValueMapValue
    private String productname;

    @ValueMapValue
    private String productid;

    public String getProductname() {
        return productname;
    }

    public String getProductid() {
        return productid;
    }
}
