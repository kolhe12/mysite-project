package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.Binary;
import java.io.InputStream;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Asset Upload Servlet",
                "sling.servlet.methods=" + "POST",
                "sling.servlet.paths=" + "/bin/uploadAsset"
        })
public class AssetUploadServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(AssetUploadServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resourceResolver = null;
        Session session = null;
        InputStream fileInputStream = null;

        try {
            // Get the uploaded file and asset name from the request
            RequestParameter fileParameter = request.getRequestParameter("file");
            if (fileParameter == null) {
                log.error("File parameter is missing.");
                response.getWriter().write("{\"status\":\"error\", \"message\":\"File parameter is missing.\"}");
                return;
            }
            fileInputStream = fileParameter.getInputStream();
            String assetName = request.getParameter("assets");

            if (assetName == null || assetName.isEmpty()) {
                log.error("Asset name is missing.");
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Asset name is missing.\"}");
                return;
            }

            // Get the ResourceResolver
            resourceResolver = request.getResourceResolver();
            if (resourceResolver == null) {
                log.error("ResourceResolver is null.");
                response.getWriter().write("{\"status\":\"error\", \"message\":\"ResourceResolver is null.\"}");
                return;
            }

            // Specify the DAM location where the asset should be saved
            String damPath = "/content/dam/assetsupload";
            Resource damFolder = resourceResolver.getResource(damPath);

            // Ensure the DAM folder exists
            if (damFolder == null) {
                log.info("DAM folder does not exist. Creating new folder at {}", damPath);
                damFolder = ResourceUtil.getOrCreateResource(resourceResolver, damPath, "sling:Folder", "nt:unstructured", true);
            }

            // Create the asset node under DAM
            Node damNode = damFolder.adaptTo(Node.class);
            if (damNode != null) {
                // Create a new node for the asset with type dam:Asset
                Node assetNode = damNode.addNode(assetName, "dam:Asset");

                // Create the jcr:content subnode with type dam:AssetContent
                Node contentNode = assetNode.addNode("jcr:content", "dam:AssetContent");

                // Set the properties for the asset content
                session = resourceResolver.adaptTo(Session.class);
                if (session != null) {
                    ValueFactory valueFactory = session.getValueFactory();
                    Binary binary = valueFactory.createBinary(fileInputStream);

                    contentNode.setProperty("jcr:data", binary);
                    contentNode.setProperty("jcr:mimeType", fileParameter.getContentType());

                    // Create metadata node under jcr:content
                    Node metadataNode = contentNode.addNode("metadata", "nt:unstructured");
                    metadataNode.setProperty("dc:title", assetName); // Example metadata property

                    // Create renditions folder under jcr:content
                    Node renditionsFolder = contentNode.addNode("renditions", "sling:Folder");

                    // Add a sample rendition (thumbnail file) for demonstration
                    Node thumbnailNode = renditionsFolder.addNode("cq5dam.thumbnail.48.48.png", "nt:file");
                    thumbnailNode.setProperty("jcr:data", binary); // Set thumbnail data if available
                    thumbnailNode.setProperty("jcr:mimeType", fileParameter.getContentType());

                    // Save the session changes
                    session.save();

                    // Send a success response
                    response.getWriter().write("{\"status\":\"success\", \"message\":\"Asset uploaded successfully\"}");
                } else {
                    log.error("Failed to adapt session.");
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to adapt session.\"}");
                }
            } else {
                log.error("Failed to adapt DAM folder to Node.");
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to adapt DAM folder to Node.\"}");
            }

        } catch (Exception e) {
            log.error("Error during asset upload", e);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.toString() + "\"}");
        } finally {
            // Close the input stream
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("Error closing InputStream", e);
                }
            }

            // Close the session and resource resolver
            if (session != null && session.isLive()) {
                session.logout();
            }
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }
}
