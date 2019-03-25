package com.redhat.cajun.navy.responder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.service.ResponderService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResponderControllerIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private ResponderService responderService;

    @Before
    public void initTest() {
        RestAssured.baseURI = String.format("http://localhost:%d", port);
        initService();
    }

    @Test
    public void testAvailableResponders() {

        given().request().get("/responders/available")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2));

    }

    private void initService() {
        Responder responder1 = new Responder.Builder(1)
                .name("John Doe")
                .phoneNumber("111-222-333")
                .latitude(new BigDecimal("30.12345"))
                .longitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(true)
                .build();

        Responder responder2 = new Responder.Builder(2)
                .name("John Foo")
                .phoneNumber("999-888-777")
                .latitude(new BigDecimal("35.12345"))
                .longitude(new BigDecimal("-75.98765"))
                .boatCapacity(2)
                .medicalKit(true)
                .available(true)
                .build();

        List<Responder> responders = new ArrayList<>();
        responders.add(responder1);
        responders.add(responder2);

        when(responderService.availableResponders()).thenReturn(responders);
    }

}
