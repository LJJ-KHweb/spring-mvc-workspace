<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <style> 
        .content {
            background-color:rgb(247, 245, 245);
            width:80%;
            margin:auto;
        }
        .innerOuter {
            border:1px solid lightgray;
            width:80%;
            margin:auto;
            padding:5% 10%;
            background-color:white;
        }

        table * {margin:5px;}
        table {width:100%;}
    </style>
</head>
<body>
	<!-- ex) 계획을 메소드 단위로 언제까지 이 메소드 구현해야지 
		5월 22일 ~ 5월 25일 까지 공부 계획 세우기
		21일 11~13	정처기 기출문제1 + 오답정리
			13~15	정처기 기출문제2 + 오답정리
			15~17	정처기 기출문제3 + 오답정리
			17~18 	정처기 기출문제4 + 오답정리
			18~20 	집가기
			20~24	정처기 기출문제5 + 오답정리
		5월 23 ==
		5월 24일 정처기 시험 오전 8시 40분 
		11시 집도착 가정
			11~12	점심
			12~완료할때까지	스프링 프레임워크 환경설정 해보기
		5월 25일
			1시 ~ 2시 :(사진게시판) 뒷단 Dao부터 작업해보기 (ex 사진게시판 글쓰기 insert 사진게시판 수정하기 update 조회하기 select+update )
			2시 ~ 5시 :(사진게시판) service 작업하기 유효성 검사를 찾아봐서하기 excpetion 클래스를 명칭마다 클래스를 생성하고 분리해서 해보자+ 여유가 있다면 정규표현식도 해보자
			5시 ~ 7시 : 저녁
			7시 ~ 9시 : Controller 작업하기 
	 -->
        
    <jsp:include page="../include/header.jsp" />

    <div class="content">
        <br><br>
        <div class="innerOuter">
            <h2>게시글 상세보기</h2>
            <br>

            <a class="btn btn-secondary" style="float:right;" href="/spring/boards?page=${ pi.currentPage }">목록으로</a>
            <br><br>

            <table id="contentArea" algin="center" class="table">
                <tr>
                    <th width="100">제목</th>
                    <td colspan="3">${ board.boardTitle }</td>
                </tr>
                <tr>
                    <th>작성자</th>
                    <td>${ board.boardWriter }</td>
                    <th>작성일</th>
                    <td>${ board.createDate }</td>
                </tr>
                <tr>
                    <th>첨부파일</th>
                    	<c:choose>
                    		<c:when test="${ not empty board.changeName }">
                    			<td colspan="3">
                        			<a href="${ board.changeName }" download="${ board.originName }">${ board.originName }</a>
                        		</td>
                        	</c:when>
                        	<c:otherwise>
                        		<td colspan="3">
			                    	첨부파일이 존재하지 않습니다
     							</td>
                        	</c:otherwise>
                       </c:choose>
                       
                    </tr>
                <tr>
                    <th>내용</th>
                    <td colspan="3"></td>
                </tr>
                <tr>
                    <td colspan="4"><p style="height:150px;">${ board.boardContent }</p></td>
                </tr>
            </table>
            <br>

            <div align="center">
                <!-- 수정하기, 삭제하기 버튼은 이 글이 본인이 작성한 글일 경우에만 보여져야 함 -->
                <a class="btn btn-primary" href="">수정하기</a>
                <a class="btn btn-danger" href="">취소하기</a>
            </div>
            <br><br>

            <table id="replyArea" class="table" align="center">
                <thead>
                    <tr>
                        <th colspan="2">
                            <textarea class="form-control" name="" id="content" cols="55" rows="2" style="resize:none; width:100%;"></textarea>
                        </th>
                        <th style="vertical-align:middle"><button class="btn btn-secondary">등록하기</button></th> 
                    </tr>
                    <tr>
                        <td colspan="3">댓글(<span id="rcount">3</span>)</td>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <th>user02</th>
                        <td>ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ</td>
                        <td>2026-05-12</td>
                    </tr>
                    <tr>
                        <th>user01</th>
                        <td>재밌어요</td>
                        <td>2026-05-11</td>
                    </tr>
                    <tr>
                        <th>admin</th>
                        <td>댓글입니다!!</td>
                        <td>2026-05-10</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <br><br>

    </div>
    
    <jsp:include page="../include/footer.jsp" />
    
</body>
</html>