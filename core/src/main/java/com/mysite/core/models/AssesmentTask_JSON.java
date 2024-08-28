package com.mysite.core.models;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class} , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = "mysite/components/exporterComponent")
@Exporter(name = "jackson", extensions = "json")
public class AssesmentTask_JSON  {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @Self
    private Resource resource;

    private String name="yamini";

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}