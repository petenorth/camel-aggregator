package com.fusesource.byexample.hellocamel.route;

import java.util.Random;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

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
    
    @EndpointInject(uri = "mock:file:/home/pfry/aggregator/")
    protected MockEndpoint fileEndpoint;
    
	@Test
	public void test() throws InterruptedException {
        String aggregationid = Integer.toString(new Random().nextInt());
    
        fooProducer.sendBodyAndHeader("Scott", "aggregationid", aggregationid);
        barProducer.sendBodyAndHeader("Brad", "aggregationid", aggregationid);
        andProducer.sendBodyAndHeader("Gary", "aggregationid", aggregationid);
		
		directEndpoint.expectedMessageCount(3);
		directEndpoint.assertIsSatisfied();
		
		fileEndpoint.expectedMessageCount(1);
		fileEndpoint.assertIsSatisfied();
	}
	
	@Override
	public String isMockEndpoints() {
		return "*";
	}
	
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new AggregatorRouteBuilder();
    }
    
    @Override
    protected CamelContext createCamelContext() throws Exception{
    	CamelContext camelContext = super.createCamelContext();
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(BROKER_URL);
        activeMQComponent.setPassword(BROKER_PASSWORD);
        activeMQComponent.setUserName(BROKER_USERNAME);
    	camelContext.addComponent("activemq", activeMQComponent);
    	return camelContext;
    }

}
