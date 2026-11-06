package com.campus.lostandfound.controller;

import com.campus.lostandfound.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle home and welcome page requests with live database metrics.
 */
@Controller
public class HomeController {

    private final ItemService itemService;

    public HomeController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "Campus Lost & Found");
        model.addAttribute("projectStatus", "Active & Connected to MySQL Database");
        model.addAttribute("totalLost", itemService.countLost());
        model.addAttribute("totalFound", itemService.countFound());
        model.addAttribute("totalResolved", itemService.countResolved());
        model.addAttribute("recentItems", itemService.getRecentItems(6));
        return "index";
    }
}
