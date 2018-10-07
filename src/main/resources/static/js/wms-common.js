/**
 * Created by duyot on 11/16/2016.
 */
// 123|Tu lanh -> 123
function valueFromSuggest(value){
    return value.split("|",1)[0];
}
//
function isContainsNonLatinCharacter(s) {
    return /[^\u0000-\u00ff]/.test(s);
}
//positive integer or float
function isValidAmount(n) {
    return Number(n) === n && n % 1 === 0 || Number(n) === n && n % 1 !== 0 || Number(n) >= 0;
}
//format xxxxx.xxxx: set specific format for number and read it.
function showPriceDetail(value, price, priceName) {
    var currentValue = unFormatFloat(value);
    var need2format = currentValue;
    if(!isValidPrice(currentValue)){
        currentValue = need2format.substr(0,need2format.indexOf(".") + 5);
    }
    if(!currentValue.includes(".")){
        priceName.text(DOCSO.doc(currentValue));
        price.val(formatFloatType(currentValue));
    }else{
        priceName.text("");
        price.val(currentValue);
    }
}
// valid pattern: number or xx.xxxx (scale 4)
function isValidPrice(price) {
    if(!price.includes(".")){
        return true;
    }
        var scale = price.substr(price.indexOf(".")+1,price.length-1);

        if(scale > 9999){
            return false;
    }
    return true;

}

function clearFileInput(id)
{
    var oldInput = document.getElementById(id);
    var newInput = document.createElement("input");

    newInput.type = "file";
    newInput.id = oldInput.id;
    newInput.name = oldInput.name;
    newInput.className = oldInput.className;
    newInput.style.cssText = oldInput.style.cssText;
    oldInput.parentNode.replaceChild(newInput, oldInput);
}

Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

//incombo, name like goodsCode | goodsName
function getGoodsNameInCombo(value){
    var arr  = value.split("|");
    return arr[1].trim();

}

function trimAtChacter(value,character){
    return value.substring(0,value.indexOf(character));
}

function isInteger(str) {
    return /^\+?(0|[1-9]\d*)$/.test(str);
}

function convertExportMessage(mesageCode,key){
    switch (mesageCode){
        case "ERROR_TOTAL_NOT_ENOUGH":
            return  "Không đủ số lượng hàng trong kho ";
            break;
        case "ERROR_UPDATE_TOTAL":
            return "Lỗi cập nhật số lượng hàng ";
        case "ERROR_NOT_FOUND_STOCK_GOODS":
            return "Hàng không có sẵn trong kho ";
        case "ERROR_NOT_FOUND_SERIAL":
            return "Serial không có sẵn trong kho: "+ key;
        case "ERROR_CREATE_STOCK_TRANS_DETAIL":
            return "Lối tạo chi tiết giao dịch ";
        case "ERROR_OVER_GOODS_NUMBER":
            return "Số hàng nhập lên vượt quá 1000. Vui lòng nhập số lượng nhỏ hơn ";
        case "ERROR_SYSTEM":
            return "Lỗi hệ thống: liên hệ admin để được hỗ trợ ";
        default:
            return "Lỗi hệ thống: liên hệ admin để được hỗ trợ ";
    }

}
//check if contain xml special character
function isContainXMLCharacter(value){
    return escapeHtml(value) != value;
}

function isContainSpecialCharacter(value) {
    return value.includes("<") || value.includes(">");
}
//		row num
function runningFormatter(value, row, index) {
    return index +1;
}

function unFormatFloat(value) {
    if(typeof(value) == "number"){
        return value;
    }
    return replaceAll(value,",","");
}

function replaceAll(value,search, replacement) {

    return value.replace(new RegExp(search, 'g'), replacement);
}

function replaceAllDot(value, replacement) {
    value.replace(/\./g,replacement);
}

function formatFloatType(text) {
    if(!text.toString().includes(".")){
        return text.toString().replace(/\B(?=(?:\d{3})+(?!\d))/g, ",");
    }else{
        return text;
    }
}

function setInfoMessage(object,value) {
    object.text(value);
    object.css('color','#337ab7');
    //
}

function setInfoMessageWithTime(object,value,timeout) {
    object.text(value);
    object.css('color','#337ab7');
    //
    object.fadeIn();
    setTimeout(function() {
        object.fadeOut('fast');
    }, timeout);
}

function setErrorMessage(object,value) {
    object.text(value);
    object.css('color','#F44336');
    //
    object.fadeIn();
    setTimeout(function() {
        object.fadeOut('fast');
    }, 8000);
}

function setErrorMessageWithTime(object,value,timeout) {
    object.text(value);
    object.css('color','#F44336');
    //
    object.fadeIn();
    setTimeout(function() {
        object.fadeOut('fast');
    }, timeout);
}

function isTableEmpty(object) {
    var data = object.bootstrapTable('getData');
    if(data.length == 0){
        return true;
    }else{
        return false;
    }
}

var _validFileExcel = [".xls", ".xlsx"];
function isValidExcel(oInput) {
    if (oInput.type == "file") {
        var sFileName = oInput.value;
        if (sFileName.length > 0) {
            var blnValid = false;
            for (var j = 0; j < _validFileExcel.length; j++) {
                var sCurExtension = _validFileExcel[j];
                if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
                    blnValid = true;
                    break;
                }
            }

            if (!blnValid) {
                alert(sFileName + " không đúng định dạng! Vui lòng nhập file " + _validFileExcel.join(", "));
                oInput.value = "";
                return false;
            }
        }
    }
    return true;
}

function initDateRangeSelect() {
    var date = new Date();
    var start = new Date(date.getFullYear(), date.getMonth(), 1);
    var end = moment();

    $('input[name="daterange"]').daterangepicker({
        timePicker: false,
        timePickerIncrement: 30,
        startDate: start,
        endDate: end,
        locale: {
            format: 'DD/MM/YYYY'
        },
        buttonClasses: ['btn btn-default'],
        ranges: {
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
            'Last 7 Days': [moment().subtract('days', 6), moment()],
            'Last 30 Days': [moment().subtract('days', 29), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
        },
        showDropdowns: true
    });
}
//------------------------------------------------------------------------------------------------------------------
//validate for upload file
function getExtension(filename) {
    var parts = filename.split('.');
    return parts[parts.length - 1];
}

function clearActionInfo() {
    $('#main-action-info').text('');
    $('#action-info').text('');
    $('#del-action-info').text('');
}

function showAdd() {
    $("#myModalLabel").text('Thêm mới');
    $("#modal-btn-update").hide();
    $("#modal-btn-add").show();
}
function showUpdate() {
    $("#myModalLabel").text('Cập nhật');
    $("#modal-btn-update").show();
    $("#modal-btn-add").hide();
}

function disableElement(button) {
    button.prop('disabled', true);
}
function enableElement(button) {
    button.prop('disabled', false);
}

function hideElement(button) {
    button.hide();
}
function showElement(button) {
    button.show();
}

function refreshTable(table) {
    table.bootstrapTable('removeAll');
}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function decodeHtml(html) {
    var txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
}

function showModal(object) {
    object.modal('show');
}

function hideModal(object) {
    object.modal('hide');
}

function isDocument(filename) {
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
        case 'doc':
        case 'docx':
        case 'xls':
        case 'xlsx':
        case 'pdf':
            //etc
            return true;
    }
    return false;
}

function preprocessInput(object) {
    object.find('input:text').each(function(){
        $(this).val($.trim($(this).val()));
    });
}

function isAllFieldValid(object) {
    var className;
    var result = true;
    object.find('input:text').each(function(){
        className = $(this).attr("class");
        if(className.indexOf("error") != -1){
            result = false;
        }
    });

    return result;
}

function removeSpecialCharForNumber(sText) {
    return sText.replace(/[^0-9.,]/g, '');
}

function searchAndUpdateMainTable(isClear,table,btnSearch, data){
    NProgress.start();
    if(isClear){
        clearActionInfo();
    }

    $.ajax({
        type: "GET",
        cache:false,
        data:data,
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: btnSearch.val(),
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
            table.bootstrapTable('load', data);
        },
        complete:function () {
            NProgress.done();
        }
    });
}
function sendEvent(type,url,jsonData,callback,dataType,clearInfor,addInfor){
    NProgress.start();
    $.ajax({
        type: type,
        cache: false,
        data: jsonData,
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: url,
        dataType: dataType,
        timeout: 600000,
        success: function (data) {
            if (typeof window[callback] === "function") {
                window[callback](data,clearInfor,addInfor);
            }
        },
        error: function (request, error) {
            setErrorMessage($('#action-info'), 'Lỗi hệ thống');
            console.log(error);

        },
        complete:function () {
            NProgress.done();
        }
    });
    //

}

function logOut(){
    $.ajax({
        type: "GET",
        cache: false,
        data: "",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url:  $("#btnLogout").attr('href'),
        dataType: "json",
        timeout: 600000,
        success: function (data) {
            if (typeof window[callback] === "function") {
                console.log(error);
            }
        },
        error: function (request, error) {
            console.log(error);

        },
        complete:function () {
        }
    });
    //

}
function logout2() {
    window.location.href = "/workspace/logout";

}
function searchEvent(type,url,jsonData,callback,addInfor) {
    sendEvent(type,url,jsonData,callback,'json',true,addInfor);
}
function updateEvent(type,url,jsonData,callback,addInfor) {
    sendEvent(type,url,jsonData,callback,'text',false,addInfor);
}
function runningFormatter(value, row, index) {
    return index + 1;
}
function emptyForm(form){
    form.find(":text").each(function(){
        var $itm = $(this);
        $itm.val('')
    });
    form.find(":password").each(function(){
        var $itm = $(this);
        $itm.val('')
    });
    form.find(":checkbox").each(function(){
        var $itm = $(this);
        $itm.bootstrapToggle('on');
    });
    form.find('.number-modal').each(function(){
        var $itm = $(this);
        $itm.val('')
    });

    $("#emp-insert-update-form").find('label.error').each(function(){
        $(this).css("display", "none");
    });
}
function deleteRow(rowId) {
    var jsonData={id: rowId} ;
    var url = $btnDel.val();
    sendEvent("POST",url,jsonData,"showNotificationAndSearch",'text',false);
    // $.ajax({
    //     type: "POST",
    //     cache: false,
    //     data: {id: rowId},
    //     contentType: "application/x-www-form-urlencoded; charset=UTF-8",
    //     url: $btnDel.val(),
    //     dataType: 'text',
    //     timeout: 600000,
    //     success: function (data) {
    //         showNotificationAndSearch(data,false);
    //     },
    //     error: function () {
    //         setErrorMessage($('#action-info'), 'Lỗi hệ thống');
    //     },
    // });
    //
    $('#myConfirmModal').modal('hide');
}
function getTreeCheckedList(formId) {
    var selectobject = $(formId).find("[id*=treemultiselect-0-]:checked");
    var ids = '';
    for(i=0;i<selectobject.length;i++) {
     var id =   $(selectobject[i]).parent().data("value");
        ids += ',' + id;
    }
    return ids.replace(',','');
}
function showNotification( data) {
    resultArr = data.split('|');
    resultCode = resultArr[0];
    resultName = resultArr[1];
    if (resultCode == 1) {
        setInfoMessage($('#action-info'), resultName);
    } else {
        setErrorMessage($('#action-info'), resultName);
    }
}
function showNotificationAndSearch(data,clearInfor) {
    showNotification( data);
    doSearch(clearInfor);
}
window.onunload = function(){
    console.log("close onunload");
}
window.onbeforeunload = function(e) {
    logOut();
}
function closeWindow(){
    document.getElementById('btnLogout').click();
}
function test3() {
    $.ajax({
        type : "DELETE",
        url : "http://45.32.103.51:9000/eureka/apps/WMS_SERVICE/localhost:WMS_SERVICE:8080",
        success: function (result) {
            console.log(result);
        },
        error: function (e) {
            console.log(e);
        }
    });

}
function test4() {
    $.ajax({
        type : "DELETE",
        url : "http://45.32.103.51:9000/eureka/apps/WMS_SERVICE/192.168.20.1:WMS_SERVICE:8080",
        success: function (result) {
            console.log(result);
        },
        error: function (e) {
            console.log(e);
        }
    });

}
$(function () {
    $("a").not('#btnLogout').click(function () {
        window.onbeforeunload = null;
    });
    $(".btn").click(function () {
        window.onbeforeunload = null;
    });
});
var DOCSO=function(){var t=["không","một","hai","ba","bốn","năm","sáu","bảy","tám","chín"],r=function(r,n){var o="",a=Math.floor(r/10),e=r%10;return a>1?(o=" "+t[a]+" mươi",1==e&&(o+=" mốt")):1==a?(o=" mười",1==e&&(o+=" một")):n&&e>0&&(o=" lẻ"),5==e&&a>=1?o+=" lăm":4==e&&a>=1?o+=" tư":(e>1||1==e&&0==a)&&(o+=" "+t[e]),o},n=function(n,o){var a="",e=Math.floor(n/100),n=n%100;return o||e>0?(a=" "+t[e]+" trăm",a+=r(n,!0)):a=r(n,!1),a},o=function(t,r){var o="",a=Math.floor(t/1e6),t=t%1e6;a>0&&(o=n(a,r)+" triệu",r=!0);var e=Math.floor(t/1e3),t=t%1e3;return e>0&&(o+=n(e,r)+" nghìn",r=!0),t>0&&(o+=n(t,r)),o};return{doc:function(r){if(0==r)return t[0];var n="",a="";do ty=r%1e9,r=Math.floor(r/1e9),n=r>0?o(ty,!0)+a+n:o(ty,!1)+a+n,a=" tỷ";while(r>0);return n.trim()}}}();

