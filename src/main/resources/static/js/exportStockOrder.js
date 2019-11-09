//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateMainModal = $('#order-export-insert-update-modal');
var $btnSearch = $('#btn-search');
$mainTable = $('#tbl-main-table');
$table = $('#tbl-export-goods');
var $btn_add = $('#btn-create-order')
$inpGoodsCode = $('#inp-goods-code');
var $inpGoodsAmount = $('#inp-amount');
var lblTotalPrice = $("#lbl-total-price");
var updatePopupButton = $("#btn-update_exportStock");
$btn_add_partner = $('#btn-add-partner');
$addUpdateModal = $('#myModal');
var exportFile = $('#btn-export-file')
//@Init component-----------------------------------------------------------------------------------------------
$(function () {
    //
    $mainTable.bootstrapTable({
        $mainTable: dataInit
    });

    $btnSearch.click(function () {
        doSearch();
    });
    $btn_add.click(function () {
        showModal($addUpdateMainModal);
        $addUpdateMainModal.on('shown.bs.modal', function () {
            $table.bootstrapTable('load', dataInit);
            setConstantInfoMessage(lblTotalPrice, "Tổng tiền xuất: " + formatFloatType(0));
            initExportPopup();
            $inpGoodsCode = $('#inp-goods-code');
            $inpGoodsAmount = $('#inp-amount');
        });

    });
    $('#order-export-insert-update-form').keydown(function (e) {
        if (e.keyCode == 13 && !$(e.target).parent().hasClass('editable-input')) {
            if ($(e.target).attr('id') == "inp-amount" || $(e.target).attr('id') == "inp-goods-code") {
                var weight = 0;
                var volume = 0;
                var amount = $inpGoodsAmount.val();
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
        doInsertData();
    });
    initDateRangeSelect();
    doSearch();


    validator = $("#cat-partner-insert-update-form").validate({
        ignore: ":hidden",
        rules: {
            code: {
                required: true,
                normalizer: function( value ) {
                    return $.trim( value );
                },
                maxlength: 50
            },
            name: {
                required: true,
                normalizer: function( value ) {
                    return $.trim( value );
                },
                maxlength: 100
            },
            telNumber: {
                required: true,
                normalizer: function( value ) {
                    return $.trim( value );
                },
                maxlength: 100
            },
            address: {
                normalizer: function( value ) {
                    return $.trim( value );
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
            $.ajax({
                type: "POST",
                url: "/workspace/cat_partner_ctr/add",
                data: $(form).serialize(),
                success: function (data) {
                    resultArr = data.split('|');
                    resultCode = resultArr[0];
                    resultName = resultArr[1];
                    if(resultCode == 1){
                        setInfoMessage($('#action-info'),resultName);
                    }else{
                        setErrorMessage($('#action-info'),resultName);
                    }
                },
                error:function(){
                    setErrorMessage($('#action-info'),'Lỗi hệ thống');
                }
            });
            $('#inp-receive-name').val ($('#modal-inp-code').val()+"|" +$('#modal-inp-name').val()+"|"+$('#modal-inp-telNumber').val());

            hideModal($modalAddUpdate);
            return false; // required to block normal submit since you used ajax
        }
    });
});

function doInsertData() {
    $body.addClass("loading");
    var stockIdValue = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    var descriptionValue = $('#inp-contract-note').val();
    var receiveValue = $('#inp-receive-name').val();
    var partnerIdValue = $('#cmb-partner').val();
    var exportMethod = $('input[name=cmb-export-method]:checked').val();

    var mjrOrder = {
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
                setErrorMessage($lblInfo,"Lỗi hệ thống")
            }else {
                $table.bootstrapTable('removeAll');
            }
            hideModal($addUpdateMainModal);
        },
        error: function (data) {
            // setErrorMessageWithTime($lblInfo, JSON.stringify(data), 8000)
        },
        complete: function () {
            hideModal($addUpdateModal);
            doSearch();
        }
    });
}

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
    return [
        '<a class="update-menu row-function" href="javascript:void(0)" title="Thực xuất">',
        '<i class="fa fa-share-square-o"></i>',
        '</a> ',
        '<a class="update-menu row-function" href="javascript:void(0)" title="Sửa">',
        '<i class="fa fa-pencil-square-o"></i>',
        '</a> ',
        '<a class="export-file row-function" href='+url+'  target="_blank" title="Xuất file">',
        '<i class="fa fa-file-word-o"></i>',
        '</a> ',
        '<a class="delete-menu row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}

//@FUNCTION-----------------------------------------------------------------------------------------------

window.operateEvents = {
    // 'click .update-menu': function (e, value, row, index) {
    //     validator.resetForm();
    //     clearActionInfo();
    //     changeModelByType(2, row['name'], row['code'], row['url'], row['imgClass'], row['orders'], row['id'], row['status'], $btn_update.val());
    //     $("#cat-menu-insert-update-form").find(".error").removeClass("error");
    //     $("#modal-menu-parentMenu").empty();
    //     var new_element = $("#modal-menu-parentMenu").clone();
    //     $("#modal-menu-parentMenu").replaceWith(new_element);
    //     $("#modal-menu-parentMenu").DropDownTree(configTree(row['parentId'], row['id']));
    //     showModal($addUpdateModal);
    //     $addUpdateModal.on('shown.bs.modal', function () {
    //         $('modal-menu-menuname').focus();
    //     });
    // },
    //
    // 'click .delete-menu': function (e, value, row, index) {
    //     clearActionInfo();
    //     $("#lbl-del-info").text('Xóa thông tin: ' + decodeHtml(row['name']));
    //     $('#myConfirmModal').modal('show');
    //     $selectedItemId = row['id'];
    // },
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
    }
};

function doSearch() {
    NProgress.start();
    var stockId = $('#cmb-stock').val();
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
function onChangeStock(sel) {
    $('#cmb-stock').val(sel.value);
    onSelectStock();
}