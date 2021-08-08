package com.stephennimmo;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.component.file.cluster.FileLockClusterService;
import org.apache.camel.component.kubernetes.cluster.KubernetesClusterService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ClusterLockProvider {

    @Produces
    @UnlessBuildProfile("prod")
    public CamelClusterService fileLockClusterService() {
        FileLockClusterService service = new FileLockClusterService();
        service.setId("timer-logger-ns");
        service.setRoot("target/cluster");
        service.setAcquireLockDelay(1, TimeUnit.SECONDS);
        service.setAcquireLockInterval(1, TimeUnit.SECONDS);
        return service;
    }

    @Produces
    @IfBuildProfile("prod")
    public CamelClusterService kubernetesClusterService() {
        KubernetesClusterService service = new KubernetesClusterService();
        service.setId("timer-logger-ns");
        service.setKubernetesNamespace("quarkus-camel-master-demo");
        service.addToClusterLabels("app", "quarkus-camel-master-demo");
        service.setLeaseDurationMillis(5000L);
        service.setRenewDeadlineMillis(2500L);
        return service;
    }

}
