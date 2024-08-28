package com.mysite.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.mysite.core.models.ArticleDialogModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;


import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Component(service = {Servlet.class})
@SlingServletPaths("/bin/pageSearch")
public class ArticleRedirectServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRedirectServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String tagName = request.getParameter("tag");
        String root_path = request.getParameter("root_path");
        if (!Objects.nonNull(root_path)) root_path = "/content/mysite/us/en";
        LOGGER.info("Received root_path: {}", root_path);
        String type = request.getParameter("type");
        if (!Objects.nonNull(type)) type = "cq:Page";
        LOGGER.info("Received type: {}", type);
        if (!Objects.nonNull(tagName)) {
            out.write("{\"error\": \"Please provide the tag name you are searching for\"}");
            LOGGER.info("No tag name provided in request");
            return;
        }

        LOGGER.info("Searching for pages with tag: {}", tagName);

        ResourceResolver resolver = request.getResourceResolver();
        List<Map<String, Object>> pages = new ArrayList<>();
        TagManager tagManager = resolver.adaptTo(TagManager.class);
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);

        Map<String, String> param = new HashMap<>();
        param.put("path", root_path);
        param.put("type", type);
        Query query = queryBuilder.createQuery(PredicateGroup.create(param), resolver.adaptTo(Session.class));

        LOGGER.info("Execute query: {}", query);
        SearchResult result = query.getResult();

        Iterator<Hit> hits = result.getHits().iterator();
        if (!hits.hasNext()) {
            out.write("{\"error\": \"No resource found under the root_path\"}");
            LOGGER.warn("No resources found under the path: {}", root_path);
            return;
        }

        while (hits.hasNext()) {
            Hit hit = hits.next();
            Resource resource = null;
            try {
                resource = hit.getResource();
                LOGGER.debug("Processing resource: {}", resource.getPath());

                ArticleDialogModel articleModel = resource.adaptTo(ArticleDialogModel.class);
                if (articleModel != null) {
                    List<ArticleDialogModel.TagDetails> tagDetailsList = articleModel.getTagDetails();


                    Map<String, Object> pageDetails = new HashMap<>();
                    pageDetails.put("path", resource.getPath());
                    pages.add(pageDetails);
                }
            } catch (RepositoryException e) {
                LOGGER.error("RepositoryException while processing search result", e);
            }
        }

        JSONWriter jsonWriter = new JSONWriter(out);
        try {
            jsonWriter.object();
            jsonWriter.key("tag").value(tagName);
            Tag resolvedTag = tagManager.resolve(tagName);
            if (resolvedTag != null) {
                jsonWriter.key("tagTitle").value(resolvedTag.getTitle());
                jsonWriter.key("tagDescription").value(resolvedTag.getDescription());
            } else {
                jsonWriter.key("tagTitle").value("Tag not found: " + tagName);
                LOGGER.warn("Tag not found: {}", tagName);
            }
            jsonWriter.key("pages");
            jsonWriter.array();
            for (Map<String, Object> page : pages) {
                jsonWriter.object();
                jsonWriter.key("path").value(page.get("path"));

                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
        } catch (JSONException e) {
            LOGGER.error("Error writing JSON response", e);
            out.write("{\"error\": \"Error generating JSON response\"}");
        } finally {
            out.flush();
            out.close();
        }
    }
}







/*
package com.mysite.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.mysite.core.models.ArticleDialogModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = {Servlet.class}, property = {
        Constants.SERVICE_DESCRIPTION + "=Servlet to get tag, page, and component details",
        "sling.servlet.path=bin/search",
        "sling.servlet.methods=GET"
})
public class ArticleRedirectServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRedirectServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get parameters from request
        String tagName = request.getParameter("tag");
        String rootPath = request.getParameter("root_path");
        String type = request.getParameter("type");

        LOGGER.info("Received parameters - rootPath: {}, type: {}, tagName: {}", rootPath, type, tagName);

        if (tagName == null || tagName.isEmpty()) {
            out.write("{\"error\": \"Tag name is missing\"}");
            LOGGER.info("No tag name provided");
            return;
        }

        if (rootPath == null || rootPath.isEmpty()) {
            rootPath = "/content/mysite/us/en";
        }

        if (type == null || type.isEmpty()) {
            type = "cq:Page";
        }

        ResourceResolver resolver = request.getResourceResolver();
        TagManager tagManager = resolver.adaptTo(TagManager.class);
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);

        // Build the query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("path", rootPath);
        queryParams.put("type", type);
        queryParams.put("tagid.property", "jcr:content/cq:tags");
        queryParams.put("tagid", tagName);

        // Create and execute the query
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryParams), resolver.adaptTo(Session.class));
        LOGGER.info("Executing query: {}", query);
        SearchResult result = query.getResult();

        if (result.getHits().isEmpty()) {
            out.write("{\"error\": \"No resources found under the root path\"}");
            LOGGER.warn("No resources found under the path: {}", rootPath);
            return;
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("tag", tagName);
        Tag resolvedTag = tagManager.resolve(tagName);
        if (resolvedTag != null) {
            jsonResponse.put("tagTitle", resolvedTag.getTitle());
            jsonResponse.put("tagDescription", resolvedTag.getDescription());
        } else {
            jsonResponse.put("tagTitle", "Tag not found: " + tagName);
            LOGGER.warn("Tag not found: {}", tagName);
        }

        JSONArray pagesArray = new JSONArray();

        for (Hit hit : result.getHits()) {
            Resource pageResource;
            try {
                pageResource = hit.getResource();
            } catch (RepositoryException e) {
                LOGGER.error("RepositoryException: ", e);
                continue;
            }
            LOGGER.debug("Processing resource: {}", pageResource.getPath());

            Resource componentResource = pageResource.getChild("jcr:content/root/articlecomp");
            if (componentResource == null) {
                LOGGER.warn("Component resource not found under the page: {}", pageResource.getPath());
                continue;
            }

            ArticleDialogModel articleModel = componentResource.adaptTo(ArticleDialogModel.class);
            if (articleModel == null) {
                LOGGER.warn("ArticleDialogModel not found for resource: {}", componentResource.getPath());
                continue;
            }

            JSONObject pageDetails = new JSONObject();
            pageDetails.put("path", pageResource.getPath());
            pageDetails.put("title", articleModel.getTitle());
            pageDetails.put("image", articleModel.getImage());
            pageDetails.put("description", articleModel.getDescription());

            JSONArray tagDetailsArray = new JSONArray();
            for (ArticleDialogModel.TagDetails tagDetails : articleModel.getTagDetails()) {
                JSONObject tagDetailsObject = new JSONObject();
                tagDetailsObject.put("title", tagDetails.getTitle());
                tagDetailsObject.put("description", tagDetails.getDescription());
                tagDetailsObject.put("resourceType", tagDetails.getResourceType());
                tagDetailsArray.put(tagDetailsObject);
            }

            pageDetails.put("tags", tagDetailsArray);
            pagesArray.put(pageDetails);
        }

        jsonResponse.put("pages", pagesArray);

        out.write(jsonResponse.toString());
        out.flush();
    }
}
*/
