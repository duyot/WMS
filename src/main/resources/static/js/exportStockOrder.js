//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateMainModal = $('#order-export-insert-update-modal');
var $btnSearch = $('#btn-search');
$mainTable = $('#tbl-main-table');
$table = $('#tbl-export-goods');
var $btn_add = $('#btn-create-order');
$inpGoodsCode = $('#inp-goods-code');
var $inpGoodsAmount = $('#inp-amount');
var lblTotalPrice = $("#lbl-total-price");
var updatePopupButton = $("#btn-update_exportStock");
$btn_add_partner = $('#btn-add-partner');
$addUpdateModal = $('#myModal');
var exportFile = $('#btn-export-file');
var isUpdate =false;
var btnExport = $('#btn-export');
var exportMethodVal = $('#exportMethod');
var isDeleteOrder = true;
var $body = $("body");
var $lblInfo = $("#export-action-info");

//@Init component-----------------------------------------------------------------------------------------------
$(function () {
    //
    $mainTable.bootstrapTable({
        $mainTable: dataInit
    });
    $mainTable.bootstrapTable('hideColumn', 'status');
    $mainTable.bootstrapTable('hideColumn', 'id');

    $btnSearch.click(function () {
        doSearch();
    });
    $btn_add.click(function () {
        isUpdate = false;
        onClickToOpenPopup(null);

        validator = $("#cat-partner-insert-update-form").validate({
            ignore: ":hidden",
            rules: {
                code: {
                    required: true,
                    normalizer: function (value) {
                        return $.trim(value);
                    },
                    maxlength: 50
                },
                name: {
                    required: true,
                    normalizer: function (value) {
                        return $.trim(value);
                    },
                    maxlength: 100
                },
                telNumber: {
                    required: true,
                    normalizer: function (value) {
                        return $.trim(value);
                    },
                    maxlength: 100
                },
                address: {
                    normalizer: function (value) {
                        return $.trim(value);
                    },
                    maxlength: 100
                }
            },
            submitHandler: function (form) {
                preprocessInput($("#cat-partner-insert-update-form"));
                //
                if (isContainSpecialCharacter($('#modal-inp-code').val())) {
                    alert("Mã đối tác chứa kí tự đặc biệt. Vui lòng loại bỏ kí tự (lớn hơn, nhỏ hơn)");
                    return;
                }
                if (isContainSpecialCharacter($('#modal-inp-name').val())) {
                    alert("Tên đối tác chứa kí tự đặc biệt. Vui lòng loại bỏ kí tự (lớn hơn, nhỏ hơn)");
                    return;
                }
                //
                $("#modal-type").val('add');
                $.ajax({
                    type: "POST",
                    url: "/WMS/workspace/cat_partner_ctr/add",
                    data: $(form).serialize(),
                    success: function (data) {
                        resultArr = data.split('|');
                        resultCode = resultArr[0];
                        resultName = resultArr[1];
                        if (resultCode == 1) {
                            setInfoMessage(null,resultName);
                        } else {
                            setErrorMessage(null, resultName);
                        }
                        $('#inp-receive-name').val($('#modal-inp-code').val() + "|" + $('#modal-inp-name').val() + "|" + $('#modal-inp-telNumber').val());
                    },
                    error: function () {
                        setErrorMessage(null, 'Lỗi hệ thống');
                    }
                });

                hideModal($("#myModal"));
                return false; // required to block normal submit since you used ajax
            }
        });

    });

    $('#order-export-insert-update-form').keydown(function (e) {
        if (e.keyCode == 13 && !$(e.target).parent().hasClass('editable-input')) {
            var amount = Number($inpGoodsAmount.val());
            if ($(e.target).attr('id') == "inp-goods-code") {
                if (!isValidAmount(amount) || amount ==0) {
                    $inpGoodsAmount.focus();
                }else{
                    moveDataToTable();
                }
            }
            if ($(e.target).attr('id') == "inp-amount") {
                moveDataToTable();
            }
            e.preventDefault();
            return false;
        }
    });
    updatePopupButton.click(function () {
        //validate
        var export_goods = $table.bootstrapTable('getData');
        if (export_goods.length == 0) {
            alert('Bạn chưa nhập thông tin hàng xuất!');
            return;
        }
        var stockIdValue = $('#cmb-stock').val();
        if (stockIdValue == null || stockIdValue == -1) {
            alert('Bạn chưa chọn kho xuất!');
            $('#cmb-stock').focus();
            return;
        }
        var partnerIdValue = $('#cmb-partner').val();
        var partnerRequireValue = $('#partnerRequire').text();
        if (partnerRequireValue != null && partnerRequireValue == 1 && partnerIdValue == -1) {
            alert('Bạn chưa chọn đối tác!');
            $('#cmb-partner').focus();
            return;
        }
        doCheckExists();

    });
    initDateRangeSelect();
    doSearch();


});

function doCheckExists() {
    $body.addClass("loading");
    var stockIdValue = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    var descriptionValue = $('#inp-contract-note').val();
    var receiveValue = $('#inp-receive-name').val();
    var partnerIdValue = $('#cmb-partner').val();
    var exportMethod = $('input[name=cmb-export-method]:checked').val();
    var orderId =    $('#order-export-id').val();
    var orderCode =    $('#order-export-code').val();

    var mjrOrder = {
        id : orderId,
        code : orderCode,
        stockId: stockIdValue,
        description: descriptionValue,
        receiveName: receiveValue,
        partnerId: partnerIdValue,
        exportMethod: exportMethod
    };
    var importData = JSON.stringify({lstMjrOrderDetailDTOS: $table.bootstrapTable('getData'), mjrOrderDTO: mjrOrder});

    if(receiveValue != '' && descriptionValue != ''){
        var existsCode;
        $.ajax({
            url: $("#btn-check-exist").val(),
            data: importData,
            cache: false,
            contentType: "application/json",
            dataType: 'json',
            type: 'POST',
            success: function (data) {
               existsCode = data['code'];
            },
            error: function (data) {
            },
            complete: function () {
                if (existsCode != '' && existsCode != null) {
                    $("#modal-del-lbl-del-info").text('Đã tồn tại yêu cầu tương tự với cùng thông tin (Kho xuất, Khách hàng nhận, Ghi chú): ' + existsCode + '. Bạn có chắc chắn muốn ghi lại?');
                    showModal($('#deleteConfirmModal'));
                    return;
                }else{
                    doInsertData();
                }
            }
        });
    }else{
        doInsertData();
    }
}

function doInsertData() {
    $body.addClass("loading");
    var stockIdValue = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    var descriptionValue = $('#inp-contract-note').val();
    var receiveValue = $('#inp-receive-name').val();
    var partnerIdValue = $('#cmb-partner').val();
    var exportMethod = $('input[name=cmb-export-method]:checked').val();
    var orderId =    $('#order-export-id').val();
    var orderCode =    $('#order-export-code').val();

    var mjrOrder = {
        id : orderId,
        code : orderCode,
        stockId: stockIdValue,
        description: descriptionValue,
        receiveName: receiveValue,
        partnerId: partnerIdValue,
        exportMethod: exportMethod
    };
    var importData = JSON.stringify({lstMjrOrderDetailDTOS: $table.bootstrapTable('getData'), mjrOrderDTO: mjrOrder});

    $.ajax({
        url: updatePopupButton.val(),
        data: importData,
        cache: false,
        contentType: "application/json",
        dataType: 'json',
        type: 'POST',
        success: function (data) {
            var errorCode = data['statusName'];
            if (errorCode == 'FAIL') {
                setErrorMessage(null, "Lỗi hệ thống")
            } else {
                $table.bootstrapTable('removeAll');
            }
            hideModal($addUpdateMainModal);
        },
        error: function (data) {
            setErrorMessageWithTime($lblInfo, JSON.stringify(data), 8000)
        },
        complete: function () {
            hideModal($addUpdateModal);
            doSearch();
            var message = "Tạo mới thành công";
            if (isUpdate){
                message = "Cập nhật thành công";
            }
            setInfoMessage(null,message);
            $body.removeClass("loading");
        }
    });
}

var btnSaveConfirm = $('#modal-del-btn-ok');
btnSaveConfirm.click(function () {
    doInsertData();
    hideModal($('#deleteConfirmModal'));
});

function runningFormatter(value, row, index) {
    return index + 1;
}

function initExportPopup() {
    loadGoodsCodeSuggestion();
    loadPartnerSuggestion();
    initEnterEvent();

}

function initEnterEvent() {


}

function operateFormatterMainForm(value, row, index) {
    var id   = row["id"];
    var url = exportFile.val() + "?orderId="+ id;
    $('.export-file').attr('href',url);

    var status = row['status'];
    if (status == 2) {
        return [].join('');
    } else {
        return [
            '<a class="export-menu row-function" href="javascript:void(0)" title="Thực xuất">',
            '<i class="fa fa-share-square-o"></i>',
            '</a> ',
            '<a class="edit-order row-function" href="javascript:void(0)" title="Sửa">',
            '<i class="fa fa-pencil-square-o"></i>',
            '</a> ',
            '<a class="export-file row-function" href='+url+'  target="_blank" title="In phiếu nhặt hàng">',
            '<i class="fa fa-file-word-o"></i>',
            '</a> ',
            '<a class="delete-order row-function" href="javascript:void(0)" title="Xóa">',
            '<i class="fa fa-trash"></i>',
            '</a> '
        ].join('');
    }
}

//@FUNCTION-----------------------------------------------------------------------------------------------

window.operateEvents = {
    'click .delete-goods': function (e, value, row, index) {
        clearActionInfo();
        $table.bootstrapTable('remove', {
            field: 'columnId',
            values: [row['columnId']]
        });
        //
        var keySerial = row['goodsCode'] + row["goodsState"] + row['serial'];
        enteredSerials = enteredSerials.remove(keySerial);
        //reset total amount
        totalPrice -= Number(row['totalMoney']);
        setConstantInfoMessage(lblTotalPrice, "Tổng tiền xuất: " + formatFloatType(Number(totalPrice)));
    },
    'click .edit-order': function (e, value, row, index) {
        isUpdate = true;
        onClickToOpenPopup(row);
    },
    'click .delete-order': function (e, value, row, index) {
        isDeleteOrder = true;
        var orderId = row['id'];
        var code = row['code'];
        $("#order_id").val(orderId);
        $("#lbl-del-info").text('Bạn có muốn xóa yêu cầu yêu cầu: ' + code + '?');
        showModal($('#myConfirmModal'));
    },
    'click .export-menu': function (e, value, row, index) {
        isDeleteOrder = false;
        var orderId = row['id'];
        var code = row['code'];
        $("#order_id").val(orderId);
        $("#lbl-del-info").text('Thực xuất yêu cầu: ' + code + '?');
        //
        showModal($('#myConfirmModal'));
    }
};

function refreshFormAndInitData( row) {
    var exportMethod = 0 ;
    var stockId = -1;
    var received = "";
    var partnerId = -1 ;
    var node = "";
    var orderId = "";
    var code = "";

    if (isUpdate && row != null){
        exportMethod = row['exportMethod'];
        stockId = row['stockId'];
        partnerId = row['partnerId'];
        received =row['receiveName'];
        node = row['description'];
        orderId = row['id'];
        code = row['code'];
    }
    $('#cmb-stock').val(stockId);
    $('#cmb-stock').selectpicker('refresh');

    $('#inp-contract-note').val(node);
    $('#inp-receive-name').val(received);
    $('#order-export-id').val(orderId);
    $('#order-export-code').val(code);
    $inpGoodsCode.val('');
    $inpGoodsAmount.val('');
    $('input[name=cmb-export-method][value='+exportMethod+']').prop('checked', true)

    $('#cmb-partner').val(partnerId);
    $('#cmb-partner').selectpicker('refresh');

    initData(isUpdate,row);
}

var btnOrderDetail = $('#btn-order-detail');

function initData(isUpdate, row) {
    var dataInit = [];
    totalPrice = 0 ;
    var total = 0 ;
    if (isUpdate){
        $.ajax({
            type: "GET",
            cache: false,
            data: { orderid:row['id']},
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            url: btnOrderDetail.val(),
            dataType: 'json',
            timeout: 600000,
            success: function (data) {
                dataInit = data;
                for (var i = 0; i < dataInit.length; i++) {
                    total += Number(dataInit[i]['totalMoney']);
                    dataInit[i]['totalMoney'] = Number(dataInit[i]['totalMoney']);
                    dataInit[i]['amount'] = Number(dataInit[i]['amount']);
                    dataInit[i]['columnId'] = Number(dataInit[i]['id']);
                }
                totalPrice = total;
                onOpenExportPopup(dataInit, total);
            },
            complete: function () {
                NProgress.done();
            }
        });

    }else {
        onOpenExportPopup(dataInit, total);
    }
}

//@Import confirm---------------------------------------------------------------------
var btnExportConfirm = $('#modal-btn-del-ok');
btnExportConfirm.click(function () {
    //
    hideModal($('#myConfirmModal'));
    $body.addClass("loading");

    var orderId = $('#order_id').val();
    if (isDeleteOrder) {
        onClickDeleteOrder(orderId);
        return;
    }
    var stock_trans_info = {
        orderId: orderId
    };
    //
    var importData = JSON.stringify({lstGoods: $table.bootstrapTable('getData'), mjrStockTransDTO: stock_trans_info});
    //
    //
    $.ajax({
        url: btnExport.val(),
        data: importData,
        cache: false,
        contentType: "application/json",
        dataType: 'json',
        type: 'POST',
        success: function (data) {
            //
            var resultMessage = data['statusCode'];
            var resultMessageDetail = data['statusName'];
            var stockTransId = data['key'];
            var successRecords = data['success'];
            //
            if (resultMessage === "SUCCESS_WITH_ERROR") {
                var totalRecords = data['total'];
                //show modal upload file
                setInfoMessageWithTime($("#modal-error-import-lbl-info"), 'Thực xuất ' + successRecords + '/' + totalRecords + ' hàng hóa thành công. Mã phiếu: ' + stockTransId, 8000);
                $("#modal-link-download").attr("href", $("#modal-inp-stock-trans-id").val() + "/" + stockTransId);
                showModal($("#myDownloadErrorImportModal"));
            } else if (resultMessage === "FAIL") {
                var details     = resultMessageDetail.split('|');
                var errorCode   = details[0];
                var errorDetail = details[1];
                var errorSerial = data['key'];
                if (errorCode === 'ERROR_NOT_FOUND_STOCK_GOODS') {
                    setErrorMessageWithTime($lblInfo, "Xuất kho không thành công, hàng không có trong kho!", 8000);
                } else if (errorCode === 'ERROR_NOT_FOUND_SERIAL') {
                    setErrorMessageWithTime($lblInfo, "Xuất kho không thành công, serial " + errorSerial + " không có trong kho!", 8000);
                }else {
                    setErrorMessageWithTime($lblInfo, "Xuất kho không thành công, hàng không có trong kho!", 8000);
                }
            } else {
                setInfoMessage($lblInfo, "Thực xuất " + successRecords + " hàng hóa thành công. Mã phiếu: " + stockTransId, 8000);
            }
        },
        error: function (data) {
            setErrorMessageWithTime($lblInfo, JSON.stringify(data), 8000)
        },
        complete: function () {
            $body.removeClass("loading");
            doSearch();
        }
    });
});

function doSearch() {
    NProgress.start();
    var stockId = $('#cmb-main-stock').val();
    var userCode = $('#cmb-user').val();
    var status = $("input[name='cmb-status']:checked").val();
    //date
    var drp = $('#create-date-range').data('daterangepicker');
    var startCreateDateVal = drp.startDate.format("DD/MM/YYYY");
    var endCreateDateVal = drp.endDate.format("DD/MM/YYYY");
    //
    $.ajax({
        type: "GET",
        cache: false,
        data: {
            stockId: stockId, createdUser: userCode, status: status,
            startDate: startCreateDateVal, endDate: endCreateDateVal
        },
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: $btnSearch.val(),
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
            $mainTable.bootstrapTable('load', data);
        },
        complete: function () {
            NProgress.done();
        }
    });

}

function onOpenExportPopup(dataInit, total) {
    $table.bootstrapTable('load', dataInit);
    setConstantInfoMessage(lblTotalPrice, "Tổng tiền xuất: " + formatFloatType(total));
    initExportPopup();
    $inpGoodsCode = $('#inp-goods-code');
    $inpGoodsAmount = $('#inp-amount');
}

function onClickToOpenPopup(row) {
    // $addUpdateMainModal.on('shown.bs.modal', function () {
    //     refreshFormAndInitData(row);
    // });
    refreshFormAndInitData(row);
    showModal($addUpdateMainModal);
    if(row == null){
        $('input[name=cmb-export-method][value='+exportMethodVal.val()+']').prop('checked', true);
    }
}
var btnDeleteOrder = $('#btn-get-deleteOrder');
function onClickDeleteOrder(orderId) {

    //
    $.ajax({
        type: "GET",
        cache: false,
        data: {orderid: orderId},
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: btnDeleteOrder.val(),
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
            var resultMessageDetail = data['statusName'];
            var key = data['key'];
            if (resultMessageDetail === 'SUCCESS') {
                setInfoMessage(null, "Xóa thành công");
            }else if (key === 'EXPORTED') {
                setErrorMessage(null, "Order đã  xuất");
            }else {
                setErrorMessage(null, "Lỗi hệ thống");
            }
            doSearch();
        },
        complete: function () {
            $body.removeClass("loading");
        }
    });

}

function onChangeStock(sel) {
    $('#cmb-stock').val(sel.value);
}

function addStyle(value, row, index) {
    var classes = ['active', 'success', 'info', 'warning', 'danger'];
    if (row["serial"] !== "" && row["serial"] !== "-" && row["serial"] != null) {
        return {
            classes: "not-active"
        };
    }
    return {};
}