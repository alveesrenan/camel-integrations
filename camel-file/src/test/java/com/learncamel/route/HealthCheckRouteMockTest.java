package com.learncamel.route;

import com.learncamel.process.HealthCheckProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.*;

@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("mock")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HealthCheckRouteMockTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new HealthCheckRoute();
    }

    @Autowired
    CamelContext context;

    @Autowired
    Environment environment;

    @Override
    protected CamelContext createCamelContext() {
        return context;
    }

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    HealthCheckProcessor healthCheckProcessor;

    @Before
    public void setUp(){

    }

    @Test
    public void shouldHealthCheck() {

        String input = "{\"status\":\"DOWN\",\"camel\":{\"status\":\"UP\",\"name\":\"camel-1\",\"version\":\"2.21.0\",\"contextStatus\":\"Started\"},\"camel-health-checks\":{\"status\":\"UP\",\"route:healthRoute\":\"UP\",\"route:mainRoute\":\"UP\"},\"mail\":{\"status\":\"UP\",\"location\":\"smtp.gmail.com:587\"},\"diskSpace\":{\"status\":\"UP\",\"total\":121123069952,\"free\":6515953664,\"threshold\":10485760},\"db\":{\"status\":\"DOWN\",\"error\":\"org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is org.postgresql.util.PSQLException: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.\"}}";

        String response = (String) producerTemplate.requestBodyAndHeader(environment.getProperty("healthRoute"), input,"env", environment.getProperty("spring.profiles.active"));

        System.out.println("The response is: " + response);

        String expectedMessage = "status compnent in the route is down \n" +
                "db compnent in the route is down ";

        Assert.assertTrue(response.contains(expectedMessage));
    }
}