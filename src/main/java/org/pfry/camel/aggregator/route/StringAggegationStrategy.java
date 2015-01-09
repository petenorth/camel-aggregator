package org.pfry.camel.aggregator.route;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//simply adds each exchange body on a new line 
class StringAggregationStrategy implements AggregationStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(StringAggregationStrategy.class);
			
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
        	LOG.info("aggregation id = " + newExchange.getIn().getHeader("aggregationid"));
            String newBody = newExchange.getIn().getBody(String.class);
            newExchange.getIn().setBody(newBody + "\n");
        	return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        oldExchange.getIn().setBody(oldBody + newBody + "\n");
        return oldExchange;
    }
}
