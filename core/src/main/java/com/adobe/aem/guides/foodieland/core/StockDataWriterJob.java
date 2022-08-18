package com.adobe.aem.guides.foodieland.core;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.ResourceUtil;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import javax.jcr.RepositoryException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import org.apache.sling.event.jobs.Job;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.event.jobs.consumer.JobConsumer;

@Component(immediate = true, service = { JobConsumer.class }, property = { "job.topics=com/adobe/aem/guide/foodieland/core/jobs/stockimportjob" })
public class StockDataWriterJob implements JobConsumer
{
    private final Logger logger;
    public static final String STOCK_IMPORT_FOLDER = "/content/stocks";
    public static final String COMPANY = "companyName";
    public static final String SECTOR = "sector";
    public static final String LASTTRADE = "lastTrade";
    public static final String UPDATETIME = "timeOfUpdate";
    public static final String DAYOFUPDATE = "dayOfLastUpdate";
    public static final String OPENPRICE = "openPrice";
    public static final String RANGEHIGH = "rangeHigh";
    public static final String RANGELOW = "rangeLow";
    public static final String VOLUME = "volume";
    public static final String UPDOWN = "upDown";
    public static final String WEEK52LOW = "week52Low";
    public static final String WEEK52HIGH = "week52High";
    public static final String YTDCHANGE = "ytdPercentageChange";
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    public StockDataWriterJob() {
        this.logger = LoggerFactory.getLogger((Class)this.getClass());
    }
    
    public JobConsumer.JobResult process(final Job job) {
        final String symbol = job.getProperty("symbol").toString();
        final String stock_url = job.getProperty("url").toString();
        final String stockUrl = stock_url + symbol + ".json";
        HttpsURLConnection request = null;
        try {
            final URL sourceUrl = new URL(stockUrl);
            request = (HttpsURLConnection)sourceUrl.openConnection();
            request.setConnectTimeout(5000);
            request.setReadTimeout(10000);
            request.connect();
        }
        catch (SocketTimeoutException e4) {
            this.logger.error("5 Second Timeout occured.");
            return JobConsumer.JobResult.FAILED;
        }
        catch (IOException e5) {
            this.logger.error("The stock symbol: " + symbol + " does not exist...");
            return JobConsumer.JobResult.FAILED;
        }
        final ObjectMapper objMapper = new ObjectMapper();
        final JsonFactory factory = new JsonFactory();
        JobConsumer.JobResult jobResult = null;
        if (request != null) {
            try {
                final JsonParser parser = factory.createParser((Reader)new InputStreamReader((InputStream)request.getContent()));
                try {
                    final Map<String, String> allQuoteData = (Map<String, String>)objMapper.readValue(parser, (TypeReference)new TypeReference<Map<String, String>>() {});
                    this.logger.info("Last trade for stock symbol {} was {}", (Object)symbol, (Object)allQuoteData.get("latestPrice"));
                    jobResult = this.writeToRepository(symbol, allQuoteData, null);
                    if (parser != null) {
                        parser.close();
                    }
                }
                catch (Throwable t) {
                    if (parser != null) {
                        try {
                            parser.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (RepositoryException e) {
                this.logger.error("Cannot write stock info for " + symbol + " to the JCR: ", (Throwable)e);
                return JobConsumer.JobResult.FAILED;
            }
            catch (JsonParseException e2) {
                this.logger.error("Cannot parse stock info for " + symbol, (Throwable)e2);
                return JobConsumer.JobResult.FAILED;
            }
            catch (IOException e3) {
                this.logger.error("IOException: ", (Throwable)e3);
                return JobConsumer.JobResult.FAILED;
            }
        }
        return jobResult;
    }
    
    private JobConsumer.JobResult writeToRepository(final String stockSymbol, final Map<String, String> quoteData, final Exception ex) throws RepositoryException {
        this.logger.info("Stock Symbol: " + stockSymbol);
        this.logger.info("JsonObject to Write: " + quoteData.toString());
        final Map<String, Object> serviceParams = new HashMap<String, Object>();
        serviceParams.put("sling.service.subservice", "training");
        try {
            final ResourceResolver resourceResolver = this.resourceResolverFactory.getServiceResourceResolver((Map)serviceParams);
            try {
                final ZoneId timeZone = ZoneId.of("America/New_York");
                final long latestUpdateTime = Long.parseLong(quoteData.get("latestUpdate"));
                final LocalDateTime timePerLatestUpdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(latestUpdateTime), timeZone);
                final ZonedDateTime timeWithZone = ZonedDateTime.of(timePerLatestUpdate, timeZone);
                final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a zz");
                final String UpdateTimeOfDay = timeWithZone.format(timeFormatter);
                final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E MMMM d, yyyy");
                final String dayOfUpdate = timeWithZone.format(dayFormatter);
                final Double lastPrice = Double.parseDouble(quoteData.get("latestPrice"));
                final Double open = Double.parseDouble(quoteData.get("open"));
                final Double high = Double.parseDouble(quoteData.get("high"));
                final Double low = Double.parseDouble(quoteData.get("low"));
                final Long latestVolume = Long.parseLong(quoteData.get("latestVolume"));
                final Double change = Double.parseDouble(quoteData.get("change"));
                final Double week52High = Double.parseDouble(quoteData.get("week52High"));
                final Double week52Low = Double.parseDouble(quoteData.get("week52Low"));
                final Double ytdChange = Double.parseDouble(quoteData.get("ytdChange"));
                final String stockPath = "/content/stocks/" + stockSymbol;
                final String tradePath = stockPath + "/trade";
                Resource trade = resourceResolver.getResource(tradePath);
                final Resource stockFolder = ResourceUtil.getOrCreateResource(resourceResolver, stockPath, "", "", false);
                if (trade == null) {
                    final Map<String, Object> stockData = new HashMap<String, Object>() {
                        {
                            ((HashMap<String, Object>)this).put("jcr:primaryType", "nt:unstructured");
                        }
                    };
                    trade = resourceResolver.create(stockFolder, "trade", (Map)stockData);
                }
                final ModifiableValueMap stockData2 = (ModifiableValueMap)trade.adaptTo((Class)ModifiableValueMap.class);
                stockData2.put((String)"companyName", (Object)quoteData.get("companyName"));
                stockData2.put((String)"sector", (Object)quoteData.get("sector"));
                stockData2.put((String)"timeOfUpdate", (Object)UpdateTimeOfDay);
                stockData2.put((String)"dayOfLastUpdate", (Object)dayOfUpdate);
                stockData2.put((String)"lastTrade", (Object)lastPrice);
                stockData2.put((String)"openPrice", (Object)open);
                stockData2.put((String)"rangeHigh", (Object)high);
                stockData2.put((String)"rangeLow", (Object)low);
                stockData2.put((String)"volume", (Object)latestVolume);
                stockData2.put((String)"upDown", (Object)change);
                stockData2.put((String)"week52High", (Object)week52High);
                stockData2.put((String)"week52Low", (Object)week52Low);
                stockData2.put((String)"ytdPercentageChange", (Object)ytdChange);
                this.logger.info("Update trade data");
                resourceResolver.commit();
                if (resourceResolver != null) {
                    resourceResolver.close();
                }
            }
            catch (Throwable t) {
                if (resourceResolver != null) {
                    try {
                        resourceResolver.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (LoginException | PersistenceException ex2) {
            final Exception e = ex;
            this.logger.error("Exception with writing resource: ", (Throwable)e);
            return JobConsumer.JobResult.FAILED;
        }
        return JobConsumer.JobResult.OK;
    }
}