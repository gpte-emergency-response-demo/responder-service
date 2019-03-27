package com.redhat.cajun.navy.responder.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.persistence.OptimisticLockException;

import com.redhat.cajun.navy.responder.entity.ResponderEntity;
import org.junit.Assert;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ResponderEntity.class, ResponderDao.class, JpaProperties.class, NarayanaJtaConfiguration.class,
        HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class ResponderDaoTest {

    @Autowired
    private ResponderDao responderDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

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

    @Test
    public void testFindById() {
        ResponderEntity responder = new ResponderEntity.Builder()
                .name("John Foo")
                .phoneNumber("999-888-777")
                .currentPositionLatitude(new BigDecimal("35.12345"))
                .currentPositionLongitude(new BigDecimal("-75.98765"))
                .boatCapacity(2)
                .medicalKit(true)
                .available(true)
                .build();

        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            responderDao.create(responder);
            return null;
        });

        template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            ResponderEntity r = responderDao.findById(responder.getId());
            assertThat(r, notNullValue());
            assertThat(r.getName(), equalTo(responder.getName()));
            return null;
        });
    }

    @Test
    public void testUpdateEntity() {
        ResponderEntity responder = new ResponderEntity.Builder()
                .name("John Foo")
                .phoneNumber("999-888-777")
                .currentPositionLatitude(new BigDecimal("35.12345"))
                .currentPositionLongitude(new BigDecimal("-75.98765"))
                .boatCapacity(2)
                .medicalKit(true)
                .available(true)
                .build();

        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            responderDao.create(responder);
            return null;
        });

        template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            ResponderEntity r = responderDao.findById(responder.getId());
            assertThat(r.isAvailable(), equalTo(true));
            ResponderEntity r2 = new ResponderEntity.Builder(r).available(false).build();
            responderDao.merge(r2);
            return null;
        });

        template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            ResponderEntity r = responderDao.findById(responder.getId());
            assertThat(r, notNullValue());
            assertThat(r.getName(), equalTo(responder.getName()));
            assertThat(r.isAvailable(), equalTo(false));
            return null;
        });


    }

    @Test
    public void testOptimisticLocking() throws Exception {

        CountDownLatch latch = new CountDownLatch(2);
        CountDownLatch thread2Latch = new CountDownLatch(1);

        ResponderEntity responder = new ResponderEntity.Builder()
                .name("John Foo")
                .phoneNumber("999-888-777")
                .currentPositionLatitude(new BigDecimal("35.12345"))
                .currentPositionLongitude(new BigDecimal("-75.98765"))
                .boatCapacity(2)
                .medicalKit(true)
                .available(true)
                .build();

        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            responderDao.create(responder);
            return null;
        });

        Runnable thread1 = () -> {
            TransactionTemplate template1 = new TransactionTemplate(transactionManager);
            template1.execute((TransactionStatus s) -> {
                try {
                    ResponderEntity r = responderDao.findById(responder.getId());
                    // wait for thread2 to finish
                    thread2Latch.await(10, TimeUnit.SECONDS);

                    ResponderEntity r2 = new ResponderEntity.Builder(r).available(false).build();
                    responderDao.merge(r2);
                    // OptimisticLockException is expected
                    Assert.fail();
                } catch (Exception e) {
                    e.printStackTrace();
                    assertThat(e, is(instanceOf(OptimisticLockException.class)));
                } finally {
                    latch.countDown();
                }
                return null;
            });
        };

        Runnable thread2 = () -> {
            TransactionTemplate template2 = new TransactionTemplate(transactionManager);
            template2.execute((TransactionStatus s) -> {
                try {
                    ResponderEntity r = responderDao.findById(responder.getId());
                    ResponderEntity r2 = new ResponderEntity.Builder(r).available(false).build();
                    responderDao.merge(r2);
                } finally {
                    thread2Latch.countDown();
                    latch.countDown();
                }
                return null;
            });
        };

        new Thread(thread1).start();
        new Thread(thread2).start();

        latch.await(10, TimeUnit.SECONDS);

        template = new TransactionTemplate(transactionManager);
        template.execute((TransactionStatus s) -> {
            ResponderEntity r = responderDao.findById(responder.getId());
            assertThat(r.isAvailable(), equalTo(false));
            return null;
        });
    }

}
