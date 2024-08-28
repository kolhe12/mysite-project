package com.mysite.core.servlets;
import com.day.cq.search.PredicateGroup;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.apache.sling.commons.json.io.JSONWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component(service = { Servlet.class })
@SlingServletPaths("/bin/SearchPageBasedOnPageName")
public class SearchPageBasedOnPageName extends SlingAllMethodsServlet {


        private static final Logger log = LoggerFactory.getLogger(PageSearchServlet.class);

        @Reference
        private QueryBuilder queryBuilder;

        @Override
        protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
            try (ResourceResolver resolver = request.getResourceResolver()) {

                // Get the page name parameter from the request
                String pageName = request.getParameter("page");

                if (pageName == null || pageName.isEmpty()) {
                    response.getWriter().write("Error: page name parameter is required");
                    return;
                }

                // Query parameters
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("path", "/content/mysite/us/en"); // Root path of your site
                queryMap.put("type", "cq:ContentPage");
                queryMap.put("nodename", pageName); // Search by page name


                log.info("Query parameters: {}", queryMap);

                Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resolver.adaptTo(Session.class));
                SearchResult result = query.getResult();

                log.info("Query executed. Total matches: {}", result.getTotalMatches());

                JSONWriter writer = new JSONWriter(response.getWriter());
                writer.array();

                for (Hit hit : result.getHits()) {
                    Resource page = hit.getResource();
                    Resource jcrContent = page.getChild("jcr:content");
                    if (jcrContent != null) {
                        log.info("Page found: {}", jcrContent.getPath());
                        writer.object();
                        writer.key("path").value(jcrContent.getPath());
                        writer.key("title").value(page.getName());
                        writer.endObject();
                    } else {
                        log.warn("jcr:content not found for page: {}", page.getPath());
                    }
                }

                writer.endArray();
            } catch (Exception e) {
                log.error("Error during search", e);
                response.getWriter().write("Error: " + e.getMessage());
            }
        }
    }





