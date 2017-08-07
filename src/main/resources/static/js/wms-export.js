$modalAddImportGoods = $('#myModal');
$table = $('#tbl-export-goods');
$inpSerial =  $('#modal-inp-serial');
$inpAmount = $("#modal-inp-amount");
var dataInit = [
];
var selectedIndex = -1;
//----------------------------------------------------------------------------------------------------------
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
//
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
        changeModelByType(false,row);
        showModal($('#myModal'))
    },

    'click .delete-goods': function (e, value, row, index) {
        clearActionInfo();
        $table.bootstrapTable('remove', {
            field: 'columnId',
            values: [row["columnId"]]
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
        $('#cmb-stockundefined').focus();
        return;
    }
    //
    $("#lbl-del-info").text('Xuất hàng?');
    //
    showModal($('#myConfirmModal'));
});

var btnExportConfirm = $('#modal-btn-del-ok');
$body = $("body");
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
            if(resultMessage == "FAIL"){
                var statusName   = data['statusName'];
                //show modal upload file
                setErrorMessage($lblInfo,"Xuất hàng không thành công: "+ converExportMessage(statusName,key));
            }else{
                var totalSuccess = data['success'];
                setInfoMessage($lblInfo,"Xuất "+ totalSuccess+" hàng thành công");
                disableElement($('#btn-export'));
                $table.bootstrapTable('removeAll');
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
    $('#modal-add-result').text('');
    changeModelByType(true,null);
    //
    var isSerial = '0';
    var price;
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$('#modal-cmb-goods').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            isSerial = data["isSerial"];
            price = data["outPrice"];
        }
    });
    //
    showElementBySerialType(isSerial);
    //
    $('#modal-lbl-price').text("Giá xuất");
    $('#modal-inp-input-price').val(formatFloatType(price));
    $('#modal-label-inp-input-price').text(DOCSO.doc(price));
    //
    initSerialSuggestion();
    //
    disableElement($('#modal-cmb-cells'));
    //
    showModal($('#myModal'));
});

function showElementBySerialType(serialType) {
    if(serialType == '1'){
        $('#modal-inp-amount').val('1');
        disableElement($('#modal-inp-amount'));
        enableElement($inpSerial);
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $inpSerial.focus();
        });
        $inpSerial.focus();
    }else{
        disableElement($inpSerial);
        enableElement($('#modal-inp-amount'));
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $('#modal-inp-amount').focus();
        });
        $('#modal-inp-amount').focus();
    }
}
//----------------------------------------------------------------------------------------------------------------
$("#modal-cmb-goods").change(function () {
    var price;
    var isSerial;
    //
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$('#modal-cmb-goods').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            currentGoods = data;
            price = currentGoods['outPrice'];
            isSerial = data["isSerial"];
        }
    });
    //
    showElementBySerialType(isSerial);
    $('#modal-inp-input-price').val(formatFloatType(price));
    $('#modal-label-inp-input-price').text(DOCSO.doc(price));
    //
    if(isSerial == '1'){
        $inpSerial.focus();
    }else{
        $('#modal-inp-amount').focus();
    }
    //
    initSerialSuggestion();
});
//----------------------------------------------------------------------------------------------------------------
//for enter press ->
$inpSerial.keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        addExportGoods();
    }
});

$('#modal-inp-amount').keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        addExportGoods();
    }
});

var $btnAddModal = $('#modal-btn-add');
$btnAddModal.click(function () {
    addExportGoods();
});

function addExportGoods() {
    //
    if(isAllFieldValid($("#form-add-goods")) == false){
        alert('Nội dung nhập chứa thông tin lỗi');
        return;
    }
    //
    var isSerial;
    var goods_code;
    //
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$('#modal-cmb-goods').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            isSerial = data["isSerial"];
            goods_code = data["code"];
        },
    });

    if(isSerial == '1'){
        if(($('#modal-inp-amount').val() != "1")){
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
    var goodsCode = $('#modal-cmb-goods').val();
    var goodsName = $("#modal-cmb-goods option:selected").text();
    var amount = unFormatFloat($('#modal-inp-amount').val());
    if(!isInteger(amount)){
        alert("Số lượng phải là số dương");
        return;
    }
    var goodsStateValue = "Hỏng";
    var goodsState = "0";
    if($('#cmb-goods-state').prop('checked')){
        goodsStateValue = "Bình thường";
        goodsState = "1";
    }
    var outputPriceValue = unFormatFloat($('#modal-inp-input-price').val());
    if(!isInteger(outputPriceValue)){
        alert("Giá xuất phải là số dương");
        return;
    }
    var serial = escapeHtml($inpSerial.val());
    var cellCode = escapeHtml($('button[data-id=modal-cmb-cells]').attr('title'));
    if(cellCode.includes("selected")){
        cellCode = "";
    }
    //add to table
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
    $('#modal-add-result').text("Bổ sung thành công");
    $('#modal-add-result').fadeIn();
    setTimeout(function() {
        $('#modal-add-result').fadeOut('fast');
    }, 3000);
    $inpSerial.focus();
    $inpSerial.val('');
    $inpSerial.focus();
    //destroy - init serial suggestion
    $( "#modal-inp-serial" ).autocomplete( "destroy" );
    initSerialSuggestion();
}
//@Update action---------------------------------------------------------------------------------------------------
var $btnUpdate = $('#btn-update');
$btnUpdate.click(function () {
    //check before show
    selectedItemArr = $table.bootstrapTable('getSelections');
    selectedItem    = selectedItemArr[0];
    if(selectedItem == null){
        alert('Chọn 1 bản ghi!');
        return;
    }

    if(selectedIndex == -1){
        alert('Chọn 1 bản ghi!');
        return;
    }

    changeModelByType(false,selectedItem);
    showModal($('#myModal'))
});

var $btnUpdateModal = $('#modal-btn-update');
$btnUpdateModal.click(function () {
    //valid require serial
    var isSerial;
    //
    $.ajax({
        async:false,
        url: $('#btn-check-serial').val(),
        data:{code:$('#modal-cmb-goods').val()},
        cache: false,
        contentType: false,
        type: 'GET',
        success: function(data){
            isSerial = data;
        },
    });
    if(isSerial == '1'){
        if(($('#modal-inp-amount').val() != "1")){
            alert($('#modal-cmb-goodsundefined').val() +" là hàng quản lý serial, số lượng xuất phải là 1.");
            return;
        }
    }
    //get data
    var goodsCode = $('#modal-cmb-goods').val();
    var goodsName = $("#modal-cmb-goods option:selected").text();
    var amount = $('#modal-inp-amount').val();
    var goodsStateValue = "Hỏng";
    var goodsState = "0";
    if($('#cmb-goods-state').prop('checked')){
        goodsStateValue = "Bình thường";
        goodsState = "1";
    }
    var outputPriceValue = $('#modal-inp-input-price').val();
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

        $('#modal-inp-amount').val(selectedItems['amountValue']);
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
    $('#modal-inp-amount').val('');
}

$("#modal-inp-input-price").keyup(function() {
    var currentValue = unFormatFloat($("#modal-inp-input-price").val());
    $('#modal-label-inp-input-price').text(DOCSO.doc(currentValue));
    $('#modal-inp-input-price').val(formatFloatType(currentValue));
});

$inpAmount.keyup(function() {
    var currentValue = unFormatFloat($inpAmount.val());
    $('#modal-inp-amount').val(formatFloatType(currentValue));
});

function initSerialSuggestion() {
    //get current serial in stock
    var serial;
    var stockId = $('#cmb-stock').val();
    var goodsCode = $('#modal-cmb-goods').val();
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
    $( "#modal-inp-serial" ).autocomplete({
        source: serial
    });
    $("#modal-inp-serial").autocomplete( "option", "appendTo", "#myModal" );
}