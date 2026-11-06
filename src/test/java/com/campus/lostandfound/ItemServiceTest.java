package com.campus.lostandfound;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import com.campus.lostandfound.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Test
    void testSaveItemWithMockPhoto() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "sample_water_bottle.png",
                "image/png",
                "dummy image content bytes".getBytes()
        );

        Item item = new Item(
                "Stainless Steel Water Bottle Milton",
                "Silver bottle with blue cap, left at cafeteria table",
                "Water Bottles",
                "Campus Cafeteria",
                LocalDate.now(),
                ItemStatus.LOST,
                "student@college.edu",
                null
        );

        Item saved = itemService.saveItem(item, mockFile);
        assertNotNull(saved.getId());
        assertNotNull(saved.getImagePath());
        assertTrue(saved.getImagePath().startsWith("/uploads/items/"));
        assertTrue(saved.getImagePath().endsWith(".png"));

        // Retrieve and test mark as resolved
        Optional<Item> fetched = itemService.getItemById(saved.getId());
        assertTrue(fetched.isPresent());

        itemService.markAsResolved(saved.getId());
        Item resolved = itemService.getItemById(saved.getId()).orElseThrow();
        assertEquals(ItemStatus.RESOLVED, resolved.getStatus());
    }

    @Test
    void testSearchAndFilterService() {
        Item item = new Item(
                "Calculus Early Transcendentals Stewart",
                "Hardcover 8th edition textbook",
                "Books & Stationery",
                "Math Dept Seminar Room",
                LocalDate.now(),
                ItemStatus.FOUND,
                "prof@college.edu",
                null
        );
        itemService.saveItem(item);

        List<Item> results = itemService.searchAndFilter("Calculus", ItemStatus.FOUND, "Books & Stationery");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(i -> i.getItemName().contains("Calculus")));
    }
}
