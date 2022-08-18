/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.aem.guides.foodieland.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Activate;
import org.slf4j.LoggerFactory;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.apache.sling.event.jobs.JobBuilder;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.event.jobs.JobManager;
import org.slf4j.Logger;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, configurationPid = { "com.adobe.aem.guides.foodieland.core.schedulers.StockImportScheduler" }, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = Configuration.class, factory = true)
public class StockImportScheduler
{
    public static final String JOB_TOPIC_STOCKIMPORT = "com/adobe/aem/guides/foodieland/core/jobs/stockimportjob";
    public static final String JOB_PROP_SYMBOL = "symbol";
    public static final String JOB_PROP_URL = "url";
    public static final String DEFAULT_IMPORT_URL = "https://raw.githubusercontent.com/Adobe-Marketing-Cloud/ADLS-Samples/master/stock-data/";
    private final Logger logger;
    @Reference
    private JobManager jobManager;
    private int schedulerID;
    private JobBuilder jobBuilder;
    private JobBuilder.ScheduleBuilder scheduleBuilder;
    private ScheduledJobInfo theScheduledJob;
    
    public StockImportScheduler() {
        this.logger = LoggerFactory.getLogger((Class)this.getClass());
    }
    
    @Activate
    @Modified
    protected void activate(final Configuration config) {
        this.logger.info("****StockImport ScheduledJob '{}' with ID: '{}' Activated", (Object)config.symbol(), (Object)this.schedulerID);
        this.schedulerID = config.symbol().hashCode();
        this.startScheduledJob(config);
    }
    
    @Modified
    protected void modified(final Configuration config) {
        this.removeScheduler(config);
        this.schedulerID = config.symbol().hashCode() + 1;
        this.startScheduledJob(config);
    }
    
    @Deactivate
    protected void deactivate(final Configuration config) {
        this.removeScheduler(config);
    }
    
    private void startScheduledJob(final Configuration config) {
        this.jobBuilder = this.jobManager.createJob("com/adobe/aem/guides/foodieland/core/jobs/stockimportjob");
        final HashMap<String, Object> jobProps = new HashMap<String, Object>();
        jobProps.put("symbol", config.symbol());
        jobProps.put("url", config.stock_url());
        this.jobBuilder.properties((Map)jobProps);
        (this.scheduleBuilder = this.jobBuilder.schedule()).cron(config.cronExpression());
        this.theScheduledJob = this.scheduleBuilder.add();
        if (this.theScheduledJob == null) {
            final List<String> errors = new ArrayList<String>();
            this.scheduleBuilder.add((List)errors);
        }
        else {
            this.logger.info("ScheduledJob added to the Queue.  Topic: " + this.theScheduledJob.getJobTopic() + "  Properties: " + this.theScheduledJob.getJobProperties().toString() + " Next Execution: " + this.theScheduledJob.getNextScheduledExecution().toString());
        }
    }
    
    private void removeScheduler(final Configuration config) {
        this.logger.info("*******Removing '{}' ScheduledJob, with ID: '{}'", (Object)config.symbol(), (Object)this.schedulerID);
        this.theScheduledJob.unschedule();
    }
    
    @ObjectClassDefinition(name = "Training Stock Importer")
    public @interface Configuration {
        @AttributeDefinition(name = "Stock Symbol", description = "Characters representing the stock to be imported", type = AttributeType.STRING)
        String symbol() default "";
        
        @AttributeDefinition(name = "Expression", description = "Run every so often as defined in the cron-job expression.", type = AttributeType.STRING)
        String cronExpression() default "0 0/2 * * * ?";
        
        @AttributeDefinition(name = "Stock URL", description = "URL to request the stock data to be imported", type = AttributeType.STRING)
        String stock_url() default "https://raw.githubusercontent.com/Adobe-Marketing-Cloud/ADLS-Samples/master/stock-data/";

        
    }
    

    public void run() {
    }
}
