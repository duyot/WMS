//GLOBAL VAR-------------------------------------------------------------------------------------------------------
$table = $('#tbl-import-goods');
$modalAddImportGoods = $('#myModal');
$inpSerial =  $('#modal-inp-serial');
$inpAmount =  $('#modal-inp-amount');
$inpPrice  = $('#modal-inp-input-price');
$cmbGoods  = $("#modal-cmb-goods")
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
    disableElement($('#btn-import'));
    //
});
//ROW FUNCTION------------------------------------------------------------------------------------------------------
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
        //
        var keySerial  = row['goodsCode'] + row["goodsState"]  + row['serial'];
        enteredSerials = enteredSerials.remove(keySerial);
    }
};
//@Upload---------------------------------------------------------------------
var btnUploadExcel = $('#btn-excel-import');
$(function () {
    btnUploadExcel.click(function () {
        //
        $("#import-action-info").text('');
        //
        var data = new FormData();
        jQuery.each(jQuery('#inp-file-import')[0].files, function(i, file) {
            data.append('file-'+i, file);
        });

        var fileName = $('#inp-file-import').val();
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
                enableElement($('#btn-import'));
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
//@Import---------------------------------------------------------------------
var btnImport = $('#btn-import');
btnImport.click(function () {
    //validate
    var contractNumber = $('#inp-contract-number').val();
    if(isContainXMLCharacter(contractNumber)){
        alert("Mã hợp đồng chứa kí tự đặc biệt");
        return;
    }
    var invoice = $('#inp-invoice').val();
    if(isContainXMLCharacter(invoice)){
        alert("Số invoce/Mã lô hàng chứa kí tự đặc biệt");
        return;
    }
    var import_goods = $table.bootstrapTable('getData');
    if(import_goods.length == 0){
        alert('Chưa có thông tin hàng nhập!');
        return;
    }
    var stockIdValue = $('#cmb-stock').val();
    if(stockIdValue == null){
        alert('Chưa có thông tin kho nhập!');
        return;
    }
    //
    $("#lbl-del-info").text('Nhập hàng lên hệ thống?');
    //
    showModal($('#myConfirmModal'));
});

var btnImportConfirm = $('#modal-btn-del-ok');
btnImportConfirm.click(function () {
    //
    hideModal($('#myConfirmModal'));
    $body.addClass("loading");
    var stockIdValue = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    var invoiceValue = $('#inp-invoice').val();
    var descriptionValue = $('#inp-contract-note').val();
    var stock_trans_info = {contractNumber:contractNumberValue,invoiceNumber:invoiceValue,stockId:stockIdValue,description:descriptionValue};
    //
    var importData = JSON.stringify({lstGoods:$table.bootstrapTable('getData'),mjrStockTransDTO:stock_trans_info});
    //
    var $lblInfo = $("#import-action-info");
    //
    $.ajax({
        url: btnImport.val(),
        data:importData,
        cache: false,
        contentType: "application/json",
        dataType: 'json',
        type: 'POST',
        success: function(data){
            //
            var resultMessage  = data['statusCode'];
            var stockTransId   = data['key'];
            var successRecords = data['success'];
            //
            if(resultMessage == "SUCCESS_WITH_ERROR"){
                var totalRecords   = data['total'];
                //show modal upload file
                $("#modal-error-import-lbl-info").text('Nhập '+successRecords+'/'+totalRecords+' hàng thành công với mã phiếu: '+ stockTransId);
                $("#modal-link-download").attr("href",$("#modal-inp-stock-trans-id").val()+"/"+ stockTransId);
                showModal($("#myDownloadErrorImportModal"));
            }else if(resultMessage == "FAIL"){
                setInfoMessage($lblInfo,"Nhập kho không thành công!");
            }else{
                setInfoMessage($lblInfo,"Nhập "+successRecords+" hàng thành công với mã giao dịch: "+ stockTransId)
            }
            disableElement($('#btn-import'));
            $table.bootstrapTable('removeAll');
	        enteredSerials = [];
        },
        error: function(data){
            setErrorMessage($lblInfo,JSON.stringify(data))
        },
        complete: function(){
            $body.removeClass("loading");
        }
    });
});

//@Add action---------------------------------------------------------------------------------------------------
$('#btn-add').click(function () {
    //
    isUpdate = false;
    $('#modal-add-result').text('');
    changeModelByType(true,null);
    //
    var isSerial = "";
    var price    = "";
    //
    $.ajax({
        type: "GET",
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val()},
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        cache: false,
        dataType: 'json',
        async:false,
        success: function(data){
            isSerial = data["isSerial"];
            price = data["inPrice"];
        }
    });
    //
    showElementBySerialType(isSerial);
    $inpPrice.val(formatFloatType(price));
    $('#modal-label-inp-input-price').text(DOCSO.doc(price));
    //
    showModal($('#myModal'));
});

$('#modal-btn-add').click(function () {
    addImportGoods();
});

function addImportGoods() {
    //valid require serial
    var isSerial = "";
    var goods_code = "";
    //
    $.ajax({
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val()},
        cache: false,
        contentType: false,
        async:false,
        type: 'GET',
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
    //
    preprocessInput($("#form-add-goods"));
    //get data
    var goodsCode = $cmbGoods.val();
    var goodsName = getGoodsNameInCombo($("#modal-cmb-goods option:selected").text());
    var amount = unFormatFloat($inpAmount.val());
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
    var inputPriceValue = unFormatFloat($inpPrice.val());
    if(!isInteger(inputPriceValue)){
        alert("Giá nhập phải là số dương");
        return;
    }
    var serial =   escapeHtml($inpSerial.val());
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
        inputPriceValue: formatFloatType(inputPriceValue),
        inputPrice: inputPriceValue,
        cellCode: cellCode,
        columnId:columnId
    });
    //
    $table.bootstrapTable('append', rows);
    enableElement($('#btn-import'));

    setInfoMessage($('#modal-add-result'),"Bổ sung thành công");
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
        data:{code:$cmbGoods.val()},
        cache: false,
        contentType: false,
        async:false,
        type: 'GET',
        success: function(data){
            isSerial = data["isSerial"];
            goods_code = data["code"];
        }
    });
    if(isSerial === '1'){
        if(($inpAmount.val() !== "1")){
            alert(goods_code +" là hàng quản lý serial, số lượng nhập phải là 1.");
            return;
        }
        //
        if(($inpSerial.val().trim().length === 0)){
            alert(goods_code +" là hàng quản lý serial, cần nhập thông tin serial.");
            return;
        }
    }
    //
    preprocessInput($("#form-add-goods"));
    //get data
    var goodsCode = $cmbGoods.val();
    var goodsName = getGoodsNameInCombo($("#modal-cmb-goods option:selected").text());
    var amount = unFormatFloat($inpAmount.val());
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
    var inputPriceValue = unFormatFloat($inpPrice.val());
    if(!isInteger(inputPriceValue)){
        alert("Giá nhập phải là số dương");
        return;
    }
    var serial =   escapeHtml($inpSerial.val());
    var cellCode = escapeHtml($('button[data-id=modal-cmb-cells]').attr('title'));
    if(cellCode.includes("selected")){
        cellCode = "";
    }
    //everything all right -> update row
    $table.bootstrapTable('updateRow', {index:selectedIndex,row:{
        goodsCode: goodsCode,
        goodsName: goodsName,
        goodsState: goodsState,
        goodsStateValue: goodsStateValue,
        serial:serial,
        amountValue: formatFloatType(amount),
        amount: amount,
        inputPriceValue: formatFloatType(inputPriceValue),
        inputPrice: inputPriceValue,
        cellCode: cellCode
    }});
    //
    hideModal($('#myModal'));
}

//OTHERS------------------------------------------------------------------------------------------------------------------
function changeModelByType(isAdd,selectedItems) {
    var $cmbCell = $('select[name=modalCmbCells]');
    //set cell for combo box
    $.ajax({
        type: 'GET',
        url: $('#btn-get-cells').val(),
        data:{stockId:$('#cmb-stock').val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            if(data.length == 0){
                disableElement($cmbCell);
            }else{
                enableElement($cmbCell);
            }
            loadSelectItems($cmbCell,data);
        }
    });
    //
    if(isAdd){
        clearContent();
        //
        showAdd();
    }else{
        clearContent();
        $('select[name=modalCmbGoods]').val(selectedItems['goodsCode']);
        $('select[name=modalCmbGoods]').selectpicker('refresh');
        //
        $inpAmount.val(selectedItems['amountValue']);
        if(selectedItems["goodsState"] =="1"){
            $('#cmb-goods-state').bootstrapToggle('on');
        }else{
            $('#cmb-goods-state').bootstrapToggle('off');
        }
        $inpPrice.val(selectedItems['inputPriceValue']);
        $inpSerial.val(selectedItems['serial']);
        $cmbCell.val(selectedItems['cellCode']);
        //
        $('.selectpicker').selectpicker('refresh');
        showUpdate();
        //
    }
}

$("#modal-inp-input-price").keyup(function() {
    var currentValue = unFormatFloat($("#modal-inp-input-price").val());
    currentValue = currentValue.replace(/\./g,"");
    $('#modal-label-inp-input-price').text(DOCSO.doc(currentValue));
    $inpPrice.val(formatFloatType(currentValue));
});

$inpAmount.keyup(function() {
    var currentValue = unFormatFloat($inpAmount.val());
    $inpAmount.val(formatFloatType(currentValue));
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
    var inPrice  = "";
    var isSerial = "";
    //
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$cmbGoods.val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            inPrice = data['inPrice'];
            isSerial = data["isSerial"];
        }
    });
    //
    showElementBySerialType(isSerial);
    $inpPrice.val(formatFloatType(inPrice));
    $('#modal-label-inp-input-price').text(DOCSO.doc(inPrice));
});

function showElementBySerialType(serialType) {
    if(serialType == '1'){
        $inpAmount.val('1');
        disableElement($inpAmount);
        enableElement($inpSerial);
        //
        $modalAddImportGoods.on('shown.bs.modal', function () {
            $inpSerial.focus();
        });
        $inpSerial.focus();
    }else{
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
        $('select[name=modalCmbCells]').val("");
        $inpAmount.val('');
}

function loadSelectItems(select, items) {
    select.empty();
    $.each(items, function(i, item) {
        var opt = document.createElement('option');
        opt.value = item.code;
        opt.innerHTML = item.code;
        select.append(opt);
    });
    select.selectpicker('refresh');
}
//for enter press ->
$inpSerial.keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        if(isUpdate){
            updateGoods();
        }else{
            addImportGoods();
        }
    }
});

$inpAmount.keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        if(isUpdate){
            updateGoods();
        }else{
            addImportGoods();
        }
    }
});
//-----------------------------------------------------------------------------------------------------------------