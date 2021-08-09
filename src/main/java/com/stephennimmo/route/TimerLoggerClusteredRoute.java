package com.stephennimmo.route;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimerLoggerClusteredRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("master:timer-logger-ns:timer://current-leader-check-timer?fixedRate=true&period=500")
                .log("Current Leader");
    }

}
