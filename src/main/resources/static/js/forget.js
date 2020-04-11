$(function () {
    $("#codeGetter").click(getCode);
    $("#changePass").click(changePass);
});

function getCode() {
    var email = $("#your-email").val();
    var reg = /^([A-Za-z0-9_\-\.\u4e00-\u9fa5])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,8})$/;
    if (email == null || email === "") {
        alert("邮箱不能为空");
        return;
    }
    if (!reg.test(email)) {
        alert("邮箱格式错误");
        return;
    }
    $.get(
        CONTEXT_PATH + "/user/forget/code",
        {"email": email},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert(data.msg);
                $("#codeGetter").attr("disabled", "disabled");
                countdown();
            } else {
                $("#hintBody").text(data.msg);
            }
        }
    );
}

function countdown() {
    var count = 60;
    var timer = setInterval(function () {
        if (count <= 0) {
            console.log("倒计时结束");
            $("#codeGetter").text("获取验证码");
            $("#codeGetter").removeAttr("disabled");
            window.clearInterval(timer);
            return;
        } else {
            $("#codeGetter").text(count);
            count--;
        }
    }, 1000);
}

function isNone(str) {
    if (str == null || str === "") {
        return true;
    }
    return false;
}

function changePass() {
    var email = $("#your-email").val();
    var reg = /^([A-Za-z0-9_\-\.\u4e00-\u9fa5])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,8})$/;
    if (isNone(email)) {
        alert("邮箱不能为空");
        return;
    }
    if (!reg.test(email)) {
        alert("邮箱格式错误");
        return;
    }
    var verifycode = $("#verifycode").val();
    if (isNone(verifycode)) {
        alert("验证码不能为空");
        return;
    }
    var password = $("#your-password").val();
    if (isNone(password)) {
        alert("密码不能为空");
        return;
    }
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");
    // $(document).ajaxSend(function(e, xhr, options){
    //     xhr.setRequestHeader(header, token);
    // });
    $.post(
        CONTEXT_PATH + "/user/forget/check",
        {"email": email, "code": verifycode, "newPassword": password},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert(data.msg);
                setTimeout(function () {
                    window.location.href="/login"
                }, 2000);
            } else {
                alert(data.msg);
            }
        }
    );
}
