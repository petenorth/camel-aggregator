package com.fusesource.byexample.hellocamel.route;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.pfry.camel.aggregator.route.AggregatorRouteBuilder;

public class AggregatorRouteBuilderTest extends CamelTestSupport {
	
	private static final String BROKER_USERNAME = "admin";
    private static final String BROKER_PASSWORD = "admin";
	//private static final String BROKER_URL = "tcp://localhost:61616";
	//private static final String BROKER_URL = "failover://tcp://amq-node2:61616,tcp://amq-node1:61616";
	private static final String BROKER_URL = "vm://localhost?broker.persistent=false";


    @Produce(uri = "activemq:queue:foo")
    protected ProducerTemplate fooProducer;
    
    @Produce(uri = "activemq:queue:bar")
    protected ProducerTemplate barProducer;
    
    @Produce(uri = "activemq:queue:and")
    protected ProducerTemplate andProducer;
    
    @EndpointInject( uri = "mock:direct:start")
    protected MockEndpoint directEndpoint;
    
	@Test
	public void test() throws Exception {
		
		String outputpath = context().resolvePropertyPlaceholders("{{aggregator.outputpath}}");
		MockEndpoint fileEndpoint = getMockEndpoint("mock:file:" + outputpath);
		
        String aggregationid = Integer.toString(new Random().nextInt());
        String countryCode = "UK";
        
        Map<String, Object> headers = createHeaders(aggregationid,
				countryCode);
    
        fooProducer.sendBodyAndHeaders("Scott", headers);
        barProducer.sendBodyAndHeaders("Brad", headers);
        andProducer.sendBodyAndHeaders("Gary", headers);
		
		directEndpoint.expectedMessageCount(3);
		directEndpoint.assertIsSatisfied();
		
		fileEndpoint.expectedMessageCount(1);
		fileEndpoint.assertIsSatisfied();
	}

	private Map<String, Object> createHeaders(String aggregationid,
			String countryCode) {
		Map<String,Object> headersFoo = new HashMap<String,Object>();
        headersFoo.put("aggregationid", aggregationid);
		headersFoo.put("CountryCode", countryCode);
		return headersFoo;
	}
	
	@Override
	public String isMockEndpoints() {
		return "*";
	}
	
    @Override
    protected RouteBuilder createRouteBuilder() {
        AggregatorRouteBuilder routeBuilder = new AggregatorRouteBuilder();
        routeBuilder.setCountryCode("UK");
        return routeBuilder;
    }
    
    @Override
    protected CamelContext createCamelContext() throws Exception{
    	CamelContext camelContext = super.createCamelContext();
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(BROKER_URL);
        activeMQComponent.setPassword(BROKER_PASSWORD);
        activeMQComponent.setUserName(BROKER_USERNAME);
    	camelContext.addComponent("activemq", activeMQComponent);
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("file://" + System.getProperty("properties.path"));
        camelContext.addComponent("properties", pc);
    	return camelContext;
    }

}
