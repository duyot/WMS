//Init data---------------------------------------------------------------------

var dataInit = [];
//---------------------------------------------------------------------
$addUpdateModal = $('#myModal');
$btnDelConfirmed = $('#modal-btn-del-ok');
var $btnSearch = $('#btn-search-cat-menu');
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
        $("#modal-menu-parentMenu").empty();
        var new_element = $("#modal-menu-parentMenu").clone();
        $("#modal-menu-parentMenu").replaceWith(new_element);
        $("#modal-menu-parentMenu").DropDownTree(configTree('0'));
        $addUpdateModal.modal('show');
        $addUpdateModal.on('shown.bs.modal', function () {
            $('#modal-menu-menuname').focus();
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
        '<a class="update-menu row-function" href="javascript:void(0)" title="Sửa">',
        '<i class="fa fa-pencil-square-o"></i>',
        '</a> ',
        '<a class="delete-menu row-function" href="javascript:void(0)" title="Xóa">',
        '<i class="fa fa-trash"></i>',
        '</a> '
    ].join('');
}

//@FUNCTION-----------------------------------------------------------------------------------------------

window.operateEvents = {
    'click .update-menu': function (e, value, row, index) {
        validator.resetForm();
        clearActionInfo();
        changeModelByType(2, row['name'], row['code'], row['url'], row['imgClass'],row['orders'], row['id'], row['status'], $btn_update.val());
        $("#cat-menu-insert-update-form").find(".error").removeClass("error");
        $("#modal-menu-parentMenu").empty();
        var new_element = $("#modal-menu-parentMenu").clone();
        $("#modal-menu-parentMenu").replaceWith(new_element);
        $("#modal-menu-parentMenu").DropDownTree(configTree( row['parentId'],row['id']));
        showModal($addUpdateModal);
        $addUpdateModal.on('shown.bs.modal', function () {
            $('modal-menu-menuname').focus();
        });
    },

    'click .delete-menu': function (e, value, row, index) {
        clearActionInfo();
        $("#lbl-del-info").text('Xóa thông tin: ' + decodeHtml(row['name']));
        $('#myConfirmModal').modal('show');
        $selectedItemId = row['id'];
    }
};


//
$(document).ready(function () {
    validator = createValidate("#cat-menu-insert-update-form",$addUpdateModal,$tblMenu,$btnSearch)

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
    search(true,$tblMenu,$btnSearch,data);
    searchFullMenu();
}

//
function changeModelByType(type, name, code, url, imgclass,order, id, status, actionVal) {
    if (type == 1) {//add
        $("#cat-menu-insert-update-form").attr("action", actionVal);
        emptyForm($("#cat-menu-insert-update-form"))
        $('#modal-cmb-status').bootstrapToggle('on');
        $("#div-status *").prop('disabled', true);
        showAdd();
        $("#myModalLabel").text('Thêm mới');
    } else {//update
        $("#cat-menu-insert-update-form").attr("action", actionVal);
        $("#modal-menu-name").val(decodeHtml(name));
        $("#modal-menu-code").val(decodeHtml(code));
        $("#modal-menu-path").val(decodeHtml(url));
        $("#modal-menu-imgclass").val(decodeHtml(imgclass));
        $("#modal-menu-order").val(decodeHtml(order));
        $("#modal-menu-id").val(id);
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

function searchFullMenu() {
    NProgress.start();
    $.ajax({
        type: "GET",
        cache:false,
        data:{status:'1',keyword:''},
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: $btnSearch.val(),
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
            buildTree(data);
        },
        complete:function () {
            NProgress.done();
        }
    });

}
function buildTree(data) {
    tree = [];
    mapKeyValue =new Object();
    mapKeyValue['0'] = '---chọn menu cha---';
    var parentList=[];
    var subList=[]
    for(var i = 0; i < data.length; i++){
        item = data[i];
        if (item['parentId']==0){
            parentList.push(item);
        }else{
            subList.push(item);
        }
        mapKeyValue[item['id']]= item['localizationName'];
    }
    var treeNoteDefaul =  {title:'---chọn menu cha---',dataAttrs:[{title:'id',data:'0'}],data:null};
    tree.push(treeNoteDefaul);
    for(var i = 0; i < parentList.length; i++){
        item = parentList[i];
        var  treeNote = {title:item['localizationName'],dataAttrs:[{title:'id',data:item['id']}],data:buildTreeNote(item,subList)};
        tree.push(treeNote);
    }


}
function buildTreeNote(parentNote,data) {
    var subtrees=[]
    for(var i = 0; i < data.length; i++){
        item = data[i];
        if(item['parentId'] == parentNote['id']){
            var  treeNote = {title:item['localizationName'],dataAttrs:[{title:'id',data:item['id']}],data:buildTreeNote(item,data)};
            subtrees.push(treeNote);
        }
    }
    if (subtrees.length == 0){
        return null;
    }
    return subtrees;
}
function configTree(id ,rowId){
    var options = {
        title : mapKeyValue[id],
        data: tree,
        maxHeight: 300,
        selectChildren : true,
        clickHandler: function(element){
            var elementId = $(element).attr('data-id');
            if(rowId !=undefined &&elementId == rowId){
                alert("Menu cha không hợp lệ");
                return;
            }
            $("#modal-menu-parentMenu").SetTitle($(element).find("a").first().text());
            $("#parentId").val(elementId);
            document.getElementById('elementID').click();
        },
        checkHandler: function(element){
        },
        closedArrow: '<i class="fa fa-caret-right" aria-hidden="true"></i>',
        openedArrow: '<i class="fa fa-caret-down" aria-hidden="true"></i>',
        multiSelect: false,
    }
    $("#parentId").val(id);
    return options;
}