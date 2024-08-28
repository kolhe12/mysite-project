package com.mysite.core.workflow;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=Mysite Create Page Workflow Process",
                Constants.SERVICE_VENDOR + "=AEM Mysite",
                Constants.SERVICE_DESCRIPTION + "=Custom workflow step to create a page."
        }
)
public class CreatePageWorkflowProcess implements WorkflowProcess {
    private static final Logger log = LoggerFactory.getLogger(CreatePageWorkflowProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) {
        log.info("Starting the Create Page Workflow Process");

        Session session = null;

        try {
            // Retrieve the necessary arguments from process arguments
            String parentPath = processArguments.get("parentPath","string");
            String pageName = processArguments.get("pageName", "string");
            String template = processArguments.get("template", "string");
            String title = processArguments.get("title", "string");

            // Adapt the workflow session to a JCR session
            session = workflowSession.adaptTo(Session.class);

            // Create the page
            if (session != null) {
                Node parentNode = session.getNode(parentPath);
                if (parentNode != null) {
                    Node newPageNode = parentNode.addNode(pageName, "cq:Page");
                    Node newPageContentNode = newPageNode.addNode("jcr:content", "cq:PageContent");
                    newPageContentNode.setProperty("cq:template", template);
                    newPageContentNode.setProperty("jcr:title", title);

                    // Save the session to persist changes
                    session.save();
                    log.info("Page created successfully at: " + parentPath + "/" + pageName);
                } else {
                    log.error("Parent node is null");
                }
            } else {
                log.error("Session is null");
            }
        } catch (RepositoryException e) {
            log.error("Error in creating page", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
