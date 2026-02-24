package com.revshop.service;

import com.revshop.model.Favorite;

import java.util.List;

public interface FavoriteService {

    Favorite addFavorite(Long buyerId, Long productId);

    void removeFavorite(Long buyerId, Long productId);

    List<Favorite> getFavoritesByBuyer(Long buyerId);

    boolean isFavorite(Long buyerId, Long productId);

}
