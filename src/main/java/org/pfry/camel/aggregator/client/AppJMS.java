package org.pfry.camel.aggregator.client;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
 
public class AppJMS {
 
	private static final String BROKER_USERNAME = "admin";
    private static final String BROKER_PASSWORD = "admin";
    //private static final String BROKER_URL = "tcp://localhost:61616";
    //private static final String BROKER_URL = "failover://tcp://amq-node2:61616,tcp://amq-node1:61616";
    private String brokerURL;  // = "discovery:(fabric:masterslave)";

    public static void main(String[] args) throws Exception {
    	
    	AppJMS appJMS = null;
    	
    	if( args.length == 0 )
    	{
    		appJMS = new AppJMS("tcp://localhost:61616");
    	}
    	else
    	{
    		appJMS = new AppJMS(args[0]);
    	}
    	
    	appJMS.sendMessages("UK");
    	appJMS.sendMessages("US");
    }
    
    public AppJMS(String brokerURL)
    {
    	this.brokerURL = brokerURL;
    }

	private void sendMessages(String countryCode) {
		for( int i = 0; i<3; i++)
    	{
    		String aggregationid = Integer.toString(new Random().nextInt());
            
    		thread(new HelloWorldProducer(brokerURL, "foo", "Scott " + countryCode, aggregationid, countryCode), false);
    		thread(new HelloWorldProducer(brokerURL, "bar", "Brad " + countryCode, aggregationid, countryCode), false);
    		thread(new HelloWorldProducer(brokerURL, "and", "Gary " + countryCode, aggregationid, countryCode), false);	 
    	}
	}
 
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
 
    public static class HelloWorldProducer implements Runnable {
    	
    	private String queue;
    	private String text;
    	private String aggregationid;
    	private String countryCode;
    	private String brokerURL;
    	
    	public HelloWorldProducer(String brokerURL, String queue, String message, String aggregationid, String countryCode)
    	{
    		this.brokerURL = brokerURL;
    		this.queue = queue;
    		this.text = message;
    		this.aggregationid = aggregationid;
    		this.countryCode = countryCode;
    	}
        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerURL);
                connectionFactory.setUserName(BROKER_USERNAME);
                connectionFactory.setPassword(BROKER_PASSWORD);
 
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();
 
                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
 
                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue(queue);
 
                // Create a MessageProducer from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
 
                // Create a messages
                //String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);
                message.setStringProperty("aggregationid", aggregationid);
                message.setStringProperty("CountryCode", countryCode);
                
                // Tell the producer to send the message
                System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);
 
                // Clean up
                session.close();
                connection.close();
            }
            catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }
 
/**    public static class HelloWorldConsumer implements Runnable, ExceptionListener {
        public void run() {
            try {
 
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
                connectionFactory.setUserName(BROKER_USERNAME);
                connectionFactory.setPassword(BROKER_PASSWORD);
 
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();
 
                connection.setExceptionListener(this);
 
                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
 
                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("TEST.FOO");
 
                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);
 
                // Wait for a message
                Message message = consumer.receive(1000);
 
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: " + text);
                } else {
                    System.out.println("Received: " + message);
                }
 
                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
 
        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }*/
}
