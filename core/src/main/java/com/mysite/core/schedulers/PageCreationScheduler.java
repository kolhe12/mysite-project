package com.mysite.core.schedulers;
import com.mysite.core.service.PageCreationService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = PageCreationScheduler.Config.class)
public class PageCreationScheduler implements Runnable {

    @ObjectClassDefinition(name = "Page Creation Scheduler Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Cron Expression", description = "Cron expression to schedule the job")
        String scheduler_expression() default "* 0 0 ? * * *";
    }

    private static final Logger LOG = LoggerFactory.getLogger(PageCreationScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private PageCreationService pageCreationService;

    private AtomicInteger schedulerId;

    @Activate
    protected void activate(Config config) {
        schedulerId = new AtomicInteger();
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.scheduler_expression());
        scheduleOptions.name("Page Creation Scheduler - " + schedulerId.incrementAndGet());
        scheduleOptions.canRunConcurrently(false);

        scheduler.schedule(this, scheduleOptions);
        LOG.info("Page Creation Scheduler activated with cron expression: {}", config.scheduler_expression());
    }

    @Deactivate
    protected void deactivate() {
        LOG.info("Page Creation Scheduler deactivated.");
    }

    @Override
    public void run() {
        try {
            LOG.info("Page Creation Scheduler running.");
            pageCreationService.createPage();
        } catch (Exception e) {
            LOG.error("Error executing Page Creation Scheduler", e);
        }
    }
}
