package de.landsh.opendata.uploadform.repository;

import de.landsh.opendata.uploadform.model.DatasetMatrix;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DatasetMatrixRepository extends PagingAndSortingRepository<DatasetMatrix, String>, CrudRepository<DatasetMatrix, String> {
}
