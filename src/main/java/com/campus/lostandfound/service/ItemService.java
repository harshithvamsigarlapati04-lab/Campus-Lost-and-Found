package com.campus.lostandfound.service;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining business logic for Campus Lost & Found items.
 */
public interface ItemService {

    List<Item> getAllItems();

    List<Item> getItemsByStatus(ItemStatus status);

    Optional<Item> getItemById(Long id);

    Item saveItem(Item item, MultipartFile imageFile);

    Item saveItem(Item item);

    List<Item> searchAndFilter(String keyword, ItemStatus status, String category);

    void markAsResolved(Long id);

    void deleteItem(Long id);

    long countLost();

    long countFound();

    long countResolved();

    List<Item> getRecentItems(int limit);
}
