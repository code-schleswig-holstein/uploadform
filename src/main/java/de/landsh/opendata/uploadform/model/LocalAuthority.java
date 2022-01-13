package de.landsh.opendata.uploadform.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class LocalAuthority {
    @Id
    String id;
    
    String name;
    String uri;
    String type;
    boolean municipality;
    boolean county;
}
