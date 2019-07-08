//GLOBAL VAR-------------------------------------------------------------------------------------------------------
$table = $('#tbl-import-goods');
$addUpdateModal = $('#myModal');
//input
$inpGoodsCode = $('#inp-goods-code');
var $inpGoodsAmount = $('#inp-amount');
//combobox
$inpPartnerName = $('#inp-partner-name');
$modalCmbCells = $("#cmb-cells");
//
var dataInit = [];
var enteredSerials = [];
var selectedIndex = -1;
$body = $("body");
var isUpdate = false;
var totalPrice = Number(0);
var lblTotalPrice = $("#lbl-total-price");
//-------------------------------------------------------------------------------------------------------
//#init table
$(function () {
    //import-action-info
    $table.bootstrapTable({
        data: dataInit,
        columns: [
            {
                field: 'id',
                title: 'STT',
                formatter: 'runningFormatter',
                align: 'center',
                width: '40px'
            },
            {
                field: 'goodsCode',
                title: 'Mã hàng',
                align: 'left',
                width: '9%'
            },
            {
                field: 'goodsName',
                title: 'Tên hàng',
                align: 'left'
            },
            {
                field: 'goodsStateValue',
                title: 'Tình trạng',
                align: 'left',
                width: '10%',
                editable: {
                    type: 'select',
                    mode: 'inline',
                    showbuttons: false,
                    source: [
                        {value: 1, text: 'Bình thường'},
                        {value: 2, text: 'Hỏng'}
                    ]
                }
            },
            {
                field: 'serial',
                title: 'Serial',
                align: 'left',
                width: '7%',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons: false
                }
            },
            {
                field: 'amount',
                title: 'Số lượng',
                cellStyle: 'addStyle',
                align: 'right',
                width: '9%',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    textAlign: 'right',
                    showbuttons: false,
                    validate: function (value) {
                        if (!isValidAmount(value)) {
                            return 'Số lượng nhập phải là số';
                        }
                        var row = $table.bootstrapTable('getData')[selectedIndex];
                        totalPrice += (value - row.amount) * row.inputPrice;
                        //
                        row.amount = value;
                        row.totalMoney = value * row.inputPrice;
                        //
                        row.weight = value * row.baseWeight;
                        row.volume = value * row.baseVolume;
                        //
                        $table.bootstrapTable('updateRow', {index: selectedIndex, row: row});
                        //
                        setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(totalPrice));
                    },
                    display: function (value) {
                        $(this).text(formatFloatType(value));
                    }
                }
            },
            {
                field: 'weight',
                title: 'Trọng lượng(kg)',
                align: 'right',
                formatter: 'subTotal',
                width: '7%'
            },
            {
                field: 'volume',
                title: 'Thể tích(m3)',
                align: 'right',
                formatter: 'subTotal',
                width: '7%'
            },
            {
                field: 'inputPrice',
                title: 'Giá nhập',
                align: 'right',
                width: '9%',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    textAlign: 'right',
                    showbuttons: false,
                    validate: function (value) {
                        var amount = unFormatFloat(value);
                        if (!isValidAmount(amount)) {
                            return 'Giá nhập nhập phải là số';
                        }
                        var row = $table.bootstrapTable('getData')[selectedIndex];
                        totalPrice += (value - row.inputPrice) * row.amount;
                        //
                        row.inputPrice = value;
                        row.totalMoney = value * row.amount;
                        //
                        $table.bootstrapTable('updateRow', {index: selectedIndex, row: row});
                        //
                        setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(totalPrice));
                    },
                    display: function (value) {
                        $(this).text(formatFloatType(value));
                    }
                }
            },
            {
                field: 'totalMoney',
                title: 'Thành tiền',
                align: 'right',
                width: '10%',
                formatter: 'subTotal'
            },
            {
                field: 'cellCode',
                title: 'Vị trí',
                align: 'left',
                width: '7%',
                editable: {
                    type: 'select',
                    mode: 'inline',
                    showbuttons: false,
                    source: $('#btn-get-cells').val() + '?stockId=' + $('#cmb-stock').val()
                }
            },
            {
                title: 'Xóa',
                formatter: 'operateFormatter',
                events: 'operateEvents',
                width: '4%',
                align: 'center'
            },
            {
                field: 'baseWeight',
                title: 'baseWeight'
            },
            {
                field: 'baseVolume',
                title: 'baseVolume'
            }
        ]
    });
    $table.bootstrapTable('hideColumn', 'columnId');
    $table.bootstrapTable('hideColumn', 'goodsId');
    $table.bootstrapTable('hideColumn', 'baseWeight');
    $table.bootstrapTable('hideColumn', 'baseVolume');

    $table.bootstrapTable({}).on('click-row.bs.table', function (e, row, $element) {
        selectedIndex = $element.attr('data-index');
    });
    //
    disableElement($('#btn-import'));
    //
    loadGoodsCodeSuggestion();
    loadPartnerSuggestion();
    //
});
//ROW FUNCTION------------------------------------------------------------------------------------------------------
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
        setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(Number(totalPrice)));
    }
};
//@Upload---------------------------------------------------------------------
var btnUploadExcel = $('#btn-excel-import');
btnUploadExcel.click(function () {
    //
    $("#import-action-info").text('');
    //
    var data = new FormData();
    jQuery.each(jQuery('#inp-file-import')[0].files, function (i, file) {
        data.append('file-' + i, file);
    });

    var fileName = $('#inp-file-import').val();
    if (fileName == '') {
        alert('Chưa có file dữ liệu');
        return;
    }
    //
    $body.addClass("loading");
    //
    $.ajax({
        url: btnUploadExcel.val(),
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',
        success: function (data) {
            if (data == '') {
                $('#myDownloadErrorModal').modal('show');
                return;
            }
            //
            var importGoods = data;
            var goodsSize = importGoods.length;
            for (i = 0; i < goodsSize; i++) {
                totalPrice += importGoods[i].amount * importGoods[i].inputPrice;
            }
            //
            setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(totalPrice));
            $table.bootstrapTable('load', data);
            //
            enableElement($('#btn-import'));
        },
        error: function (data) {
            alert(data);
        },
        complete: function () {
            $body.removeClass("loading");
        }
    });
});
//@Import---------------------------------------------------------------------
var btnImport = $('#btn-import');
btnImport.click(function () {
    //validate

    var import_goods = $table.bootstrapTable('getData');
    if (import_goods.length == 0) {
        alert('Chưa có thông tin hàng nhập!');
        return;
    }
    var stockIdValue = $('#cmb-stock').val();
    if (stockIdValue == null || stockIdValue == -1) {
        alert('Bạn chưa chọn kho nhập!');
        $('#cmb-stock').focus();
        return;
    }
    //
    $("#lbl-del-info").text('Nhập hàng lên hệ thống?');
    //
    showModal($('#myConfirmModal'));
});
//@Import confirm---------------------------------------------------------------------
var btnImportConfirm = $('#modal-btn-del-ok');
btnImportConfirm.click(function () {
    hideModal($('#myConfirmModal'));
    $body.addClass("loading");
    var stockIdValue = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    // var invoiceValue     = $('#inp-invoice').val();
    var descriptionValue = $('#inp-contract-note').val();
    var partnerValue = $('#inp-partner-name').val();
    var stock_trans_info = {
        contractNumber: contractNumberValue,
        stockId: stockIdValue,
        description: descriptionValue,
        partnerName: partnerValue
    };
    //
    var importData = JSON.stringify({lstGoods: $table.bootstrapTable('getData'), mjrStockTransDTO: stock_trans_info});
    var $lblInfo = $("#import-action-info");
    //
    $.ajax({
        url: btnImport.val(),
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
            //
            if (resultMessage == "SUCCESS_WITH_ERROR") {
                var totalRecords = data['total'];
                //show modal upload file
                $("#modal-error-import-lbl-info").text('Nhập ' + successRecords + '/' + totalRecords + ' hàng thành công với mã giao dịch: ' + stockTransId);
                $("#modal-link-download").attr("href", $("#modal-inp-stock-trans-id").val() + "/" + stockTransId);
                showModal($("#myDownloadErrorImportModal"));
            } else if (resultMessage == "FAIL") {
                setErrorMessage($lblInfo, "Nhập kho không thành công!");
            } else {
                setInfoMessageWithTime($lblInfo, "Nhập " + successRecords + " hàng thành công với mã giao dịch: " + stockTransId, 8000);
                $inpGoodsCode.val('');
                $inpGoodsAmount.val('');
                $inpPartnerName.val('');
                $('#inp-contract-note').val('');
            }
            //
            disableElement($('#btn-import'));
            $table.bootstrapTable('removeAll');
            $('#inp-partner-name').val("");
            enteredSerials = [];
            totalPrice = Number(0);
            setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + totalPrice);


        },
        error: function (data) {
            setErrorMessage($lblInfo, JSON.stringify(data))
        },
        complete: function () {
            $body.removeClass("loading");
        }
    });
});
//@Add action---------------------------------------------------------------------------------------------------
$btn_add_partner = $('#btn-add-partner');
$(function () {
    $btn_add_partner.click(function () {

        $addUpdateModal.modal('show');
        $("#cat-partner-insert-update-form").attr("action", $btn_add_partner.val());
        $("#modal-inp-code").val('');
        $("#modal-inp-name").val('');
        $("#modal-inp-telNumber").val('');
        $("#modal-inp-address").val('');
        //set default active-disable combo status
        $('#modal-cmb-status').bootstrapToggle('on');
        $("#div-status *").prop('disabled', true);
        showAdd();
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-inp-code').focus();
        });
    });
});
//@Refresh table----------------------------------------------------------
$('#btn-refresh-table').click(function () {
    //
    $("#modal-del-lbl-del-info").text('Xóa toàn bộ dữ liệu đã nhập?');
    //
    showModal($('#deleteConfirmModal'));
});

var btnClearTableConfirm = $('#modal-del-btn-ok');
btnClearTableConfirm.click(function () {
    $table.bootstrapTable('removeAll');
    enteredSerials = [];
    $("#import-action-info").text('');
    //
    hideModal($('#deleteConfirmModal'))
});
function clearContent() {
    $('#cmb-goods-state').bootstrapToggle('on');
    $inpPartnerName.val('');
}
//#event
//-------------enter goods code
$inpGoodsCode.keypress(function (e) {
    var key = e.which;
    var goodsItem;
    var weight = 0;
    var volume = 0;
    if (key == 13)  // the enter key code
    {
        //1. check goods serial: serial:focus to serial field, no_serial: append to table
        $.ajax({
            type: 'GET',
            url: $('#btn-check-serial').val(),
            data: {code: valueFromSuggest($inpGoodsCode.val())},
            cache: false,
            contentType: false,
            async: false,
            success: function (data) {
                goodsItem = data;
                weight = data["weight"];
                volume = data["volume"];
            }
        });
        if (goodsItem == null) {
            alert("Không có mặt hàng tương ứng");
            return;
        }
        //move to amount
        if (goodsItem["isSerial"] == '1') {
            var columnId = ~~(Math.random() * 10000) * -1;
            rows = [];
            rows.push({
                goodsCode: goodsItem['code'],
                goodsName: goodsItem['name'],
                goodsState: '1',
                goodsStateValue: '1',
                serial: '-',
                amount: '1',
                weight:  Number(weight),
                volume:  Number(volume),
                inputPrice: goodsItem['inPrice'],
                inputPriceValue: formatFloatType(goodsItem['inPrice']),
                totalMoney: Number(goodsItem['inPrice']),
                cellCode: $('#cmb-cells').val(),
                columnId: columnId
            });
            //
            totalPrice += Number(goodsItem['inPrice']);
            setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(Number(totalPrice)));
            //
            $table.bootstrapTable('append', rows);
            enableElement($('#btn-import'));

            setInfoMessage($('#modal-add-result'), "Bổ sung thành công");

            $inpGoodsCode.val('');
            $inpGoodsAmount.val('');
            $inpGoodsCode.focus();
        }else{
            $inpGoodsAmount.focus();
        }
    }
});
//-------------enter goods amount
$inpGoodsAmount.keypress(function (e) {
    var key = e.which;
    var weight = 0;
    var volume = 0;
    var amount = $inpGoodsAmount.val();
    if (key == 13)//enter
    {
        moveDataToTable();
    }
});
function moveDataToTable() {
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data: {code: valueFromSuggest($inpGoodsCode.val())},
        cache: false,
        contentType: false,
        async: false,
        success: function (data) {
            goodsItem = data;
            isSerial = data["isSerial"];
            weight = data["weight"];
            volume = data["volume"];
        }
    });
    if (goodsItem == null) {
        alert("Không có mặt hàng tương ứng");
        return;
    }

    var columnId = ~~(Math.random() * 10000) * -1,
        rows = [];
    var isSerial = "";
    var amount = Number($inpGoodsAmount.val());
    if (!isValidAmount(amount)) {
        alert("Vui lòng nhập số lượng hợp lệ");
        return;
    }
    if (isSerial == '1' && amount != 1) {
        alert("Hàng serial số lượng phải là 1");
        return;
    }
    rows.push({
        goodsCode: goodsItem['code'],
        goodsName: goodsItem['name'],
        goodsState: '1',
        goodsStateValue: '1',
        serial: '',
        amount: amount,
        weight: amount * Number(weight),
        volume: amount * Number(volume),
        inputPrice: goodsItem['inPrice'],
        inputPriceValue: formatFloatType(goodsItem['inPrice']),
        totalMoney: amount * Number(goodsItem['inPrice']),
        cellCode: $('#cmb-cells').val(),
        columnId: columnId,
        baseWeight: weight,
        baseVolume: volume
    });
    //
    totalPrice += Number(goodsItem['inPrice']) * amount ;
    setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(Number(totalPrice)));
    //
    $table.bootstrapTable('append', rows);
    enableElement($('#btn-import'));

    setInfoMessage($('#modal-add-result'), "Bổ sung thành công");
    $inpGoodsCode.val('');
    $inpGoodsAmount.val('');
    $inpGoodsCode.focus();
}

function onSelectStock() {
    $.ajax({
        type: 'GET',
        url: $('#btn-get-cells').val(),
        data: {stockId: $('#cmb-stock').val()},
        cache: false,
        contentType: false,
        async: false,
        success: function (data) {
            if (data.length == 0) {
                disableElement($modalCmbCells);
            } else {
                enableElement($modalCmbCells);
            }
            loadSelectItems($modalCmbCells, data);
        }
    });
}
//-------------other
function loadSelectItems(select, items) {
    select.empty();
    $.each(items, function (i, item) {
        var opt = document.createElement('option');
        opt.value = item.value;
        opt.innerHTML = item.text;
        select.append(opt);
    });
    select.selectpicker('refresh');
}

function loadGoodsCodeSuggestion() {
    var goodsCode = null;
    $.ajax({
        type: 'GET',
        url: $('#btn-get-goods-code').val(),
        cache: false,
        contentType: false,
        async: false,
        success: function (data) {
            goodsCode = data;
        }
    });
    setAutoComplete($('#inp-goods-code'),goodsCode);
}

function loadPartnerSuggestion() {
    var partnerName = null;
    $.ajax({
        type: 'GET',
        url: $('#btn-get-partner').val(),
        cache: false,
        contentType: false,
        async: false,
        success: function (data) {
            partnerName = data;
        }
    });
    setAutoComplete($('#inp-partner-name'),partnerName);
}
//-------------#formatter
function operateFormatter(value, row, index) {
    return [
        '<a class="delete-goods row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}

function addStyle(value, row, index) {
    var classes = ['active', 'success', 'info', 'warning', 'danger'];
    if (row["serial"] !== "" && row["serial"] !== "-") {
        return {
            classes: "not-active"
        };
    }
    return {};
}

function subTotal(value, row, index) {
    return formatFloatType(value);
}
//-----------------------------------------------------------------------------------------------------------------