/*
 * Copyright (C) Red Hat, Inc.
 * http://www.redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pfry.camel.aggregator.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.ReplyToType;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.ShutdownStrategy;

public class AppCamel {
	
	private static final String BROKER_USERNAME = "admin";
        private static final String BROKER_PASSWORD = "admin";
	//private static final String BROKER_URL = "tcp://localhost:61616";
	private static final String BROKER_URL = "failover://tcp://amq-node2:61616,tcp://amq-node1:61616";

	public static void main(String args[]) throws Exception {
    	
        CamelContext context = new DefaultCamelContext();
        
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(BROKER_URL);
        activeMQComponent.setPassword(BROKER_PASSWORD);
        activeMQComponent.setUserName(BROKER_USERNAME);
        context.addComponent("activemq", activeMQComponent);

        ProducerTemplate template = context.createProducerTemplate();

        context.start();

        String aggregationid = Integer.toString(new Random().nextInt());
        
        Map<String, Object> headersUK = createHeaders(aggregationid,
				"UK");

        String aggregationidUS = Integer.toString(new Random().nextInt());
        
        Map<String, Object> headersUS = createHeaders(aggregationidUS,
				"US");        
        
        String responseFoo = (String)template.requestBodyAndHeaders("activemq:queue:foo", "ScottUK", headersUK);
        String responseBar = (String)template.requestBodyAndHeaders("activemq:queue:bar", "Brad",   headersUK);
        String responseAnd = (String)template.requestBodyAndHeaders("activemq:queue:and", "Gary",   headersUK);
        
        String responseFooUS = (String)template.requestBodyAndHeaders("activemq:queue:foo", "ScottUS", headersUS);
        String responseBarUS = (String)template.requestBodyAndHeaders("activemq:queue:bar", "Brad",   headersUS);
        String responseAndUS = (String)template.requestBodyAndHeaders("activemq:queue:and", "Gary",   headersUS);

        System.out.println("response for foo is: " + responseFoo);
        System.out.println("response for bar is: " + responseBar);
        System.out.println("response for and is: " + responseAnd);

        context.stop();
        
        System.out.println("stopped context");
    }
	
	private static Map<String, Object> createHeaders(String aggregationid,
			String countryCode) {
		Map<String,Object> headersFoo = new HashMap<String,Object>();
        headersFoo.put("aggregationid", aggregationid);
		headersFoo.put("CountryCode", countryCode);
		return headersFoo;
	}	
}
