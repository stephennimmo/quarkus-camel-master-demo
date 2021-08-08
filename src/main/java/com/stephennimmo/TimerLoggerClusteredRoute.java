package com.stephennimmo;

import org.apache.camel.builder.RouteBuilder;

public class TimerLoggerClusteredRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("master:timer-logger-ns:timer://foo?fixedRate=true&period=500")
                .log("Current Leader");
    }

}
