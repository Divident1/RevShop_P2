package com.revshop.controller;

import com.revshop.model.Favorite;
import com.revshop.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{buyerId}/{productId}")
    public ResponseEntity<Favorite> addFavorite(
            @PathVariable Long buyerId,
            @PathVariable Long productId) {
        Favorite favorite = favoriteService.addFavorite(buyerId, productId);
        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    @DeleteMapping("/{buyerId}/{productId}")
    public ResponseEntity<String> removeFavorite(
            @PathVariable Long buyerId,
            @PathVariable Long productId) {
        favoriteService.removeFavorite(buyerId, productId);
        return ResponseEntity.ok("Removed from favorites");
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<Favorite>> getFavoritesByBuyer(@PathVariable Long buyerId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByBuyer(buyerId));
    }

    @GetMapping("/{buyerId}/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> isFavorite(
            @PathVariable Long buyerId,
            @PathVariable Long productId) {
        boolean isFav = favoriteService.isFavorite(buyerId, productId);
        return ResponseEntity.ok(Map.of("isFavorite", isFav));
    }
}
