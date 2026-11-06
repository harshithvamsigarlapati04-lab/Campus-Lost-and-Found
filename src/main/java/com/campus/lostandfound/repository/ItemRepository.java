package com.campus.lostandfound.repository;

import com.campus.lostandfound.model.Item;
import com.campus.lostandfound.model.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Item entity operations.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOrderByDateDescCreatedAtDesc();

    List<Item> findByStatusOrderByDateDescCreatedAtDesc(ItemStatus status);

    List<Item> findByItemNameContainingIgnoreCaseOrderByDateDesc(String itemName);

    List<Item> findByItemNameContainingIgnoreCaseAndStatusOrderByDateDesc(String itemName, ItemStatus status);

    long countByStatus(ItemStatus status);

    /**
     * Flexible multi-criteria search and filtering for items.
     * Supports filtering by keyword (matches item name, description, or location),
     * lifecycle status (LOST, FOUND, RESOLVED), and category.
     */
    @Query("SELECT i FROM Item i WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:category IS NULL OR :category = '' OR i.category = :category) " +
           "ORDER BY i.date DESC, i.createdAt DESC")
    List<Item> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("status") ItemStatus status,
            @Param("category") String category
    );
}
