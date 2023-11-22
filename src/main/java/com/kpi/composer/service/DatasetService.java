package com.kpi.composer.service;

import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.exception.NotOwnerException;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.mapper.FileMapper;
import com.kpi.composer.service.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatasetService {

    private final DatasetDao datasetDao;

    private final UserService userService;

    private final FileMapper fileMapper;

    private final AuthenticationFacade auth;

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
        final String currentUsername = this.getCurrentUsername();
        if (!dataset.get().getOwner().getUsername().equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        return dataset.get();
    }

    public DatasetDto findDtoById(Long id) {
        return fileMapper.datasetToDto(this.findById(id));
    }

    public Collection<DatasetDto> findByOwner(String username) {
        final String currentUsername = this.getCurrentUsername();
        if (!username.equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        return datasetDao
                .findAllByOwner(username)
                .stream()
                .map(fileMapper::datasetToDto)
                .toList();
    }

    public DatasetDto create(DatasetDto fileDto) {
        final String currentUsername = this.getCurrentUsername();
        if (datasetDao.count(currentUsername) >= datasetMaxNumber) {
            throw new MaxNumberExceededException("Max number of datasets reached: " + datasetMaxNumber);
        }

        final User owner = userService.findByUsername(currentUsername);
        return fileMapper.datasetToDto(datasetDao.save(fileMapper.dtoToDataset(fileDto, owner)));
    }

    private String getCurrentUsername() {
        return auth.getAuthentication().getName();
    }
}
