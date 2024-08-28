package com.mysite.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=AEM Workflow step",
                Constants.SERVICE_VENDOR + "=AEM Mysite",
                Constants.SERVICE_DESCRIPTION + "=custom workflow step."
        }
)
public class WorkflowStep implements WorkflowProcess{
    private static final Logger log = LoggerFactory.getLogger(CreatePageWorkflowProcess.class);

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
        log.info("==============================custom workflow step==================================");
    }
}
