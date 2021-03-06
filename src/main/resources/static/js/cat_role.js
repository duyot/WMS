//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateModal = $('#myModal');
var mapRoleMenu = $('#mapRoleMenu');
$btnDelConfirmed = $('#modal-btn-del-ok');
var $btnSearch = $('#btn-search');
var mainTable = $('#tbl-table');
$btn_add = $('#btn-add');
var isRoot =$('#btn-root').val();
$btn_update = $('#btn-update');
$btnDel = $('#btn-delete');
var $selectedItemId;
var $selectedItemCode;
var validator;
var tree = [];
var mapKeyValue;
var btnExecuse = $('#modal-btn-execuse');
//@Init component-----------------------------------------------------------------------------------------------
$(function () {
    mainTable.bootstrapTable({
        data: dataInit
    });
    //

    $btn_add.click(function () {
        clearActionInfo();
        changeModelByType(1, null, null,null,null,null,null, $btn_add.val());
        $addUpdateModal.modal('show');
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-code').focus();
        });

    });
    $btnSearch.click(function () {
        doSearch();
    });
    $btnDelConfirmed.click(function () {
        deleteRow($selectedItemId);}
    );
    btnExecuse.click(function () {
     var ids= getTreeCheckedList("#mapRoleMenu");
     var roleCode =   $("#modal-roleId").val();
     var custId =   $("#modal-custId").val();
     var data = {id : roleCode,menuIds:ids , custId :custId};

     updateEvent("POST",btnExecuse.val(),data,"afterAssignRollSuccess");

    });
    doSearch();

});
function afterAssignRollSuccess(data) {
    showNotificationAndSearch(data);
    $('#mapRoleMenu').modal('hide');

}
function operateFormatter(value, row, index) {
    return [
        '<a class="update-row row-function" href="javascript:void(0)" title="Sửa">',
        '<i class="fa fa-pencil-square-o"></i>',
        '</a> ',
        '<a class="delete-row row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> ',
        '<a class="assign-row row-function" href="javascript:void(0)" title="Gán menu">',
        '<i class="fa fa-arrows"></i>',
        '</a> '
    ].join('');
}

//@FUNCTION-----------------------------------------------------------------------------------------------

window.operateEvents = {
    'click .update-row': function (e, value, row, index) {
        validator.resetForm();
        clearActionInfo();
        changeModelByType(2, row['name'], row['code'], row['id'], row['status'],row["type"],row["custId"] ,$btn_update.val());
        $("#cat-insert-update-form").find(".error").removeClass("error");
        showModal($addUpdateModal);
        $addUpdateModal.on('shown.bs.modal', function () {
            $('modal-code').focus();
        });
    },

    'click .delete-row': function (e, value, row, index) {
        clearActionInfo();
        $("#lbl-del-info").text('Xóa thông tin: ' + decodeHtml(row['name']));
        $('#myConfirmModal').modal('show');
        $selectedItemId = row['id'];
    },
    'click .assign-row': function (e, value, row, index) {
        $("#modal-roleId").val(row['id']);
        $("#modal-custId").val(row['custId']);
        bindDataToTree(row["menuIds"]);
      showModal(mapRoleMenu);
    }
};


//
$(document).ready(function () {
    validator = createValidate("#cat-insert-update-form",$addUpdateModal,mainTable,$btnSearch)
    var tree1 = $("#map-role-menu").treeMultiselect({ enableSelectAll: true ,hideSidePanel:true});

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
    searchAndUpdateMainTable(false,mainTable,$btnSearch,data);
}

//
function changeModelByType( changeType,name, code, id , status,type,custId , actionVal) {
    if (changeType == 1) {//add
        $("#cat-insert-update-form").attr("action", actionVal);
        emptyForm($("#cat-insert-update-form"));
        if(isRoot == "true"){
            $('#modal-cmb-custId').val($('#modal-cmb-custId option:first ').val());
            $("#div-root").css("display","block");
        }
        $('#modal-cmb-status').bootstrapToggle('on');
        $("#div-status *").prop('disabled', true);

        showAdd();
    } else {//update
        $("#cat-insert-update-form").attr("action", actionVal);
        $("#modal-name").val(decodeHtml(name));
        $("#modal-code").val(decodeHtml(code));
        $("#modal-id").val(id);
        if(isRoot == true){
            $('#modal-cmb-custId').val(custId);
            $("#div-root").css("display","none");
        }
        $("#modal-type").val(decodeHtml(type));

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
function bindDataToTree(menuIds) {
    $("#mapRoleMenu").find(":checkbox").each(function (item) {
        $(this).prop('checked', false);
    })
    if(menuIds == null){
        return;
    }
    var ids = menuIds.split(',');
    for(i=0;i<ids.length;i++){
        $("#mapRoleMenu").find("[data-value= "+ids[i]+"]").children(":checkbox").prop('checked', true);
    }



}

