package de.landsh.opendata.uploadform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    int year;
    String organization;

    String file;
    LocalDateTime uploadDate;

    @ManyToOne
    private DatasetMatrix datasetMatrix;
}
