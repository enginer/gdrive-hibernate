package org.gdrive;

import org.gdrive.dao.CommonDao;
import org.gdrive.model.TestModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestHibernate.class)
@Configuration
@WebAppConfiguration
@ComponentScan(basePackages = {"org.gdrive"})
@PropertySource("classpath:database.properties")
@ImportResource({"classpath:spring/hibernate.xml"})
public class TestHibernate {

    @Autowired CommonDao commonDao;

    @Test
    public void test() throws Exception {
        commonDao.getAll(TestModel.class);
    }


    @Bean // for @PropertySource work
    public static PropertySourcesPlaceholderConfigurer pspc(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
