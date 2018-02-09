//GLOBAL VAR-------------------------------------------------------------------------------------------------------
$table = $('#tbl-export-goods');
$modalAddImportGoods = $('#myModal');
//input
$inpSerial =  $('#modal-inp-serial');
$inpAmount =  $('#modal-inp-amount');
$inpGoodsCode   =  $('#inp-goods-code');
$inpGoodsSerial =  $('#inp-serial');
$inpPrice  = $('#modal-inp-input-price');
//combobox
$cmbGoods       = $("#modal-cmb-goods");

//
var dataInit = [
];
var enteredSerials = [];
var selectedIndex = -1;
$body = $("body");
var isUpdate = false;
//-------------------------------------------------------------------------------------------------------
//#init table
$(function () {
    //
    $table.bootstrapTable({
        data: dataInit,
        columns:[
            {
                field: 'id',
                title: 'STT',
                formatter:'runningFormatter',
                align:'center',
                width:'40px'
            },
            {
                field: 'goodsCode',
                title: 'Mã hàng'
            },
            {
                field: 'goodsName',
                title: 'Tên hàng'
            },
            {
                field: 'goodsStateValue',
                title: 'Trạng thái',
                editable: {
                    type: 'select',
                    mode: 'inline',
                    showbuttons:false,
                    source: [
                        {value: 1, text: 'Bình thường'},
                        {value: 0, text: 'Hỏng'}
                    ]
                }
            },
            {   field: 'serial',
                title: 'Serial',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons:false
                }
            },
            {   field: 'amount',
                title: 'Số lượng',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons:false,
                    validate: function(value) {
                        var amountValue = unFormatFloat(value);
                        if(!isValidAmount(amountValue)){
                            return 'Số lượng xuất phải là số';
                        }

                    },
                    display: function(value) {
                        $(this).text(formatFloatType(value));
                    }
                }
            },
            {   field: 'inputPrice',
                title: 'Giá xuất',
                editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons:false,
                    validate: function(value) {
                        var amount = unFormatFloat(value);
                        if(!isValidAmount(amount)){
                            return 'Giá xuất phải là số';
                        }
                    },
                    display: function(value) {
                        $(this).text(formatFloatType(value));
                    }
                }
            },
            {
                title: 'Thao tác',
                formatter:'operateFormatter',
                events:'operateEvents',
                align:'center'
            }
        ]
    });
    $table.bootstrapTable('hideColumn', 'columnId');
    $table.bootstrapTable('hideColumn', 'goodsId');
    //$table.bootstrapTable('hideColumn', 'isSerial');
    //

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
//@Import---------------------------------------------------------------------
var btnExport = $('#btn-export');
btnExport.click(function () {
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
    $("#lbl-del-info").text('Xuất hàng ra khỏi kho?');
    //
    showModal($('#myConfirmModal'));
});
//@Import confirm---------------------------------------------------------------------
var btnExportConfirm = $('#modal-btn-del-ok');
btnExportConfirm.click(function () {
    //
    hideModal($('#myConfirmModal'));
    $body.addClass("loading");
    var stockIdValue        = $('#cmb-stock').val();
    var contractNumberValue = $('#inp-contract-number').val();
    var descriptionValue = $('#inp-contract-note').val();
    var stock_trans_info = {contractNumber:contractNumberValue,stockId:stockIdValue,description:descriptionValue};
    //
    var importData = JSON.stringify({lstGoods:$table.bootstrapTable('getData'),mjrStockTransDTO:stock_trans_info});
    //
    var $lblInfo = $("#export-action-info");
    //
    $.ajax({
        url: btnExport.val(),
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
            alert (resultMessage);
            //
            if(resultMessage == "SUCCESS_WITH_ERROR"){
                var totalRecords   = data['total'];
                //show modal upload file
                $("#modal-error-import-lbl-info").text('Xuất '+successRecords+'/'+totalRecords+' hàng thành công với mã phiếu: '+ stockTransId);
                $("#modal-link-download").attr("href",$("#modal-inp-stock-trans-id").val()+"/"+ stockTransId);
                showModal($("#myDownloadErrorImportModal"));
            }else if(resultMessage == "FAIL"){
                setInfoMessage($lblInfo,"Xuất kho không thành công!");
            }else{
                setInfoMessage($lblInfo,"Xuất "+successRecords+" hàng thành công với mã giao dịch: "+ stockTransId)
            }
            disableElement($('#btn-export'));
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
//@Add show modal---------------------------------------------------------------------------------------------------
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
    showPriceDetail(price,$inpPrice,$('#modal-label-inp-input-price'));
    //
    showModal($('#myModal'));
});

//#modal #add #confirm
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
    //
    var amount = unFormatFloat($inpAmount.val());
    if(!isValidAmount(amount)){
        alert("Số lượng nhập phải là số");
        return;
    }
    //
    var goodsStateValue = "0";
    var goodsState = "0";
    if($('#cmb-goods-state').prop('checked')){
        goodsStateValue = "1";
        goodsState = "1";
    }
    //
    var inputPriceValue = unFormatFloat($inpPrice.val());
    if(!isValidAmount(inputPriceValue)){
        alert("Giá nhập phải là số");
        return;
    }
    //
    var serial =   escapeHtml($inpSerial.val());

    //check whether serial is entered before
    var keySerial = goodsCode + goodsState + serial;
    if(isSerial == '1' && enteredSerials.indexOf(keySerial) > -1){
        setErrorMessage($('#modal-add-result'),"Serial đã được nhập");
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
    if(!isValidAmount(amount)){
        alert("Số lượng phải là số ");
        return;
    }
    var goodsStateValue = "Hỏng";
    var goodsState = "0";
    if($('#cmb-goods-state').prop('checked')){
        goodsStateValue = "Bình thường";
        goodsState = "1";
    }
    var inputPriceValue = unFormatFloat($inpPrice.val());
    if(!isValidAmount(inputPriceValue)){
        alert("Giá nhập phải là số dương");
        return;
    }
    var serial =   escapeHtml($inpSerial.val());

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
        inputPrice: inputPriceValue
    }});
    //
    hideModal($('#myModal'));
}

//OTHERS------------------------------------------------------------------------------------------------------------------
function changeModelByType(isAdd,selectedItems) {
    //set cell for combo box

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

        //
        $('.selectpicker').selectpicker('refresh');
        showUpdate();
        //
    }
}

$("#modal-inp-input-price").keyup(function() {
    showPriceDetail($("#modal-inp-input-price").val(),$inpPrice,$('#modal-label-inp-input-price'));
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
            inPrice = data['outPrice'];
            isSerial = data["isSerial"];
        }
    });
    //
    showElementBySerialType(isSerial);
    //
    //
    showPriceDetail(inPrice,$inpPrice,$('#modal-label-inp-input-price'));
});

function showElementBySerialType(serialType) {
    if(serialType === '1'){
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
    //$('select[name=modalCmbCells]').val("");
    $inpAmount.val('');
}

function loadSelectItems(select, items) {
    select.empty();
    $.each(items, function(i, item) {
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
    if(key == 13)  // the enter key code
    {
        //1. check goods serial: serial:focus to serial field, no_serial: append to table
        $.ajax({
            type: 'GET',
            url: $('#btn-check-serial').val(),
            data:{code:$inpGoodsCode.val()},
            cache: false,
            contentType: false,
            async:false,
            success: function(data){
                goodsItem = data;
            }
        });
        if(goodsItem == null){
            alert("Không có mặt hàng tương ứng");
            return;
        }
        //
        if(goodsItem["isSerial"] == '1'){
            $inpGoodsSerial.focus();
        }else{
            var columnId = ~~(Math.random() * 100) * -1,
                rows = [];
            rows.push({
                goodsCode: goodsItem['code'],
                goodsName:  goodsItem['name'],
                goodsState: '1',
                goodsStateValue: '1',
                serial:'',
                amount: '1',
                inputPrice: formatFloatType(goodsItem['outPrice']),
                columnId:columnId
            });
            //
            $table.bootstrapTable('append', rows);
            enableElement($('#btn-import'));

            setInfoMessage($('#modal-add-result'),"Bổ sung thành công");
            $inpSerial.val('');
            $inpSerial.focus();
        }
    }
});

$inpGoodsSerial.keypress(function (e) {
    var key = e.which;
    if(key == 13)  // the enter key code
    {
        $.ajax({
            type: 'GET',
            url: $('#btn-check-serial').val(),
            data:{code:$inpGoodsCode.val()},
            cache: false,
            contentType: false,
            async:false,
            success: function(data){
                goodsItem = data;
            }
        });
        if(goodsItem == null){
            alert("Không có mặt hàng tương ứng");
            return;
        }
        if(goodsItem["isSerial"] == '1' ){
            if  ($inpGoodsSerial.val().trim() =='') {
                alert("Mặt hàng bắt buộc phải nhập serial");
                $inpGoodsSerial.focus();
                return;
            }
            //Set gia tri de goi vao ham addImportGoods() khong bi tra ve fail
            $inpAmount.value = '1';
        }
        var columnId = ~~(Math.random() * 100) * -1,
            rows = [];
        rows.push({
            goodsCode: goodsItem['code'],
            goodsName:  goodsItem['name'],
            goodsState: '1',
            goodsStateValue: '1',
            serial:$inpGoodsSerial.val(),
            amount: '1',
            inputPrice: formatFloatType(goodsItem['outPrice']),
            columnId:columnId
        });
        //
        $table.bootstrapTable('append', rows);
        enableElement($('#btn-import'));

        setInfoMessage($('#modal-add-result'),"Bổ sung thành công");
        $inpSerial.val('');
        $inpSerial.focus();
    }
});

function getGoodsObject() {
    $.ajax({
        type: 'GET',
        url: $('#btn-check-serial').val(),
        data:{code:$inpGoodsCode.val()},
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            alert(data);
            return data;
        }
    });
}

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
    if(key === 13)  // the enter key code
    {
        alert ("1");
        if(isUpdate){
            updateGoods();
        }else{
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
        async:false,
        success: function(data){
            goodsCode = data;
        }
    });
    $('#inp-goods-code').autocomplete({
        source: goodsCode
    });
}
function loadPartnerSuggestion() {
    var partnerName = null;
    $.ajax({
        type: 'GET',
        url: $('#btn-get-partner').val(),
        cache: false,
        contentType: false,
        async:false,
        success: function(data){
            partnerName = data;
        }
    });
    $('#inp-partner-name').autocomplete({
        source: partnerName
    });
}

function operateFormatter(value, row, index) {
    return [
        '<a class="delete-goods row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}
//-----------------------------------------------------------------------------------------------------------------