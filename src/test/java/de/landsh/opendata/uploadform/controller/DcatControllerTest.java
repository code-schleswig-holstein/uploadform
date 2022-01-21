package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.model.LocalAuthority;
import de.landsh.opendata.uploadform.services.DatasetService;
import de.landsh.opendata.uploadform.services.LocalAuthorityService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DcatControllerTest {
    private DcatController controller;


    private DatasetService datasetService = Mockito.mock(DatasetService.class);

    private LocalAuthorityService localAuthorityService = Mockito.mock(LocalAuthorityService.class);

    @BeforeEach
    public void setUp() {
        controller = new DcatController(datasetService, localAuthorityService);
    }

    @Test
    public void catalogXML() throws DocumentException {
        String result = controller.catalogXML();
        Document doc = new SAXReader().read(new StringReader(result));

    }

    @Test
    public void toDcat() throws  DocumentException {
        DatasetMatrix datasetMatrix = new DatasetMatrix();
        datasetMatrix.setId("vornamen");
        datasetMatrix.setTitle("Vornamen");
        datasetMatrix.setDescription("Die Vornamensstatistik ... siehe https://example.org?a=1&b=2");

        final Dataset dataset = new Dataset();
        dataset.setId(1L);
        dataset.setDatasetMatrix(datasetMatrix);
        dataset.setOrganization("010585890");  // Amt Hüttener Berge
        dataset.setYear(2021);
        dataset.setUploadDate(LocalDateTime.parse("2021-01-21T06:39:00"));

        final LocalAuthority localAuthority = new LocalAuthority();
        localAuthority.setUri("http://dcat-ap.de/def/politicalGeocoding/municipalAssociationKey/010585890");
        localAuthority.setName("Hüttener Berge");
        localAuthority.setType("Amt");
        localAuthority.setCounty(false);
        localAuthority.setMunicipality(true);

        Mockito.when(localAuthorityService.findById("010585890")).thenReturn(localAuthority);

        String result = controller.toDcat(dataset, true);

        // assert that XML is well-formed
        Document doc = new SAXReader().read(new StringReader(result));

        Node  node = doc.selectSingleNode("//dcatde:politicalGeocodingURI");
        assertNotNull(node);
        assertEquals("http://dcat-ap.de/def/politicalGeocoding/municipalAssociationKey/010585890", ((Element)node).attributeValue("resource"));
    }
}
