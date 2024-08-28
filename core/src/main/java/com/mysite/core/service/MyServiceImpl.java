package com.mysite.core.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;

@Component(service = MyService.class)
public class MyServiceImpl implements MyService {

    private static final String SERVLET_URL = "http://localhost:4502/bin/getServiceMyServlet"; // Replace with your servlet URL


    @Override
    public String callMyServlet() {
            String servletResponse = null;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(SERVLET_URL);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    servletResponse = EntityUtils.toString(entity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return servletResponse;
        }
    }
