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
package com.adobe.aem.guides.foodieland.core.listeners;

import org.osgi.service.cm.Configuration;
import java.util.Iterator;
import org.osgi.framework.InvalidSyntaxException;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.sling.api.resource.observation.ResourceChange;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.api.resource.observation.ResourceChangeListener;

@Component(immediate = true, property = { "resource.paths=/content/stocks", "resource.change.types=ADDED", "resource.change.types=REMOVED" })
public class StockListener implements ResourceChangeListener
{
    private final Logger logger;
    private final String stockImportSchedulerPID = "com.adobe.aem.guides.foodieland.core.schedulers.StockImportScheduler";
    @Reference
    private ConfigurationAdmin configAdmin;
    
    public StockListener() {
        this.logger = LoggerFactory.getLogger((Class)this.getClass());
    }
    
    public void onChange(final List<ResourceChange> changes) {
        for (final ResourceChange change : changes) {
            this.logger.info("Resource Change Detected: {}", (Object)change);
            String folderName = change.getPath().substring(change.getPath().lastIndexOf("/") + 1);
            if (folderName.length() == 4 && folderName.matches("^[a-zA-Z]*$") && !folderName.equals("trade")) {
                folderName = folderName.toUpperCase();
                if (change.getType().equals((Object)ResourceChange.ChangeType.ADDED)) {
                    try {
                        final Configuration config = this.configAdmin.createFactoryConfiguration("com.adobe.training.core.schedulers.StockImportScheduler");
                        final Dictionary<String, Object> properties = new Hashtable<String, Object>();
                        properties.put("symbol", folderName);
                        config.update((Dictionary)properties);
                    }
                    catch (IOException e) {
                        this.logger.error("Could not add OSGi config for: " + folderName);
                    }
                }
                else {
                    if (!change.getType().equals((Object)ResourceChange.ChangeType.REMOVED)) {
                        continue;
                    }
                    try {
                        final String filter = "(service.factoryPid=com.adobe.aem.guides.foodieland.core.schedulers.StockImportScheduler)";
                        final Configuration[] listConfigurations;
                        final Configuration[] configArray = listConfigurations = this.configAdmin.listConfigurations(filter);
                        for (final Configuration config2 : listConfigurations) {
                            final Dictionary<String, Object> properties2 = (Dictionary<String, Object>)config2.getProperties();
                            if (properties2.get("symbol").equals(folderName)) {
                                this.logger.error("Removing config for: " + folderName);
                                config2.delete();
                            }
                        }
                    }
                    catch (IOException e) {
                        this.logger.error("Could not delete OSGi config for: " + folderName);
                    }
                    catch (InvalidSyntaxException e2) {
                        this.logger.error("Could not delete OSGi config for: " + folderName);
                    }
                }
            }
        }
    }
}

