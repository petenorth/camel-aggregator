package com.fusesource.byexample.hellocamel.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

public class AggregatorRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("activemq:foo").to("direct:start");
		from("activemq:bar").to("direct:start");
		from("activemq:and").to("direct:start");
		
		from("direct:start")
	    .aggregate(header("aggregationid"))
	        .aggregationStrategy(new StringAggregationStrategy())
	        .completionSize(3)
	    .to("file:///home/pfry/aggregator/");

	}

}
