package com.assignment.camel.timer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.assignment.dataprocessing.AssignmentDataProcessing;

@Component
public class SimpleMessageRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        /**
         * generates an event/constant message every 5 minutes for testing purposes
         */
        from("timer:active-mq-timer?period=300000")
                .transform().constant("Hello from Apache Camel -Kiran Assignment")
                .bean(AssignmentDataProcessing.class, "processData()") 
                .log("${body}");
               
    }
}