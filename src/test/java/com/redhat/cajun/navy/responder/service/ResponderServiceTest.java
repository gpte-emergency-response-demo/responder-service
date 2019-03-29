package com.redhat.cajun.navy.responder.service;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.redhat.cajun.navy.responder.dao.ResponderDao;
import com.redhat.cajun.navy.responder.entity.ResponderEntity;
import com.redhat.cajun.navy.responder.model.Responder;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

public class ResponderServiceTest {

    @Mock
    private ResponderDao responderDao;

    @Captor
    private ArgumentCaptor<ResponderEntity> entityCaptor;

    private ResponderService service;

    @Before
    public void init() {
        initMocks(this);
        service = new ResponderService();
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

        when(responderDao.availableResponders()).thenReturn(responderEntities);

        List<Responder> responders = service.availableResponders();
        assertThat(responders, notNullValue());
        assertThat(responders.size(), equalTo(2));
        Responder responder = responders.get(0);
        assertThat(responder.getId(), anyOf(equalTo("1"), equalTo("2")));
        ResponderEntity matched;
        if (responder.getId().equals("1")) {
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

    @Test
    public void testUpdateResponder() {

        Responder toUpdate = new Responder.Builder("1").available(false).build();

        ResponderEntity currentEntity = new ResponderEntity.Builder()
                .name("John Doe")
                .phoneNumber("111-222-333")
                .currentPositionLatitude(new BigDecimal("30.12345"))
                .currentPositionLongitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(true)
                .build();
        setField(currentEntity, "id", 1, null);

        ResponderEntity updatedEntity = new ResponderEntity.Builder()
                .name("John Doe")
                .phoneNumber("111-222-333")
                .currentPositionLatitude(new BigDecimal("30.12345"))
                .currentPositionLongitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(false)
                .build();
        setField(updatedEntity, "id", 1, null);

        when(responderDao.findById(1L)).thenReturn(currentEntity);
        when(responderDao.merge(any(ResponderEntity.class))).thenReturn(updatedEntity);

        Triple<Boolean, String, Responder> result = service.updateResponder(toUpdate);
        assertThat(result, notNullValue());
        assertThat(result.getLeft(), equalTo(true));
        assertThat(result.getRight(), notNullValue());
        Responder updated = result.getRight();
        assertThat(updated.getId(), equalTo("1"));
        assertThat(updated.getName(), equalTo("John Doe"));
        assertThat(updated.getPhoneNumber(), equalTo("111-222-333"));
        assertThat(updated.getLatitude(), equalTo(new BigDecimal("30.12345")));
        assertThat(updated.getLongitude(), equalTo(new BigDecimal("-70.98765")));
        assertThat(updated.getBoatCapacity(), equalTo(3));
        assertThat(updated.isMedicalKit(), equalTo(true));
        assertThat(updated.isAvailable(), equalTo(false));
        verify(responderDao).findById(1L);
        verify(responderDao).merge(entityCaptor.capture());
        ResponderEntity entity = entityCaptor.getValue();
        assertThat(entity, notNullValue());
        assertThat(entity.getId(), equalTo(1L));
        assertThat(entity.getName(), equalTo("John Doe"));
        assertThat(entity.getPhoneNumber(), equalTo("111-222-333"));
        assertThat(entity.getCurrentPositionLatitude(), equalTo(new BigDecimal("30.12345")));
        assertThat(entity.getCurrentPositionLongitude(), equalTo(new BigDecimal("-70.98765")));
        assertThat(entity.getBoatCapacity(), equalTo(3));
        assertThat(entity.getMedicalKit(), equalTo(true));
        assertThat(entity.isAvailable(), equalTo(false));
    }

    @Test
    public void testUpdateResponderWhenStateHasNotChanged() {

        Responder toUpdate = new Responder.Builder("1").available(false).build();

        ResponderEntity currentEntity = new ResponderEntity.Builder()
                .name("John Doe")
                .phoneNumber("111-222-333")
                .currentPositionLatitude(new BigDecimal("30.12345"))
                .currentPositionLongitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(false)
                .build();
        setField(currentEntity, "id", 1, null);

        when(responderDao.findById(1L)).thenReturn(currentEntity);

        Triple<Boolean, String, Responder> result = service.updateResponder(toUpdate);
        assertThat(result, notNullValue());
        assertThat(result.getLeft(), equalTo(false));
        Responder updated = result.getRight();
        assertThat(updated.getId(), equalTo("1"));
        assertThat(updated.getName(), equalTo("John Doe"));
        assertThat(updated.getPhoneNumber(), equalTo("111-222-333"));
        assertThat(updated.getLatitude(), equalTo(new BigDecimal("30.12345")));
        assertThat(updated.getLongitude(), equalTo(new BigDecimal("-70.98765")));
        assertThat(updated.getBoatCapacity(), equalTo(3));
        assertThat(updated.isMedicalKit(), equalTo(true));
        assertThat(updated.isAvailable(), equalTo(false));
        verify(responderDao).findById(1L);
        verify(responderDao, never()).merge(any());
    }

}
