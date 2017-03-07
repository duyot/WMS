/**
 * Created by duyot on 11/16/2016.
 */
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
        showDropdowns: true,
    });
}
//------------------------------------------------------------------------------------------------------------------
//validate for upload file
function getExtension(filename) {
    var parts = filename.split('.');
    return parts[parts.length - 1];
}

function isImage(filename) {
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
        case 'jpg':
        case 'gif':
        case 'bmp':
        case 'png':
            //etc
            return true;
    }
    return false;
}

function clearActionInfo() {
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

function disableButton(button) {
    button.prop('disabled', true);
}
function enableButton(button) {
    button.prop('disabled', false);
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

var messages = {
    welcome: "Welcome",
    goodbye: "Goodbye",
    error: "Something bad happend. Sowwy!"
};



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

