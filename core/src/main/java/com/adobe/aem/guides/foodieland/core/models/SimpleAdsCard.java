package com.adobe.aem.guides.foodieland.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { ComponentExporter.class }, resourceType = {
        "foodieland/components/content/simpleadscard" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = { "json" })
public class SimpleAdsCard implements ComponentExporter {
    protected static final String RESOURCE_TYPE = "foodieland/components/content/simpleadscard";

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String textLink;


    @ScriptVariable
    protected Resource resource;

    public SimpleAdsCard() {

    }

    public String getFileReference() {
        return fileReference;
    }

    public String getTitle() {
        return title;
    }

    public String getTextLink() {
        return textLink;
    }

    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

}
