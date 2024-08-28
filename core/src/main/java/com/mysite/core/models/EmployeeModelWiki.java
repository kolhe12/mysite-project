package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
public class EmployeeModelWiki
{


    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public class EmployeeModel {

        @Self
        private Resource resource;

        @ValueMapValue
        @Optional
        private String text;

        @ValueMapValue
        @Optional
        private String image;

        private List<EmployeeWiki> employees;

        @PostConstruct
        protected void init() {
            employees = new ArrayList<>();
            Resource employeeResource = resource.getChild("employee");
            if (employeeResource != null) {
                for (Resource empDetailsResource : employeeResource.getChildren()) {
                    ValueMap empDetailsProps = empDetailsResource.getValueMap();
                    String empName = empDetailsProps.get("employeename", String.class);
                    String empEmail = empDetailsProps.get("employeemail", String.class);
                    int empId = empDetailsProps.get("employeeid", Integer.class);
                    String empDepartment = empDetailsProps.get("employeedep", String.class);
                    employees.add(new EmployeeWiki(empName, empEmail, empId, empDepartment));
                }
            }
        }

        public String getText() {
            return text;
        }

        public String getImage() {
            return image;
        }

        public List<EmployeeWiki> getEmployees() {
            return employees;
        }
    }




}
