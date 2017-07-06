/**
 * Created by duyot on 11/16/2016.
 */
function trimAtChacter(value,character){
    return value.substring(0,value.indexOf(character));
}

function converExportMessage(mesageCode,key){
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
//		row num
function runningFormatter(value, row, index) {
    return index +1;
}

function unFormatFloat(value) {
    return value.replace(",","");
}

function formatFloatType(text) {
    return text.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function setInfoMessage(object,value) {
    object.text(value);
    object.css('color','#337ab7');
}

function setErrorMessage(object,value) {
    object.text(value);
    object.css('color','#F44336');
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

var DOCSO=function(){var t=["không","một","hai","ba","bốn","năm","sáu","bảy","tám","chín"],r=function(r,n){var o="",a=Math.floor(r/10),e=r%10;return a>1?(o=" "+t[a]+" mươi",1==e&&(o+=" mốt")):1==a?(o=" mười",1==e&&(o+=" một")):n&&e>0&&(o=" lẻ"),5==e&&a>=1?o+=" lăm":4==e&&a>=1?o+=" tư":(e>1||1==e&&0==a)&&(o+=" "+t[e]),o},n=function(n,o){var a="",e=Math.floor(n/100),n=n%100;return o||e>0?(a=" "+t[e]+" trăm",a+=r(n,!0)):a=r(n,!1),a},o=function(t,r){var o="",a=Math.floor(t/1e6),t=t%1e6;a>0&&(o=n(a,r)+" triệu",r=!0);var e=Math.floor(t/1e3),t=t%1e3;return e>0&&(o+=n(e,r)+" nghìn",r=!0),t>0&&(o+=n(t,r)),o};return{doc:function(r){if(0==r)return t[0];var n="",a="";do ty=r%1e9,r=Math.floor(r/1e9),n=r>0?o(ty,!0)+a+n:o(ty,!1)+a+n,a=" tỷ";while(r>0);return n.trim()}}}();

