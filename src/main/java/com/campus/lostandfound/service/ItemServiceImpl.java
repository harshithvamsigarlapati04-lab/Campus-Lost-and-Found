package com.campus.lostandfound.service;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import com.campus.lostandfound.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ItemService with MySQL persistence and image file handling.
 */
@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);
    private static final String UPLOAD_DIR = "uploads/items";

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAllByOrderByDateDescCreatedAtDesc();
    }

    @Override
    public List<Item> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatusOrderByDateDescCreatedAtDesc(status);
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Item saveItem(Item item, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadedFileName = storeImageFile(imageFile);
            if (uploadedFileName != null) {
                item.setImagePath("/uploads/items/" + uploadedFileName);
            }
        }
        return itemRepository.save(item);
    }

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> searchAndFilter(String keyword, ItemStatus status, String category) {
        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String trimmedCategory = (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) ? category.trim() : null;

        return itemRepository.searchAndFilter(trimmedKeyword, status, trimmedCategory);
    }

    @Override
    public void markAsResolved(Long id) {
        itemRepository.findById(id).ifPresent(item -> {
            item.setStatus(ItemStatus.RESOLVED);
            itemRepository.save(item);
            log.info("Item #{} marked as RESOLVED", id);
        });
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public long countLost() {
        return itemRepository.countByStatus(ItemStatus.LOST);
    }

    @Override
    public long countFound() {
        return itemRepository.countByStatus(ItemStatus.FOUND);
    }

    @Override
    public long countResolved() {
        return itemRepository.countByStatus(ItemStatus.RESOLVED);
    }

    @Override
    public List<Item> getRecentItems(int limit) {
        return itemRepository.findAllByOrderByDateDescCreatedAtDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Stores an uploaded image to the local uploads directory.
     */
    private String storeImageFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = StringUtils.cleanPath(
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg"
            );
            // Sanitize filename and prepend unique UUID
            String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String uniqueFilename = UUID.randomUUID().toString() + "_" + sanitized;

            Path destination = uploadPath.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Saved item image to: {}", destination.toAbsolutePath());
            return uniqueFilename;
        } catch (IOException e) {
            log.error("Failed to store uploaded image: {}", e.getMessage(), e);
            return null;
        }
    }
}
