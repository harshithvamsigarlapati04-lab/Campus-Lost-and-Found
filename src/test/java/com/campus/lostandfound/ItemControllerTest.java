package com.campus.lostandfound;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import com.campus.lostandfound.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Test
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("appName", "totalLost", "totalFound", "totalResolved"));
    }

    @Test
    void testItemsListLoads() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/list"))
                .andExpect(model().attributeExists("items", "categories"));
    }

    @Test
    void testReportLostFormLoads() throws Exception {
        mockMvc.perform(get("/items/report-lost"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attribute("formType", "LOST"))
                .andExpect(model().attributeExists("item", "categories"));
    }

    @Test
    void testReportFoundFormLoads() throws Exception {
        mockMvc.perform(get("/items/report-found"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attribute("formType", "FOUND"))
                .andExpect(model().attributeExists("item", "categories"));
    }

    @Test
    void testFormValidationRejectionOnEmptyFields() throws Exception {
        mockMvc.perform(post("/items/report")
                        .param("itemName", "") // empty item name!
                        .param("description", "")
                        .param("category", "")
                        .param("location", "")
                        .param("contactEmail", "invalid-email")
                        .param("status", "LOST"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("item", "itemName", "description", "category", "location", "contactEmail"));
    }

    @Test
    void testSuccessfulItemSubmissionRedirectsToDetail() throws Exception {
        mockMvc.perform(post("/items/report")
                        .param("itemName", "Blue Campus Backpack")
                        .param("description", "Navy blue Wildcraft backpack containing notebooks")
                        .param("category", "Bags & Backpacks")
                        .param("location", "Auditorium Row F")
                        .param("date", LocalDate.now().toString())
                        .param("status", "FOUND")
                        .param("contactEmail", "finder@college.edu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/items/*"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testViewItemDetail() throws Exception {
        Item item = new Item(
                "Student ID Card - Harsh Kumar",
                "Computer Science Department ID card",
                "College ID & Documents",
                "Canteen Table 4",
                LocalDate.now(),
                ItemStatus.LOST,
                "harsh@college.edu",
                null
        );
        Item saved = itemService.saveItem(item);

        mockMvc.perform(get("/items/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("items/detail"))
                .andExpect(model().attribute("item", hasProperty("itemName", is("Student ID Card - Harsh Kumar"))));
    }
}
