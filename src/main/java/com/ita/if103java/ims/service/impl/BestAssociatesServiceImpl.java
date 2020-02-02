package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.BestAssociatesDao;
import com.ita.if103java.ims.dto.BestAssociateDto;
import com.ita.if103java.ims.dto.BestAssociatesDto;
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
            associatesByType.get(AssociateType.SUPPLIER),
            associatesByType.get(AssociateType.CLIENT)
        );
    }

    private Map<AssociateType, List<BestAssociateDto>> getGroupedByType(List<BestAssociateDto> associates) {
        return associates.stream().collect(Collectors.groupingBy(x -> x.getAssociate().getType()));
    }
}
