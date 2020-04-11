$(function(){
    $("#publishBtn").click(changePass);
    $("#uploadForm").submit(upload);
});

function upload() {
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");
    // $(document).ajaxSend(function(e, xhr, options){
    //     xhr.setRequestHeader(header, token);
    // });
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
            if(data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        console.log(data);
                        if(data.code == 0) {
                            alert("上传成功！");
                            setTimeout(function () {
                                window.location.reload();
                            }, 2000)
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    return false;
}

function changePass() {
    var confirmPwd = $("input[id='confirm-password']").val();
    var newPwd = $("input[id='new-password']").val();
    var oldPwd = $("input[id='old-password']").val();
    if (confirmPwd !== newPwd) {
        alert("两次输入的密码不一致！");
        return;
    }
    if (newPwd.length < 6) {
        alert("密码长度至少为6位！");
        return;
    }
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");
    // $(document).ajaxSend(function(e, xhr, options){
    //     xhr.setRequestHeader(header, token);
    // });
    $.post(
        CONTEXT_PATH + "/user/password",
        {"sourcePass": oldPwd,"newPass":confirmPwd},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("修改成功！");
                setTimeout(function () {
                    window.location.reload();
                }, 2000);
            } else {
                alert(data.msg);
            }
        }
    );
}