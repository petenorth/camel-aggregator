package org.pfry.camel.aggregator.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregatorRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(AggregatorRouteBuilder.class);
	
	private String countryCode;
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public void configure() throws Exception {

		LOG.info("Country Code = " + getCountryCode());
		from("activemq:foo?selector=" + java.net.URLEncoder.encode("CountryCode='" + getCountryCode() + "'","UTF-8")).to("log:hello.world?level=INFO").to("direct:start");
		from("activemq:bar?selector=" + java.net.URLEncoder.encode("CountryCode='" + getCountryCode() + "'","UTF-8")).to("log:hello.world?level=INFO").to("direct:start");
		from("activemq:and?selector=" + java.net.URLEncoder.encode("CountryCode='" + getCountryCode() + "'","UTF-8")).to("log:hello.world?level=INFO").to("direct:start");
		
		from("direct:start")
	    .aggregate(header("aggregationid"))
	        .aggregationStrategy(new StringAggregationStrategy())
	        .completionSize(3).to("log:hello.world?level=INFO")
	    .to("file://{{aggregator.outputpath}}");

	}

}
