package de.landsh.opendata.uploadform.repository;

import de.landsh.opendata.uploadform.model.LocalAuthority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LocalAuthorityRepository extends PagingAndSortingRepository<LocalAuthority, String>, CrudRepository<LocalAuthority, String> {
}
