package com.mysite.core.service;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = EmpService.class)
@Designate(ocd = EmpConfiguration.class)
public class EmpServiceImpl implements EmpService {


    private String name;
    private String email;

    @Activate
    @Modified
    protected void activate(EmpConfiguration config) {
        this.name = config.name();
        this.email = config.email();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }
}

