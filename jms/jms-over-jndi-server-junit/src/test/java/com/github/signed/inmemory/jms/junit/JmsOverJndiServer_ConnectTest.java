package com.github.signed.inmemory.jms.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.Rule;
import org.junit.Test;

import com.github.signed.inmemory.jms.JmsServerConfigurationBuilder;
import com.github.signed.inmemory.jndi.JndiServerConfigurationBuilder;

public class JmsOverJndiServer_ConnectTest {

    private final JmsServerConfigurationBuilder jmsConfiguration = JmsServerConfigurationBuilder.anyJmsServerConfigurationBut().createQueue("queue1").createTopic("topic1");
    private final JndiServerConfigurationBuilder jndiConfiguration = JndiServerConfigurationBuilder.anyJndiServerConfigurationBut();

    @Rule
    public final JmsOverJndiServer jmsServer = new JmsOverJndiServer(jndiConfiguration.build(), jmsConfiguration.build());

    @Test
    public void sendAndReceiveMessageViaJndiLookup() throws Exception {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, jmsServer.initialContextFactory());
        env.put(Context.PROVIDER_URL, jmsServer.providerUrl());
        Context context = new InitialContext(env);
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(jmsServer.connectionFactoryName());
        Queue queue = (Queue) context.lookup("queue1");

        assertThatProduceConsumeRoundTripIsWorking(connectionFactory, queue);
    }

    private void assertThatProduceConsumeRoundTripIsWorking(ConnectionFactory connectionFactory, Queue queue) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("Hello World");
        producer.send(message);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        connection.start();
        TextMessage messageReceived = (TextMessage) messageConsumer.receive(1000);

        assertThat(messageReceived.getText(), is("Hello World"));
        connection.close();
    }
}