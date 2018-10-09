var table = $('#tbl-sale');
var dataInit = [];
var selectedIndex = -1;
var totalPrice = Number(0);
var lblTotalPrice = $("#lbl-total-price");
var inpGoodsCode = $('#inp-goods-code');
var mapGoodCode;
var inpGoodsSerial = $('#inp-serial');
var rowSerial = $('#row-serial');
var $body = $("body");
$(function () {
    //
    var oldPriceValue;
    table.bootstrapTable({
        data: dataInit,
        columns: [
            {
                field: 'stt',
                title: 'STT',
                formatter: 'runningFormatter',
                align: 'center',
                width: '40px'
            },
            {
                field: 'goodsCode',
                align: 'center',
                title: 'Mã hàng'
            },
            {
                field: 'goodsState',
                align: 'center',
                title: 'Trạng thái',
                visible: false
            },
            {
                field: 'isSerial',
                align: 'center',
                visible: false
            },
            {
                field: 'goodsId',
                align: 'center',
                title: 'Trạng thái',
                visible: false
            },
            {
                field: 'goodsName',
                align: 'center',
                title: 'Tên hàng'
            },
            {
                field: 'serial',
                align: 'center',
                title: 'Serial',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons: false
                }
            },
            {
                field: 'amount',
                title: 'Số lượng',
                align: 'center',
                cellStyle:'addStyle',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons: false,
                    validate: function (value) {
                        if (!isValidAmount(value)) {
                            return 'Số lượng nhập phải là số';
                        }

                        var row = table.bootstrapTable('getData')[selectedIndex];
                        var total = (value - row.amount)*row.outputPrice;
                        totalPrice = totalPrice + total ;
                        row.amount = value;
                        row.totalMoney = row.amount *row.outputPrice;;
                        table.bootstrapTable('updateRow', {index: selectedIndex, row:row});
                        updateTotalPrice(totalPrice);
                    },
                    display: function (value) {
                        $(this).text(formatFloatType(value));
                    }
                }
            },
            {
                field: 'unitName',
                align: 'center',
                title: 'Đơn vị',
                cellStyle:'table-align-center',
            },
            {
                field: 'priceValue',
                align: 'center',
                title: 'Giá bán',
                cellStyle:'table-align-right',
            },
            {
                field: 'totalMoney',
                align: 'center',
                title: 'Thành tiền',
                cellStyle:'table-align-right',
                formatter: 'subTotal',
            },
            {
                title: 'Thao tác',
                formatter: 'operateFormatter',
                events: 'operateEvents',
                events: 'operateEvents',
                align: 'center'
            }
        ]
    });
    table.bootstrapTable({}).on('click-row.bs.table', function (e, row, $element) {
        selectedIndex = $element.attr('data-index');
    });
    // init autocomplete goodCode

    var goodsName = [];
//     build map
    mapGoodCode = new Object();
    for(i = 0 ; i <lstGoods.length ; i++){
        mapGoodCode[lstGoods[i].code.toLowerCase()] = lstGoods[i];
        mapGoodCode[lstGoods[i].name.toLowerCase()] = lstGoods[i];
        goodsName.push(lstGoods[i].name);
        goodsName.push(lstGoods[i].code);
    }
    $('#inp-goods-code').autocomplete({
        source: goodsName
    });
    $('#inp-cust').autocomplete({
        source: lstPartner
    });
//    init mayment infor
    resetPaymentInfor();
});

inpGoodsCode.keypress(function (e) {
    var key = e.which;
    var goodsItem;
    if (key == 13) {// the enter key code
        onAddGoods();
    }
});
inpGoodsSerial.keypress(function (e) {
    var key = e.which;
    var goodsItem;
    if (key == 13) {// the enter key code
        onAddGoods();
    }
})
function updateData(goodsItem) {
    var columnId = ~~(Math.random() * 100) * -1;
    rows = [];
    rows.push({
        goodsCode: goodsItem['code'],
        goodsName: goodsItem['name'],
        goodsId:goodsItem['id'],
        isSerial:goodsItem['isSerial'],
        serial: goodsItem['serial'],
        amount: '1',
        goodsState:'1',
        unitName: goodsItem['unitTypeName'],
        outputPrice: Number(goodsItem['outPrice']),
        priceValue: formatFloatType(Number(goodsItem['outPrice'])),
        totalMoney: Number(1) * Number(goodsItem['outPrice']),
        columnId: columnId
    });
    //
    totalPrice += Number(goodsItem['outPrice']);
    updateTotalPrice(totalPrice);
    //
    table.bootstrapTable('append', rows);
    inpGoodsCode.val('');
    inpGoodsSerial.val('');
    $('#row-serial').css("display", "none");
    checkDisableEnablePaymentButton();
}
function resetPaymentInfor() {
    setInfoMessage(lblTotalPrice, "Tổng tiền: " + formatFloatType(Number(totalPrice)));
    $("#inp-totalCurrentcy").val("0");
    $("#inp-discount").val("0");
    $("#inp-currency").val("0");
    $("#inp-customerCurrency").val("0");
    $("#inp-returnCustomer").val("0");
    checkDisableEnablePaymentButton();

}
function subTotal(value, row, index) {
    var total = row.amount * row.inputPrice;
    return formatFloatType(value);
}
function operateFormatter(value, row, index) {
    return [
        '<a class="delete-goods row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}
window.operateEvents = {
    'click .delete-goods': function (e, value, row, index) {
        clearActionInfo();
        table.bootstrapTable('remove', {
            field: 'columnId',
            values: [row['columnId']]
        });
        //
        totalPrice -= Number(row['totalMoney']);
        updateTotalPrice(totalPrice);
        checkDisableEnablePaymentButton();
    }
};
function updateTotalPrice(totalPrice) {
    setInfoMessage(lblTotalPrice, "Tổng tiền: " + formatFloatType(Number(totalPrice)));
    updatePaymentInformation();
}
function updatePaymentInformation() {
    $("#inp-totalCurrentcy").val( formatFloatType(Number(totalPrice)));
    $("#inp-discount").val("0")
    $("#inp-currency").val(formatFloatType(totalPrice));
    $("#inp-customerCurrency").val("0");
    $("#inp-returnCustomer").val("0");
}
$("#inp-discount").keypress(function (evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)&& charCode !=46) {
        return false;
    }

    if (charCode == 13) {
        $("#inp-customerCurrency").focus();
    }})
//jQuery
$("#inp-discount").on('keyup', function(evt){
         var value = $(this).val().replace(/\,/g,"");
        $(this).val(formatFloatType(Number(value)));
        var discountValue = $("#inp-discount").val().replace(/\,/g,"");
        var currency = Number($("#inp-totalCurrentcy").val().replace(/\,/g,""));
        if( discountValue >= currency){
            alert('Số tiền chiết khấu phải nhỏ hơn tổng tiền');
            return "false";
        }
        var curencyValue = totalPrice - Number(discountValue);
        $("#inp-currency").val(formatFloatType(curencyValue));

        var value = Number($("#inp-customerCurrency").val().replace(/\,/g,""));

        var refund = value - curencyValue;
        $("#inp-returnCustomer").val(formatFloatType(refund));

});

$("#inp-customerCurrency").keypress(function (evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)&& charCode !=46) {
        return false;
    }
})

$("#inp-customerCurrency").on('keyup', function(evt){
    var value = $(this).val().replace(/\,/g,"");
    $(this).val(formatFloatType(Number(value)));
    var currency = Number($("#inp-currency").val().replace(/\,/g,""));
    var value = Number($("#inp-customerCurrency").val().replace(/\,/g,""));
    var refund = value - currency;
    $("#inp-returnCustomer").val(formatFloatType(refund));
});


var btnPayment = $('#btn-payment');
btnPayment.click(function () {
    //
    var data =  table.bootstrapTable('getData');
    if(data.length == 0){
        setErrorMessage($lblInfo, "Không có hàng hóa")
        return;
    }
    var stockIdValue = $('#cmb-stock').val();
    var custValue = $('#inp-cust').val();
    if(custValue.trim() !=""){
          if(lstPartner.indexOf(custValue)== -1){
              alert("Khách hàng không tồn tại nhập đúng khách hàng hoặc để trống");
              $('#inp-cust').focus();
              return;
          }
    }
    $body.addClass("loading");
    var transMoneyTotal = convertCurrentcyToNumber($('#inp-totalCurrentcy').val());
    var transMoneyDiscount = convertCurrentcyToNumber($('#inp-discount').val());
    var transMoneyRequire = convertCurrentcyToNumber($('#inp-currency').val());
    var customerMoney = convertCurrentcyToNumber($('#inp-customerCurrency').val());
    var returnMoney = convertCurrentcyToNumber($('#inp-returnCustomer').val());
    var stockName = $("#cmb-stock :selected").text();
    var stock_trans_info = {
        stockId: stockIdValue,
        receiveName: custValue,
        stockName:stockName,
        transMoneyTotal:transMoneyTotal,
        transMoneyDiscount:transMoneyDiscount,
        transMoneyRequire:transMoneyRequire,
        customerMoney:customerMoney,
        returnMoney:returnMoney
    };
    //
    var importData = JSON.stringify({lstGoods: table.bootstrapTable('getData'), mjrStockTransDTO: stock_trans_info,printInvoice:$('input[name=rad-bill]:checked').val()});
    //
    var $lblInfo = $("#import-action-info");
    //
    $.ajax({
        url: btnPayment.val(),
        data: importData,
        cache: false,
        contentType: "application/json",
        dataType: 'json',
        type: 'POST',
        success: function (data) {
            //
            var resultMessage = data['statusCode'];
            var stockTransId = data['key'];
            var successRecords = data['success'];
            var invoice = data['filePath'];
            //
            if (resultMessage == "SUCCESS_WITH_ERROR") {
                var totalRecords = data['total'];
                //show modal upload file
                $("#modal-error-import-lbl-info").text('Xuất ' + successRecords + '/' + totalRecords + ' hàng thành công với mã giao dịch: ' + stockTransId)
            } else if (resultMessage == "FAIL") {
                setErrorMessage($lblInfo, "Lỗi :" +  data['statusName'] + " "+(data['key'] == null ? "": data['key']));
            } else {
                setInfoMessageWithTime($lblInfo, "Xuất " + successRecords + " hàng thành công với mã giao dịch: " + stockTransId, 8000);
                // document.getElementById('myIframe').src = filePath;
                if( $('input[name=rad-bill]:checked').val() == "0"){
                    printJS(invoice);
                }

            }
            table.bootstrapTable('removeAll');
            enteredSerials = [];
            totalPrice = Number(0);
            resetPaymentInfor();
        },
        error: function (data) {
            setErrorMessage($lblInfo, JSON.stringify(data))
        },
        complete: function () {
            $body.removeClass("loading");
        }
    });
});


function addStyle(value, row, index) {
    if (row["serial"]!="") {
        return {
            classes: "not-active table-align-right"
        };
    }
    return {};
}

$("#inp-discount").click(function () {
    $(this).select();
})
$("#inp-customerCurrency").click(function () {
    $(this).select();
})

$('#btn-sale').click(function () {
    onAddGoods();
})
function onAddGoods() {
    goodsItem = mapGoodCode[inpGoodsCode.val().trim().toLowerCase()];
    if (goodsItem == null) {
        alert("Không có mặt hàng tương ứng");
        return;
    }
    goodsItem.serial = '';
    if (goodsItem["isSerial"] == '1') {
        if($('#row-serial').css("display") == "none"){
            $('#row-serial').css("display", "block");
            inpGoodsSerial.val('');
            inpGoodsSerial.focus();
            return;
        }else {
            if (inpGoodsSerial.val().trim() == '') {
                alert("Mặt hàng bắt buộc phải nhập serial");
                inpGoodsSerial.focus();
                return;
            }
            goodsItem.serial = inpGoodsSerial.val();
        }
    }
    updateData(goodsItem);
}
function checkDisableEnablePaymentButton() {
    var data =  table.bootstrapTable('getData');
    if(data.length == 0){
        disableElement($("#btn-payment"));
    }else{
        enableElement($("#btn-payment"));
    }
}
function caculateRefund() {


    var customerValue = Number($("#inp-customerCurrency").val().replace(/\,/g,""));

    if(currency> customerValue){
        alert('Số tiền khách đưa phải lớn hơn số tiền phải thu');
        return "error";
    }
}