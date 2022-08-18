package com.adobe.aem.guides.foodieland.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class InstagramModel {

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE)
    @Default(values="No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;
    
    private String message;

    
    @ValueMapValue
    private String title;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String buttonText;

    @ValueMapValue
    private String instagramImg;
    







    public String getInstagramImg() {
        return instagramImg;
    }

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(Page::getPath).orElse("");

        message = 
             "Resource type is: " + resourceType + "\n"
            + "Current page is:  " + currentPagePath + "\n";
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return StringUtils.isNotBlank(this.title) ? this.title : "Title here!";
    }



    public String getText() {
        return StringUtils.isNotBlank(this.text) ? this.text : "text here!";
    }

    public String getButtonText() {
        return buttonText;
    }




}
