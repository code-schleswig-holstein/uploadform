package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.DatasetMatrix;
import de.landsh.opendata.uploadform.services.DatasetMatrixService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DatasetMatrixController {
    private static final Logger logger = LoggerFactory.getLogger(DatasetMatrixController.class);
    public final int ROW_PER_PAGE = 20;
    private final DatasetMatrixService datasetmatrixService;

    @GetMapping(value = "/datasetmatrix")
    public String index() {
        return "redirect:/datasetmatrix/list";
    }

    @GetMapping(value = "/datasetmatrix/list")
    public String getDatasetMatrixs(Model model,
                              @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        final List<DatasetMatrix> datasetmatrixs = datasetmatrixService.findAll(pageNumber, ROW_PER_PAGE);
        long count = datasetmatrixService.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = ((long) pageNumber * ROW_PER_PAGE) < count;
        model.addAttribute("datasetmatrixs", datasetmatrixs);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageNumber - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageNumber + 1);
        return "datasetmatrix/list";
    }

    @GetMapping(value = "/datasetmatrix/{datasetmatrixId}")
    public String getDatasetMatrixById(Model model, @PathVariable String datasetmatrixId) {
        DatasetMatrix datasetmatrix = null;
        try {
            datasetmatrix = datasetmatrixService.findById(datasetmatrixId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Contact not found");
        }
        model.addAttribute("datasetmatrix", datasetmatrix);
        return "datasetmatrix/show";
    }


    @GetMapping(value = {"/datasetmatrix/add"})
    public String showAddDatasetMatrix(Model model) {
        final DatasetMatrix datasetmatrix = new DatasetMatrix();
        model.addAttribute("add", true);
        model.addAttribute("datasetmatrix", datasetmatrix);

        return "datasetmatrix/edit";
    }


    @PostMapping(value = "/datasetmatrix/add")
    public String addDatasetMatrix(Model model, @ModelAttribute("datasetmatrix") DatasetMatrix datasetmatrix) {
        try {
            final DatasetMatrix newDatasetMatrix = datasetmatrixService.save(datasetmatrix);
            return "redirect:/datasetmatrix/list";
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            //model.addAttribute("datasetmatrix", datasetmatrix);
            model.addAttribute("add", true);
            return "datasetmatrix/edit";
        }
    }


    @GetMapping(value = {"/datasetmatrix/{datasetmatrixId}/edit"})
    public String showEditDatasetMatrix(Model model, @PathVariable String datasetmatrixId) {
        DatasetMatrix datasetmatrix = null;
        try {
            datasetmatrix = datasetmatrixService.findById(datasetmatrixId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "DatasetMatrix not found");
        }
        model.addAttribute("add", false);
        model.addAttribute("datasetmatrix", datasetmatrix);
        return "datasetmatrix/edit";
    }


    @PostMapping(value = {"/datasetmatrix/{datasetmatrixId}/edit"})
    public String updateDatasetMatrix(Model model,
                                @PathVariable String datasetmatrixId,
                                @ModelAttribute("datasetmatrix") DatasetMatrix datasetmatrix) {
        try {
            datasetmatrix.setId(datasetmatrixId);
            datasetmatrixService.update(datasetmatrix);
            return "redirect:/datasetmatrix/" + datasetmatrix.getId();
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("add", false);
            return "datasetmatrix/edit";
        }
    }


    @GetMapping(value = {"/datasetmatrix/{datasetmatrixId}/delete"})
    public String showDeleteDatasetMatrixById(
            Model model, @PathVariable String datasetmatrixId) {
        DatasetMatrix datasetmatrix = null;
        try {
            datasetmatrix = datasetmatrixService.findById(datasetmatrixId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "DatasetMatrix not found");
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("datasetmatrix", datasetmatrix);
        return "datasetmatrix/show";
    }


    @PostMapping(value = {"/datasetmatrix/{datasetmatrixId}/delete"})
    public String deleteDatasetMatrixById(
            Model model, @PathVariable String datasetmatrixId) {
        try {
            datasetmatrixService.deleteById(datasetmatrixId);
            return "redirect:/datasetmatrix/list";
        } catch (ResourceNotFoundException ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "datasetmatrix/show";
        }
    }
}
