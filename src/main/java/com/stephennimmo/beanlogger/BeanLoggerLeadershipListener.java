package com.stephennimmo.beanlogger;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.cluster.CamelClusterEventListener;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.component.kubernetes.cluster.KubernetesClusterService;
import org.apache.camel.support.cluster.ClusterServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;

@Startup
public class BeanLoggerLeadershipListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanLoggerLeadershipListener.class);

    private CamelContext camelContext;
    private BeanLoggerService beanLoggerService;

    public BeanLoggerLeadershipListener(CamelContext camelContext, BeanLoggerService beanLoggerService) {
        this.camelContext = camelContext;
        this.beanLoggerService = beanLoggerService;
    }

    void onStart(@Observes StartupEvent ev) throws Exception {
        CamelClusterService camelClusterService = ClusterServiceHelper.lookupService(camelContext).orElseThrow(() -> new RuntimeException("Unable to lookupService for CamelClusterService"));
        if (camelClusterService instanceof KubernetesClusterService) {
            KubernetesClusterService kcs = (KubernetesClusterService) camelClusterService;
            LOGGER.info("KubernetesClusterService: LeaseResourceType={}, KubernetesNamespace={}, KubernetesResourceName={}, MasterUrl={}",
                    kcs.getLeaseResourceType().name(), kcs.getKubernetesNamespace(), kcs.getKubernetesResourceName(), kcs.getMasterUrl());
        }
        camelClusterService.getView("bean-logger-ns").addEventListener((CamelClusterEventListener.Leadership) (view, leader) -> {
            LOGGER.info("LeadershipEvent[bean-logger-ns]: {}", leader);
            boolean weAreLeader = leader.isPresent() && leader.get().isLocal();
            if (weAreLeader) {
                beanLoggerService.start();
            } else {
                beanLoggerService.stop();
            }
        });
    }

}
