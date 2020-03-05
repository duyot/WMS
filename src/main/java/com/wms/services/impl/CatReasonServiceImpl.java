package com.wms.services.impl;

import com.wms.dataprovider.CatReasonDP;
import com.wms.dto.CatReasonDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by duyot on 2/17/2017.
 */
@Service("catReasonService")
public class CatReasonServiceImpl extends BaseServiceImpl<CatReasonDTO, CatReasonDP> {
    @Autowired
    CatReasonDP catReasonDP;

    @PostConstruct
    public void setupService() {
        this.tdp = catReasonDP;
    }

}
