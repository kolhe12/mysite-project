package com.mysite.core.servlets;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.mysite.core.models.CreateTagModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/example/tag"
        }
)
public class GetTagServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GetTagServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        ResourceResolver resolver = request.getResourceResolver();
        TagManager tagManager = resolver.adaptTo(TagManager.class);
        Tag tag = tagManager.resolve("/content/cq:tags/color");
        try {
            tagManager.createTag("/content/cq:tags/color","pink","newTag");
        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        }

        Iterator<Tag>listChildren = tag.listChildren();
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        while (listChildren.hasNext())
        {
            Tag childTag = listChildren.next();
            jsonObject.add("title",childTag.getTitle());
            jsonObject.add("Path",childTag.getPath());
            jsonArray.add(jsonObject);

        }
        response.getWriter().write(jsonArray.build().toString());
    }
}

