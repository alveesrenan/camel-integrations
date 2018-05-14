package com.learncamel.route;

import com.learncamel.alert.MailProcessor;
import com.learncamel.process.HealthCheckProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HealthCheckRoute extends RouteBuilder {

    @Autowired
    private HealthCheckProcessor healthCheckProcessor;

    @Autowired
    private MailProcessor mailProcessor;

    Predicate isNotMock = header("env").isNotEqualTo("mock");

    @Override
    public void configure() throws Exception {

        log.info("Starting HealthCheckRoute");

        from("{{healthRoute}}").routeId("healthRoute")
                .choice()
                    .when(isNotMock)
                        .pollEnrich("http://localhost:8080/health")
                    .end()
                .process(healthCheckProcessor)
                .choice()
                    .when(header("error").isEqualTo(true))
                    .choice()
                        .when(isNotMock)
                            .process(mailProcessor)
                        .end()
                .end();
    }
}
