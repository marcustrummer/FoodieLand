package com.adobe.aem.guides.foodieland.core.models;

import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.ValueMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.models.annotations.injectorspecific.ResourcePath;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.day.cq.wcm.api.designer.Style;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ComponentExporter;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { ComponentExporter.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = { "foodieland/components/content/stockplex" })
@Exporter(name = "jackson", extensions = { "json" })
public class Stockplex implements ComponentExporter
{
    protected static final String RESOURCE_TYPE = "foodieland/components/content/stockplex";
    @ScriptVariable
    private Resource resource;
    @ScriptVariable
    private Style currentStyle;
    @ValueMapValue
    private String symbol;
    @ValueMapValue
    private String summary;
    @ValueMapValue
    private String showStockDetails;
    @ResourcePath(path = "/content/stocks")
    private Resource stocksRoot;
    private double currentPrice;
    private Map<String, Object> data;
    
    @PostConstruct
    public void constructDataMap() {
        ValueMap tradeValues = null;
        if (this.stocksRoot != null) {
            final Resource stockResource = this.stocksRoot.getChild(this.symbol);
            if (stockResource != null) {
                final Resource lastTradeResource = stockResource.getChild("trade");
                if (lastTradeResource != null) {
                    tradeValues = lastTradeResource.getValueMap();
                }
            }
        }
        this.data = new HashMap<String, Object>();
        if (tradeValues != null) {
            this.currentPrice = (double)tradeValues.get("lastTrade", (Class)Double.class);
            this.data.put("Request Date", tradeValues.get("dayOfLastUpdate", (Class)String.class));
            this.data.put("Request Time", tradeValues.get("timeOfUpdate", (Class)String.class));
            this.data.put("UpDown", tradeValues.get("upDown", (Class)Double.class));
            this.data.put("Open Price", tradeValues.get("openPrice", (Class)Double.class));
            this.data.put("Range High", tradeValues.get("rangeHigh", (Class)Double.class));
            this.data.put("Range Low", tradeValues.get("rangeLow", (Class)Double.class));
            this.data.put("Volume", tradeValues.get("volume", (Class)Integer.class));
            this.data.put("Company", tradeValues.get("companyName", (Class)String.class));
            this.data.put("Sector", tradeValues.get("sector", (Class)String.class));
            this.data.put("52 Week Low", tradeValues.get("week52Low", (Class)Double.class));
        }
        else {
            this.data.put(this.symbol, "No import config found. If the StockListener.java class is apart of your project: Go to Sites console > Create Folder: stocks > Create Folder: ADBE");
        }
    }
    
    public String getSymbol() {
        return this.symbol;
    }
    
    public String getSummary() {
        return this.summary;
    }
    
    public String getShowStockDetails() {
        return this.showStockDetails;
    }
    
    public Double getCurrentPrice() {
        return this.currentPrice;
    }
    
    public Map<String, Object> getData() {
        return this.data;
    }
    
    public String getExportedType() {
        return this.resource.getResourceType();
    }
}
