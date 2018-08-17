//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateModal = $('#insert-update-modal');

$btnDelConfirmed = $('#modal-btn-del-ok');
var $btnSearch = $('#btn-search');
var mainTable = $('#tbl-table');
var isRoot =$('#btn-root').val();
$btnDel = $('#btn-delete');
var $selectedItemId;
var $btn_add = $('#btn-add')
var validator;
var searchDeptLink = $('#btn-getListDept').val();
var tableAssignRole = $('#tbl-table-role');
var tableAssignStock = $('#tbl-table-stock');
var tree = [];
var mapKeyValue;
var currentRoleId = '';
var currentUserId='';
var btnUpdateDept = $('#update-department-execuse');
//button link
var url = $('#btn-url').val();
var url_getRoles = url+"getRoles";
var url_getStock = url+"getListStock";
var url_update = url+"update";
//@Init component-----------------------------------------------------------------------------------------------
$(function () {
    searchDept();
    mainTable.bootstrapTable({
        data: dataInit
    });
    tableAssignRole.bootstrapTable({
        data: dataInit
    });
    tableAssignStock.bootstrapTable({
        data: dataInit
    });
    //
    $btn_add.click(function () {
        clearActionInfo();
        changeModelByType(1, null, null,null,null,null,null,null, $btn_add.val());
        showModal($addUpdateModal);
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


    btnUpdateDept.click(function () {
        var ids= getTreeCheckedList("#updateDepartment");
        var data = {userId : currentUserId,deptId:ids};

        updateEvent("GET",btnUpdateDept.val(),data,"afterAssignDeptSuccess");

    })
    $('#modal-update-assign-role').click(function () {
        doUpdateUserRole();
        })

    $('#modal-update-assign-stock').click(function () {
        doUpdateUserStock();
    })
    $('#modal-btn-reset').click(function () {
        doResetPass();
        
    })
    $('#search-usageUnit').val("0");
    doSearch();



});

$('#confirm').on('keyup', function () {
    if ($('#password').val() == $('#confirm').val()) {
        $('#message').html('Mật khẩu khớp').css('color', 'green');
    } else
        $('#message').html('Mật khẩu không khớp').css('color', 'red');
});
function operateFormatter(value, row, index) {
    if(isRoot == "true"){
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
            '<a class="resetkey row-function" href="javascript:void(0)" title="Đổi mật khẩu">',
            '<i class="fa  fa-key"></i>',
            '</a> '
        ].join('');
    }else{
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
            '<a class="resetkey row-function" href="javascript:void(0)" title="Đổi mật khẩu">',
            '<i class="fa  fa-key"></i>',
            '</a> ',
            '<a class="assynDept row-function" href="javascript:void(0)" title="Gán phong ban">',
            '<i class="fa  fa-building"></i>',
            '</a> '
        ].join('');
    }
}

//@FUNCTION-----------------------------------------------------------------------------------------------

window.operateEvents = {
    'click .update-row': function (e, value, row, index) {
        validator.resetForm();
        clearActionInfo();
        changeModelByType(2, row['name'], row['code'], row['id'], row['status'],row["telNumber"],row["email"] ,row['custId'],url_update);
        $("#emp-insert-update-form").find(".error").removeClass("error");
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

    'click .assign-role': function (e, value, row, index) {
        currentRoleId = row['roleId'];
        currentUserId = row['id'];
        doGetDataAndShowform(row['custId'],row['block']);
    },
    'click .assign-stock': function (e, value, row, index) {
        currentUserId = row['id'];
        processAssignStock(row['custId'], row['code']);
    },
    'click .resetkey': function (e, value, row, index) {
      doPrepareShowForm(row['code'],row['id']);
    },
    'click .assynDept': function (e, value, row, index) {
        currentUserId = row['id']
        doPrepareShowDept(row['deptId']);
    },
};


//
$(document).ready(function () {
    validator = createValidate("#emp-insert-update-form",$addUpdateModal,mainTable,$btnSearch)
    var tree1 = $("#update-dept").treeMultiselect({ enableSelectAll: true ,hideSidePanel:true,maxSelections: '1'});

});

function doSearch(clearInfor) {
    var statusVal;
    var usageUnit = '0';
    if($('#cmb-status').prop('checked')){
        statusVal = '1';
    }else{
        statusVal = '0';
    }
    var keyword = $('#inp-keyword').val().trim();
    var deptId =   $("#deptId").val();
    var usageUnit;
    if(isRoot == "true"){
        usageUnit = $('#search-usageUnit').val();
    }else{
        usageUnit = "";
    }
    var data = {status:statusVal, keyword:keyword , deptId: deptId,usageUnit :usageUnit};
    searchAndUpdateMainTable(clearInfor,mainTable,$btnSearch,data);
}

//
function changeModelByType( changeType,name, code, id , status, tel,email ,custId,actionVal) {
    if (changeType == 1) {//add
        $("#emp-insert-update-form").attr("action", actionVal);
        emptyForm($("#emp-insert-update-form"));
        $('#modal-cmb-status').bootstrapToggle('on');
        $("#div-status *").prop('disabled', true);
        showAdd();
        $("#usageUnit").val($("#usageUnit option").eq(1).val());
    } else {//update
        $("#emp-insert-update-form").attr("action", actionVal);
        $("#modal-name").val(decodeHtml(name));
        $("#modal-code").val(decodeHtml(code));
        $("#modal-id").val(id);
        $("#modal-email").val(decodeHtml(email));
        $("#modal-tel").val(decodeHtml(tel));
        $("#modal-custId").val(decodeHtml(custId));
        if(isRoot == "true"){
            $('#usageUnit').val(custId);
        }

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
function doGetDataAndShowform(custId,block) {
    var data = {custId : custId};

    searchEvent("GET",url_getRoles , data,'getRoleDataDone',block)

}
function processAssignStock(custId ,code ) {
    var data = {custId : custId,userId : currentUserId};
    $('#assign-stock-user-code').text(code);
    searchEvent("GET",url_getStock , data,'getStocksDataDone');
}
function getRoleDataDone( data,clearInfor ,block) {
    showModal($('#assygnRoleUser'));
    $('input[name=rad-block][value='+block+']').prop('checked', true)
    tableAssignRole.bootstrapTable('load', data);
    if (currentRoleId!= undefined && currentRoleId != null && data!= null){
        for(i = 0 ; i <data.length ; i++){
            if(data[i]['id'] == currentRoleId){
                tableAssignRole.bootstrapTable('check', i);
            }
        }
    }
}

function getStocksDataDone(data) {
    showModal($('#assygnStockUser'));
    tableAssignStock.bootstrapTable('load', data.lstCatStocks);
    var listStocks = data.lstCatStocks;
    var sellectedStock = data.userStocks;
    for(i = 0 ; i <listStocks.length ; i++){

        for( j = 0 ; j <sellectedStock.length; j++){
            if(listStocks[i]['id'] == sellectedStock[j]){
                tableAssignStock.bootstrapTable('check', i);
                break;
            }
        }

    }
}
function doPrepareShowDept(deptId) {
    $("#updateDepartment").find(":checkbox").each(function (item) {
        $(this).prop('checked', false);
    })
    showModal($('#updateDepartment'));
    if( deptId != '');
      $("#updateDepartment").find("[data-value= "+deptId+"]").children(":checkbox").prop('checked', true);
}

function doUpdateUserRole() {
    var role = tableAssignRole.bootstrapTable('getSelections');
    var block = $('input[name=rad-block]:checked').val();
    var data ;
    if(role.length == 0){
       data = {roleId : '-1' ,roleName :'-1', block : block , userId : currentUserId};
    }else{
        data = {roleId : role[0]['id'] ,roleName : role[0]['name'], block : block , userId : currentUserId};
    }
    updateEvent("GET",  $('#modal-update-assign-role').val(),data,"showNotificationAndSearch",false);
    hideModal($('#assygnRoleUser'));
}
function doUpdateUserStock() {
    var stock = tableAssignStock.bootstrapTable('getSelections');
    var stocks = '';
    for(i = 0 ; i <stock.length ; i ++){
        stocks = stocks + ',' + stock[i]['id'];
    }
    var data = {userId : currentUserId, stockId: stocks};
    updateEvent("GET",  $('#modal-update-assign-stock').val(),data,"showNotificationAndSearch",false);
    hideModal($('#assygnStockUser'));
}
function doPrepareShowForm(code,id) {
    $('#modal-reset-code').text(decodeHtml(code));
    emptyForm($('#reset-password-form'));
    $('#modal-userId').val(id);
    $('#message').text('');
    showModal($('#resetpasss'));
}
function doResetPass() {
    if ($('#password').val() != $('#confirm').val()) {
        $('#message').html('Mật khẩu không khớp').css('color', 'red');
    } else{
    updateEvent("POST",$('#modal-btn-reset').val(),$('#reset-password-form').serialize(),"showNotification");
        hideModal($('#resetpasss'));
    }

}
function afterAssignDeptSuccess(data) {
    showNotificationAndSearch(data,false);
    hideModal($('#updateDepartment'))

}