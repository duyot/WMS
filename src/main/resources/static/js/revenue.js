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
                        //clearInputContent();
                        //$("#modal-inp-amount").focus();
                    }else{
                        hideModal($addUpdateModal);
                    }
                },
                error:function(){
                    setErrorMessage($('#action-info'),'Lỗi hệ thống2');
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
            '<a class="edit-revenue row-function" href="javascript:void(0)" title="Cập nhật">',
            '<i class="fa fa-edit"></i>',
            '</a> ',
            '<a class="trans-export row-function" id="lbl-trans-export"  title="Xuất phiếu" href='+url+' >',
            '<i class="fa fa-file-word-o"></i>',
            '</a> ',
            '<a class="trans-detail row-function" href="javascript:void(0)" title="Chi tiết">',
            '<i class="fa fa-info-circle"></i>',
            '</a> '

        ].join('');
    }else{
        return [
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
        changeModelByType(2,row['id'],row['partnerId'],row['amount'],row['vat'],row['charge'],row['totalAmount'],row['description'],row['createdDate'],row['type'],$btn_update.val());
        showModal($addUpdateModal);
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-inp-amount').focus();
        });
    }
    ,
    'click .delete-revenue': function (e, value, row, index) {
        clearActionInfo();
        $("#lbl-del-info").text('Xóa doanh thu của: '+ decodeHtml(row['partnerName']));
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


    $.ajax({
        type: "GET",
        cache:false,
        data:{createdUser:createdUser,startDate:startDateVal,endDate:endDateVal,type:type, partnerId: partnerId},
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

function changeModelByType(type,id,partnerId,amount,vat,charge,totalAmount,description,createdDate,revenueType,actionVal) {
    $("#revenue-insert-update-form").attr("action",actionVal);

    if(revenueType ==1){
        disableElement($('#modal-inp-amount'));
        disableElement($('#modal-inp-createdDate'));
        disableElement($('#modal-cmb-partner'));
        $("#modal-inp-createdDate").val('dd/mm/yyyy', new Date());
    }
    if(type == 1){//add
        clearInputContents();
        //set default active-disable combo status
        vat = -1.0;
        $("#modal-type").val('add');
        $('select[name=partnerId]').val(-1);
        $('select[name=partnerId]').selectpicker('refresh');
        $('input[name=vat][value='+String(vat)+']').prop('checked', true);
        var fullDate = new Date();
        var twoDigitMonth = ((fullDate.getMonth().length+1) === 1)? (fullDate.getMonth()+1) :(fullDate.getMonth()+1);
        var currentDate = fullDate.getDate() + "/" + twoDigitMonth + "/" + fullDate.getFullYear();
        $("#modal-inp-createdDate").val(currentDate);
        showAdd();
    }else{//update
        $("#modal-inp-amount").val(decodeHtml(amount));
        vat = 5.0;
        if(vat != null && vat != ''){
            $('input[name=vat][value='+vat+']').prop('checked', true);
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
        showUpdate();
    }
}

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

//@Add action---------------------------------------------------------------------------------------------------
$btn_add = $('#btn-add');
$(function () {
    $btn_add.click(function () {
        clearActionInfo();
        changeModelByType(1,null,null,null,null,null,null,null,null,2,$btn_add.val());
        $addUpdateModal.modal('show');
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-inp-amount').focus();
        });
    });
});

function clearInputContents() {

    $("#modal-inp-amount").val('');
    $("#modal-inp-charge").val('');
    $("#modal-inp-total-amount").val('');
    $("#modal-inp-createdDate").val('');
    $("#modal-inp-description").val('');
    $("#modal-inp-id").val('');
}

//-------------------------------------------------------------------------