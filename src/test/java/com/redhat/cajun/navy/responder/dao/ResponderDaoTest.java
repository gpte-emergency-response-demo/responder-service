package com.redhat.cajun.navy.responder.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import com.redhat.cajun.navy.responder.entity.ResponderEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.jta.NarayanaJtaConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ResponderEntity.class, ResponderDao.class, JpaProperties.class, NarayanaJtaConfiguration.class,
        HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class ResponderDaoTest {

    @Autowired
    private ResponderDao responderDao;

    @Test
    @Transactional
    public void testPersistResponderEntity() {

        assertThat(responderDao, notNullValue());

        ResponderEntity responder = new ResponderEntity.Builder()
                .name("John Doe")
                .phoneNumber("111-222-333")
                .currentPositionLatitude(new BigDecimal("30.12345"))
                .currentPositionLongitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(true)
                .build();

        responderDao.create(responder);
        assertThat(responder.getId(), not(equalTo(0)));

    }

    @Test
    @Transactional
    public void testAvailableResponders() {

        responderDao.deleteAll();

        ResponderEntity responder1 = new ResponderEntity.Builder()
                .name("John Doe")
                .phoneNumber("111-222-333")
                .currentPositionLatitude(new BigDecimal("30.12345"))
                .currentPositionLongitude(new BigDecimal("-70.98765"))
                .boatCapacity(3)
                .medicalKit(true)
                .available(false)
                .build();

        ResponderEntity responder2 = new ResponderEntity.Builder()
                .name("John Foo")
                .phoneNumber("999-888-777")
                .currentPositionLatitude(new BigDecimal("35.12345"))
                .currentPositionLongitude(new BigDecimal("-75.98765"))
                .boatCapacity(2)
                .medicalKit(true)
                .available(true)
                .build();

        responderDao.create(responder1);
        responderDao.create(responder2);

        List<ResponderEntity> responders = responderDao.availableResponders();
        assertThat(responders.size(), equalTo(1));
        ResponderEntity responder = responders.get(0);
        assertThat(responder.getName(), equalTo("John Foo"));
    }

}
