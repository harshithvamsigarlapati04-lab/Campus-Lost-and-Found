package com.campus.lostandfound;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import com.campus.lostandfound.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void testSaveAndFindItem() {
        Item item = new Item(
                "Scientific Calculator Casio fx-991EX",
                "Black calculator with stickers on the back cover",
                "Electronics & Gadgets",
                "Library 2nd Floor Desk 12",
                LocalDate.now(),
                ItemStatus.LOST,
                "student1@college.edu",
                null
        );

        Item saved = itemRepository.save(item);
        assertNotNull(saved.getId());
        assertEquals("Scientific Calculator Casio fx-991EX", saved.getItemName());

        List<Item> lostItems = itemRepository.findByStatusOrderByDateDescCreatedAtDesc(ItemStatus.LOST);
        assertEquals(1, lostItems.size());
        assertEquals(ItemStatus.LOST, lostItems.get(0).getStatus());
    }

    @Test
    void testSearchAndFilterQuery() {
        Item lostWatch = new Item(
                "Fossil Leather Strap Watch",
                "Brown leather watch with silver dial",
                "Clothing & Accessories",
                "Sports Complex Locker Room",
                LocalDate.now().minusDays(1),
                ItemStatus.LOST,
                "athlete@college.edu",
                null
        );
        Item foundKeys = new Item(
                "Honda Bike Key with Red Tag",
                "Single bike key found near parking lot A",
                "Keys & Keychains",
                "Campus Main Gate Parking",
                LocalDate.now(),
                ItemStatus.FOUND,
                "security@college.edu",
                null
        );

        itemRepository.save(lostWatch);
        itemRepository.save(foundKeys);

        // Search by keyword "watch"
        List<Item> searchResults = itemRepository.searchAndFilter("watch", null, null);
        assertEquals(1, searchResults.size());
        assertEquals("Fossil Leather Strap Watch", searchResults.get(0).getItemName());

        // Filter by status FOUND
        List<Item> foundResults = itemRepository.searchAndFilter(null, ItemStatus.FOUND, null);
        assertEquals(1, foundResults.size());
        assertEquals("Honda Bike Key with Red Tag", foundResults.get(0).getItemName());

        // Count queries
        assertEquals(1, itemRepository.countByStatus(ItemStatus.LOST));
        assertEquals(1, itemRepository.countByStatus(ItemStatus.FOUND));
        assertEquals(0, itemRepository.countByStatus(ItemStatus.RESOLVED));
    }
}
