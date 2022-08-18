package com.adobe.aem.guides.foodieland.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;


@Model(adaptables={SlingHttpServletRequest.class}, resourceType={"foodieland/components/structure/footer"}, defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
public class Footer
{
  protected static final String RESOURCE_TYPE = "foodieland/components/structure/footer";
  @ScriptVariable
  private Style currentStyle;
  @ScriptVariable
  private Page currentPage;
  @ScriptVariable
  private PageManager pageManager;
  @ScriptVariable
  private Logger log;
  private ArrayList<Page> pageList;
  private int curYear;
  
  public Footer() {}
  
  @PostConstruct
  public void setup()
  {
    pageList = new ArrayList();
    Object policyValue = currentStyle.get("pages");
    String[] pagesFromPolicy = null;
    

    if ((policyValue instanceof String[])) {
      pagesFromPolicy = (String[])policyValue;
    } else if ((policyValue instanceof String)) {
      pagesFromPolicy = new String[1];
      pagesFromPolicy[0] = ((String)policyValue);
    }
    
    if ((pagesFromPolicy != null) && (pagesFromPolicy.length > 0))
    {
      for (String pathPath : pagesFromPolicy) {
        Page page = pageManager.getPage(pathPath);
        pageList.add(page);
      }
    }
    else
    {
      Page root = currentPage.getAbsoluteParent(2);
      if (root == null)
        root = currentPage;
      Object it = root.listChildren(new PageFilter());
      while (((Iterator)it).hasNext()) {
        pageList.add((Page)((Iterator)it).next());
      }
    }
    

    curYear = Calendar.getInstance().get(1);
  }
  
  public int getCurrentYear()
  {
    return curYear;
  }
  
  public ArrayList<Page> getItems() {
    return new ArrayList(pageList);
  }
}
