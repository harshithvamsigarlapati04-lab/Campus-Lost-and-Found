package com.campus.lostandfound.controller;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import com.campus.lostandfound.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller handling web requests for reporting, viewing, and searching items.
 */
@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @ModelAttribute("categories")
    public List<String> populateCategories() {
        return Arrays.asList(
                "Electronics & Gadgets",
                "College ID & Documents",
                "Books & Stationery",
                "Bags & Backpacks",
                "Wallets & Purses",
                "Keys & Keychains",
                "Clothing & Accessories",
                "Water Bottles",
                "Other Belongings"
        );
    }

    /**
     * Browse all items with search and filter capabilities.
     */
    @GetMapping
    public String listItems(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) ItemStatus status,
            @RequestParam(value = "category", required = false) String category,
            Model model
    ) {
        List<Item> items = itemService.searchAndFilter(keyword, status, category);

        model.addAttribute("items", items);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("totalLost", itemService.countLost());
        model.addAttribute("totalFound", itemService.countFound());
        model.addAttribute("totalResolved", itemService.countResolved());

        return "items/list";
    }

    /**
     * Quick filter shortcut for LOST items.
     */
    @GetMapping("/lost")
    public String listLostItems(Model model) {
        return listItems(null, ItemStatus.LOST, null, model);
    }

    /**
     * Quick filter shortcut for FOUND items.
     */
    @GetMapping("/found")
    public String listFoundItems(Model model) {
        return listItems(null, ItemStatus.FOUND, null, model);
    }

    /**
     * Display Report Lost Item form.
     */
    @GetMapping("/report-lost")
    public String showReportLostForm(Model model) {
        Item item = new Item();
        item.setStatus(ItemStatus.LOST);
        item.setDate(LocalDate.now());

        model.addAttribute("item", item);
        model.addAttribute("formType", "LOST");
        model.addAttribute("formTitle", "Report a Lost Item");
        model.addAttribute("formSubtitle", "Provide detailed information to help campus members locate your lost item.");

        return "items/form";
    }

    /**
     * Display Report Found Item form.
     */
    @GetMapping("/report-found")
    public String showReportFoundForm(Model model) {
        Item item = new Item();
        item.setStatus(ItemStatus.FOUND);
        item.setDate(LocalDate.now());

        model.addAttribute("item", item);
        model.addAttribute("formType", "FOUND");
        model.addAttribute("formTitle", "Report a Found Item");
        model.addAttribute("formSubtitle", "Let the owner know you found their item on campus so they can safely reclaim it.");

        return "items/form";
    }

    /**
     * Handle form submission for both Lost and Found reports.
     */
    @PostMapping("/report")
    public String saveReport(
            @Valid @ModelAttribute("item") Item item,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formType", item.getStatus() != null ? item.getStatus().name() : "LOST");
            model.addAttribute("formTitle", item.getStatus() == ItemStatus.FOUND ? "Report a Found Item" : "Report a Lost Item");
            model.addAttribute("formSubtitle", "Please correct the highlighted errors below.");
            return "items/form";
        }

        Item saved = itemService.saveItem(item, imageFile);

        String successMsg = String.format("'%s' has been successfully reported as %s!",
                saved.getItemName(), saved.getStatus().name());
        redirectAttributes.addFlashAttribute("successMessage", successMsg);

        return "redirect:/items/" + saved.getId();
    }

    /**
     * View detailed information for a single item.
     */
    @GetMapping("/{id}")
    public String viewItemDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Item> itemOptional = itemService.getItemById(id);
        if (itemOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The requested item was not found.");
            return "redirect:/items";
        }

        model.addAttribute("item", itemOptional.get());
        return "items/detail";
    }

    /**
     * Mark an item as RESOLVED (e.g. Returned / Reclaimed).
     */
    @PostMapping("/{id}/resolve")
    public String markResolved(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        itemService.markAsResolved(id);
        redirectAttributes.addFlashAttribute("successMessage", "Item marked as resolved successfully!");
        return "redirect:/items/" + id;
    }
}
