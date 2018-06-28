//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateModal = $('#myModal');

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
var searchDeptLink = $('#btn-getListDept').val();
var btnExecuse = $('#modal-btn-execuse');
var tableAssignRole = $('#tbl-table-role');
var tree = [];
var mapKeyValue;
var currentRoleId = '';
var currentUserId='';

//button link
var url = $('#btn-url').val();
var url_getRoles = url+"getRoles";

//@Init component-----------------------------------------------------------------------------------------------
$(function () {
    searchDept();
    mainTable.bootstrapTable({
        data: dataInit
    });
    tableAssignRole.bootstrapTable({
        data: dataInit
    });
    //
    $btn_add.click(function () {
        // clearActionInfo();
        // changeModelByType(1, null, null,null,null,null,null, $btn_add.val());
        // $addUpdateModal.modal('show');
        // $addUpdateModal.on('shown.bs.modal', function () {
        //     $('#modal-code').focus();
        // });

    });
    $btnSearch.click(function () {
        doSearch();
    });
    $btnDelConfirmed.click(function () {
        deleteRow($selectedItemId);}
    );
    btnExecuse.click(function () {
     // var ids= getTreeCheckedList("#mapRoleMenu");
     // var roleCode =   $("#modal-roleId").val();
     // var data = {id : roleCode,menuIds:ids};
     //
     // sendEvent("POST",btnExecuse.val(),data,"afterAssignRollSuccess");

    })
    $('#modal-update-assign-role').click(function () {
        doUpdateUserRole();

        }

    )
    doSearch();


});

function operateFormatter(value, row, index) {
    return [
        '<a class="update-row row-function" href="javascript:void(0)" title="Sửa">',
        '<i class="fa fa-pencil-square-o"></i>',
        '</a> ',
        '<a class="delete-row row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> ',
        '<a class="assign-role row-function" href="javascript:void(0)" title="Gán vai trò">',
        '<i class="fa  fa-user-circle-o"></i>',
        '</a> ',
        '<a class="assign-stock row-function" href="javascript:void(0)" title="Gán kho">',
        '<i class="fa  fa-home"></i>',
        '</a> ',
        '<a class="resetkey row-function" href="javascript:void(0)" title="Reset mật khẩu">',
        '<i class="fa  fa-key"></i>',
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
        $('#assygnRoleUser').modal('show');
        $selectedItemId = row['id'];
    },

    'click .assign-role': function (e, value, row, index) {
        doGetDataAndShowform(row['custId']);
        currentRoleId = row['roleId'];
        currentUserId = row['id']
    },

};


//
$(document).ready(function () {
    validator = createValidate("#cat-insert-update-form",$addUpdateModal,mainTable,$btnSearch)

});

function doSearch(clearInfor) {
    var statusVal;
    if($('#cmb-status').prop('checked')){
        statusVal = '1';
    }else{
        statusVal = '0';
    }
    var keyword = $('#inp-keyword').val().trim();
    var deptId =   $("#deptId").val();
    var data = {status:statusVal,keyword:keyword , deptId: deptId};
    searchAndUpdateMainTable(clearInfor,mainTable,$btnSearch,data);
}

//
function changeModelByType( changeType,name, code, id , status,type,custId , actionVal) {
    if (changeType == 1) {//add
        $("#cat-insert-update-form").attr("action", actionVal);
        emptyForm($("#cat-insert-update-form"));
        if(isRoot){
            $('#modal-cmb-custId').val($('#modal-cmb-custId option:first ').val());
            $("#div-root").css("display","block");
        }
        $('#modal-cmb-status').bootstrapToggle('on');
        $("#div-status *").prop('disabled', true);
        showAdd();
        $("#myModalLabel").text('Thêm mới');
    } else {//update
        $("#cat-insert-update-form").attr("action", actionVal);
        $("#modal-name").val(decodeHtml(name));
        $("#modal-code").val(decodeHtml(code));
        $("#modal-id").val(id);
        if(isRoot){
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
function searchDept() {
    var data = {status:'1'};
    searchEvent("GET",searchDeptLink,data,"buildTree")
}
function buildTree(data) {
    tree = [];
    mapKeyValue =new Object();
    mapKeyValue['0'] = '---chọn ---';
    var parentList=[];
    var subList=[]
    for(var i = 0; i < data.length; i++){
        item = data[i];
        if (item['parentId']==0){
            parentList.push(item);
        }else{
            subList.push(item);
        }
        mapKeyValue[item['id']]= item['name'];
    }
    var treeNoteDefaul =  {title:'---chọn---',dataAttrs:[{title:'id',data:'0'}],data:null};
    tree.push(treeNoteDefaul);
    for(var i = 0; i < parentList.length; i++){
        item = parentList[i];
        var  treeNote = {title:item['name'],dataAttrs:[{title:'id',data:item['id']}],data:buildTreeNote(item,subList)};
        tree.push(treeNote);
    }
    $("#modal-dept").DropDownTree(configTree('0'));

}
function buildTreeNote(parentNote,data) {
    var subtrees=[]
    for(var i = 0; i < data.length; i++){
        item = data[i];
        if(item['parentId'] == parentNote['id']){
            var  treeNote = {title:item['name'],dataAttrs:[{title:'id',data:item['id']}],data:buildTreeNote(item,data)};
            subtrees.push(treeNote);
        }
    }
    if (subtrees.length == 0){
        return null;
    }
    return subtrees;
}
function configTree(id){
    var options = {
        title : mapKeyValue[id],
        data: tree,
        maxHeight: 300,
        selectChildren : true,
        clickHandler: function(element){
            var elementId = $(element).attr('data-id');
            $("#modal-dept").SetTitle($(element).find("a").first().text());
            $("#deptId").val(elementId);
            document.getElementById('elementID').click();
        },
        checkHandler: function(element){
        },
        closedArrow: '<i class="fa fa-caret-right" aria-hidden="true"></i>',
        openedArrow: '<i class="fa fa-caret-down" aria-hidden="true"></i>',
        multiSelect: false,
    }
    $("#deptId").val(id);
    return options;
}
function doGetDataAndShowform(custId) {
    var custIdVal = custId;
    var data = {custId : custIdVal};

    searchEvent("GET",url_getRoles , data,'getRoleDataDone')

}
function getRoleDataDone( data) {
    showModal($('#assygnRoleUser'));

    tableAssignRole.bootstrapTable('load', data);
    if (currentRoleId!= undefined && currentRoleId != null && data!= null){
        for(i = 0 ; i <data.length ; i++){
          if(data[i]['id'] == currentRoleId){
              tableAssignRole.bootstrapTable('check', i);
          }
        }
    }
}


function doUpdateUserRole() {
    var role = tableAssignRole.bootstrapTable('getSelections');
    var block = $('input[name=rad-block]:checked').val();
    var data = {roleId : role[0]['id'] , block : block , userId : currentUserId};
    updateEvent("GET",  $('#modal-update-assign-role').val(),data,"showNotificationAndSearch",false);
    hideModal($('#assygnRoleUser'));
}