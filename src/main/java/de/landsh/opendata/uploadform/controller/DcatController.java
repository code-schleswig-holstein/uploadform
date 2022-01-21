package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.model.LocalAuthority;
import de.landsh.opendata.uploadform.services.DatasetService;
import de.landsh.opendata.uploadform.services.LocalAuthorityService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DcatController {

    private final DatasetService datasetService;
    private final LocalAuthorityService localAuthorityService;

    @GetMapping(value = "/catalog.xml", produces = "application/rdf+xml")
    @ResponseBody
    public String catalogXML() {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rdf:RDF\n" +
                "  xmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n" +
                "  xmlns:spdx=\"http://spdx.org/rdf/terms#\"\n" +
                "  xmlns:locn=\"http://www.w3.org/ns/locn#\"\n" +
                "  xmlns:hydra=\"http://www.w3.org/ns/hydra/core#\"\n" +
                "  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "  xmlns:dcat=\"http://www.w3.org/ns/dcat#\"\n" +
                "  xmlns:dct=\"http://purl.org/dc/terms/\"\n" +
                "  xmlns:dcatde=\"http://dcat-ap.de/def/dcatde/\"\n" +
                "  xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n" +
                "  xmlns:schema=\"http://schema.org/\">\n" +
                "  <dcat:Catalog rdf:about=\"https://opendata.schleswig-holstein.de/upload/\">\n");

        List<Dataset> datasetList = datasetService.findAll(1, 100);
        for (Dataset dataset : datasetList) {
            if (dataset.getUploadDate() != null) {
                sb.append(toDcat(dataset, false));
            }
        }

        sb.append("</dcat:Catalog></rdf:RDF>\n");

        return sb.toString();
    }

    String toDcat(Dataset dataset, boolean withPrefix) {
        final DatasetMatrix dm = dataset.getDatasetMatrix();
        final StringBuilder sb = new StringBuilder();
        final LocalAuthority localAuthority = localAuthorityService.findById(dataset.getOrganization());
        if( withPrefix) {
            sb.append("<dcat:dataset xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                    "  xmlns:dcat=\"http://www.w3.org/ns/dcat#\"\n" +
                    "  xmlns:dct=\"http://purl.org/dc/terms/\"\n" +
                    "  xmlns:dcatde=\"http://dcat-ap.de/def/dcatde/\"\n" +
                    "  xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n" +
                    "  xmlns:schema=\"http://schema.org/\">\n");
        }else {
            sb.append("<dcat:dataset>\n");
        }
        sb.append("<dcat:Dataset rdf:about=\"https://opendata.schleswig-holstein.de/upload/dataset/");
        sb.append(dataset.getId());
        sb.append("\">\n");

        sb.append("<dct:title>").append(StringEscapeUtils.escapeXml11(dm.getTitle())).append("</dct:title>\n");
        sb.append("<dct:description>").append(StringEscapeUtils.escapeXml11(dm.getDescription())).append("</dct:description>\n");

        if (localAuthority != null) {
            sb.append("<dct:spatial rdf:resource='").append(localAuthority.getUri()).append("'/>\n");
            sb.append("<dcatde:politicalGeocodingURI rdf:resource=\"").append(localAuthority.getUri()).append("\"/>\n");
        }
        sb.append("<dct:issued rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">")
                .append(dataset.getUploadDate().format(DateTimeFormatter.ISO_DATE))
                .append("</dct:issued>");
        sb.append("<dct:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">")
                .append(dataset.getUploadDate().format(DateTimeFormatter.ISO_DATE))
                .append("</dct:modified>");
        sb.append("<dct:temporal><dct:PeriodOfTime>");
        sb.append("<schema:startDate rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">").append(dataset.getYear()).append("-01-01</schema:startDate>");
        sb.append("<schema:endDate rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">").append(dataset.getYear()).append("-01-01</schema:endDate>");
        sb.append("</dct:PeriodOfTime></dct:temporal>");
        sb.append("</dcat:Dataset>\n");
        sb.append("</dcat:dataset>\n");

        return sb.toString();
    }
}
