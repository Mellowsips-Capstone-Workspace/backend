package com.capstone.workspace.services.cart;

import com.capstone.workspace.dtos.cart.AddProductToCartDto;
import com.capstone.workspace.entities.cart.Cart;
import com.capstone.workspace.entities.cart.CartItem;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductAddon;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.cart.CartItemModel;
import com.capstone.workspace.models.cart.CartModel;
import com.capstone.workspace.models.product.ProductAddonModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.cart.CartItemRepository;
import com.capstone.workspace.repositories.cart.CartRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.product.ProductAddonService;
import com.capstone.workspace.services.product.ProductService;
import com.capstone.workspace.services.store.StoreService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    @NonNull
    private final CartRepository repository;

    @NonNull
    private final ProductService productService;

    @NonNull
    private final StoreService storeService;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final CartItemRepository cartItemRepository;

    @NonNull
    private final ProductAddonService productAddonService;

    @NonNull
    private final ModelMapper mapper;

    @Transactional
    public CartItemModel addProductToCart(AddProductToCartDto dto) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        Product product = productService.getProductById(dto.getProductId());

        List<ProductAddon> productAddons = Collections.emptyList();
        if (dto.getAddons() != null && !dto.getAddons().isEmpty()) {
            productAddons = dto.getAddons().stream().map(id -> productAddonService.checkIfAddonBelongToProduct(id, product.getId())).toList();
        }

        List<ProductOptionSection> productOptionSections = product.getProductOptionSections();
        for (ProductOptionSection section : productOptionSections) {
            if (section.getIsRequired() != null && section.getIsRequired()) {
                boolean hasAddon = productAddons.stream().anyMatch(addon -> section.getId().equals(addon.getProductOptionSection().getId()));
                if (!hasAddon) {
                    throw new BadRequestException("Missing required addon");
                }
                continue;
            }

            if (!productAddons.isEmpty()) {
                int maxAllowedChoices = section.getMaxAllowedChoices();
                long numberOfChoices = productAddons.stream().filter(addon -> section.getId().equals(addon.getProductOptionSection().getId())).count();
                if (numberOfChoices > maxAllowedChoices) {
                    throw new BadRequestException("Exceeded max allowed addon items in section");
                }
            }
        }

        CartItem cartItem = cartItemRepository.findByCreatedByAndProduct_IdAndAddonsAndNote(
                username,
                dto.getProductId(),
                dto.getAddons().toArray(UUID[]::new),
                dto.getNote()
        );

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + dto.getQuantity();
            cartItem.setQuantity(newQuantity);
        } else {
            Cart cart = repository.findByCreatedByAndStoreId(
                    username,
                    product.getStoreId()
            );

            if (cart == null) {
                Cart newEntity = new Cart();
                newEntity.setStoreId(product.getStoreId());
                cart = repository.save(newEntity);
            }

            cartItem = mapper.map(dto, CartItem.class);
            cartItem.setProduct(product);
            cartItem.setCart(cart);
        }

        cartItem = cartItemRepository.save(cartItem);
        CartItemModel model = mapper.map(cartItem, CartItemModel.class);
        model.setAddons(mapper.map(productAddons, new TypeToken<List<ProductAddonModel>>() {}.getType()));

        long addonsPrice = productAddons.stream().reduce(0L, (res, item) -> res + item.getPrice(), Long::sum);
        long tempPrice = model.getQuantity() * (model.getProduct().getPrice() + addonsPrice);
        model.setTempPrice(tempPrice);
        model.setFinalPrice(tempPrice);

        return model;
    }

    public List<CartModel> getAllCartsByUser() {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        List<Cart> entities = repository.findAllByCreatedBy(username);
        return entities.stream().map(item -> {
            CartModel model = mapper.map(item, CartModel.class);

            int numberOfItems = item.getCartItems().stream().reduce(0, (res, el) -> res + el.getQuantity(), Integer::sum);
            model.setNumberOfItems(numberOfItems);

            Store store = storeService.getStoreById(UUID.fromString(item.getStoreId()));
            StoreModel storeModel = mapper.map(store, StoreModel.class);
            model.setStore(storeModel);

            return model;
        }).toList();
    }

    public CartDetailsModel getCartById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        Cart entity = repository.findById(id).orElse(null);

        if (entity == null || !username.equals(entity.getCreatedBy())) {
            throw new NotFoundException("Cart not found");
        }

        List<CartItemModel> cartItems = entity.getCartItems().stream().map(item -> {
            CartItemModel model = mapper.map(item, CartItemModel.class);
            List<ProductAddon> addons = productAddonService.getBulk(item.getAddons());
            model.setAddons(mapper.map(addons, new TypeToken<List<ProductAddonModel>>() {}.getType()));

            long addonsPrice = addons.stream().reduce(0L, (res, el) -> res + el.getPrice(), Long::sum);
            long tempPrice = model.getQuantity() * (model.getProduct().getPrice() + addonsPrice);
            model.setTempPrice(tempPrice);
            model.setFinalPrice(tempPrice);

            return model;
        }).toList();

        Store store = storeService.getStoreById(UUID.fromString(entity.getStoreId()));
        StoreModel storeModel = mapper.map(store, StoreModel.class);

        CartDetailsModel model = mapper.map(entity, CartDetailsModel.class);
        model.setCartItems(cartItems);
        model.setStore(storeModel);

        long tempPrice = cartItems.stream().reduce(0L, (res, item) -> res + item.getFinalPrice(), Long::sum);
        model.setTempPrice(tempPrice);
        model.setFinalPrice(tempPrice);

        return model;
    }
}
