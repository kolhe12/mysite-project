package com.mysite.core.models;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;

@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = "mysite/components/practiceComp")
@Exporter(name = "jackson", extensions = "json")
public class Practice_JSON_Exp
{
    @ValueMapValue
    private String fname;

    @ValueMapValue
    private String lname;


    public String getFname() {
        return fname+" "+"shiva";
    }

    public String getLname() {
        return lname +" "+ "shakti"+" "+"prabhat";
    }


}
