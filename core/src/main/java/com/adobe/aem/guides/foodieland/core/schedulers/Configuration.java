package com.adobe.aem.guides.foodieland.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import java.lang.annotation.Annotation;

@ObjectClassDefinition(name = "Training Stock Importer")
public @interface Configuration {
    @AttributeDefinition(name = "Stock Symbol", description = "Characters representing the stock to be imported", type = AttributeType.STRING)
    String symbol() default "";
    
    @AttributeDefinition(name = "Expression", description = "Run every so often as defined in the cron-job expression.", type = AttributeType.STRING)
    String cronExpression() default "0 0/2 * * * ?";
    
    @AttributeDefinition(name = "Stock URL", description = "URL to request the stock data to be imported", type = AttributeType.STRING)
    String stock_url() default "https://raw.githubusercontent.com/Adobe-Marketing-Cloud/ADLS-Samples/master/stock-data/";
}
