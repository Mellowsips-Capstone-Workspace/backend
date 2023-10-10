package com.capstone.workspace.services.order;

import com.capstone.workspace.dtos.order.CreateOrderDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.store.QrCode;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.order.OrderRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.cart.CartService;
import com.capstone.workspace.services.store.QrCodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    @NonNull
    private final OrderRepository repository;

    @NonNull
    private final CartService cartService;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final QrCodeService qrCodeService;

    @NonNull
    private final ModelMapper mapper;

    @Transactional
    public Order create(CreateOrderDto dto) {
        UUID cartId = dto.getCartId();
        CartDetailsModel cartDetails = cartService.getCartDetails(cartId);

        UUID qrId = dto.getQrId();
        String code = dto.getQrCode();
        QrCode qrCode = null;

        if (qrId == null && code == null) {
            throw new BadRequestException("Missing QR information");
        } else if (qrId != null) {
            qrCode = qrCodeService.getOneById(qrId);
        } else {
            qrCode = qrCodeService.getOneByCode(code);
        }

        QrCodeModel qrCodeModel = mapper.map(qrCode, QrCodeModel.class);

        Order entity = new Order();
        entity.setStatus(OrderStatus.ORDERED);
        entity.setDetails(cartDetails);
        entity.setQrCode(qrCodeModel);

        StoreModel store = cartDetails.getStore();
        if (!qrCode.getStoreId().equals(String.valueOf(store.getId()))) {
            throw new ConflictException("QR Code does not belong to this store");
        }

        entity.setStoreId(String.valueOf(store.getId()));
        entity.setPartnerId(store.getPartnerId());
        entity.setFinalPrice(cartDetails.getFinalPrice());

        cartService.deleteCart(cartId);

        return repository.save(entity);
    }

    public Order getOneById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();
        UserType userType = userIdentity.getUserType();

        Order entity = repository.findById(id).orElse(null);

        if (
            entity == null
            || (userType == UserType.CUSTOMER && !username.equals(entity.getCreatedBy()))
            || (userType == UserType.EMPLOYEE && (!entity.getPartnerId().equals(userIdentity.getPartnerId()) || (userIdentity.getStoreId() != null && !userIdentity.getStoreId().equals(entity.getStoreId()))))
        ) {
            throw new NotFoundException("Order not found");
        }

        return entity;
    }
}
