package de.landsh.opendata.uploadform.services;

import de.landsh.opendata.uploadform.ResourceAlreadyExistsException;
import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.repository.DatasetRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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

    /**
     * Liefert für eine DatasetMatrix eine Übersichtstabelle, welche Datensätze vorhanden sind.
     */
    public List<List<String>> getDatasetOverview(DatasetMatrix datasetMatrix) {
        final List<List<String>> table = new ArrayList<>();

        if (datasetMatrix == null) {
            return table;
        }

        final SortedSet<Integer> availableYears = new TreeSet<>();
        final SortedSet<String> availableOrganizations = new TreeSet<>();
        final Collection<Dataset> datasets = datasetRepository.findAllByDatasetMatrix(datasetMatrix);
        for (Dataset ds : datasets) {
            availableYears.add(ds.getYear());
            availableOrganizations.add(ds.getOrganization());
        }

        final List<String> header = new ArrayList<>(availableYears.size() + 1);
        header.add("Kommune");
        for (Integer year : availableYears) {
            header.add(Integer.toString(year));
        }
        table.add(header);

        for (String org : availableOrganizations) {
            final List<String> row = new ArrayList<>(availableYears.size() + 1);
            row.add(org);
            for (Integer year : availableYears) {
                Long datasetId = findExistingDatasetId(datasets, org, year);
                row.add(datasetId == null ? StringUtils.EMPTY : Long.toString(datasetId));
            }
            table.add(row);
        }
        return table;
    }

    Long findExistingDatasetId(Collection<Dataset> allDatasets, String organization, int year) {
        for (Dataset ds : allDatasets) {
            if (ds.getYear() == year && organization.equals(ds.getOrganization()) && StringUtils.isEmpty(ds.getFile())) {
                return ds.getId();
            }
        }
        return null;
    }

}
