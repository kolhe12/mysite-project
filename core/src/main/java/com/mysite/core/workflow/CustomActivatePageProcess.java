package com.mysite.core.workflow;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;

@Component(
        service = WorkflowProcess.class,
        property = {
                "process.label=Custom Activate Page Process"
        }
)
public class CustomActivatePageProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(CustomActivatePageProcess.class);

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
        String path = item.getWorkflowData().getPayload().toString();
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {

            replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.ACTIVATE, path);
            LOG.info("Page activated: {}", path);
        } catch (Exception e) {
            LOG.error("Error activating page: {}", path, e);
            throw new WorkflowException(e);
        }
    }
}
