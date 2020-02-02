package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.BestAssociatesDao;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto.WeightedBestAssociateDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.mapper.dto.BestAssociateDtoMapper;
import com.ita.if103java.ims.service.BestAssociatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BestAssociatesServiceImpl implements BestAssociatesService {
    private final BestAssociatesDao bestAssociatesDao;
    private final BestAssociateDtoMapper mapper;

    @Autowired
    public BestAssociatesServiceImpl(BestAssociatesDao bestAssociatesDao, BestAssociateDtoMapper mapper) {
        this.bestAssociatesDao = bestAssociatesDao;
        this.mapper = mapper;
    }

    @Override
    public BestAssociatesDto findByItem(Long accountId, Long itemId) {
        final List<BestAssociateDto> associates = mapper.toDtoList(bestAssociatesDao.findByItem(accountId, itemId, 3));
        final Map<AssociateType, List<BestAssociateDto>> associatesByType = getGroupedByType(associates);
        return new BestAssociatesDto(
            getWeightedAssociates(associatesByType.get(AssociateType.SUPPLIER)),
            getWeightedAssociates(associatesByType.get(AssociateType.CLIENT))
        );
    }

    private Map<AssociateType, List<BestAssociateDto>> getGroupedByType(List<BestAssociateDto> associates) {
        return associates.stream().collect(Collectors.groupingBy(x -> x.getReference().getType()));
    }

    private List<WeightedBestAssociateDto> getWeightedAssociates(List<BestAssociateDto> associates) {
        final double sum = associates.stream().mapToDouble(BestAssociateDto::getTotalTransactionQuantity).sum();
        return associates.stream()
            .map(x -> new WeightedBestAssociateDto(x, x.getTotalTransactionQuantity() / sum))
            .collect(Collectors.toUnmodifiableList());
    }
}
