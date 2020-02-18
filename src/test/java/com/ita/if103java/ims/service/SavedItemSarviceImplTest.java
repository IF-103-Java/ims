package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.service.impl.SavedItemServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class SavedItemSarviceImplTest {
    @InjectMocks
    SavedItemServiceImpl savedItemService;
    @Mock
    private ItemDao itemDao;
    @Mock
    private SavedItemDao savedItemDao;
    @Mock
    private WarehouseDao warehouseDao;
    @Mock
    private AssociateDao associateDao;

}
