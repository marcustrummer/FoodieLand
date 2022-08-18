package com.adobe.aem.guides.foodieland.core.models;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import java.time.LocalDate;
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
public class RecipeModel {

    @ValueMapValue(name = PROPERTY_RESOURCE_TYPE)
    @Default(values = "No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    private String message;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String recipeImg;




    @ValueMapValue
    private LocalDate date = LocalDate.now();

    @ValueMapValue
    private String tag1;

    @ValueMapValue
    private String tag2;


    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(Page::getPath).orElse("");

        message = "Resource type is: " + resourceType + "\n"
                + "Current page is:  " + currentPagePath + "\n";
    }

    public String getMessage() {
        return message;
    }

    public String getRecipeImg() {
        return recipeImg;
    }


    public String getTitle() {
        return StringUtils.isNotBlank(title) ? title : "Title here!";
    }


    public String getTag1() {
        return StringUtils.isNotBlank(tag1) ?  tag1: "tag1 here!";
    }

    public String getTag2() {
        return StringUtils.isNotBlank(tag2) ?  tag2 : "tag2 here!";
    }

    public LocalDate getDate() {
        return date;
    }
}
