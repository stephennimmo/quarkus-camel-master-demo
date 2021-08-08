package com.stephennimmo;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.component.file.cluster.FileLockClusterService;
import org.apache.camel.component.kubernetes.cluster.KubernetesClusterService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ClusterLockProvider {

    @ConfigProperty(name = "lock.namespace")
    Optional<String> lockNamespace;

    @Produces
    @UnlessBuildProfile("prod")
    public CamelClusterService fileLockClusterService() {
        FileLockClusterService service = new FileLockClusterService();
        service.setRoot("target/cluster");
        service.setAcquireLockDelay(1, TimeUnit.SECONDS);
        service.setAcquireLockInterval(1, TimeUnit.SECONDS);
        return service;
    }

    @Produces
    @IfBuildProfile("prod")
    public CamelClusterService kubernetesClusterService() {
        if (lockNamespace.isEmpty()) {
            throw new RuntimeException("lock.namespace is not configured");
        }
        KubernetesClusterService service = new KubernetesClusterService();
        service.setKubernetesNamespace(lockNamespace.get());
        service.addToClusterLabels("app", "quarkus-camel-master-demo");
        return service;
    }

}
