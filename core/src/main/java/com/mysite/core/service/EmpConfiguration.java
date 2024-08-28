package com.mysite.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
 @ObjectClassDefinition(name = "Emp Configuration", description = "Employee Detail")
    public @interface EmpConfiguration {

        @AttributeDefinition(name = "Name", description = "Employee name")
        String name() default "ngggg";

        @AttributeDefinition(name = "Email", description = "Employee email")
        String email() default "nbgggg@gmail.com";
    }


