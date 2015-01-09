Camel-Aggregator Camel OSGi example
=============================

This project demonstrates a Camel project that implements the Aggregator EIP. It is deployed as a OSGi bundle, has a unit test and
a test client that uses Camel. The camel route consumes from three activemq queues (foo, bar, and) with a 'CountryCode' message header selector and then sends these to a 'direct:start' producer endpoint.
A corresponding 'direct:start' consumer then aggregates the messages correlating the messages based on a header with name 'aggregatorid', there is a message count condition fixed at 3 (corresponding receiving messages from 3 queues). The aggregator implementation writes the content of each correlated message received on a separate line into a single file.  

### Requirements:
* JBoss Fuse 6.1.0 (http://www.jboss.org/jbossfuse)
* Maven 3.x (http://maven.apache.org/)
* Java SE 6

Prebuild
--------

    cat ><JBOSS FUSE HOME>/etc/aggregatorapp.cfg <<EOL
    brokerurl=tcp://localhost:61616
    #brokerurl=failover://tcp://amq-node2:61616,tcp://amq-node1:61616
    brokerusername=admin
    brokerpassword=admin
    aggregator.outputpath=<SOME PATH TO A DIRECTORY FOR THE OUTPUT FILES>
    country.code=UK
    EOL


    mkdir <SOME PATH TO A DIRECTORY FOR THE OUTPUT FILES>


Building
--------

To build

    mvn clean install -Dproperties.path=<JBOSS FUSE HOME>/etc/aggregatorapp.cfg

Note that ONLY the aggregator.outputpath from the aggregatorapp.cfg properties file are USED by the unit test. 

To build without running the unit test

    mvn clean install -Dmaven.test.skip=true

Deploying to JBoss Fuse (standlone)
-----------------------

To install in JBoss Fuse

    <JBoss Fuse Home>/bin/fuse

In JBoss Fuse Console

    karaf@root> osgi:install -s mvn:com.fusesource.byexample.hellocamel/HelloCamel/1.0.0

Once you've started the service - the '-s' option to osgi:install will start the bundle once installed 

Running from the command line
-----------------------------

There are two standalone clients AppJMS and AppClient. The former uses the ActiveMQ JMS API and the latter uses the ProducerTemplate available in Apache Camel. The former executes cleanly where as the latter prints out some warning error messages. The AppJMS client is multi-threaded and generates messages for two different country codes 'UK' and 'US'. 

To run the standalone client (AppJMS), which concurrently will

1) send messages to three queues ('foo', 'bar', 'and') with content 'Scott UK', 'Brad' and 'Gary' respectively all with a random aggregatorid header and a CountryCode header of UK.

2) send messages to three queues ('foo', 'bar', 'and') with content 'Scott US', 'Brad' and 'Gary' respectively all with a random aggregatorid header and a CountryCode header of US.

run the following command

    mvn exec:java

Then look at output found in the value used for aggregator.outputpath . There should only be files corresponding to the CountryCode header of UK in this directory (study the aggregatorapp.cfg file created previously).

Deploying to JBoss Fuse (Fabric)
--------------------------------

For background see:

https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_Fuse/6.1/html/Fabric_Guide/index.html

In the JBoss Fuse Console create a Fuse fabric

	karaf@root> fabric:create --new-user admin --new-user-password admin --zookeeper-password admin --wait-for-provisioning

then create two fabric child container 

	karaf@root> fabric:container-create-child root child 2

then confirm what has been created

	karaf@root> fabric:container-list

Create a profile based on the feature-camel profile, it will be used as the basis for the profiles that we will use for the two containers.

	karaf@root> fabric:profile-create --parents feature-camel aggregator-app

Create two profiles based on the aggregator-app profile created previously.

	karaf@root> fabric:profile-create --parents aggregator-app aggregator-app-child1
	karaf@root> fabric:profile-create --parents aggregator-app aggregator-app-child2

Open the fabric profile editor for the parent profile

	karaf@root> fabric:profile-edit aggregator-app

edit it so that it has the following contents

	attribute.parents = feature-camel
	repository.apache-activemq=mvn:org.apache.activemq/activemq-karaf/${version:activemq}/xml/features
	feature.mq-camel = mq-fabric-camel
	bundle.aggregator=mvn:com.fusesource.byexample.hellocamel/HelloCamel/1.0.0

save and exit.

Open the fabric editor for the PID properties 'aggregatorapp' for the aggregator-app-child1 profile 

	karaf@root> fabric:profile-edit --pid aggregatorapp aggregator-app-child1

edit it so that it has the following contents:

	brokerurl=tcp://localhost:61616
	#brokerurl=failover://tcp://amq-node2:61616,tcp://amq-node1:61616
	brokerusername=admin
	brokerpassword=admin
	aggregator.outputpath=<PATH TO WHERE YOU WANT THE UK MESSAGES TO GO>
        country.code=UK

save and exit.

Open the fabric editor for the PID properties 'aggregatorapp' for the aggregator-app-child2 profile 

	karaf@root> fabric:profile-edit --pid aggregatorapp aggregator-app-child2

edit it so that it has the following contents:

	brokerurl=tcp://localhost:61616
	#brokerurl=failover://tcp://amq-node2:61616,tcp://amq-node1:61616
	brokerusername=admin
	brokerpassword=admin
	aggregator.outputpath=<PATH TO WHERE YOU WANT THE US MESSAGES TO GO>
	country.code=US

save and exit. Then associate the profiles with the child containers.

	karaf@root> fabric:container-change-profile child1 aggregator-app-child1
	karaf@root> fabric:container-change-profile child2 aggregator-app-child2

and then back in the project directory run the following command

    mvn exec:java

and you should see 'UK' messages in the aggregator.outputpath you chose for the UK and 'US' messages in the aggregator,outputpath you chose for the US.

Another useful thing to do is to set up a fault tolerant JBoss A-MQ installation so that the failover broker URL can be demonstrated (you will notice these URLs being commented out).
