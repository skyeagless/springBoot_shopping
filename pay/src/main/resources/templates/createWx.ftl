<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>微信Native支付</title>
    <script src="https://cdn.bootcss.com/jquery/1.5.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
</head>
<body>
    <div id="myQrcode"></div>
    <div id="orderId" hidden>${orderId}</div>
    <div id="returnUrl" hidden>${returnUrl}</div>
</body>
<script>
    jQuery('myQrcode').qrcode({
        text: "${codeUrl}"
    });

    $(function () {
        //定时器：定时ajax异步请求后端的api
        setInterval(function () {
            $.ajax({
                url:'/pay/queryByOrderId',
                data:{
                    'orderId': $('#orderId').text();
                },
                success: function (result) {
                    if(result.platformStatus != null && result.platformStatus === "SUCCESS"){
                        location.href = $("#returnUrl").text();
                    }
                },
                error:function (result) {

                }

            })
        },2000)
    })
</script>
</html>
