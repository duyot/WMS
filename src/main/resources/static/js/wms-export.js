$modalAddImportGoods = $('#myModal');
$table     = $('#tbl-export-goods');
$inpSerial =  $('#modal-inp-serial');
$inpAmount = $("#modal-inp-amount");
$cmbGoods = $('#modal-cmb-goods');
var dataInit = [
];
var enteredSerials = [];
var selectedIndex = -1;
$body = $("body");
var isUpdate = false;
//-------------------------------------------------------------------------------------------------------
function operateFormatter(value, row, index) {
    return [
        '<a class="update-goods row-function" href="javascript:void(0)" title="Sửa">',
        '<i class="fa fa-pencil-square-o"></i>',
        '</a> ',
        '<a class="delete-goods row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}
$(function () {
    //
    $table.bootstrapTable({
        data: dataInit
    });
    $table.bootstrapTable('hideColumn', 'columnId');
    $table.bootstrapTable('hideColumn', 'goodsId');
    $table.bootstrapTable('hideColumn', 'isSerial');
    //

    $table.bootstrapTable({}).on('click-row.bs.table', function (e, row, $element) {
        selectedIndex = $element.attr('data-index');
    });
    //
    disableElement($('#btn-export'));
    //
    $("#myModal").on('shown.bs.modal', function () {
        $('#modal-cmb-goodsundefined').focus();
        $('#modal-cmb-goodsundefined').attr("autocomplete","off");
    });
});
//
window.operateEvents = {
    'click .update-goods': function (e, value, row, index) {
        isUpdate = true;
        changeModelByType(false,row);
        showModal($('#myModal'))
    },

    'click .delete-goods': function (e, value, row, index) {
        clearActionInfo();
        $table.bootstrapTable('remove', {
            field: 'columnId',
            values: [row['columnId']]
        });
    }
};
//@Upload---------------------------------------------------------------------
var btnUploadExcel = $('#btn-excel-export');
$(function () {
    btnUploadExcel.click(function () {
        //
        $("#export-action-info").text('');
        //
        var data = new FormData();
        jQuery.each(jQuery('#inp-file-export')[0].files, function(i, file) {
            data.append('file-'+i, file);
        });

        var fileName = $('#inp-file-export').val();
        if(fileName == ''){
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
            success: function(data){
                if(data == ''){
                    $('#myDownloadErrorModal').modal('show');
                    return;
                }
                $table.bootstrapTable('load', data);
                //
                enableElement($('#btn-export'));
            },
            error: function(data){
                alert(data);
            },
            complete: function(){
                $body.removeClass("loading");
            }
        });
    });

});
//@export---------------------------------------------------------------------
var btnexport = $('#btn-export');
btnexport.click(function () {
    var contractNumber = $('#inp-contract-number').val();
    if(isContainXMLCharacter(contractNumber)){
        alert("Mã hợp đồng chứa kí tự đặc biệt!");
        return;
    }
    //validate
    var export_goods = $table.bootstrapTable('getData');
    if(export_goods.length == 0){
        alert('Chưa có thông tin hàng xuất!');
        return;
    }
    var stockIdValue = $('#cmb-stock').val();
    if(stockIdValue == null){
        alert('Chưa có thông tin kho xuất!');
        return;
    }
    //
    $("#lbl-del-info").text('Xuất hàng?');
    //
    showModal($('#myConfirmModal'));
});

var btnExportConfirm = $('#modal-btn-del-ok');
btnExportConfirm.click(function () {
    //
    hideModal($('#myConfirmModal'));
    $body.addClass("loading");
    var stockIdValue = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    var invoiceValue = $('#inp-invoice').val();
    var descriptionValue = $('#inp-contract-note').val();
    var stock_trans_info = {contractNumber:contractNumberValue,invoiceNumber:invoiceValue,stockId:stockIdValue,description:descriptionValue};
    //
    var exportData = JSON.stringify({lstGoods:$table.bootstrapTable('getData'),mjrStockTransDTO:stock_trans_info});
    //
    var $lblInfo = $("#export-action-info");
    //
    $.ajax({
        url: btnexport.val(),
        data:exportData,
        cache: false,
        contentType: "application/json",
        dataType: 'json',
        type: 'POST',
        success: function(data){
            //
            var resultMessage  = data['statusCode'];
            var key   = data['key'];
            //
            if(resultMessage === "FAIL"){
                var statusName   = data['statusName'];
                //show modal upload file
                setErrorMessage($lblInfo,"Xuất hàng không thành công: "+ convertExportMessage(statusName,key));
            }else{
                var totalSuccess = data['success'];
                setInfoMessage($lblInfo,"Xuất "+ totalSuccess+" hàng thành công với mã phiếu: "+ key);
                disableElement($('#btn-export'));
                $table.bootstrapTable('removeAll');
		        enteredSerials = [];
            }
        },
        error: function(data){
            setErrorMessage($lblInfo,data)
        },
        complete: function(){
            $body.removeClass("loading");
        }
    });
});
//@Del action---------------------------------------------------------------------------------------------------
var $btnDel = $('#btn-delete');
$btnDel.click(function () {
    //check before show
    var selectedItemArr = $table.bootstrapTable('getSelections');
    var selectedItem    = selectedItemArr[0];
    if(selectedItem == null){
        alert('Chọn 1 bản ghi!');
        return;
    }
    //
    clearActionInfo();
    var ids = $.map($table.bootstrapTable('getSelections'), function (row) {
        return row.columnId;
    });
    $table.bootstrapTable('remove', {
        field: 'columnId',
        values: ids
    });

});
//@Add action---------------------------------------------------------------------------------------------------
var $btnAdd = $('#btn-add');
$btnAdd.click(function () {
    isUpdate = false;
    $('#modal-add-result').text('');
    $('#modal-label-available-amount').text('');
    changeModelByType(true,null);
    //
    var isSerial = '0';
    var price = '0';
    var availableAmount = '0';
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val(),stockId:$('#cmb-stock').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            isSerial = data["isSerial"];
            price = data["outPrice"];
            availableAmount = data["amount"];
        }
    });
    //
    showElementBySerialType(isSerial);
    //
    $('#modal-lbl-price').text("Giá xuất");
    //
    showPriceDetail(price,$('#modal-inp-input-price'),$('#modal-label-inp-input-price'));

    if(isSerial === '1'){
        initSerialSuggestion();
    }
    //
    $('#modal-label-available-amount').text("Số lượng trong kho: "+ availableAmount);
    //
    disableElement($('#modal-cmb-cells'));
    //
    showModal($('#myModal'));
});

function showElementBySerialType(serialType) {
    if(serialType == '1'){
        $inpAmount.val('1');
        disableElement($inpAmount);
        enableElement($inpSerial);
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $inpSerial.focus();
        });
        $inpSerial.focus();
    }else{
        disableElement($inpSerial);
        enableElement($inpAmount);
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $inpAmount.focus();
        });
        $inpAmount.focus();
    }
}
//----------------------------------------------------------------------------------------------------------------
$cmbGoods.change(function () {
    var price;
    var isSerial;
    var availableAmount;
    //
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val(),stockId:$('#cmb-stock').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            currentGoods = data;
            price = currentGoods['outPrice'];
            isSerial = data["isSerial"];
            availableAmount = data["amount"];
        }
    });
    //
    showElementBySerialType(isSerial);
    showPriceDetail(price,$('#modal-inp-input-price'),$('#modal-label-inp-input-price'));
    //
    if(isSerial == '1'){
        $inpSerial.focus();
    }else{
        $inpAmount.focus();
    }
    if(isSerial == '1'){
        initSerialSuggestion();
    }
    $('#modal-label-available-amount').text("Số lượng trong kho: "+ availableAmount);
});
//----------------------------------------------------------------------------------------------------------------
//for enter press ->
$inpSerial.keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        if(isUpdate){
            updateExportGoods();
        }else{
            addExportGoods();
        }
    }
});

$inpAmount.keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        if(isUpdate){
            updateExportGoods();
        }else{
            addExportGoods();
        }
    }
});

var $btnAddModal = $('#modal-btn-add');
$btnAddModal.click(function () {
    addExportGoods();
});

function addExportGoods() {
    if(isAllFieldValid($("#form-add-goods")) == false){
        alert('Nội dung nhập chứa thông tin lỗi');
        return;
    }
    var isSerial;
    var goods_code;
    //
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val(),stockId:$('#cmb-stock').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            isSerial = data["isSerial"];
            goods_code = data["code"];
        }
    });

    if(isSerial == '1'){
        if(($inpAmount.val() != "1")){
            alert(goods_code +" là hàng quản lý serial, số lượng nhập phải là 1.");
            return;
        }
        //
        if(($inpSerial.val().trim().length === 0)){
            alert(goods_code +" là hàng quản lý serial, cần nhập thông tin serial.");
            return;
        }
    }
    //pre-process data
    preprocessInput($("#form-add-goods"));
    //get data
    var goodsCode = $cmbGoods.val();
    var goodsName = getGoodsNameInCombo($("#modal-cmb-goods option:selected").text());
    var amount = unFormatFloat($inpAmount.val());
    //
    if(!isValidAmount(amount)){
        alert("Số lượng xuất phải là số");
        return;
    }
    //
    var goodsStateValue = "Hỏng";
    var goodsState = "0";
    if($('#cmb-goods-state').prop('checked')){
        goodsStateValue = "Bình thường";
        goodsState = "1";
    }
    //
    var outputPriceValue = unFormatFloat($('#modal-inp-input-price').val());
    if(!isValidAmount(outputPriceValue)){
        alert("Giá xuất phải là số");
        return;
    }
    //
    var serial = escapeHtml($inpSerial.val());
    var cellCode = escapeHtml($('button[data-id=modal-cmb-cells]').attr('title'));
    if(cellCode.includes("selected")){
        cellCode = "";
    }
    //check whether serial is entered before
    var keySerial = goodsCode + goodsState + serial;
    if(isSerial == '1' && enteredSerials.indexOf(keySerial) > -1){
        setErrorMessage($('#modal-add-result').css('color','#F44336'),"Serial đã được nhập");
        $inpSerial.val('');
        $inpSerial.focus();
        return;
    }else{
        enteredSerials.push(keySerial);
    }
    //everything ok -> add to table
    var columnId = ~~(Math.random() * 100) * -1,
        rows = [];
    rows.push({
        goodsCode: goodsCode,
        goodsName: goodsName,
        goodsState: goodsState,
        goodsStateValue: goodsStateValue,
        serial:serial,
        amountValue: formatFloatType(amount),
        amount: amount,
        outputPriceValue: formatFloatType(outputPriceValue),
        outputPrice: outputPriceValue,
        cellCode: cellCode,
        columnId:columnId
    });
    //
    $table.bootstrapTable('append', rows);
    enableElement($('#btn-export'));
    //
    setInfoMessage($('#modal-add-result'),"Bổ sung thành công");
    $inpSerial.focus();
    $inpSerial.val('');
    $inpSerial.focus();
    //destroy - init serial suggestion
    $inpSerial.autocomplete( "destroy" );
    initSerialSuggestion();
}
//@Update action---------------------------------------------------------------------------------------------------
var $btnUpdateModal = $('#modal-btn-update');
$btnUpdateModal.click(function () {
    updateExportGoods();
});

function updateExportGoods() {
    //valid require serial
    var isSerial;
    //
    $.ajax({
        async:false,
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val(),stockId:$('#cmb-stock').val()},
        cache: false,
        contentType: false,
        type: 'GET',
        success: function(data){
            isSerial = data;
        }
    });
    if(isSerial === '1'){
        if(($inpAmount.val() !== "1")){
            alert($('#modal-cmb-goodsundefined').val() +" là hàng quản lý serial, số lượng xuất phải là 1.");
            return;
        }
    }
    //get data
    var goodsCode = $cmbGoods.val();
    var goodsName = getGoodsNameInCombo($("#modal-cmb-goods option:selected").text());
    var amount = unFormatFloat($inpAmount.val());
    //
    if(!isValidAmount(amount)){
        alert("Số lượng nhập phải là số");
        return;
    }
    //
    var goodsStateValue = "Hỏng";
    var goodsState = "0";
    if($('#cmb-goods-state').prop('checked')){
        goodsStateValue = "Bình thường";
        goodsState = "1";
    }
    var outputPriceValue = unFormatFloat($('#modal-inp-input-price').val());
    if(!isValidAmount(amount)){
        alert("Gía xuất phải là số");
        return;
    }
    //
    var serial = $inpSerial.val();
    var cellCode = $('#modal-inp-cell').val();


    $table.bootstrapTable('updateRow', {index:selectedIndex,row:{
        goodsCode: goodsCode,
        goodsName: goodsName,
        goodsState: goodsState,
        goodsStateValue: goodsStateValue,
        serial:serial,
        amountValue: formatFloatType(amount),
        amount: amount,
        outputPriceValue: formatFloatType(outputPriceValue),
        outputPrice: outputPriceValue,
        cellCode: cellCode
    }});
    //
    hideModal($('#myModal'));
}
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
    $("#export-action-info").text('');
    //
    hideModal($('#deleteConfirmModal'))
});
//-------------------------------------------------------------------------
function changeModelByType(isAdd,selectedItems) {
    if(isAdd){
        clearContent();
        //
        showAdd();
    }else{
        clearContent();
        $('select[name=modalCmbGoods]').val(selectedItems['goodsCode']);
        $('select[name=modalCmbGoods]').selectpicker('refresh');

        $inpAmount.val(selectedItems['amountValue']);
        if(selectedItems["goodsState"] =="1"){
            $('#cmb-goods-state').bootstrapToggle('on');
        }else{
            $('#cmb-goods-state').bootstrapToggle('off');
        }
        $('#modal-inp-input-price').val(selectedItems['outputPriceValue']);
        $inpSerial.val(selectedItems['serial']);
        $('#modal-inp-cell').val(selectedItems['cellCode']);
        //
        showUpdate();
        //
    }
}

function clearContent() {
    $('#cmb-goods-state').bootstrapToggle('on');
    $('#modal-inp-input-price').val('');
    $inpSerial.val('');
    $('#modal-inp-cell').val('');
    $inpAmount.val('');
}

$("#modal-inp-input-price").keyup(function() {
    showPriceDetail($("#modal-inp-input-price").val(),$('#modal-inp-input-price'),$('#modal-label-inp-input-price'));
});

$inpAmount.keyup(function() {
    var currentValue = unFormatFloat($inpAmount.val());
    var need2format = currentValue;
    if(!isValidPrice(currentValue)){
        currentValue = need2format.substr(0,need2format.indexOf(".") + 5);
    }
    if(!currentValue.includes(".")){
        $inpAmount.val(formatFloatType(currentValue));
    }else{
        $inpAmount.val(currentValue);
    }
});

function initSerialSuggestion() {
    //get current serial in stock
    var serial;
    var stockId = $('#cmb-stock').val();
    var goodsCode = $cmbGoods.val();
    var goodsState = '0';
    if($('#cmb-goods-state').prop('checked')){
        goodsState = "1";
    }
    $.ajax({
        type: 'GET',
        url: $('#btn-get-serial').val(),
        data:{stockId:stockId,goodsCode:goodsCode,goodsState:goodsState},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            serial = data;
        }
    });
    $inpSerial.autocomplete({
        source: serial
    });
    $inpSerial.autocomplete( "option", "appendTo", "#myModal" );
}