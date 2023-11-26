package com.capstone.workspace.services.voucher;

import com.capstone.workspace.dtos.voucher.CreateVoucherOrderDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.entities.voucher.VoucherOrder;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.repositories.voucher.VoucherOrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherOrderService {
    @NonNull
    private final VoucherOrderRepository repository;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final VoucherService voucherService;

    @Transactional
    public VoucherOrder create(Order order, CreateVoucherOrderDto dto) {
        VoucherOrder entity = mapper.map(dto, VoucherOrder.class);
        entity.setOrder(order);

        Voucher voucher = voucherService.useVoucher(dto.getVoucherId());
        entity.setVoucher(voucher);

        return repository.save(entity);
    }

    @Transactional
    public void revoke(Order order) {
        List<VoucherOrder> voucherOrders = order.getVoucherOrders();

        for (VoucherOrder item: voucherOrders) {
            UUID voucherId = item.getVoucher().getId();
            voucherService.revokeVoucher(voucherId);
        }

        if (order.getStatus() == OrderStatus.REJECTED) {
            repository.deleteAll(voucherOrders);
        }
    }
}
