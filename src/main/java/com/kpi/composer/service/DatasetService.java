package com.kpi.composer.service;

import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.model.dto.FileDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class DatasetService {

    private DatasetDao datasetDao;

    private FileMapper fileMapper;

    public Collection<Dataset> findAll() {
        return datasetDao.findAll();
    }

    public Dataset findById(Long id) {
        return datasetDao.findById(id).get();
    }

    public Dataset create(FileDto fileDto) {
        return datasetDao.save(fileMapper.dtoToDataset(fileDto));
    }
}
