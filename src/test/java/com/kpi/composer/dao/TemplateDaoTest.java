package com.kpi.composer.dao;

import com.kpi.composer.TestUtil;
import com.kpi.composer.model.entities.Template;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TemplateDaoTest {

    private final List<Template> fakeTemplates = TestUtil.templateExample();

    @Autowired
    private TemplateDao templateDao;

    @Test
    void saveTest() {
        List<Template> saved = new ArrayList<>();
        for (Template fakeTemplate : fakeTemplates) {
            fakeTemplate.setId(null);
            saved.add(templateDao.save(fakeTemplate));
        }

        assertThat(saved)
                .hasSize(3)
                .allSatisfy(item -> assertNotNull(item.getId()));

        for (int i = 0; i < 3; i++) {
            assertTrue(new ReflectionEquals(saved.get(i)).matches(fakeTemplates.get(i)));
        }
    }

    @Test
    void findAllTest() {
        Collection<Template> all = templateDao.findAll();

        assertThat(all)
                .hasSize(3)
                .allSatisfy(item -> assertNotNull(item.getId()));

        int i = 0;
        for (Template template : all) {
            assertTrue(new ReflectionEquals(template).matches(fakeTemplates.get(i++)));
        }
    }

    @Test
    void findByIdTest() {
        Template template = templateDao.findById(2L).orElseThrow();
        assertTrue(new ReflectionEquals(template).matches(fakeTemplates.get(1)));
    }

    @Test
    void countTest() {
        assertThat(templateDao.count())
                .isEqualTo(3);
    }
}