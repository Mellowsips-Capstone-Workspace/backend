package com.capstone.workspace.services.cart;

import com.capstone.workspace.entities.cart.Cart;
import com.capstone.workspace.entities.cart.CartItem;
import com.capstone.workspace.repositories.cart.CartItemRepository;
import com.capstone.workspace.repositories.cart.CartRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {
    @NonNull
    private final CartItemRepository repository;

    @NonNull
    private final CartRepository cartRepository;

    public void deleteCartItemByProductId(UUID productId) {
        List<CartItem> cartItems = repository.findAllByProduct_Id(productId);
        if (cartItems != null && !cartItems.isEmpty()) {
            cartItems.forEach(cartItem -> {
                repository.delete(cartItem);
            });
        }
    }

/*    @Transactional
    public void deleteCartItem(UUID id) {
        CartItem cartItem = getCartItemById(id);

        Cart cart = cartItem.getCart();
        if (cart.getCartItems().size() == 1) {
            repository.delete(cart);
            return;
        }

        cartItemRepository.delete(cartItem);
    }*/
}
