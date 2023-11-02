package com.kpi.composer.dao;

import com.kpi.composer.TestUtil;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ComposedDocumentDaoTest {

    private final List<ComposedDocument> fakeDocuments = TestUtil.documentExample();

    @Autowired
    private ComposedDocumentDao composedDocumentDao;

    @Test
    void findAllTest() {
        Collection<ComposedDocument> all = composedDocumentDao.findAll();

        assertThat(all)
                .hasSize(3)
                .allSatisfy(item -> assertNotNull(item.getId()));

        int i = 0;
        for (ComposedDocument document : all) {
            assertThat(document)
                    .usingRecursiveComparison()
                    .isEqualTo(fakeDocuments.get(i));
            i++;
        }
    }

    @Test
    void findByIdTest() {
        ComposedDocument document = composedDocumentDao.findById(3L).orElseThrow();
        assertThat(document)
                .usingRecursiveComparison()
                .isEqualTo(fakeDocuments.get(2));
    }

    @Test
    void saveTest() {
        List<ComposedDocument> saved = new ArrayList<>();
        for (ComposedDocument fake : fakeDocuments) {
            fake.setId(null);
            saved.add(composedDocumentDao.save(fake));
        }

        assertThat(saved)
                .hasSize(3)
                .allSatisfy(item -> assertNotNull(item.getId()));

        for (int i = 0; i < 3; i++) {
            assertThat(saved.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(fakeDocuments.get(i));
        }
    }

    @Test
    void countTest() {
        assertThat(composedDocumentDao.count())
                .isEqualTo(3);
    }

    @Test
    void deleteLastTest() {
        assertThat(composedDocumentDao.count())
                .isEqualTo(3);

        composedDocumentDao.deleteLast();

        Collection<ComposedDocument> afterDelete = composedDocumentDao.findAll();

        assertThat(afterDelete)
                .hasSize(2)
                .allSatisfy(item -> assertNotNull(item.getId()));

        int i = 0;
        for (ComposedDocument document : afterDelete) {
            assertThat(document)
                    .usingRecursiveComparison()
                    .isEqualTo(fakeDocuments.get(i));
            i++;
        }
    }
}