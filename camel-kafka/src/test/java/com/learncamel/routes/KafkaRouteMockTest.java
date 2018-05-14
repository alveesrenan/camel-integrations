package com.learncamel.routes;

import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
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
public class KafkaRouteMockTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private Environment environment;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    public void shouldUnmarshal(){
        String input = "{\"transactionType\":\"ADD\", \"sku\":\"100\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}";

        Item item = (Item) producerTemplate.requestBodyAndHeader(environment.getProperty("fromRoute"),input,"env","mock");

        System.out.println("Item is: " + item.toString());

        assertEquals("100",item.getSku());
    }

    @Test(expected = CamelExecutionException.class)
    public void shouldNotUnmarshalDataException(){
        String input = "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"ABC TV\", \"price\":\"500.00\"}";

        Item item = (Item) producerTemplate.requestBodyAndHeader(environment.getProperty("fromRoute"),input,"env","mock");

    }
}