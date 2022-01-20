package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.services.DatasetMatrixService;
import de.landsh.opendata.uploadform.services.DatasetService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@RequiredArgsConstructor
public class UploadController {
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    private final DatasetMatrixService datasetMatrixService;
    private final DatasetService datasetService;

    @Value("${uploader.directory}")
    private File targetDir;

    @GetMapping("/upload")
    public String uploadForm(
            @RequestParam String dataset,
            @RequestParam String organization,
            @RequestParam String code,
            Model model) {

        final DatasetMatrix datasetMatrix = datasetMatrixService.findById(dataset);
        final List<Dataset> datasets = datasetService.findAllByMatrixAndOrganization(datasetMatrix, organization);

        if (datasetMatrixService.isValidSecurityCode(datasetMatrix, organization, code)) {
            model.addAttribute("datasetMatrix", datasetMatrix);
            model.addAttribute("datasets", datasets);
            model.addAttribute("organization", organization);
            model.addAttribute("code", code);
            return "upload";
        } else {
            return "invalid-code";
        }
    }

    @PostMapping("/upload")
    public String uploadForm(@RequestParam String code, MultipartRequest request,
                             Model model) throws IOException {
        code = StringUtils.substringBefore(code, ","); // For some reason the hidden parameter code is duplicated.
        final Map<String, MultipartFile> files = request.getFileMap();

        Set<Dataset> uploadedDatasets = new HashSet<>();

        for (String name : files.keySet()) {
            if (name.matches("file-[0-9]+")) {
                final long id = NumberUtils.toLong(StringUtils.substringAfter(name, "file-"));
                final Dataset ds = datasetService.findById(id);
                if (isValidForUpload(ds, code)) {
                    final MultipartFile file = files.get(name);
                    if (file.getSize() > 0) {
                        final String suffix = StringUtils.substringAfterLast(file.getContentType(), "/");
                        final String targetName = ds.getDatasetMatrix().getId() + "-" + ds.getYear() + "-" + ds.getOrganization() + "." + suffix;
                        file.transferTo(new File(targetDir, targetName));

                        // mark dataset as done
                        ds.setFile(file.getOriginalFilename());
                        ds.setUploadDate(LocalDateTime.now());
                        datasetService.update(ds);

                        log.info("Upload for dataset matrix {} from organization {} with filename {}",
                                ds.getDatasetMatrix().getId(), ds.getOrganization(), ds.getFile());

                        uploadedDatasets.add(ds);
                    }
                }
            }
        }

        model.addAttribute("datasets", uploadedDatasets);

        return "thankyou";
    }

    boolean isValidForUpload(Dataset dataset, String code) {
        if (dataset == null) {
            return false;
        }
        String expectedCode = datasetMatrixService.getSecurityCode(dataset.getDatasetMatrix(), dataset.getOrganization());
        return expectedCode.equals(code);
    }

}
