package com.mysite.core.models;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(adaptables = Resource.class)
public class ContentFragmentsModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    private String fragmentPath;
    private String displayMode;
    private List<String> elementNames;
    private String variationName;
    private String id;

    @PostConstruct
    protected void init() {
        // Initialize properties
        fragmentPath = StringUtils.EMPTY;
        displayMode = StringUtils.EMPTY;
        elementNames = new ArrayList<>();
        variationName = StringUtils.EMPTY;
        id = StringUtils.EMPTY;

        // Get properties from the current resource (dialog resource)
        Resource currentResource = resourceResolver.getResource("/content/dam/mysite/formcf");
        if (currentResource != null) {
            fragmentPath = getProperty(currentResource, "fragmentPath", StringUtils.EMPTY);
            displayMode = getProperty(currentResource, "displayMode", StringUtils.EMPTY);
            elementNames = getMultiFieldProperty(currentResource, "elementNames");
            variationName = getProperty(currentResource, "variationName", StringUtils.EMPTY);
            id = getProperty(currentResource, "id", StringUtils.EMPTY);
        }
    }

    private String getProperty(Resource resource, String propertyName, String defaultValue) {
        return resource.getValueMap().get(propertyName, defaultValue);
    }

    private List<String> getMultiFieldProperty(Resource resource, String propertyName) {
        List<String> values = new ArrayList<>();
        Resource multiFieldResource = resource.getChild(propertyName);
        if (multiFieldResource != null) {
            for (Resource child : multiFieldResource.getChildren()) {
                values.add(getProperty(child, "value", StringUtils.EMPTY));
            }
        }
        return values;
    }

    public String getFragmentPath() {
        return fragmentPath;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public List<String> getElementNames() {
        return elementNames;
    }

    public String getVariationName() {
        return variationName;
    }

    public String getId() {
        return id;
    }
}
