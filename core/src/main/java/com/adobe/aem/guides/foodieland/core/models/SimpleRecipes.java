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
        "foodieland/components/content/simplerecipes" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = { "json" })
public class SimpleRecipes implements ComponentExporter {
    protected static final String RESOURCE_TYPE = "foodieland/components/content/simplerecipes";

    @ValueMapValue(name = "jcr:title")
    private String title;

    @ValueMapValue
    private String text;

    @ScriptVariable
    protected Resource resource;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getAlt() {
        return "Example Alt Text";
    }

    public String getExportedType() {
        return resource.getResourceType();
    }

    public String getCategorieName() {
        return null;
    }

}
