package com.wms.controller.stock_managerment;

import com.google.common.collect.Lists;
import com.wms.base.BaseController;
import com.wms.constants.Constants;
import com.wms.dto.*;
import com.wms.services.interfaces.BaseService;
import com.wms.services.interfaces.CatUserService;
import com.wms.services.interfaces.OrderExportService;
import com.wms.utils.DataUtil;
import com.wms.utils.FunctionUtils;
import com.wms.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/workspace/export_stock_order_ctr")
@Scope("session")
public class ExportOrderStockController extends BaseController {
	@Autowired
	OrderExportService mjrOrderService;
	private Logger log = LoggerFactory.getLogger(ExportOrderStockController.class);
	@Autowired
	public CatUserService catUserService;
	@Autowired
	BaseService catStockCellService;
	private List<CatUserDTO> lstUsers;
	private List<MjrOrderDTO> lstOrder;
	List<ComboSourceDTO> cells;
	@RequestMapping()
	public String home(Model model) {
		model.addAttribute("menuName", "menu.exportStockOrder");
		model.addAttribute("controller", "/workspace/export_stock_order_ctr/");
		return "stock_management/export_stock_order";
	}

	@ModelAttribute("lstUsers")
	public List<CatUserDTO> setUsers(HttpServletRequest request) {
		if (selectedCustomer == null) {
			this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
		}
		//
		if (lstUsers == null) {
			lstUsers = FunctionUtils.getCustomerUsers(catUserService, selectedCustomer);
		}
		return lstUsers;
	}
	@ModelAttribute("cells")
	public List<ComboSourceDTO> getCells(HttpServletRequest request) {
		if (selectedCustomer == null) {
			this.selectedCustomer = (CatCustomerDTO) request.getSession().getAttribute("selectedCustomer");
		}
		//
		if (currentUser == null) {
			this.currentUser = (CatUserDTO) request.getSession().getAttribute("user");
		}
		//
		if (lstStock == null || isStockModified(request)) {
			lstStock = FunctionUtils.getListStock(stockService, currentUser);
			buildMapStock();
			request.getSession().setAttribute("isStockModifiedImportStock", false);
		}
		//
		if (!DataUtil.isListNullOrEmpty(lstStock)) {
			int currentStockId = Integer.parseInt(lstStock.get(0).getId());
			if (cells == null || cells.size() == 0) {
				cells = Lists.newArrayList();
				List<Condition> conditions = Lists.newArrayList();
				conditions.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, currentStockId + ""));
				List<CatStockCellDTO> cellsDTO = catStockCellService.findByCondition(conditions);
				if (!DataUtil.isStringNullOrEmpty(cellsDTO)) {
					for (CatStockCellDTO i : cellsDTO) {
						cells.add(new ComboSourceDTO(Integer.parseInt(i.getId()), i.getCode(), i.getId(), i.getCode()));
					}
				}
			}
		}
		return cells;
	}
	private boolean isStockModified(HttpServletRequest request) {
		if (request.getSession().getAttribute("isStockModifiedExportStock") == null) {
			return true;
		}
		return (boolean) request.getSession().getAttribute("isStockModifiedExportStock");
	}

	@RequestMapping(value = "/findDataByCondition", method = RequestMethod.GET)
	public @ResponseBody
	List<MjrOrderDTO> findOrder(@RequestParam("stockId") String stockId, @RequestParam("createdUser") String createdUser,
									 @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate , @RequestParam("status") String status) {
		List<Condition> lstCon = Lists.newArrayList();

		lstCon.add(new Condition("custId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, selectedCustomer.getId()));
		if (!DataUtil.isStringNullOrEmpty(stockId) && !stockId.equals(Constants.STATS_ALL)) {
			lstCon.add(new Condition("stockId", Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, stockId));
		}
		if (!DataUtil.isStringNullOrEmpty(startDate) && !DataUtil.isStringNullOrEmpty(endDate)) {
			lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.BETWEEN, startDate + "|" + endDate));
		}
		if (!DataUtil.isStringNullOrEmpty(createdUser) && !createdUser.equals(Constants.STATS_ALL)) {
			lstCon.add(new Condition("createdUser", Constants.SQL_OPERATOR.EQUAL, createdUser));
		}
		lstCon.add(new Condition("type",Constants.SQL_PRO_TYPE.LONG, Constants.SQL_OPERATOR.EQUAL, "2"));

		if (!DataUtil.isStringNullOrEmpty(status) && !status.equals(Constants.STATS_ALL)) {
			lstCon.add(new Condition("status", Constants.SQL_PRO_TYPE.BYTE ,Constants.SQL_OPERATOR.EQUAL, status));
		}
		lstCon.add(new Condition("createdDate", Constants.SQL_OPERATOR.ORDER, "desc"));
//		TODO: status here
		lstOrder = mjrOrderService.findByCondition(lstCon);
		if (DataUtil.isListNullOrEmpty(lstOrder)) {
			return Lists.newArrayList();
		}
		lstOrder.forEach(e->{
			e.setStockValue(mapStockIdStock.get(e.getStockId()).getName());
			String value = "";
			if (e.getStatus().equalsIgnoreCase("1")){
				value = "Chưa thực xuất";
			}else if (e.getStatus().equalsIgnoreCase("2")){
				value = "Đã thực xuất";
			}
			e.setTypeValue(value);
		});
		return lstOrder;
	}
	@RequestMapping(value = "/orderExport", method = RequestMethod.POST)
	@ResponseBody
	public ResponseObject orderExport(@RequestBody OrderExportDTO orderExportDTO) {
		initExportOrder(orderExportDTO.getMjrOrderDTO());
		orderExportDTO.getLstMjrOrderDetailDTOS().forEach(e->{
			e.setGoodsId(mapGoodsCodeGoods.get(e.getGoodsCode()).getId());
		});
		return mjrOrderService.orderExport(orderExportDTO);
	}
	public List<ComboSourceDTO> getCells() {
		return cells;
	}

	public void setCells(List<ComboSourceDTO> cells) {
		this.cells = cells;
	}

	private void initExportOrder(MjrOrderDTO mjrOrderDTO){
		mjrOrderDTO.setCustId(selectedCustomer.getId());
		mjrOrderDTO.setType(Constants.IMPORT_TYPE.EXPORT);
		mjrOrderDTO.setStatus("1");
		mjrOrderDTO.setCreatedUser(currentUser.getCode());
		//Nguoi nhan khi xuat
		if (mjrOrderDTO.getReceiveName() != null && !mjrOrderDTO.getReceiveName().trim().equals("")){
			String [] splitPartner = mjrOrderDTO.getReceiveName().split("\\|");
			if (splitPartner.length > 0 ){
				String partnerCode = splitPartner[0];
				CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService,selectedCustomer.getId(), partnerCode, null );
				if (catPartnerDTO != null){
					String receiverName = "";
					if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())){
						receiverName = receiverName + catPartnerDTO.getName();
					}
					if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())){
						receiverName = receiverName+ "|" + catPartnerDTO.getTelNumber();
					}
					mjrOrderDTO.setReceiveId(catPartnerDTO.getId());
					mjrOrderDTO.setReceiveName(receiverName);
				}
			}
		}
		//Xuat hang cua doi tac
		if (mjrOrderDTO.getPartnerId() != null){
			CatPartnerDTO catPartnerDTO = FunctionUtils.getPartner(catPartnerService,selectedCustomer.getId(), null, mjrOrderDTO.getPartnerId() );
			if (catPartnerDTO != null) {
				String receiverName = "";
				if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getName())){
					receiverName = receiverName + catPartnerDTO.getName();
				}
				if (!DataUtil.isStringNullOrEmpty(catPartnerDTO.getTelNumber())){
					receiverName = receiverName+ "|" + catPartnerDTO.getTelNumber();
				}
				mjrOrderDTO.setPartnerId(catPartnerDTO.getId());
				mjrOrderDTO.setPartnerName(receiverName);
			}
		}
		//
	}
}
