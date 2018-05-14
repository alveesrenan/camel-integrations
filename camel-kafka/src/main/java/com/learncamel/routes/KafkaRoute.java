package com.learncamel.routes;

import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import com.learncamel.processor.BuildSQLProcessor;
import com.learncamel.processor.MailProcessor;
import com.learncamel.processor.ValidateDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class KafkaRoute extends RouteBuilder {

    @Autowired
    private Environment environment;

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Autowired
    private MailProcessor mailProcessor;

    @Autowired
    private ValidateDataProcessor validateDataProcessor;

    @Autowired
    private BuildSQLProcessor buildSQLProcessor;

    @Override
    public void configure() throws Exception {

        Predicate isNotMock = header("env").isNotEqualTo("mock");

        GsonDataFormat itemFormat = new GsonDataFormat(Item.class);

        onException(PSQLException.class).log(LoggingLevel.ERROR,"PSQLException in the route ${body}")
                .maximumRedeliveries(3).redeliveryDelay(3000).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR);

        onException(DataException.class,RuntimeException.class).log(LoggingLevel.ERROR, "DataException in the route ${body}")
                .choice()
                    .when(isNotMock)
                        .process(mailProcessor)
                    .end()
                .to("{{errorRoute}}");


        from("{{fromRoute}}")
                .log("Read Message from Kafka ${body}")
                .unmarshal(itemFormat)
                .log("Unmarshalled message is ${body}")
                .process(validateDataProcessor)
                .process(buildSQLProcessor)
                .to("{{toRoute}}");

    }
}
