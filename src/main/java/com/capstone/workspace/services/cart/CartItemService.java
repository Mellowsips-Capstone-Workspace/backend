package com.capstone.workspace.services.cart;

import com.capstone.workspace.entities.cart.Cart;
import com.capstone.workspace.entities.cart.CartItem;
import com.capstone.workspace.repositories.cart.CartItemRepository;
import com.capstone.workspace.repositories.cart.CartRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {
    @NonNull
    private final CartItemRepository repository;

    @NonNull
    private final CartRepository cartRepository;

    public void deleteByProductId(UUID productId) {
        List<CartItem> cartItems = repository.findAllByProduct_Id(productId);
        if (cartItems != null && !cartItems.isEmpty()) {
            cartItems.forEach(cartItem -> {
                delete(cartItem.getId());
            });
        }
    }

    public CartItem getCartItemById(UUID id) {
        CartItem entity = repository.findById(id).orElse(null);
        return entity;
    }

    public void delete(UUID id) {
        CartItem cartItem = getCartItemById(id);

        if(cartItem != null) {
            Cart cart = cartItem.getCart();
            long activeCartItemCount = cart.getCartItems().stream()
                    .filter(c -> !c.isDeleted())
                    .count();
            if (activeCartItemCount == 1) {
                cartRepository.delete(cart);
            }
        }
        repository.delete(cartItem);
    }

}
