package com.mysite.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.osgi.service.component.annotations.Component;

import javax.inject.Inject;
import javax.inject.Named;


@Model(adaptables = Resource.class)
//@Model(adaptables = SlingHttpServletRequest.class)
public class ResourceCompSling
{
  @Inject
  private String fname;

  @Inject
  private String lname;

  @Inject
  @Named("jcr:lastModified")
  private String jcrLastModified;


  @Inject
  @Named("jcr:lastModifiedBy")
  private String jcrLastModifiedby;


  @ValueMapValue
  @Named("sling:resourceType")
  private String slingResourceType;


  @ValueMapValue
  @Named("jcr:created")
  private String jcrCreated;

  @ValueMapValue
  @Named("jcr:createdBy")
  private String jcrCreatedby;

/*
  @ValueMapValue(name = "jcr:title")
  private String title;

   public String getTitle()
   {
     return title;
   }*/


  public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getJcrLastModified()
    {
        return jcrLastModified;
    }

  public String getJcrLastModifiedby()
  {
    return jcrLastModifiedby;
  }


  public String getSlingResourceType()
    {
        return slingResourceType;
    }


    public String getJcrCreated()
    {
        return jcrCreated;
    }

  public String getJcrCreatedby()
  {
    return jcrCreatedby;
  }

  public String getFullName() {
        String firstName = fname != null ? fname : "";
        String lastName = lname != null ? lname : "";
        return fname + " " + lname;
    }


}
