package com.mysite.core.service;

import com.mysite.core.servlets.ArticleRedirectServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = PracticeOSGi_service.class)
public class PracticeOSGi_serviceImpl implements PracticeOSGi_service
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRedirectServlet.class);
    @Override
    public String getName() {
        LOGGER.info("getName",getName());
        return "Iam OSGi Service..";
    }
}
