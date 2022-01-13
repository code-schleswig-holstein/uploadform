package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.Dataset;
import de.landsh.opendata.uploadform.services.DatasetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DatasetController {
    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);
    public final int ROW_PER_PAGE = 20;
    private final DatasetService datasetService;

    @GetMapping(value = "/dataset")
    public String index() {
        return "redirect:/dataset/list";
    }

    @GetMapping(value = "/dataset/list")
    public String getDatasets(Model model,
                              @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        final List<Dataset> datasets = datasetService.findAll(pageNumber, ROW_PER_PAGE);
        long count = datasetService.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = ((long) pageNumber * ROW_PER_PAGE) < count;
        model.addAttribute("datasets", datasets);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageNumber - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageNumber + 1);
        return "dataset/list";
    }

    @GetMapping(value = "/dataset/{datasetId}")
    public String getDatasetById(Model model, @PathVariable long datasetId) {
        Dataset dataset = null;
        try {
            dataset = datasetService.findById(datasetId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Contact not found");
        }
        model.addAttribute("dataset", dataset);
        return "dataset/show";
    }


    @GetMapping(value = {"/dataset/add"})
    public String showAddDataset(Model model) {
        final Dataset dataset = new Dataset();
        model.addAttribute("add", true);
        model.addAttribute("dataset", dataset);

        return "dataset/edit";
    }


    @PostMapping(value = "/dataset/add")
    public String addDataset(Model model, @ModelAttribute("dataset") Dataset dataset) {
        try {
            final Dataset newDataset = datasetService.save(dataset);
            return "redirect:/dataset/list";
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            //model.addAttribute("dataset", dataset);
            model.addAttribute("add", true);
            return "dataset/edit";
        }
    }


    @GetMapping(value = {"/dataset/{datasetId}/edit"})
    public String showEditDataset(Model model, @PathVariable long datasetId) {
        Dataset dataset = null;
        try {
            dataset = datasetService.findById(datasetId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Dataset not found");
        }
        model.addAttribute("add", false);
        model.addAttribute("dataset", dataset);
        return "dataset/edit";
    }


    @PostMapping(value = {"/dataset/{datasetId}/edit"})
    public String updateDataset(Model model,
                                @PathVariable long datasetId,
                                @ModelAttribute("dataset") Dataset dataset) {
        try {
            dataset.setId(datasetId);
            datasetService.update(dataset);
            return "redirect:/dataset/" + dataset.getId();
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("add", false);
            return "dataset/edit";
        }
    }


    @GetMapping(value = {"/dataset/{datasetId}/delete"})
    public String showDeleteDatasetById(
            Model model, @PathVariable long datasetId) {
        Dataset dataset = null;
        try {
            dataset = datasetService.findById(datasetId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Dataset not found");
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("dataset", dataset);
        return "dataset/show";
    }


    @PostMapping(value = {"/dataset/{datasetId}/delete"})
    public String deleteDatasetById(
            Model model, @PathVariable long datasetId) {
        try {
            datasetService.deleteById(datasetId);
            return "redirect:/dataset/list";
        } catch (ResourceNotFoundException ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "dataset/show";
        }
    }
}
