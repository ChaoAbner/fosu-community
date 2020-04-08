$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
    $("#cancelWonderfulBtn").click(cancelWonderful);
    $("#cancelTopBtn").click(cancelTop);
});

function reload() {
    setTimeout(function () {
        window.location.reload();
    }, 1000)
}

function like(btn, entityType, entityId, entityUserId, postId) {
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 置顶
function setTop() {
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val(), "type": 1},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("置顶成功")
                reload()
            } else {
                alert(data.msg);
            }
        }
    );
}

function cancelTop() {
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val(), "type": 0},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("取消置顶成功")
                reload()
            } else {
                alert(data.msg);
            }
        }
    );
}

// 加精
function setWonderful() {
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val(), "status": 1},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("加精成功")
                reload()
                // $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 取消加精
function cancelWonderful() {
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val(), "status": 0},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("取消加精成功")
                reload()
                // $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}


// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}