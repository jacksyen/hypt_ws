<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE HTML>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>Hello,world</title>
<style type="text/css">
html {
	height: 100%
}

body {
	height: 100%;
	margin: 0px;
	padding: 0px
}

#container {
	height: 100%
}
</style>
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>
<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=d05cbaac9f35812b93de8ab502c65e35">	
</script>
<script type="text/javascript" src="${ctx}/js/jquery-2.1.3.min.js"></script>

<script type="text/javascript">
	function initialize() {
		$.ajax({
	        type: 'get',
	        url: '/hypt/ws/0.1/vstatus/location?vid=1',
	        async: false,
	        dataType : 'JSON',
	        cache: false,
	        contentType: 'application/json;charset=utf-8',
	        success: function(items) {
				 var position=new AMap.LngLat(items.longitude, items.latitude);
				 var mapObj=new AMap.Map("container",{
				 	 view: new AMap.View2D({//创建地图二维视口
				 	 center:position,//创建中心点坐标
				 	 zoom:14, //设置地图缩放级别
				  	rotation:0 //设置地图旋转角度
				 	}),lang:"zh_cn"//设置地图语言类型，默认：中文简体
				 });
				
				var marker = new AMap.Marker(
					{
						position : new AMap.LngLat(items.longitude, items.latitude),//基点位置                 
						offset : new AMap.Pixel(-14, -34),//相对于基点的偏移位置                 
						icon : "http://code.mapabc.com/images/car_03.png"
					});
				marker.setMap(mapObj);
				 var info = [];                 
				 info.push("<b> 车辆信息</b>");                 
				 info.push("  车牌 :  京A88888"); 
				 info.push("  车速 : "+items.velocity);
				 info.push("  油量 : "+items.gas);
				 info.push("  里程 : "+items.mileage);
				 info.push("  地址 : 北京市望京阜通东大街方恒国际中心A座16层");
				 var inforWindow = new AMap.InfoWindow({                 
					  offset:new AMap.Pixel(0,-23),                 
					  content:info.join("<br>")                 
					});  
				AMap.event.addListener(marker,"click",function(e){                 
					  inforWindow.open(mapObj,marker.getPosition());                 
				});      
	        }
		});

	}
</script>
</head>

<body onload="initialize()">
	<div id="container"></div>
</body>
</html>