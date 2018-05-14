package com.learncamel.route;

import com.learncamel.process.SuccessProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("mock")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SimpleCamelRouteMockTest extends CamelTestSupport {

    @Autowired
    CamelContext context;

    @Autowired
    Environment environment;
    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    protected CamelContext createCamelContext(){
        return context;
    }

    @Test
    public void shouldMoveFileMock() throws InterruptedException {

        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV, 500\n" +
                "ADD,101, LG TV, 500";

        MockEndpoint mockEndpoint = getMockEndpoint(environment.getProperty("toRoute1"));
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(message);

        producerTemplate.sendBodyAndHeader(environment.getProperty("startRoute"),
                message,"env",
                environment.getProperty("spring.profiles.active"));

        assertMockEndpointsSatisfied();

    }

    @Test
    public void shouldMoveFileMockAndDB() throws InterruptedException {

        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV, 500\n" +
                "ADD,101, LG TV, 500";

        String expectedSusccessProcessorMessage = SuccessProcessor.SUCCESS_MESSAGE;

        String expectedBuildSQLProcessorMessage = "Some insert";

        MockEndpoint mockEndpoint = getMockEndpoint(environment.getProperty("toRoute1"));
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(message);

        //TODO Why is it not working? Shouldn't I get 2 INSERT sentences as well as 2 messages???
//        MockEndpoint mockEndpointBuildSQL = getMockEndpoint(environment.getProperty("toRoute2"));
//        mockEndpointBuildSQL.expectedMessageCount(1); //After splitting we get 2 items
//        mockEndpointBuildSQL.expectedBodiesReceived(expectedBuildSQLProcessorMessage);

        MockEndpoint mockEndpointSuccess = getMockEndpoint(environment.getProperty("toRoute3"));
        mockEndpointSuccess.expectedMessageCount(1);
        mockEndpointSuccess.expectedBodiesReceived(expectedSusccessProcessorMessage);

        producerTemplate.sendBodyAndHeader(environment.getProperty("startRoute"),
                message,"env",
                environment.getProperty("spring.profiles.active"));

        assertMockEndpointsSatisfied();

    }
}
