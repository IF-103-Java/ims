package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.mapper.AssociateDtoMapper;
import com.ita.if103java.ims.service.AssociateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociateServiceImpl implements AssociateService {

    private AssociateDao associateDao;
    private AccountDao accountDao;
    private AssociateDtoMapper associateDtoMapper;

    @Autowired
    public AssociateServiceImpl(AssociateDao associateDao, AccountDao accountDao, AssociateDtoMapper associateDtoMapper) {
        this.associateDao = associateDao;
        this.accountDao = accountDao;
        this.associateDtoMapper = associateDtoMapper;
    }

    @Override
    public Optional<AssociateDto> create(AssociateDto associateDto) {

        if (allowToCreateNewAssociate(associateDto.getAccountId(), associateDto.getType())) {
            Associate associate = associateDao.create(associateDtoMapper.convertAssociateDtoToAssociate(associateDto));
            return Optional.of(associateDtoMapper.convertAssociateToAssociateDto(associate));
        }

        return Optional.empty();
    }

    @Override
    public AssociateDto update(AssociateDto associateDto) {
        Associate associate = associateDao.update(associateDtoMapper.convertAssociateDtoToAssociate(associateDto));
        return associateDtoMapper.convertAssociateToAssociateDto(associate);
    }

    @Override
    public AssociateDto view(Long id) {
        return associateDtoMapper.convertAssociateToAssociateDto(associateDao.findById(id));
    }

    @Override
    public boolean delete(Long id) {
        return associateDao.delete(id);
    }

    private boolean allowToCreateNewAssociate(Long accountId, AssociateType associateType) {
        Account account = accountDao.findById(accountId);
        List<Associate> associates = associateDao.findByAccountId(account.getId());

        if (associateType == AssociateType.SUPPLIER) {
            return account.getType().getMaxSuppliers() > associates.stream().filter(a -> a.getType() == AssociateType.SUPPLIER).count();
        } else if (associateType == AssociateType.CLIENT) {
            return account.getType().getMaxClients() > associates.stream().filter(a -> a.getType() == AssociateType.CLIENT).count();
        }

        return false;
    }
}
