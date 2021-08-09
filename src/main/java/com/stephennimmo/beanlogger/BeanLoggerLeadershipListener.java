package com.stephennimmo.beanlogger;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.cluster.CamelClusterEventListener;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.support.cluster.ClusterServiceHelper;

import javax.enterprise.event.Observes;

@Startup
public class BeanLoggerLeadershipListener {

    private CamelContext camelContext;
    private BeanLoggerService beanLoggerService;

    public BeanLoggerLeadershipListener(CamelContext camelContext, BeanLoggerService beanLoggerService) {
        this.camelContext = camelContext;
        this.beanLoggerService = beanLoggerService;
    }

    void onStart(@Observes StartupEvent ev) throws Exception {
        CamelClusterService camelClusterService = ClusterServiceHelper.lookupService(camelContext).orElseThrow(() -> new RuntimeException("Unable to lookupService for CamelClusterService"));
        camelClusterService.getView("bean-logger-ns").addEventListener((CamelClusterEventListener.Leadership) (view, leader) -> {
            boolean weAreLeader = leader.isPresent() && leader.get().isLocal();
            if (weAreLeader) {
                beanLoggerService.start();
            } else {
                beanLoggerService.stop();
            }
        });
    }

}
