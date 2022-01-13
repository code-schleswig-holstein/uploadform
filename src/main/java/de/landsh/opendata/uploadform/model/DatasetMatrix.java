package de.landsh.opendata.uploadform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetMatrix {
    @Id
    private String id;

    private String title;
    private String description;

}
