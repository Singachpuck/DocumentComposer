package com.kpi.composer.dao;

import com.kpi.composer.TestUtil;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DatasetDaoTest {

    private final List<Dataset> fakeDatasets = TestUtil.datasetExample();

    @Autowired
    private DatasetDao datasetDao;

    @Test
    void findAllTest() {
        Collection<Dataset> all = datasetDao.findAll();

        assertThat(all)
                .hasSize(3)
                .allSatisfy(item -> assertNotNull(item.getId()));

        int i = 0;
        for (Dataset dataset : all) {
            assertTrue(new ReflectionEquals(dataset).matches(fakeDatasets.get(i++)));
        }
    }

    @Test
    void findByIdTest() {
        Dataset template = datasetDao.findById(3L).orElseThrow();
        assertTrue(new ReflectionEquals(template).matches(fakeDatasets.get(2)));
    }

    @Test
    void saveTest() {
        List<Dataset> saved = new ArrayList<>();
        for (Dataset fakeDataset : fakeDatasets) {
            fakeDataset.setId(null);
            saved.add(datasetDao.save(fakeDataset));
        }

        assertThat(saved)
                .hasSize(3)
                .allSatisfy(item -> assertNotNull(item.getId()));

        for (int i = 0; i < 3; i++) {
            assertTrue(new ReflectionEquals(saved.get(i)).matches(fakeDatasets.get(i)));
        }
    }

    @Test
    void countTest() {
        assertThat(datasetDao.count())
                .isEqualTo(3);
    }
}