Camel-Aggregator Camel OSGi example
=============================

This project demonstrates a Camel project that implements the Aggregator EIP. It is deployed as a OSGi bundle, has a unit test and
a test client that uses Camel. The camel route consumes from three activemq queues (foo, bar, and) and then sends these to a 'direct:start' producer endpoint.
A corresponding 'direct:start' consumer then aggregates the messages correlating the messages based on a header with name 'aggregatorid', there is a message count condition fixed at 3 (corresponding receiving messages from 3 queues). The aggregator implementation writes the content of each correlated message received on a separate line into a single file.  

### Requirements:
* JBoss Fuse 6.1.0 (http://www.jboss.org/jbossfuse)
* Maven 3.x (http://maven.apache.org/)
* Java SE 6

Prebuild
--------

    cat ><JBOSS FUSE HOME>/etc/aggregatorapp.cfg <<EOL
    brokerurl=tcp://localhost:61616
    brokerusername=admin
    brokerpassword=admin
    aggregator.outputpath=<SOME PATH TO A DIRECTORY FOR THE OUTPUT FILES>
    EOL


    mkdir <SOME PATH TO A DIRECTORY FOR THE OUTPUT FILES>


Building
--------

To build

    mvn clean install -Dproperties.path=<JBOSS FUSE HOME>/etc/aggregatorapp.cfg

To build without running the unit test

    mvn clean install -Dmaven.test.skip=true

Running from the command line
-----------------------------

To run the standalone client, which will a message to three queues ('foo', 'bar', 'and') with content 'Scott', 'Brad' and 'Gary' respectively

    mvn exec:java

Then look at output found in <SOME PATH TO A DIRECTORY FOR THE OUTPUT FILES>

Deploying to JBoss Fuse
-----------------------

To install in JBoss Fuse

    <JBoss Fuse Home>/bin/fuse

In JBoss Fuse Console

    karaf@root> osgi:install -s mvn:com.fusesource.byexample.hellocamel/HelloCamel/1.0.0

Once you've started the service - the '-s' option to osgi:install will start the bundle once installed - you
can test using the standalone client

    mvn exec:java

There are some errors when the client's camel context is shut down but they can be ignored, I will investigate cleaning this up!
