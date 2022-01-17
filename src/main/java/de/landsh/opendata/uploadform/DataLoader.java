package de.landsh.opendata.uploadform;

import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.repository.DatasetRepository;
import de.landsh.opendata.uploadform.services.DatasetMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final DatasetMatrixService datasetMatrixService;
    private final DatasetRepository datasetRepository;

    @Override
    public void run(ApplicationArguments args)  {

        if (datasetMatrixService.count() == 0) {
            Set<DatasetMatrix> datasetMatrix = new HashSet<>();
            datasetMatrix.add(datasetMatrixService.save(new DatasetMatrix("standesamt-statistik-besondere-beurkundungen", "Statistik Besondere Beurkundungen des Standesamtes", "")));
            datasetMatrix.add(datasetMatrixService.save(new DatasetMatrix("standesamt-statistik-sterberegister", "Statistik Sterberegister des Standesamtes", "")));
            datasetMatrix.add(datasetMatrixService.save(new DatasetMatrix("standesamt-statistik-geburtenregister", "Statistik Geburtenregister des Standesamtes", "")));
            datasetMatrix.add(datasetMatrixService.save(new DatasetMatrix("standesamt-statistik-lebenspartnerschaftsregister", "Statistik Lebenspartnerschaftsregister des Standesamtes", "")));
            datasetMatrix.add(datasetMatrixService.save(new DatasetMatrix("standesamt-statistik-eheregister", "Statistik Eheregister des Standesamtes", "")));
            datasetMatrix.add(datasetMatrixService.save(new DatasetMatrix("vornamen", "Vornamenliste", "")));

            String[] orgs = new String[]{"010595974", "010600063", "010610029", "010560050"};

            for (String org : orgs) {
                for (DatasetMatrix dm : datasetMatrix) {
                    System.out.println("https://opendata-stage.schleswig-holstein.de/upload/upload?dataset=" + dm.getId() + "&organization=" + org + "&code=" + datasetMatrixService.getSecurityCode(dm, org));
                    for (int year = 2015; year < 2022; year++) {

                        datasetRepository.save(new Dataset(null, year, org, null, null, dm));
                    }
                }
            }
        }


    }
}
