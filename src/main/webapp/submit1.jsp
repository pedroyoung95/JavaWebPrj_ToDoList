<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
  href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script
  src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
  src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script
  src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script src="https://kit.fontawesome.com/a076d05399.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	$("#outside").click(function() {
		$("#my-form").submit();
	});
	$("#inside").click(function(e) {
		e.preventDefault(); //원래 해야 하는 기본(default)일을 막음(prevent)
		//funtion의 파라미터로 넣음으로써 event 객체를 얻어옴
		//필요한 작업 수행...
		console.log("인싸버튼클릭");
		$("#my-form").submit();
	});
});
</script>

<title>Insert title here</title>
</head>
<body>
<h1>name : ${param.name }</h1>
<form id="my-form" action="">
	<input type="text" name="name" value="java"> <br>
	<input type="submit" value="전송"> <br>
	<button id="inside">또 다른 전송</button>
</form>

<button id="outside">밖에 있는 버튼</button>

</body>
</html>