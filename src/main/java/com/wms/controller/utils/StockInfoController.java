package com.wms.controller.utils;

import com.wms.controller.stock_managerment.ExportStockController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by duyot on 3/21/2017.
 */
@Controller
@RequestMapping("/workspace/utils/stockInfo")
public class StockInfoController {
    Logger log = LoggerFactory.getLogger(ExportStockController.class);
}
