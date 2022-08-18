package com.adobe.aem.guides.foodieland.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;


@Model(adaptables={SlingHttpServletRequest.class}, resourceType={"foodieland/components/structure/header"}, defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
public class Header
{
  protected static final String RESOURCE_TYPE = "foodieland/components/structure/header";
  @ScriptVariable
  private Page currentPage;
  private Page root;
  private ArrayList<Page> pages;
  
  public Header() {}
  
  @PostConstruct
  private void setup()
  {
    root = currentPage.getAbsoluteParent(2);
    if (root == null) {
      root = currentPage;
    }
    
    pages = new ArrayList();
    Iterator<Page> it = root.listChildren(new PageFilter());
    while (it.hasNext()) {
      pages.add((Page)it.next());
    }
  }
  
  public Page getRootPage()
  {
    return root;
  }
  
  public ArrayList<Page> getItems() {
    return new ArrayList(pages);
  }
}
