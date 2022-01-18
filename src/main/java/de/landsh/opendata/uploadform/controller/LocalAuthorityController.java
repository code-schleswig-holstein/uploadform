package de.landsh.opendata.uploadform.controller;

import de.landsh.opendata.uploadform.ResourceNotFoundException;
import de.landsh.opendata.uploadform.model.LocalAuthority;
import de.landsh.opendata.uploadform.services.LocalAuthorityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class LocalAuthorityController {
    private static final Logger logger = LoggerFactory.getLogger(LocalAuthorityController.class);
    public final int ROW_PER_PAGE = 20;
    private final LocalAuthorityService localauthorityService;

    @GetMapping(value = "/localauthority")
    public String index() {
        return "redirect:/localauthority/list";
    }

    @GetMapping(value = "/localauthority/list")
    public String getLocalAuthoritys(Model model,
                              @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        final List<LocalAuthority> localauthoritys = localauthorityService.findAll(pageNumber, ROW_PER_PAGE);
        long count = localauthorityService.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = ((long) pageNumber * ROW_PER_PAGE) < count;
        model.addAttribute("localauthoritys", localauthoritys);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageNumber - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageNumber + 1);
        return "localauthority/list";
    }

    @GetMapping(value = "/localauthority/{localauthorityId}")
    public String getLocalAuthorityById(Model model, @PathVariable String localauthorityId) {
        LocalAuthority localauthority = null;
        try {
            localauthority = localauthorityService.findById(localauthorityId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Contact not found");
        }
        model.addAttribute("localauthority", localauthority);
        return "localauthority/show";
    }


    @GetMapping(value = {"/localauthority/add"})
    public String showAddLocalAuthority(Model model) {
        final LocalAuthority localauthority = new LocalAuthority();
        model.addAttribute("add", true);
        model.addAttribute("localauthority", localauthority);

        return "localauthority/edit";
    }


    @PostMapping(value = "/localauthority/add")
    public String addLocalAuthority(Model model, @ModelAttribute("localauthority") LocalAuthority localauthority) {
        try {
            final LocalAuthority newLocalAuthority = localauthorityService.save(localauthority);
            return "redirect:/localauthority/list";
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            //model.addAttribute("localauthority", localauthority);
            model.addAttribute("add", true);
            return "localauthority/edit";
        }
    }


    @GetMapping(value = {"/localauthority/{localauthorityId}/edit"})
    public String showEditLocalAuthority(Model model, @PathVariable String localauthorityId) {
        LocalAuthority localauthority = null;
        try {
            localauthority = localauthorityService.findById(localauthorityId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "LocalAuthority not found");
        }
        model.addAttribute("add", false);
        model.addAttribute("localauthority", localauthority);
        return "localauthority/edit";
    }


    @PostMapping(value = {"/localauthority/{localauthorityId}/edit"})
    public String updateLocalAuthority(Model model,
                                @PathVariable String localauthorityId,
                                @ModelAttribute("localauthority") LocalAuthority localauthority) {
        try {
            localauthority.setId(localauthorityId);
            localauthorityService.update(localauthority);
            return "redirect:/localauthority/" + localauthority.getId();
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("add", false);
            return "localauthority/edit";
        }
    }


    @GetMapping(value = {"/localauthority/{localauthorityId}/delete"})
    public String showDeleteLocalAuthorityById(
            Model model, @PathVariable String localauthorityId) {
        LocalAuthority localauthority = null;
        try {
            localauthority = localauthorityService.findById(localauthorityId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "LocalAuthority not found");
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("localauthority", localauthority);
        return "localauthority/show";
    }


    @PostMapping(value = {"/localauthority/{localauthorityId}/delete"})
    public String deleteLocalAuthorityById(
            Model model, @PathVariable String localauthorityId) {
        try {
            localauthorityService.deleteById(localauthorityId);
            return "redirect:/localauthority/list";
        } catch (ResourceNotFoundException ex) {
            final String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "localauthority/show";
        }
    }
}
