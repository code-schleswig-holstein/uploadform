package de.landsh.opendata.uploadform.services;

import de.landsh.opendata.uploadform.ResourceAlreadyExistsException;
import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.LocalAuthority;
import de.landsh.opendata.uploadform.repository.LocalAuthorityRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalAuthorityService {

    @Autowired
    private LocalAuthorityRepository localAuthorityRepository;

    private boolean existsById(String id) {
        return localAuthorityRepository.existsById(id);
    }

    public LocalAuthority findById(String id) throws ResourceNotFoundException {
        return localAuthorityRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<LocalAuthority> findAll(int pageNumber, int rowPerPage) {
        final List<LocalAuthority> localAuthoritys = new ArrayList<>();
        final Pageable sortedByIdAsc = PageRequest.of(pageNumber - 1, rowPerPage, Sort.by("id").ascending());
        localAuthorityRepository.findAll(sortedByIdAsc).forEach(localAuthoritys::add);
        return localAuthoritys;
    }

    public LocalAuthority save(LocalAuthority localAuthority) throws ResourceAlreadyExistsException {
        if (localAuthority.getId() != null && existsById(localAuthority.getId())) {
            throw new ResourceAlreadyExistsException("LocalAuthority with id: " + localAuthority.getId() + " already exists");
        }
        return localAuthorityRepository.save(localAuthority);
    }

    public void update(LocalAuthority localAuthority) throws ResourceNotFoundException {
        if (!existsById(localAuthority.getId())) {
            throw new ResourceNotFoundException("Cannot find LocalAuthority with id: " + localAuthority.getId());
        }
        localAuthorityRepository.save(localAuthority);
    }

    public void deleteById(String id) throws ResourceNotFoundException {
        if (!existsById(id)) {
            throw new ResourceNotFoundException("Cannot find localAuthority with id: " + id);
        } else {
            localAuthorityRepository.deleteById(id);
        }
    }

    public long count() {
        return localAuthorityRepository.count();
    }

    public void importFromCSV(InputStream csvData) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(csvData));
        String line = in.readLine();

        if (!"ARS\tName\tTyp\tURI\tAufgaben Kreis\tAufgaben Gemeinde".equals(line)) {
            throw new RuntimeException("Invalid format for LocalAuthority CSV data.");
        }

        line = in.readLine();
        while (line != null) {
            final String[] s = line.split("\t");
            final LocalAuthority localAuthority = new LocalAuthority();
            localAuthority.setId(s[0]);
            localAuthority.setName(s[1]);
            localAuthority.setType(s[2]);
            localAuthority.setUri(s[3]);
            localAuthority.setCounty(BooleanUtils.toBoolean(s[4]));
            localAuthority.setCounty(BooleanUtils.toBoolean(s[5]));
            save(localAuthority);

            line = in.readLine();
        }
    }

    public String name(String id) {
        final Optional<LocalAuthority> localAuthority = localAuthorityRepository.findById(id);
        return localAuthority.map(authority -> authority.getType() + " " + authority.getName()).orElse(StringUtils.EMPTY);
    }

}
