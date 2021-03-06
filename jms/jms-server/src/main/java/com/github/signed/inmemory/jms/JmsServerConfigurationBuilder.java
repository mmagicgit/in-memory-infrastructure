package com.github.signed.inmemory.jms;

import java.util.ArrayList;
import java.util.List;

import com.github.signed.inmemory.shared.configuration.AddressAndPort;
import com.github.signed.inmemory.shared.configuration.ExplicitPort;
import com.github.signed.inmemory.shared.configuration.Port;
import com.github.signed.inmemory.shared.configuration.RandomPort;

public class JmsServerConfigurationBuilder {

    public static JmsServerConfigurationBuilder anyJmsServerConfigurationBut() {
        return new JmsServerConfigurationBuilder();
    }

    private final List<TopicConfiguration> topicsToCreate = new ArrayList<TopicConfiguration>();
    private final List<QueueConfiguration> queuesToCreate = new ArrayList<QueueConfiguration>();
    private AddressAndPort host = new AddressAndPort(RandomPort.AnyUserPort());
    private String connectionFactoryName = "ConnectionFactoryName";

    public JmsServerConfigurationBuilder listenOnPort(int port) {
        return listenOnPort(new ExplicitPort(port));
    }

    public JmsServerConfigurationBuilder listenOnPort(Port port) {
        host = new AddressAndPort(port);
        return this;
    }

    public JmsServerConfigurationBuilder publishConnectionFactoryUnder(String connectionFactoryName){
        this.connectionFactoryName = connectionFactoryName;
        return this;
    }

    public JmsServerConfigurationBuilder createQueue(String name) {
        queuesToCreate.add(new QueueConfiguration(name));
        return this;
    }

    public JmsServerConfigurationBuilder createTopic(String name) {
        topicsToCreate.add(new TopicConfiguration(name));
        return this;
    }

    public JmsServerConfiguration build() {
        return new JmsServerConfiguration(host, topicsToCreate, queuesToCreate, connectionFactoryName);
    }

}
