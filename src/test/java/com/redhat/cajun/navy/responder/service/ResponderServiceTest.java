package com.redhat.cajun.navy.responder.service;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.redhat.cajun.navy.responder.dao.ResponderDao;
import com.redhat.cajun.navy.responder.entity.ResponderEntity;
import com.redhat.cajun.navy.responder.model.Responder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ResponderServiceTest {

    @Mock
    private ResponderDao responderDao;

    private ResponderServiceImpl service;

    @Before
    public void init() {
        initMocks(this);
        service = new ResponderServiceImpl();
        setField(service, null, responderDao, ResponderDao.class);
    }

    @Test
    public void testAvailableResponders() {

        ResponderEntity responder1 = new ResponderEntity.Builder()
                .name("John Doe")
                .phoneNumber("111-222-333")
                .currentPositionLatitude(new BigDecimal("30.12345"))
                .currentPositionLongitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(true)
                .build();
        setField(responder1, "id", 1, null);

        ResponderEntity responder2 = new ResponderEntity.Builder()
                .name("John Foo")
                .phoneNumber("999-888-777")
                .currentPositionLatitude(new BigDecimal("35.12345"))
                .currentPositionLongitude(new BigDecimal("-75.98765"))
                .boatCapacity(2)
                .medicalKit(true)
                .available(true)
                .build();
        setField(responder2, "id", 2, null);

        List<ResponderEntity> responderEntities = new ArrayList<>();
        responderEntities.add(responder1);
        responderEntities.add(responder2);

        Mockito.when(responderDao.availableResponders()).thenReturn(responderEntities);

        List<Responder> responders = service.availableResponders();
        assertThat(responders, notNullValue());
        assertThat(responders.size(), equalTo(2));
        Responder responder = responders.get(0);
        assertThat(responder.getId(), anyOf(equalTo(1L), equalTo(2L)));
        ResponderEntity matched;
        if (responder.getId() == 1) {
            matched = responder1;
        } else {
            matched = responder2;
        }
        assertThat(responder.getName(), equalTo(matched.getName()));
        assertThat(responder.getPhoneNumber(), equalTo(matched.getPhoneNumber()));
        assertThat(responder.getLatitude(), equalTo(matched.getCurrentPositionLatitude()));
        assertThat(responder.getLongitude(), equalTo(matched.getCurrentPositionLongitude()));
        assertThat(responder.getBoatCapacity(), equalTo(matched.getBoatCapacity()));
        assertThat(responder.isMedicalKit(),equalTo(matched.getMedicalKit()));
        assertThat(responder.isAvailable(), equalTo(matched.isAvailable()));
    }

}
