package de.landsh.opendata.uploadform.services;

import de.landsh.opendata.uploadform.ResourceAlreadyExistsException;
import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.repository.DatasetMatrixRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetMatrixService {

    @Autowired
    private DatasetMatrixRepository datasetmatrixRepository;
    @Value("${uploader.secret}")
    private String secret;

    private boolean existsById(String id) {
        return datasetmatrixRepository.existsById(id);
    }

    public DatasetMatrix findById(String id) throws ResourceNotFoundException {
        return datasetmatrixRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<DatasetMatrix> findAll(int pageNumber, int rowPerPage) {
        final List<DatasetMatrix> datasetmatrixs = new ArrayList<>();
        final Pageable sortedByIdAsc = PageRequest.of(pageNumber - 1, rowPerPage, Sort.by("id").ascending());
        datasetmatrixRepository.findAll(sortedByIdAsc).forEach(datasetmatrixs::add);
        return datasetmatrixs;
    }

    public DatasetMatrix save(DatasetMatrix datasetmatrix) throws ResourceAlreadyExistsException {
        if (datasetmatrix.getId() != null && existsById(datasetmatrix.getId())) {
            throw new ResourceAlreadyExistsException("DatasetMatrix with id: " + datasetmatrix.getId() + " already exists");
        }
        return datasetmatrixRepository.save(datasetmatrix);
    }

    public void update(DatasetMatrix datasetmatrix) throws ResourceNotFoundException {
        if (!existsById(datasetmatrix.getId())) {
            throw new ResourceNotFoundException("Cannot find DatasetMatrix with id: " + datasetmatrix.getId());
        }
        datasetmatrixRepository.save(datasetmatrix);
    }

    public void deleteById(String id) throws ResourceNotFoundException {
        if (!existsById(id)) {
            throw new ResourceNotFoundException("Cannot find datasetmatrix with id: " + id);
        } else {
            datasetmatrixRepository.deleteById(id);
        }
    }

    public long count() {
        return datasetmatrixRepository.count();
    }

    /**
     * Calculates a secret code for a dataset matrix and an organization.
     */
    public String getSecurityCode(DatasetMatrix datasetMatrix, String organization) {
        return DigestUtils.sha256Hex(secret + datasetMatrix.getId() + organization);
    }

    public boolean isValidSecurityCode(DatasetMatrix datasetMatrix, String organization, String code) {
        return code.equals(getSecurityCode(datasetMatrix,organization));
    }
}
