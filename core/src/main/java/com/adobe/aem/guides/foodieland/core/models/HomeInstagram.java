package com.adobe.aem.guides.foodieland.core.models;
import com.adobe.cq.export.json.ComponentExporter;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { ComponentExporter.class }, resourceType = {
        "foodieland/components/content/homeinstagram" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = { "json" })
public class HomeInstagram
        implements ComponentExporter {
    protected static final String RESOURCE_TYPE = "foodieland/components/content/homeinstagram";

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String heading;

    @ValueMapValue
    private String buttonLabel;

    @ValueMapValue
    private String buttonLinkTo;


    @ScriptVariable
    protected Resource resource;

    public HomeInstagram() {
    }

    public String getTitle() {
        return title;
    }

    public String getHeading() {
        return heading;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getButtonLinkTo() {
        return buttonLinkTo;
    }

    public String getAlt() {
        return "Example Alt Text";
    }

    public String getExportedType() {
        return resource.getResourceType();
    }
}
