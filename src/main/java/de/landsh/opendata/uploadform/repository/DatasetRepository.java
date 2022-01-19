package de.landsh.opendata.uploadform.repository;

import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface DatasetRepository extends PagingAndSortingRepository<Dataset, Long>, CrudRepository<Dataset, Long> {
    Dataset findByDatasetMatrixAndOrganizationAndYear(DatasetMatrix datasetMatrix, String organization, int year);
    List<Dataset> findAllByDatasetMatrixAndOrganizationOrderByYear(DatasetMatrix datasetMatrix, String organization);

    Collection<Dataset> findAllByDatasetMatrix(DatasetMatrix datasetMatrix);

    Collection<Dataset>  findAllByOrganization(String id);
}
