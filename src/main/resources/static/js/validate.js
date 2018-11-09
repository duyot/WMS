function validateForm(formid) {
    var items = [];
  $(formid).find(":text").not(":hidden").each(function(){
        var $itm = $(this);
        items.push({
            'name' : $itm.attr('name'),
            'length' : $itm.attr('data-package'),
            'required' : $itm.children('.required').text()== required ? true:false});
    });


    var string = '';
    var ruleObj  =  new Object();
    items.forEach(function(item) {
       var obj  =  {
           required:item['required'],
           maxlength:item['length'],
           normalizer:function (value) {
            return $.trim( value );
        }
       }
        ruleObj[item['name']] = obj;
    });
 return ruleObj;
}
function clearForm(formid) {
    $(formid).find(":text").each(function(){
       if(!$(this).attr("hidden")){
           $(this).val('');
       }
    })

}
function createValidate(formid, addUpdateModal,table,btnSearch) {
    var validator = $(formid).validate({
        ignore: ":hidden",
        rules: validateForm(formid),
        submitHandler: function (form) {
            preprocessInput($(formid));
            //
            $.ajax({
                type: "POST",
                url: $(formid).attr('action'),
                data: $(form).serialize(),
                success: function (data) {
                    resultArr = data.split('|');
                    resultCode = resultArr[0];
                    resultName = resultArr[1];
                    if (resultCode == 1) {
                        setInfoMessage($('#action-info'), resultName);
                    } else {
                        setErrorMessage($('#action-info'), resultName);
                    }
                    doSearch();
                },
                error: function () {
                    setErrorMessage($('#action-info'), 'Lỗi hệ thống');
                }
            });
            //
            hideModal(addUpdateModal);
            return false; // required to block normal submit since you used ajax
        }
    });
    return validator;
}
