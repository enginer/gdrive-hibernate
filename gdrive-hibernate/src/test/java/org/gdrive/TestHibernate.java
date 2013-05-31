package org.gdrive;

import org.gdrive.model.TestModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestHibernate.class)
@Configuration
@ComponentScan(basePackages = {"org.gdrive"})
@PropertySource({"database.properties", "google.properties"})
@ImportResource({"classpath:spring/hibernate.xml"})
public class TestHibernate {

    @Autowired
    org.gdrive.dao.CommonDao commonDao;

    @Test
    public void test() throws Exception {
        List all = commonDao.getAll(TestModel.class);
        Assert.assertTrue(all.size() > 0);
    }

    @Bean // for @PropertySource
    public static org.springframework.context.support.PropertySourcesPlaceholderConfigurer pspc(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
