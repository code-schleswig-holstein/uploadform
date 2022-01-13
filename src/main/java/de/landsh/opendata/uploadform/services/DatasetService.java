package de.landsh.opendata.uploadform.services;

import de.landsh.opendata.uploadform.ResourceAlreadyExistsException;
import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;

    private boolean existsById(Long id) {
        return datasetRepository.existsById(id);
    }

    public Dataset findById(Long id) throws ResourceNotFoundException {
        return datasetRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Dataset> findAll(int pageNumber, int rowPerPage) {
        final List<Dataset> datasets = new ArrayList<>();
        final Pageable sortedByIdAsc = PageRequest.of(pageNumber - 1, rowPerPage, Sort.by("id").ascending());
        datasetRepository.findAll(sortedByIdAsc).forEach(datasets::add);
        return datasets;
    }

    public Dataset save(Dataset dataset) throws ResourceAlreadyExistsException {
        if (dataset.getId() != null && existsById(dataset.getId())) {
            throw new ResourceAlreadyExistsException("Dataset with id: " + dataset.getId() + " already exists");
        }
        return datasetRepository.save(dataset);
    }

    public void update(Dataset dataset) throws ResourceNotFoundException {
        if (!existsById(dataset.getId())) {
            throw new ResourceNotFoundException("Cannot find Dataset with id: " + dataset.getId());
        }
        datasetRepository.save(dataset);
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!existsById(id)) {
            throw new ResourceNotFoundException("Cannot find dataset with id: " + id);
        } else {
            datasetRepository.deleteById(id);
        }
    }

    public long count() {
        return datasetRepository.count();
    }

    public List<Dataset> findAllByMatrixAndOrganization(DatasetMatrix datasetMatrix, String organization) {
        return datasetRepository.findAllByDatasetMatrixAndOrganizationOrderByYear(datasetMatrix, organization);
    }


}
