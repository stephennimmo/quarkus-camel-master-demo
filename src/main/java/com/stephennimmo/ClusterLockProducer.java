package com.stephennimmo;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.apache.camel.CamelContext;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.component.file.cluster.FileLockClusterService;
import org.apache.camel.component.kubernetes.cluster.KubernetesClusterService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ClusterLockProducer {

    @ConfigProperty(name = "namespace")
    Optional<String> namespace;

    @Produces
    @UnlessBuildProfile("prod")
    public CamelClusterService fileLockClusterService(CamelContext camelContext) throws Exception {
        FileLockClusterService service = new FileLockClusterService();
        service.setRoot("cluster");
        service.setAcquireLockDelay(1, TimeUnit.SECONDS);
        service.setAcquireLockInterval(1, TimeUnit.SECONDS);
        return service;
    }

    @Produces
    @IfBuildProfile("prod")
    public CamelClusterService kubernetesClusterService(CamelContext camelContext) {
        KubernetesClusterService service = new KubernetesClusterService();
        if (namespace.isPresent()){
            service.setKubernetesNamespace(namespace.get());
        }
        return service;
    }

}
