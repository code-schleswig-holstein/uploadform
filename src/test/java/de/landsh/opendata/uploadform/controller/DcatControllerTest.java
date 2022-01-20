package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.services.DatasetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

class DcatControllerTest {

    private DcatController controller;

    @MockBean
    private DatasetService datasetService;

    @BeforeEach
    public void setUp() {
        controller = new DcatController(datasetService);
    }

    @Test
    void toDcat() throws ParserConfigurationException, SAXException, IOException {
        DatasetMatrix datasetMatrix = new DatasetMatrix();
        datasetMatrix.setId("vornamen");
        datasetMatrix.setTitle("Vornamen");
        datasetMatrix.setDescription("Die Vornamensstatistik ... siehe https://example.org?a=1&b=2");

        Dataset dataset = new Dataset();
        dataset.setId(1L);
        dataset.setDatasetMatrix(datasetMatrix);
        dataset.setOrganization("010585890");  // Amt Hüttener Berge
        dataset.setYear(2021);

        String result = controller.toDcat(dataset);

        // assert that XML is well-formed
        final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(new InputSource(new StringReader(result)), new DefaultHandler());

    }
}
