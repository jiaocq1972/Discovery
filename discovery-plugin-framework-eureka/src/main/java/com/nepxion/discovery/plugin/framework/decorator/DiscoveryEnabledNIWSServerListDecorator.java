package com.nepxion.discovery.plugin.framework.decorator;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.List;

import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;

import com.nepxion.discovery.plugin.framework.context.PluginContextAware;
import com.nepxion.discovery.plugin.framework.listener.impl.LoadBalanceListenerExecutor;
import com.netflix.client.config.IClientConfig;
import com.netflix.discovery.EurekaClient;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class DiscoveryEnabledNIWSServerListDecorator extends DiscoveryEnabledNIWSServerList {
    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private LoadBalanceListenerExecutor loadBalanceListenerExecutor;

    @Autowired
    private IClientConfig clientConfig;

    @Deprecated
    public DiscoveryEnabledNIWSServerListDecorator() {
        super();
    }

    @Deprecated
    public DiscoveryEnabledNIWSServerListDecorator(String vipAddresses) {
        super(vipAddresses);
    }

    @Deprecated
    public DiscoveryEnabledNIWSServerListDecorator(IClientConfig clientConfig) {
        super(clientConfig);
    }

    public DiscoveryEnabledNIWSServerListDecorator(String vipAddresses, Provider<EurekaClient> eurekaClientProvider) {
        super(vipAddresses, eurekaClientProvider);
    }

    public DiscoveryEnabledNIWSServerListDecorator(IClientConfig clientConfig, Provider<EurekaClient> eurekaClientProvider) {
        super(clientConfig, eurekaClientProvider);
    }

    @Override
    public List<DiscoveryEnabledServer> getInitialListOfServers() {
        List<DiscoveryEnabledServer> servers = super.getInitialListOfServers();

        filter(servers);

        return servers;
    }

    @Override
    public List<DiscoveryEnabledServer> getUpdatedListOfServers() {
        List<DiscoveryEnabledServer> servers = super.getUpdatedListOfServers();

        filter(servers);

        return servers;
    }

    private void filter(List<DiscoveryEnabledServer> servers) {
        Boolean discoveryControlEnabled = PluginContextAware.isDiscoveryControlEnabled(environment);
        if (discoveryControlEnabled) {
            String serviceId = clientConfig.getClientName();
            loadBalanceListenerExecutor.onGetServers(serviceId, servers);
        }
    }
}