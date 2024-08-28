package com.mysite.core.models;



import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.annotation.Resource;
import javax.inject.Inject;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EmpClass {
    @Inject
    String empname;

    @Inject
    String empmail;

    public String getEmpname() {
        return empname;
    }

    public String getEmpmail() {
        return empmail;
    }
}


