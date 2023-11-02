package com.kpi.composer.service;

import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.FileDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatasetService {

    private final DatasetDao datasetDao;

    private final FileMapper fileMapper;

    @Value("${entity.dataset.max-number}")
    private long datasetMaxNumber;

    public Collection<DatasetDto> findAll() {
        return datasetDao
                .findAll()
                .stream()
                .map(fileMapper::datasetToDto)
                .toList();
    }

    public Dataset findById(Long id) {
        final Optional<Dataset> dataset = datasetDao.findById(id);
        if (dataset.isEmpty()) {
            throw new EntityException("Dataset with id " + id + " does not exist.");
        }
        return dataset.get();
    }

    public DatasetDto findDtoById(Long id) {
        return fileMapper.datasetToDto(this.findById(id));
    }

    public DatasetDto create(FileDto fileDto) {
        if (datasetDao.count() >= datasetMaxNumber) {
            throw new MaxNumberExceededException("Max number of datasets reached: " + datasetMaxNumber);
        }
        return fileMapper.datasetToDto(datasetDao.save(fileMapper.dtoToDataset(fileDto)));
    }
}
