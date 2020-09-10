//---------------------------------------------------------------------
$addUpdateModal = $('#myModal');

$table = $('#tbl-stock-info');
var $btnDelConfirmed = $('#modal-btn-del-ok');
$tableGoodsDetailImport = $('#tbl-trans-goods-detail-import');
$tableGoodsDetailExport = $('#tbl-trans-goods-detail-export');
var validator;
var $btn_add = $('#btn-add')
$btn_update = $('#btn-update');
$btnDel = $('#btn-delete');

var dataInit = [
];
//

$(document).ready(function () {
    validator = $("#revenue-insert-update-form").validate({
        ignore: ":hidden",
        rules: {
            amount: {
                required: true,
                normalizer: function( value ) {
                    return $.trim( value );
                },
                maxlength: 15
            },
            charge: {
                required: false,
                normalizer: function( value ) {
                    return $.trim( value );
                },
                maxlength: 15
            }
        },
        submitHandler: function (form) {
            preprocessInput($("#revenue-insert-update-form"));
            //
            if (isContainSpecialCharacter($('#modal-inp-description').val())) {
                alert("Trường Ghi chú chứa kí tự đặc biệt. Vui lòng loại bỏ kí tự (lớn hơn, nhỏ hơn)");
                return;
            }
            
            $.ajax({
                type: "POST",
                url: $("#revenue-insert-update-form").attr('action'),
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
                    search(false);
                    if($("#modal-type").val().includes("add")){
                        clearInputContent();
                        $("#modal-inp-amount").focus();
                    }else{
                        hideModal($addUpdateModal);
                    }
                },
                error:function(){
                    setErrorMessage($('#action-info'),'Lỗi hệ thống');
                }
            });
            return false; // required to block normal submit since you used ajax
        }
    });
});

function operateFormatter(value, row, index) {
    var createdDate = row["createdDate"].substring(0,10);
    var transId   = row["stockTransId"];
    var type   = row["type"];

    var url = $('#btn-export-info').val() + "?transId="+ transId;
    $('.trans-export').attr('href',url);
    if(type==1){
        return [
            '<a class="edit-payment row-function" href="javascript:void(0)" title="Thanh toán">',
            '<i class="fa fa-money"></i>',
            '</a> ',
            '<a class="edit-revenue row-function" href="javascript:void(0)" title="Cập nhật">',
            '<i class="fa fa-edit"></i>',
            '</a> ',
            '<a class="trans-export row-function" id="lbl-trans-export"  title="Xuất phiếu" href='+url+' >',
            '<i class="fa fa-file-word-o"></i>',
            '</a> '/*,
            '<a class="trans-detail row-function" href="javascript:void(0)" title="Chi tiết">',
            '<i class="fa fa-info-circle"></i>',
            '</a> '*/

        ].join('');
    }else{
        return [
            '<a class="edit-payment row-function" href="javascript:void(0)" title="Thanh toán">',
            '<i class="fa fa-money"></i>',
            '</a> ',
            '<a class="edit-revenue row-function" href="javascript:void(0)" title="Cập nhật">',
            '<i class="fa fa-edit"></i>',
            '</a> ',
            '<a class="delete-revenue row-function" href="javascript:void(0)" title="Xóa">',
            '<i class="fa fa-trash"></i>',
            '</a> '
        ].join('');
    }


}
//
$(function () {
    //
    $table.bootstrapTable({
        data: dataInit
    });

    $tableGoodsDetailImport.bootstrapTable({
        data: dataInit
    });
    $tableGoodsDetailExport.bootstrapTable({
        data: dataInit
    });
    //
    initDateRangeSelect();
    initPropertyDateSelect();

});
//
window.operateEvents = {
    'click .trans-detail': function (e, value, row, index) {
        //
        NProgress.start();
        //
        var transId   = row["stockTransId"];
        var transType = '2';

        $("#export-lbl-trans-code-val").text(row["stockTransCode"]);
        $("#export-lbl-receiver-val").text(row["partnerName"]);
        $("#export-lbl-note-val").text(row["description"]);
        $("#export-lbl-trans-type-val").text("Xuất kho");
        $("#export-lbl-create-user-val").text(row["createdUser"]);
        $("#export-lbl-export-time-val").text(row["createdDate"]);


        var $downloadLinkModalImport = $('#link-download-goods-detail');
        var $downloadLinkModalExport = $('#link-download-goods-detail-export');
        var originalLink = $downloadLinkModalImport.attr("href");
        if(originalLink.includes("?")){
            originalLink = trimAtChacter(originalLink,"?");
        }
        $downloadLinkModalImport.attr("href",originalLink +"?transId="+transId);
        $downloadLinkModalExport.attr("href",originalLink +"?transId="+transId);
        //
        $.ajax({
            type: "GET",
            cache:false,
            data:{transId:transId},
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            url: $('#btn-view-info').val(),
            dataType: 'json',
            timeout: 600000,
            success: function (data) {
                if(transType ==1){
                    $tableGoodsDetailImport.bootstrapTable('load', data);
                    showModal($('#goods-detail-modal-import'));
                }else{
                    $tableGoodsDetailExport.bootstrapTable('load', data);
                    showModal($('#goods-detail-modal-export'));
                }
            },
            complete:function () {
                NProgress.done();
            }
        });
    }
    ,
    'click .edit-revenue': function (e, value, row, index) {
        validator.resetForm();
        $("#revenue-insert-update-form").find(".error").removeClass("error");
        //
        clearActionInfo();
        changeModelByType(2,row['id'],row['partnerId'],row['amount'],row['vat'],row['charge'],row['totalAmount'],row['description'],row['createdDate'],row['type'],row['paymentAmount'],row['paymentStatus'],row['paymentDate'],row['paymentDescription'],$btn_update.val());
        showModal($addUpdateModal);
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-inp-amount').focus();
        });
    },
    'click .edit-payment': function (e, value, row, index) {
        validator.resetForm();
        $("#revenue-insert-update-form").find(".error").removeClass("error");
        //
        clearActionInfo();
        changeModelByType(3,row['id'],row['partnerId'],row['amount'],row['vat'],row['charge'],row['totalAmount'],row['description'],row['createdDate'],row['type'],row['paymentAmount'],row['paymentStatus'],row['paymentDate'],row['paymentDescription'],$btn_update.val());
        showModal($addUpdateModal);
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-inp-paymentAmount').focus();
        });
    },
    'click .delete-revenue': function (e, value, row, index) {
        clearActionInfo();
        $("#lbl-del-info").text('Xóa doanh thu '+ decodeHtml(row['totalAmount']) +' của '+ decodeHtml(row['partnerName']));
        $('#myConfirmModal').modal('show');
        $selectedItemId = row['id'];
    }
};


$btnDelConfirmed.click(function () {
    $.ajax({
        type: "POST",
        cache:false,
        data:{id:$selectedItemId},
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: $btnDel.val(),
        dataType: 'text',
        timeout: 600000,
        success: function (data) {
            resultArr = data.split('|');
            resultCode = resultArr[0];
            resultName = resultArr[1];
            if(resultCode == 1){
                setInfoMessage($('#action-info'),resultName);
            }else{
                setErrorMessage($('#action-info'),resultName);
            }
            search(false);

        },
        error:function(){
            setErrorMessage($('#action-info'),'Lỗi hệ thống');
        },
    });
    //
    $('#myConfirmModal').modal('hide');
    //
});

var $btnSearch = $('#btn-search-serial');
$btnSearch.click(function () {
    search(true);
});

function search(isClear){
    //
    NProgress.start();
    //
    if(isClear){
        $("#action-info").text('');
    }
    //search info stock_id/goods_group/goods/serial
    var createdUser = $('#cmb-user').val();
    var partnerId = $('#cmb-partner').val();
    var drp = $('#create-date-range').data('daterangepicker');
    var startDateVal = drp.startDate.format("DD/MM/YYYY");
    var endDateVal   = drp.endDate.format("DD/MM/YYYY");
    var type = $("input[name='cmb-revenue-type']:checked").val();
    var paymentStatus = $("input[name='cmb-payment-status']:checked").val();


    var $downloadLink = $('#link-download-sum-revenue');
    var originalLink = $downloadLink.attr("href");
    if(originalLink.includes("?")){
        originalLink = trimAtChacter(originalLink,"?");
    }
    $downloadLink.attr("href",originalLink +"?partnerId="+partnerId+"&startDate="+startDateVal+"&endDate="+endDateVal);


    $.ajax({
        type: "GET",
        cache:false,
        data:{createdUser:createdUser,startDate:startDateVal,endDate:endDateVal,type:type, partnerId: partnerId, paymentStatus: paymentStatus},
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: $btnSearch.val(),
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
            $table.bootstrapTable('load', data);
        },
        complete:function () {
            NProgress.done();
        }
    });
}


function loadSelectItems(select, items) {
    select.empty();
    var opt = document.createElement('option');
    opt.value = "-1";
    opt.innerHTML = "Tất cả";
    select.append(opt);
    $.each(items, function (i, item) {
        opt = document.createElement('option');
        opt.value = item.id;
        opt.innerHTML = item.name;
        select.append(opt);
    });
    select.selectpicker('refresh');
}

function changeModelByType(type,id,partnerId,amount,vat,charge,totalAmount,description,createdDate,revenueType,paymentAmount, paymentStatus, paymentDate, paymentDescription,actionVal) {
    $("#revenue-insert-update-form").attr("action",actionVal);

    if(revenueType ==1){
        //disableElement($('#modal-inp-amount'));
        //disableElement($('#modal-inp-createdDate'));
        //disableElement($('#modal-cmb-partner'));
        $('#modal-inp-amount').prop('readonly', true);
        $('#modal-inp-createdDate').prop('readonly', true);
    }else{
        //enableElement($('#modal-inp-amount'));
        //enableElement($('#modal-inp-createdDate'));
        //enableElement($('#modal-cmb-partner'));
        $('#modal-inp-amount').prop('readonly', false);
        $("#modal-inp-createdDate").val('dd/mm/yyyy', new Date());
        $('#modal-inp-createdDate').prop('readonly', false);

    }
    if(type == 1){//add
        showRevenueElement();
        enableElement($('select[name=partnerId]'));
        clearInputContent();
        //set default active-disable combo status
        vat = '-1.0';
        $("#modal-type").val('add');
        $('select[name=partnerId]').val(-1);
        $('select[name=partnerId]').selectpicker('refresh');
        $("input[name=vat][value='"+vat+"']").prop('checked', true);
        var fullDate = new Date();
        var twoDigitMonth = ((fullDate.getMonth().length+1) === 1)? (fullDate.getMonth()+1) :(fullDate.getMonth()+1);
        var currentDate = fullDate.getDate() + "/" + twoDigitMonth + "/" + fullDate.getFullYear();
        $("#modal-inp-createdDate").val(currentDate);
        $("#paymentAction").val('');
        showAdd();
    }else if (type ==2){//update
        showRevenueElement();
        enableElement($('select[name=partnerId]'));
        $("#modal-inp-amount").val(decodeHtml(amount));
        if(vat != null && vat != ''){
            $("input[name=vat][value='"+vat+"']").prop('checked', true);
        }
        $("#modal-inp-charge").val(decodeHtml(charge));
        $("#modal-inp-total-amount").val(decodeHtml(totalAmount));
        $("#modal-inp-createdDate").val(decodeHtml(createdDate));
        $("#modal-inp-description").val(decodeHtml(description));
        $("#modal-inp-id").val(id);
        $("#modal-inp-type").val(revenueType);
        $('select[name=partnerId]').val(partnerId);
        $('select[name=partnerId]').selectpicker('refresh');
        $("#modal-type").val('update');
        $("#paymentAction").val('');
        showUpdate();
    }else{//payment
        hideRevenueElement();
        $("#modal-inp-id").val(id);
        $("#modal-inp-total-amount").val(decodeHtml(totalAmount));
        $('select[name=partnerId]').val(partnerId);
        $('select[name=partnerId]').selectpicker('refresh');
        disableElement($('select[name=partnerId]'));
        disableElement($("#modal-inp-total-amount"));

        if(paymentStatus != null && paymentStatus != ''){
            $("input[name=paymentStatus][value='"+paymentStatus+"']").prop('checked', true);
        }
        $("#modal-inp-paymentAmount").val(decodeHtml(paymentAmount));

        var fullDate = new Date();
        var twoDigitMonth = ((fullDate.getMonth().length+1) === 1)? (fullDate.getMonth()+1) :(fullDate.getMonth()+1);
        var currentDate = fullDate.getDate() + "/" + twoDigitMonth + "/" + fullDate.getFullYear();
        if(paymentDate == ''){
            paymentDate = currentDate;
        }
        $("#modal-inp-paymentDate").val(decodeHtml(paymentDate));
        $("#modal-inp-paymentDescription").val(decodeHtml(paymentDescription));
        $("#modal-type").val('update');
        $("#paymentAction").val('true');

        showUpdate();
        $("#myModalLabel").text('Cập nhật thanh toán');
    }
}

$('.input-number').keyup(function () {
    this.value = this.value.replace(/[^0-9\.]/g,'');
});

function calTotalAmount() {
    var vat = Number($('input[name=vat]:checked').val());
    if(vat == -1.0){
        vat = 0;
    }
    var amount = Number($("#modal-inp-amount").val().replace(/,/g, ""));
    var charge = Number($("#modal-inp-charge").val().replace(/,/g, ""));
    $("#modal-inp-total-amount").val(formatFloatType(amount*vat/100 + amount + charge));
    $("#modal-inp-amount").val(formatFloatType(amount));
    $("#modal-inp-charge").val(formatFloatType(charge));

}

function calPaymentAmount() {
    var paymentStatus = Number($('input[name=paymentStatus]:checked').val());
    if(paymentStatus == 3){
        $("#modal-inp-paymentAmount").val($("#modal-inp-total-amount").val());
    }else if(paymentStatus == 2 && Number($("#modal-inp-paymentAmount").val().replace(/,/g, "")) < Number($("#modal-inp-total-amount").val().replace(/,/g, ""))){
        $("#modal-inp-paymentAmount").val(formatFloatType(Number($("#modal-inp-paymentAmount").val().replace(/,/g, ""))));
    }else{
        $("#modal-inp-paymentAmount").val('');
    }
}

function changePaymentAmount() {
    if(Number($("#modal-inp-paymentAmount").val().replace(/,/g, "")) >= Number($("#modal-inp-total-amount").val().replace(/,/g, ""))){
        $("input[name=paymentStatus][value='3']").prop('checked', true);
        $("#modal-inp-paymentAmount").val($("#modal-inp-total-amount").val());
    }else{
        if($("#modal-inp-paymentAmount").val() != ''){
            $("input[name=paymentStatus][value='2']").prop('checked', true);
            $("#modal-inp-paymentAmount").val(formatFloatType(Number($("#modal-inp-paymentAmount").val().replace(/,/g, ""))));
        }else{
            $("input[name=paymentStatus][value='1']").prop('checked', true);
        }
    }
}

//@Add action---------------------------------------------------------------------------------------------------
$btn_add = $('#btn-add');
$(function () {
    $btn_add.click(function () {
        clearActionInfo();
        changeModelByType(1,null,null,null,null,null,null,null,null,2,null, null, null, null, $btn_add.val());
        $addUpdateModal.modal('show');
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-inp-amount').focus();
        });
    });
});

function clearInputContent() {
    $("#modal-inp-amount").val('');
    $("#modal-inp-charge").val('');
    $("#modal-inp-total-amount").val('');
    $("#modal-inp-createdDate").val('');
    $("#modal-inp-description").val('');
    $("#modal-inp-id").val('');
    $("#modal-inp-paymentAmount").val('');
    $("#modal-inp-paymentDescription").val('');
    $("#modal-inp-paymentDate").val('');
    $("#modal-inp-paymentStatus").val('1');
    $('select[name=partnerId]').val('-1');
    $('select[name=partnerId]').selectpicker('refresh');
}
function hideRevenueElement() {
    //Hide revenue info
    $("#label-modal-inp-amount").hide();
    $("#row-modal-inp-amount").hide();

    $("#label-modal-inp-vat").hide();
    $("#row-modal-inp-vat").hide();

    $("#label-modal-inp-charge").hide();
    $("#row-modal-inp-charge").hide();

    $("#label-modal-inp-createdDate").hide();
    $("#row-modal-inp-createdDate").hide();

    $("#label-modal-inp-description").hide();
    $("#row-modal-inp-description").hide();

    //show payment info
    $("#label-modal-inp-paymentStatus").show();
    $("#row-modal-inp-paymentStatus").show();

    $("#label-modal-inp-paymentAmount").show();
    $("#row-modal-inp-paymentAmount").show();

    $("#label-modal-inp-paymentDate").show();
    $("#row-modal-inp-paymentDate").show();

    $("#label-modal-inp-paymentDescription").show();
    $("#row-modal-inp-paymentDescription").show();

}

function showRevenueElement() {
    //show revenue info
    $("#label-modal-inp-amount").show();
    $("#row-modal-inp-amount").show();

    $("#label-modal-inp-vat").show();
    $("#row-modal-inp-vat").show();

    $("#label-modal-inp-charge").show();
    $("#row-modal-inp-charge").show();

    $("#label-modal-inp-createdDate").show();
    $("#row-modal-inp-createdDate").show();

    $("#label-modal-inp-description").show();
    $("#row-modal-inp-description").show();

    //hide payment info
    $("#label-modal-inp-paymentStatus").hide();
    $("#row-modal-inp-paymentStatus").hide();

    $("#label-modal-inp-paymentAmount").hide();
    $("#row-modal-inp-paymentAmount").hide();

    $("#label-modal-inp-paymentDate").hide();
    $("#row-modal-inp-paymentDate").hide();

    $("#label-modal-inp-paymentDescription").hide();
    $("#row-modal-inp-paymentDescription").hide();
}

//-------------------------------------------------------------------------