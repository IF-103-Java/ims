package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.BestTradeDao;
import com.ita.if103java.ims.dto.WeightAssociateDto;
import com.ita.if103java.ims.entity.TotalTransactionQuantity;
import com.ita.if103java.ims.service.BestTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BestTradeServiceImpl implements BestTradeService {

    private final BestTradeDao bestTradeDao;

    @Autowired
    public BestTradeServiceImpl(BestTradeDao bestTradeDao) {
        this.bestTradeDao = bestTradeDao;
    }

    @Override
    public List<WeightAssociateDto> findBestSuppliersByItemId(Long itemId) {
        final List<TotalTransactionQuantity> suppliers = bestTradeDao.findBestSuppliersByItemId(itemId, 3);
        return toDtoList(suppliers);
    }

    @Override
    public List<WeightAssociateDto> findBestClientsByItemId(Long itemId) {
        final List<TotalTransactionQuantity> clients = bestTradeDao.findBestClientsByItemId(itemId, 3);
        return toDtoList(clients);
    }

    private List<WeightAssociateDto> toDtoList(List<TotalTransactionQuantity> associates) {
        final double sum = associates.stream().mapToDouble(TotalTransactionQuantity::getTotalQuantity).sum();
        return associates
            .stream()
            .map(x -> new WeightAssociateDto(x.getReferenceId(), x.getTotalQuantity() / sum))
            .collect(Collectors.toList());
    }
}
