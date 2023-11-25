package com.capstone.workspace.services.cart;

import com.capstone.workspace.dtos.cart.AddProductToCartDto;
import com.capstone.workspace.dtos.cart.CalculateCartDto;
import com.capstone.workspace.dtos.cart.UpdateCartItemDto;
import com.capstone.workspace.entities.cart.Cart;
import com.capstone.workspace.entities.cart.CartItem;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductAddon;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.cart.CartItemModel;
import com.capstone.workspace.models.cart.CartModel;
import com.capstone.workspace.models.product.ProductAddonModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.models.voucher.VoucherCartModel;
import com.capstone.workspace.repositories.cart.CartItemRepository;
import com.capstone.workspace.repositories.cart.CartRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.product.ProductAddonService;
import com.capstone.workspace.services.product.ProductService;
import com.capstone.workspace.services.store.StoreService;
import com.capstone.workspace.services.voucher.VoucherService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

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

    @NonNull
    private final VoucherService voucherService;

    @Transactional
    public CartItemModel addProductToCart(AddProductToCartDto dto) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        List<Cart> carts = repository.findAllByCreatedBy(username);
        if (carts.size() >= 10) {
            throw new BadRequestException("Exceeded max allowed carts");
        }

        Product product = productService.getProductById(dto.getProductId());
        if (product.getStoreId() == null) {
            throw new BadRequestException("This product cannot be added to the cart");
        }

        List<ProductAddon> productAddons = getAddonList(product, dto.getAddons());

        CartItem cartItem = cartItemRepository.findByCreatedByAndProduct_IdAndAddonsAndNote(
                username,
                dto.getProductId(),
                dto.getAddons().toArray(UUID[]::new),
                dto.getNote()
        );

        boolean isExistItem = cartItem != null;
        if (isExistItem) {
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
        Cart cart = cartItem.getCart();

        int numberOfItems;
        if (cart.getCartItems() == null) {
            numberOfItems = cartItem.getQuantity();
        } else {
            int currentNoItems = cart.getCartItems().stream().reduce(0, (res, el) -> res + el.getQuantity(), Integer::sum);
            numberOfItems = isExistItem ? currentNoItems : currentNoItems + cartItem.getQuantity();
        }

        if (numberOfItems > 10) {
            throw new BadRequestException("Exceeded max allowed items in cart");
        }

        return getCartItemModel(cartItem, productAddons);
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

    public CartDetailsModel getCartDetails(UUID id) {
        Cart entity = getCartById(id);

        List<CartItemModel> cartItems = entity.getCartItems().stream().map(item -> {
            List<ProductAddon> addons = productAddonService.getBulk(item.getAddons());
            return getCartItemModel(item, addons);
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

    public CartItemModel updateCartItem(UUID id, UpdateCartItemDto dto) {
        CartItem cartItem = getCartItemById(id);

        Product product = cartItem.getProduct();
        List<ProductAddon> productAddons = getAddonList(product, dto.getAddons());

        BeanUtils.copyProperties(dto, cartItem, AppHelper.commonProperties);
        cartItem = cartItemRepository.save(cartItem);

        return getCartItemModel(cartItem, productAddons);
    }

    public CartItem getCartItemById(UUID id) {
        CartItem entity = cartItemRepository.findById(id).orElse(null);

        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        if (entity == null || !entity.getCreatedBy().equals(username)) {
            throw new NotFoundException("Cart item not found");
        }

        return entity;
    }

    private List<ProductAddon> getAddonList(Product product, Set<UUID> addonIds) {
        List<ProductAddon> productAddons = Collections.emptyList();
        if (addonIds != null && !addonIds.isEmpty()) {
            productAddons = addonIds.stream().map(addonId -> productAddonService.checkIfAddonBelongToProduct(addonId, product.getId())).toList();
        }

        List<ProductOptionSection> productOptionSections = product.getProductOptionSections();
        for (ProductOptionSection section : productOptionSections) {
            if (section.getIsRequired() != null && section.getIsRequired()) {
                int addonLength = (int) productAddons.stream().filter(addon -> section.getId().equals(addon.getProductOptionSection().getId())).count();

                if (addonLength != 1) {
                    throw new BadRequestException("Missing required addon or exceeded max allowed addon items in section");
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

        return productAddons;
    }

    private CartItemModel getCartItemModel(CartItem entity, List<ProductAddon> productAddons) {
        CartItemModel model = mapper.map(entity, CartItemModel.class);
        model.setAddons(mapper.map(productAddons, new TypeToken<List<ProductAddonModel>>() {}.getType()));

        long addonsPrice = productAddons.stream().reduce(0L, (res, item) -> res + item.getPrice(), Long::sum);
        long tempPrice = model.getQuantity() * (model.getProduct().getPrice() + addonsPrice);
        model.setTempPrice(tempPrice);
        model.setFinalPrice(tempPrice);

        return model;
    }

    @Transactional
    public void deleteCartItem(UUID id) {
        CartItem cartItem = getCartItemById(id);

        Cart cart = cartItem.getCart();
        if (cart.getCartItems().size() == 1) {
            repository.delete(cart);
            return;
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void deleteCart(UUID id, boolean isBought) {
        Cart entity = getCartById(id);

        List<CartItem> cartItems = entity.getCartItems();
        for (CartItem item: cartItems) {
            if (isBought) {
                item.setIsBought(true);
            }
            item.setDeleted(true);
        }
        cartItemRepository.saveAll(cartItems);

        repository.delete(entity);
    }

    private Cart getCartById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        Cart entity = repository.findById(id).orElse(null);

        if (entity == null || !username.equals(entity.getCreatedBy())) {
            throw new NotFoundException("Cart not found");
        }

        return entity;
    }

    public Map getVouchers(UUID cartId) {
        CartDetailsModel cartDetailsModel = getCartDetails(cartId);
        return voucherService.customerGetCartVoucher(cartDetailsModel, true);
    }

    public CartDetailsModel calculatePrice(UUID cartId, CalculateCartDto dto) {
        CartDetailsModel cartDetailsModel = getCartDetails(cartId);
        Map availableVouchers = voucherService.customerGetCartVoucher(cartDetailsModel, null);

        List<VoucherCartModel> availableSystemVouchers = (List<VoucherCartModel>) availableVouchers.get("SYSTEM");
        List<VoucherCartModel> availableBusinessVouchers = (List<VoucherCartModel>) availableVouchers.get("BUSINESS");

        Set<String> dtoVouchers = dto.getVouchers();
        List<VoucherCartModel> systemVouchers = availableSystemVouchers == null
                ? Collections.emptyList()
                : availableSystemVouchers.stream()
                .filter(item -> (dtoVouchers.contains(String.valueOf(item.getId())) || dtoVouchers.contains(item.getCode()) && item.getPartnerId() == null) && item.getCanUse())
                .toList();
        List<VoucherCartModel> businessVouchers = availableBusinessVouchers == null
                ? Collections.emptyList()
                : availableBusinessVouchers.stream()
                .filter(item -> (dtoVouchers.contains(String.valueOf(item.getId())) || dtoVouchers.contains(item.getCode()) && item.getPartnerId() != null) && item.getCanUse())
                .toList();

        if (systemVouchers.size() > 1 || businessVouchers.size() > 1) {
            throw new BadRequestException("Exceed max allowed system or business vouchers");
        }

        List<VoucherCartModel> vouchers = Stream.concat(systemVouchers.stream(), businessVouchers.stream()).toList();
        cartDetailsModel.setVouchers(vouchers);

        Long totalDiscountAmount = vouchers.isEmpty() ? 0L : vouchers.stream().reduce(0L, (res, item) -> res + item.getDiscountAmount(), Long::sum);
        cartDetailsModel.setFinalPrice(cartDetailsModel.getTempPrice() - totalDiscountAmount);

        System.out.println(vouchers);
        System.out.println(dto.getVouchers());
        if (vouchers.size() != dto.getVouchers().size()) {
            cartDetailsModel.setIsChange(true);
        }

        return cartDetailsModel;
    }
}
