package se.tradechannel;/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c)  2000-2017, TradeChannel AB. All rights reserved.
 * Any right to utilize the System under this Agreement shall be subject to the terms and condition of the
 * License Agreement between Customer "X" and TradeChannel AB.
 *
 * TradeseC contains third party software which includes software owned or licensed by a third party and
 * sub licensed to the Customer by TradeChannel AB in accordance with the License Agreement.
 *
 * TradeChannel AB owns the rights to the software product TradeseC.
 *
 * TradeChannel AB grants a right to the Customer and the Customer accepts a non-exclusive,
 * non-transferrable license to use TradeseC and Third Party Software, in accordance with the conditions
 * specified in this License Agreement.
 *
 * The Customer may not use TradeseC or the Third Party Software for time-sharing, rental,
 * service bureau use, or similar use. The Customer is responsible for that all use of TradeseC
 * and the Third Party Software is in accordance with the License Agreement.
 *
 * The Customer may not transfer, sell, sublicense, let, lend or in any other way permit any person or entity
 * other than the Customer, avail himself, herself or itself of or otherwise any rights to TradeseC or the
 * Third Party Software, either directly or indirectly.
 *
 * The Customer may not use, copy, modify or in any other way transfer or use TradeseC or the
 * Third Party Software wholly or partially, nor allow another person or entity to do so, in any way other than
 * what is expressly permitted according to the License Agreement. Nor, consequently, may the Customer,
 * independently or through an agent, reverse engineer, decompile or disassemble TradeseC, the Third Party Software
 * or any accessories that may be related to it.
 *
 * The Customer acknowledges TradeseC <i>(including but not limited to any copyrights, trademarks,
 * documentation, enhancements or other intellectual property or proprietary rights relating to it)</i>
 * and Third Party Software is the proprietary material of the Supplier and respectively Third Party.
 *
 * The Third Party Software are protected by copyright law.
 *
 * The Customer shall not remove, erase or hide from view any information about a patent, copyright,
 * trademark, confidentiality notice, mark or legend appearing on any of TradeseC or Third Party Software,
 * any medium by which they are made available or any form of output produced by them.
 *
 * The License Agreement will only grant the Customer the right to use TradeseC and Third Party Software
 * under the terms of the License Agreement.
 *
 *
 */


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.JmsTransactionManager;

import static org.springframework.jms.listener.DefaultMessageListenerContainer.CACHE_CONSUMER;

/**
 * The class Config
 *
 * @author mgr, (c) TradeChannel AB, 2016-12-05
 * @version 1.0
 */
@Configuration
public class ConfigurationBeans {

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory();
    }

    /**
     * Pooled connection factory pooled connection factory.
     *
     * @return the pooled connection factory
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Primary
    public PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setMaxConnections(8);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(500);
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        return pooledConnectionFactory;
    }

    /**
     * Transaction manager jms transaction manager.
     *
     * @return the jms transaction manager
     */
    @Bean
    public JmsTransactionManager transactionManager() {
        final JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(pooledConnectionFactory());
        return transactionManager;
    }

    /**
     * Jms configuration jms configuration.
     *
     * @return the jms configuration
     */
    @Bean
    public JmsConfiguration jmsConfiguration() {
        final JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(pooledConnectionFactory());
        jmsConfiguration.setTransacted(true);
        jmsConfiguration.setTransactionManager(transactionManager());
        jmsConfiguration.setConcurrentConsumers(10);

        return jmsConfiguration;
    }


    /**
     * Active mq component active mq component.
     *
     * @return the active mq component
     */
    @Bean
    public ActiveMQComponent activeMQComponent(final JmsConfiguration jmsConfiguration,
                                               final PooledConnectionFactory pooledConnectionFactory,
                                               final JmsTransactionManager transactionManager) {
        final ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConfiguration(jmsConfiguration);
        activeMQComponent.setConnectionFactory(pooledConnectionFactory);
        activeMQComponent.setTransactionManager(transactionManager);
        activeMQComponent.setTransacted(true);
        activeMQComponent.setUsePooledConnection(true);
        activeMQComponent.setCacheLevel(CACHE_CONSUMER);
        activeMQComponent.setCacheLevelName("CACHE_CONSUMER");
        return activeMQComponent;
    }

}
