//GLOBAL VAR-------------------------------------------------------------------------------------------------------
$table = $('#tbl-import-goods');
$addUpdateModal = $('#myModal');
//input
$inpSerial = $('#modal-inp-serial');
$inpAmount = $('#modal-inp-amount');
$inpGoodsCode = $('#inp-goods-code');
var $inpGoodsAmount = $('#inp-amount');
$inpPrice = $('#modal-inp-input-price');
//combobox
$cmbGoods = $("#modal-cmb-goods");
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
    var oldPriceValue;
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
                width: '10%'
            },
            {
                field: 'goodsName',
                title: 'Tên hàng',
                align: 'left'
            },
            {
                field: 'goodsStateValue',
                title: 'Trạng thái',
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
                width: '10%',
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
                width: '7%',
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
                width: '7%'
            },
            {
                field: 'volume',
                title: 'Kích thước (cm3)',
                align: 'right',
                width: '7%'
            },
            {
                field: 'inputPrice',
                title: 'Giá nhập',
                align: 'right',
                width: '7%',
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
                width: '9%'
                //formatter: 'subTotal'
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
                title: 'Thao tác',
                formatter: 'operateFormatter',
                events: 'operateEvents',
                width: '7%',
                align: 'center'
            }
        ]
    });
    $table.bootstrapTable('hideColumn', 'columnId');
    $table.bootstrapTable('hideColumn', 'goodsId');

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
    'click .update-goods': function (e, value, row, index) {
        isUpdate = true;
        changeModelByType(false, row);
        showModal($('#myModal'))
    },

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
    if (stockIdValue == null) {
        alert('Chưa có thông tin kho nhập!');
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
    //
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
    //
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
//@Add show modal---------------------------------------------------------------------------------------------------
$('#btn-add').click(function () {
    //
    isUpdate = false;
    $('#modal-add-result').text('');
    changeModelByType(true, null);
    //
    var isSerial = "";
    var price = "";
    //
    $.ajax({
        type: "GET",
        url: $('#btn-check-serial').val(),
        data: {code: $cmbGoods.val()},
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        cache: false,
        dataType: 'json',
        async: false,
        success: function (data) {
            isSerial = data["isSerial"];
            price = data["inPrice"];
        }
    });
    //
    showElementBySerialType(isSerial);
    showPriceDetail(price, $inpPrice, $('#modal-label-inp-input-price'));
    //
    showModal($('#myModal'));
});

//#modal #add #confirm
$('#modal-btn-add').click(function () {
    addImportGoods();
});

function addImportGoods() {
    //
    var isSerial = "";
    var goods_code = "";
    var amount = $inpAmount.val();
    var weight = 0;
    var volume = 0;
    //
    $.ajax({
        url: $('#btn-check-serial').val(),
        data: {code: $cmbGoods.val()},
        cache: false,
        contentType: false,
        async: false,
        type: 'GET',
        success: function (data) {
            isSerial = data["isSerial"];
            goods_code = data["code"];
            weight = data["weight"];
            volume = data["volume"];
        }
    });
    //validation
    if (isSerial == '1') {
        if (($inpAmount.val() != "1")) {
            alert(goods_code + " là hàng quản lý serial, số lượng nhập phải là 1.");
            return;
        }
        //
        if (($inpSerial.val().trim().length === 0)) {
            alert(goods_code + " là hàng quản lý serial, cần nhập thông tin serial.");
            return;
        }
    }
    //
    preprocessInput($("#form-add-goods"));
    //get data
    var goodsCode = $cmbGoods.val();
    var goodsName = getGoodsNameInCombo($("#modal-cmb-goods option:selected").text());
    //
    var goodsStateValue = "0";
    var goodsState = "0";
    if ($('#cmb-goods-state').prop('checked')) {
        goodsStateValue = "1";
        goodsState = "1";
    }
    //
    var inputPriceValue = unFormatFloat($inpPrice.val());
    if (!isValidAmount(inputPriceValue)) {
        alert("Giá nhập phải là số");
        return;
    }
    //
    var serial = escapeHtml($inpSerial.val());

    //check whether serial is entered before
    var keySerial = goodsCode + serial;
    if (isSerial == '1' && enteredSerials.indexOf(keySerial) > -1) {
        setErrorMessage($('#modal-add-result'), "Serial đã được nhập");
        $inpSerial.val('');
        $inpSerial.focus();
        return;
    } else {
        enteredSerials.push(keySerial);
    }


    //everything ok -> add to table
    var columnId = ~~(Math.random() * 10000) * -1,
        rows = [];
    rows.push({
        goodsCode: goodsCode,
        goodsName: goodsName,
        goodsState: goodsState,
        goodsStateValue: goodsStateValue,
        serial: serial,
        amountValue: formatFloatType(amount),
        amount: amount,
        weight: Number(amount) * Number(weight),
        volume: Number(amount) * Number(volume),
        inputPriceValue: formatFloatType(inputPriceValue),
        inputPrice: inputPriceValue,
        total: Number(amount) * Number(inputPriceValue),
        cellCode: cellCode,
        columnId: columnId
    });
    //
    totalPrice += Number(inputPriceValue);
    setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(Number(totalPrice)));
    //
    $table.bootstrapTable('append', rows);
    enableElement($('#btn-import'));

    setInfoMessage($('#modal-add-result'), "Bổ sung thành công");
    $inpSerial.val('');
    $inpSerial.focus();
}

//@Update action---------------------------------------------------------------------------------------------------
var $btnUpdateModal = $('#modal-btn-update');
$btnUpdateModal.click(function () {
    updateGoods();
});

function updateGoods() {
    //valid require serial
    var isSerial = "";
    var goods_code = "";
    //
    $.ajax({
        url: $('#btn-check-serial').val(),
        data: {code: $cmbGoods.val()},
        cache: false,
        contentType: false,
        async: false,
        type: 'GET',
        success: function (data) {
            isSerial = data["isSerial"];
            goods_code = data["code"];
        }
    });
    if (isSerial === '1') {
        if (($inpAmount.val() !== "1")) {
            alert(goods_code + " là hàng quản lý serial, số lượng nhập phải là 1.");
            return;
        }
        //
        if (($inpSerial.val().trim().length === 0)) {
            alert(goods_code + " là hàng quản lý serial, cần nhập thông tin serial.");
            return;
        }
    }
    //
    preprocessInput($("#form-add-goods"));
    //get data
    var goodsCode = $cmbGoods.val();
    var goodsName = getGoodsNameInCombo($("#modal-cmb-goods option:selected").text());
    var amount = unFormatFloat($inpAmount.val());
    if (!isValidAmount(amount)) {
        alert("Số lượng phải là số ");
        return;
    }
    var goodsStateValue = "Hỏng";
    var goodsState = "0";
    if ($('#cmb-goods-state').prop('checked')) {
        goodsStateValue = "Bình thường";
        goodsState = "1";
    }
    var inputPriceValue = unFormatFloat($inpPrice.val());
    if (!isValidAmount(inputPriceValue)) {
        alert("Giá nhập phải là số dương");
        return;
    }
    var serial = escapeHtml($inpSerial.val());
    var cellCode = escapeHtml($('button[data-id=modal-cmb-cells]').attr('title'));
    if (cellCode.includes("selected")) {
        cellCode = "";
    }
    //everything all right -> update row
    $table.bootstrapTable('updateRow', {
        index: selectedIndex, row: {
            goodsCode: goodsCode,
            goodsName: goodsName,
            goodsState: goodsState,
            goodsStateValue: goodsStateValue,
            serial: serial,
            amountValue: formatFloatType(amount),
            amount: amount,
            inputPriceValue: formatFloatType(inputPriceValue),
            inputPrice: inputPriceValue,
            cellCode: cellCode
        }
    });
    //
    hideModal($('#myModal'));
}

//OTHERS------------------------------------------------------------------------------------------------------------------
function changeModelByType(isAdd, selectedItems) {
    //set cell for combo box
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
    //
    if (isAdd) {
        clearContent();
        //
        showAdd();
    } else {
        clearContent();
        $('select[name=modalCmbGoods]').val(selectedItems['goodsCode']);
        $('select[name=modalCmbGoods]').selectpicker('refresh');
        //
        $inpAmount.val(selectedItems['amountValue']);
        if (selectedItems["goodsState"] == "1") {
            $('#cmb-goods-state').bootstrapToggle('on');
        } else {
            $('#cmb-goods-state').bootstrapToggle('off');
        }
        $inpPrice.val(selectedItems['inputPriceValue']);
        $inpSerial.val(selectedItems['serial']);
        $modalCmbCells.val(selectedItems['cellCode']);
        //
        $('.selectpicker').selectpicker('refresh');
        showUpdate();
        //
    }
}

$("#modal-inp-input-price").keyup(function () {
    showPriceDetail($("#modal-inp-input-price").val(), $inpPrice, $('#modal-label-inp-input-price'));
});

$inpAmount.keyup(function () {
    var currentValue = unFormatFloat($inpAmount.val());
    var need2format = currentValue;
    if (!isValidPrice(currentValue)) {
        currentValue = need2format.substr(0, need2format.indexOf(".") + 5);
    }
    if (!currentValue.includes(".")) {
        $inpAmount.val(formatFloatType(currentValue));
    } else {
        $inpAmount.val(currentValue);
    }
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
//on changed cmb goods----------------------------------------------------------
$cmbGoods.change(function () {
    var inPrice = "";
    var isSerial = "";
    //
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data: {code: $cmbGoods.val()},
        cache: false,
        contentType: false,
        async: false,
        success: function (data) {
            inPrice = data['inPrice'];
            isSerial = data["isSerial"];
        }
    });
    //
    showElementBySerialType(isSerial);
    //
    //
    showPriceDetail(inPrice, $inpPrice, $('#modal-label-inp-input-price'));
});

function showElementBySerialType(serialType) {
    if (serialType === '1') {
        $inpAmount.val('1');
        disableElement($inpAmount);
        enableElement($inpSerial);
        //
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $inpSerial.focus();
        });
        $inpSerial.focus();
    } else {
        disableElement($inpSerial);
        $inpSerial.val('');
        enableElement($inpAmount);
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $inpAmount.focus();
        });
        $inpAmount.focus();
    }
}

function clearContent() {
    $('#cmb-goods-state').bootstrapToggle('on');
    $inpPrice.val('');
    $inpSerial.val('');
    //$('select[name=modalCmbCells]').val("");
    $inpAmount.val('');
    $inpPartnerName.val('');
}

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

//for enter press ->
$inpGoodsCode.keypress(function (e) {
    var key = e.which;
    var goodsItem;
    var weight = 0;
    var volume = 0;
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
        $inpGoodsAmount.focus();
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
        }
    }
});

$inpGoodsAmount.keypress(function (e) {
    var key = e.which;
    var weight = 0;
    var volume = 0;
    var amount = $inpGoodsAmount.val();
    if (key == 13)  // the enter key code
    {
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
        if (amount == '' || amount == undefined) {
            alert("Vui lòng nhập số lượng");
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
            columnId: columnId
        });
        //
        totalPrice += Number(goodsItem['inPrice']) * amount ;
        setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(Number(totalPrice)));
        //
        $table.bootstrapTable('append', rows);
        enableElement($('#btn-import'));

        setInfoMessage($('#modal-add-result'), "Bổ sung thành công");
        $inpGoodsAmount.val('');
    }
});

$inpSerial.keypress(function (e) {
    var key = e.which;
    if (key == 13)  // the enter key code
    {
        if (isUpdate) {
            updateGoods();
        } else {
            addImportGoods();
        }
    }
});

$inpAmount.keypress(function (e) {
    var key = e.which;
    if (key === 13)  // the enter key code
    {
        if (isUpdate) {
            updateGoods();
        } else {
            addImportGoods();
        }
    }
});

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

function operateFormatter(value, row, index) {
    return [
        '<a class="delete-goods row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}

// duyot onchange stock
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
    var total = row.amount * row.inputPrice;
    return formatFloatType(value);
}

function moveDataToTable() {
    var weight = 0;
    var volume = 0;
    var amount = $inpAmount.val();
    console.log('moveDataToTable');

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
            console.log(amount);
            console.log(weight);
            console.log(volume);
        }
    });
    if (goodsItem == null) {
        alert("Không có mặt hàng tương ứng");
        return;
    }

    var columnId = ~~(Math.random() * 10000) * -1,
        rows = [];
    rows.push({
        goodsCode: goodsItem['code'],
        goodsName: goodsItem['name'],
        goodsState: '1',
        goodsStateValue: '1',
        serial: '-',
        amount: $inpGoodsAmount.val(),
        weight: Number(amount) * Number(weight),
        volume: Number(amount) * Number(volume),
        inputPrice: goodsItem['inPrice'],
        inputPriceValue: formatFloatType(goodsItem['inPrice']),
        totalMoney: Number(amount) * Number(goodsItem['inPrice']),
        cellCode: $('#cmb-cells').val(),
        columnId: columnId
    });
    //
    totalPrice += Number(goodsItem['inPrice']) * Number(amount);
    setTextForLabel(lblTotalPrice, "Tổng tiền nhập: " + formatFloatType(Number(totalPrice)));
    //
    $table.bootstrapTable('append', rows);
    enableElement($('#btn-import'));

    setInfoMessage($('#modal-add-result'), "Bổ sung thành công");
    $inpGoodsAmount.val('');
}

//-----------------------------------------------------------------------------------------------------------------