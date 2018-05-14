package com.learncamel.route;

import com.learncamel.process.SuccessProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class SimpleCamelRouteTest {

    private static final String DIRECTORY_OUTPUT = "data/output";
    private static final String DIRECTORY_INPUT = "data/input";

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private Environment environment;

    @BeforeClass
    public static void cleanUp() throws IOException {

        FileUtils.cleanDirectory(new File(DIRECTORY_INPUT));
        FileUtils.deleteDirectory(new File(DIRECTORY_OUTPUT));

    }

    @Test
    public void shouldMoveFile() throws InterruptedException {

        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV, 500\n" +
                "ADD,101, LG TV, 500";

        String fileName = "fileTest.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);



        Thread.sleep(3000);

        File file = new File(DIRECTORY_OUTPUT+"/"+fileName);

        assertTrue(file.exists());
    }


    @Test
    public void shouldMoveFile_ADD() throws InterruptedException, IOException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV, 500\n" +
                "ADD,101, LG TV, 500";

        String fileName = "fileTest.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);


        Thread.sleep(3000);

        File file = new File(DIRECTORY_OUTPUT+"/"+fileName);

        assertTrue(file.exists());

        String expectedMessage = SuccessProcessor.SUCCESS_MESSAGE;

        String output = new String(Files.readAllBytes(Paths.get("data/output/SuccessFile.txt")));

        assertEquals(expectedMessage, output);

    }

    /**
     * Attention, given we are enabling retry for this error handler, camel is not going to move forward
     * with the route, so the success file is not going to be generated. If we disable the retry, then it's
     * going to fail for the first row, log it accordingly and then move to the second row which has no problem
     * thus creating the success file. It's something that has to be kept in mind.
     *
     * That's just scary
     *
     */

    @Test
    public void shouldntMoveFile_ADD_Exception() throws InterruptedException, IOException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,,Samsung TV, 500\n" +
                "ADD,101, LG TV, 500";

        String fileName = "fileTest.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);


        Thread.sleep(3000);

        File file = new File(DIRECTORY_OUTPUT+"/"+fileName);

        assertTrue(file.exists());

        File errorDirectory = Paths.get("data/input/error/"+fileName).toFile();
        assertTrue(errorDirectory.exists());

    }

    @Test
    public void shouldMoveFile_UPDATE() throws InterruptedException, IOException {
        String message = "type,sku#,itemdescription,price\n" +
                "UPDATE,100,Samsung TV, 600";

        String fileName = "fileUpdate.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);


        Thread.sleep(3000);

        File file = new File(DIRECTORY_OUTPUT+"/"+fileName);

        assertTrue(file.exists());

        String expectedMessage = SuccessProcessor.SUCCESS_MESSAGE;

        String output = new String(Files.readAllBytes(Paths.get("data/output/SuccessFile.txt")));

        assertEquals(expectedMessage, output);

    }


    @Test
    public void shouldMoveFile_DELETE() throws InterruptedException, IOException {
        String message = "type,sku#,itemdescription,price\n" +
                "DELETE,100,Samsung TV, 600";

        String fileName = "fileFileDelete.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File file = new File(DIRECTORY_OUTPUT+"/"+fileName);

        assertTrue(file.exists());

        String expectedMessage = SuccessProcessor.SUCCESS_MESSAGE;

        String output = new String(Files.readAllBytes(Paths.get("data/output/SuccessFile.txt")));

        assertEquals(expectedMessage, output);

    }
}