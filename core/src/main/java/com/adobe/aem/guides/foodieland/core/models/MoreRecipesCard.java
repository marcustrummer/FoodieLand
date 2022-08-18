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
        "foodieland/components/content/morerecipescard" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = { "json" })
public class MoreRecipesCard implements ComponentExporter {
    protected static final String RESOURCE_TYPE = "foodieland/components/content/morerecipescard";

    @ValueMapValue
    private Boolean heartLike;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String prepareTime;

    @ValueMapValue
    private String mainIngredient;

    @ScriptVariable
    protected Resource resource;

    public MoreRecipesCard() {

    }


    public String getFileReference() {
        return fileReference;
    }

    public Boolean getHeartLike() {
        if (heartLike == true) {
            return this.heartLike;
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public String getPrepareTime() {
        return prepareTime;
    }

    public String getMainIngredient() {
        return mainIngredient;
    }

    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

}

