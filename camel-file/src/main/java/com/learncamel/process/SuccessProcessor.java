package com.learncamel.process;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class SuccessProcessor implements org.apache.camel.Processor {

    public static final String SUCCESS_MESSAGE = "Message updated successfully";

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setBody(SUCCESS_MESSAGE);
    }
}
