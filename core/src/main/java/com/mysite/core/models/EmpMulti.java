package com.mysite.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EmpMulti
{

    @Inject
    @Via("resource")
    public List<EmpClass> employee;

   // private static final Logger log = (Logger) LoggerFactory.getLogger(Employee.class);


    public List<EmpClass> getEmp() {
        return employee;
    }
}


