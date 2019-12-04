//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateModal = $('#myModal');
$btnDelConfirmed = $('#modal-btn-del-ok');
var $btnSearch = $('#btn-search-cat-customer');
$tblMenu = $('#tbl-menu');
$btn_add = $('#btn-add');
$btn_update = $('#btn-update');
$btnDel = $('#btn-delete');
var $selectedItemId;
var $selectedItemCode;
var validator;
var tree = [];
var mapKeyValue;
//@Init component-----------------------------------------------------------------------------------------------
$(function () {
    $tblMenu.bootstrapTable({
        data: dataInit
    });
    //

    $btn_add.click(function () {
        clearActionInfo();
        changeModelByType(1, null, null, null, null, null, null,null, $btn_add.val());

        $addUpdateModal.modal('show');
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-cust-code').focus();
        });

    });
    $btnSearch.click(function () {
        doSearch();
    });
    $btnDelConfirmed.click(function () {
        deleteRow($selectedItemId);}
    );

    doSearch();

});

function operateFormatter(value, row, index) {
    return [
        '<a class="update-customer row-function" href="javascript:void(0)" title="Sửa">',
        '<i class="fa fa-pencil-square-o"></i>',
        '</a> ',
        '<a class="delete-customer row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}

//@FUNCTION-----------------------------------------------------------------------------------------------

window.operateEvents = {
    'click .update-customer': function (e, value, row, index) {
        clearActionInfo();
        changeModelByType(2, row['code'], row['name'], row['telNumber'], row['email'],row['address'], row['id'], row['status'], $btn_update.val());
        $("#cat-cust-insert-update-form").find(".error").removeClass("error");
        showModal($addUpdateModal);
        $addUpdateModal.on('shown.bs.modal', function () {
            $('modal-cust-code').focus();
        });
    },

    'click .delete-customer': function (e, value, row, index) {
        clearActionInfo();
        $("#lbl-del-info").text('Xóa thông tin: ' + decodeHtml(row['name']));
        $('#myConfirmModal').modal('show');
        $selectedItemId = row['id'];
    }
};


//
$(document).ready(function () {
    validator = createValidate("#cat-cust-insert-update-form",$addUpdateModal,$tblMenu,$btnSearch)

});

function doSearch() {
    var statusVal;
    if($('#cmb-status').prop('checked')){
        statusVal = '1';
    }else{
        statusVal = '0';
    }
    var keyword = $('#inp-keyword').val().trim();
    var data = {status:statusVal,keyword:keyword};
    searchAndUpdateMainTable(true,$tblMenu,$btnSearch,data);
}

//
function changeModelByType(type, code, name, telNumber, email,address, id, status, actionVal) {
    if (type == 1) {//add
        $("#cat-cust-insert-update-form").attr("action", actionVal);
        emptyForm($("#cat-cust-insert-update-form"))
        $('#modal-cmb-status').bootstrapToggle('on');
        $("#div-status *").prop('disabled', true);
        showAdd();
    } else {//update
        $("#cat-cust-insert-update-form").attr("action", actionVal);
        $("#modal-cust-code").val(decodeHtml(code));
        $("#modal-cust-name").val(decodeHtml(name));
        $("#modal-cust-telNumber").val(decodeHtml(telNumber));
        $("#modal-cust-email").val(decodeHtml(email));
        $("#modal-cust-address").val(decodeHtml(address));
        $("#modal-cust-id").val(id);
        var currentStatus = status;
        if (currentStatus == '0') {
            $('#modal-cmb-status').bootstrapToggle('off');
        } else {
            $('#modal-cmb-status').bootstrapToggle('on');
        }
        $("#div-status *").prop('disabled', false);
        showUpdate();
    }


}


