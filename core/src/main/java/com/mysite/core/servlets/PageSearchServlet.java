package com.mysite.core.servlets;

import com.day.cq.search.PredicateGroup;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.apache.sling.commons.json.io.JSONWriter;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component(service = { Servlet.class })
@SlingServletPaths("/bin/searchPages")
public class PageSearchServlet extends SlingAllMethodsServlet {

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resolver = null;
        try {
            resolver = request.getResourceResolver();

            // Query parameters
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("path", "/content/mysite/us/en"); // Root path of your site
            queryMap.put("type", "cq:ContentPage");
            queryMap.put("p.limit", "-1");

            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resolver.adaptTo(Session.class));
            SearchResult result = query.getResult();

            JSONWriter writer = new JSONWriter(response.getWriter());
            writer.array();

            for (Hit hit : result.getHits()) {
                Resource page = hit.getResource();
                writer.object();
                writer.key("path").value(page.getPath());
                writer.key("title").value(page.getName());
                writer.endObject();
            }

            writer.endArray();
        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
