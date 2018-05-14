package com.learncamel.route;

import com.learncamel.alert.MailProcessor;
import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import com.learncamel.process.BuildSQLProcessor;
import com.learncamel.process.SuccessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class SimpleCamelRoute extends RouteBuilder {

    @Autowired
    private Environment environment;

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Autowired
    private BuildSQLProcessor buildSQLProcessor;

    @Autowired
    private SuccessProcessor sucessProcessor;

    @Autowired
    private MailProcessor mailProcessor;

    @Override
    public void configure() throws Exception {

        log.info("Starting SimpleCamelRoute");

        DataFormat bindy = new BindyCsvDataFormat(Item.class);

        /**
         * errorHandler wraps the whole route. So it can be our generic error handling. While onException
         * can handle exception by exception type.
         *
         */

        //Now we are overring the Default Error Handling of Camel and logging it accordingly
        errorHandler(deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true")
                .maximumRedeliveries(3) //We try more 3 times
                .redeliveryDelay(3000) //Setting 3 seconds in between each attempt
                .backOffMultiplier(2) // Then for the next attempt I am going to double the timeframe
                .retryAttemptedLogLevel(LoggingLevel.ERROR)); //All retry attempt log level is going to be flagged as error

        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route ${body}")
                .maximumRedeliveries(3)
                .redeliveryDelay(3000)
                .retryAttemptedLogLevel(LoggingLevel.ERROR);

        //Notice that I am not going to rety so if this exception gets thrown, just one row should appear in the log
        onException(DataException.class).log(LoggingLevel.ERROR, "DataException in the route ${body}")
                .process(mailProcessor);

        from("{{startRoute}}").routeId("mainRoute")
                .log("{{message}}")
                .log("Timer invoked and the body is " + environment.getProperty("message"))
                .choice()
                .when(header("env")
                        .isNotEqualTo("mock"))
                    .pollEnrich("{{fromRoute}}") // I don't want to use the 'from' method, so we use pollEnrich
                .otherwise()
                .end()
                .to("{{toRoute1}}")
                .unmarshal(bindy)
                .log("The unmarshaled object is ${body}")
                .split(body()) //http://camel.apache.org/splitter.html
                    .log("Record is ${body}")
                    .process(buildSQLProcessor) //Before insert we need to create our SQL statement
                    .to("{{toRoute2}}") //We use jdbc component interact with db
                    .end()
                    .process(sucessProcessor) // We will assert this result from tests. (it won't reach this point if it fails inserting)
        .to("{{toRoute3}}")
        .end();


        log.info("Ending SimpleCamelRoute");
    }
}
