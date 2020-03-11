<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>	
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판</title>
    <style>
    	.pagination {
            justify-content: center;
        }
        #searchForm{
            position: relative;
        }

        #searchForm>*{
            top : 0;
        }
        
        .boardTitle > img{
        	width: 50px;
        	height: 50px;
        }
        
	</style>
	
</head>
<body>
	<div class="container">
		<jsp:include page="../common/header.jsp"/>
		<jsp:include page="../common/nav.jsp"/>

		<div class="container">
	        <div>
	            <table class="table table-hover table-striped" id="list-table">
	                <thead>
	                    <tr>
	                        <th>글번호 </th>
	                        <th>카테고리 </th>
	                        <th>제목</th>
	                        <th>작성자</th>
	                        <th>조회수</th>
	                        <th>작성일</th>
	                    </tr>
	                </thead>
	                <tbody>
						<c:if test="${empty list }">
							<tr>
								<td colspan="6">존재하는 게시글이 없습니다.</td>
							</tr>
						</c:if>
						
						<c:if test="${!empty list }">
							<c:forEach var="board" items="${list}" varStatus="vs">
							
								<tr>
									<td>${board.boardNo}</td>
									<td>${board.boardCategory}</td>
									<td class="boardTitle">
										
										<!----------------- 썸네일 부분 ----------------->
				                        
				                        <!-- scope : page, request, session, application -->
				                        <c:set var="src" value="${contextPath}/resources/images/noImage.png"/>
				                        <c:forEach var="th" items="${thList}">
				                        	<c:if test="${th.boardId==board.boardNo}">
				                        		<c:set var="src" value="${contextPath}/resources/uploadFiles/${th.fileChangeName}"/>
				                        	</c:if>
				                       	</c:forEach>
				                        
				                        <img src="${src}">
										
										${board.boardTitle}
									</td>
									
									<td>${board.boardWriter}</td>
									<td>${board.boardCount}</td>
									<td>${board.boardModifyDate}</td>
									
								</tr>
							</c:forEach>
						</c:if>
	                </tbody>
	            </table>
	        </div>
	
	        <hr>
	        
	        <%-- 로그인이 되어있는 경우 --%>
	       	<c:if test="${!empty loginMember }">
		        <button type="button" class="btn btn-success float-right" id="insertBtn" onclick="location.href='insertForm';">글쓰기</button>
	        </c:if>

 	       	<!--------------------------------- 페이징바  ---------------------------------->
			<div style="clear: both;">
	            <ul class="pagination">
	            	<c:if test="${pInf.currentPage > 1}">
		                <li>
		                	<!-- 맨 처음으로(<<) -->
		                    <a class="page-link text-success" 
		                    	href=" 
		                    	<c:url value="list">
		                    		<c:if test="${!empty param.searchKey }">
						        		<c:param name="searchKey" value="${param.searchKey}"/>
						        		<c:param name="searchValue" value="${param.searchValue}"/>
						        	</c:if>
						        	
						        	<%-- <c:if test="${!empty param.searchValue }">
						        		<c:param name="searchValue" value="${param.searchValue}"/>
						        	</c:if> --%>
						        	
						        	<c:if test="${!empty paramValues.searchCategory }">
							       		<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
							       			<c:param name="searchCategory" value="${ct}"/>
							       		</c:forEach>
						       		</c:if>
		                    		<c:param name="currentPage" value="1"/>
		                    	</c:url>
	                    	">
			                    &lt;&lt;
			                </a>
		                </li>
		                
		                <li>
		                	<!-- 이전으로(<) -->
	                   		<a class="page-link text-success" 
		                    	href=" 
		                    	<c:url value="list">
		                    		<c:if test="${!empty param.searchKey }">
						        		<c:param name="searchKey" value="${param.searchKey}"/>
						        		<c:param name="searchValue" value="${param.searchValue}"/>
						        	</c:if>
						        	
						        	<c:if test="${!empty paramValues.searchCategory }">
							       		<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
							       			<c:param name="searchCategory" value="${ct}"/>
							       		</c:forEach>
						       		</c:if>
						       		
		                    		<c:param name="currentPage" value="${pInf.currentPage-1}"/>
		                    	</c:url>
	                    	">
			                    &lt;
			                </a>
		                </li>
	                </c:if>
	                
	                <!-- 10개의 페이지 목록 -->
	                <c:forEach var="p" begin="${pInf.startPage}" end="${pInf.endPage}">
	                
	                
	                	<c:if test="${p == pInf.currentPage}">
			                <li>
			                    <a class="page-link">${p}</a>
			                </li>
		                </c:if>
	                	
	                	<c:if test="${p != pInf.currentPage}">
	                		<li>
		                    	<a class="page-link text-success" 
			                    	href=" 
			                    	<c:url value="list">
			                    		<c:if test="${!empty param.searchKey }">
							        		<c:param name="searchKey" value="${param.searchKey}"/>
							        		<c:param name="searchValue" value="${param.searchValue}"/>
							        	</c:if>
							        	
							        	<c:if test="${!empty paramValues.searchCategory }">
								       		<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
								       			<c:param name="searchCategory" value="${ct}"/>
								       		</c:forEach>
							       		</c:if>
							       		
			                    		<c:param name="currentPage" value="${p}"/>
			                    	</c:url>
		                    	">
				                    ${p}
				                </a>
		                	</li>
	                	</c:if>
	                	
                	</c:forEach>
	                
	                <!-- 다음 페이지로(>) -->
	                <c:if test="${pInf.currentPage < pInf.maxPage }">
		                <li>
							<a class="page-link text-success" 
		                    	href=" 
		                    	<c:url value="list">
		                    		<c:if test="${!empty param.searchKey }">
						        		<c:param name="searchKey" value="${param.searchKey}"/>
						        		<c:param name="searchValue" value="${param.searchValue}"/>
						        	</c:if>
						        	
						        	<c:if test="${!empty paramValues.searchCategory }">
							       		<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
							       			<c:param name="searchCategory" value="${ct}"/>
							       		</c:forEach>
						       		</c:if>
						       		
		                    		<c:param name="currentPage" value="${pInf.currentPage+1}"/>
		                    	</c:url>
	                    	">
			                    &gt;
			                </a>
		                </li>
		                
		                <!-- 맨 끝으로(>>) -->
		                <li>
		                    <a class="page-link text-success" 
		                    	href=" 
		                    	<c:url value="list">
		                    		<c:if test="${!empty param.searchKey }">
						        		<c:param name="searchKey" value="${param.searchKey}"/>
						        		<c:param name="searchValue" value="${param.searchValue}"/>
						        	</c:if>
						        	
						        	<c:if test="${!empty paramValues.searchCategory }">
							       		<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
							       			<c:param name="searchCategory" value="${ct}"/>
							       		</c:forEach>
						       		</c:if>
						       		
		                    		<c:param name="currentPage" value="${pInf.maxPage}"/>
		                    	</c:url>
	                    	">
			                    &gt;&gt;
			                </a>
		                </li>
	                
	                </c:if>
	            </ul>
	        </div>	        

	        <div>
	            <form action="list" method="GET" class="text-center" id="searchForm" style="margin-bottom:100px;">
	            	<span>
	            		카테고리(다중 선택 가능)<br>
		                <label for="exercise">운동</label> 
		                <input type="checkbox" name="searchCategory" value="운동" id="exercise">
		                &nbsp;
		                <label for="movie">영화</label> 
		                <input type="checkbox" name="searchCategory" value="영화" id="movie">
		                &nbsp;
		                <label for="music">음악</label> 
		                <input type="checkbox" name="searchCategory" value="음악" id="music">
		                &nbsp;
		                <label for="cooking">요리</label> 
		                <input type="checkbox" name="searchCategory" value="요리" id="cooking">
		                &nbsp;
		                <label for="game">게임</label> 
		                <input type="checkbox" name="searchCategory" value="게임" id="game">
		                &nbsp;
		                <label for="etc">기타</label> 
		                <input type="checkbox" name="searchCategory" value="기타" id="etc">
		                &nbsp;
	                </span>
	                <br>
	                <select name="searchKey" class="form-control" style="width:100px; display: inline-block;">
	                    <option value="title">글제목</option>
	                    <option value="content">내용</option>
	                    <option value="titcont">제목+내용</option>
	                </select>
	                <input type="text" name="searchValue" class="form-control" style="width:25%; display: inline-block;">
	                <button class="form-control btn btn-success" style="width:100px; display: inline-block;">검색</button>
	            </form>
	            
            	<script>
					$(function(){
						var searchKey = "${param.searchKey}";
						var searchValue = "${param.searchValue}";
						
						if(searchKey != "null" && searchValue != "null"){
							$.each($("select[name=searchKey] > option"), function(index, item){
								if($(item).val() == searchKey){
									$(item).prop("selected","true");
								} 
							});
							$("input[name=searchValue]").val(searchValue);
						}
					});
					
					// script 태그 내에서도 JSTL 사용 가능
					// 서버 동작 시 코드를 읽어 나가는 우선 순서 
					// JAVA > EL/JSTL > HTML > Javascript 
					// -> EL/JSTL이 먼저 완성 되어있을 때 javascript와 혼용해서 사용해도 문제 없음.
					<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
						$.each($("input[name=searchCategory]"), function(index, item){
							console.log(1);
							if($(item).val() == "${ct}"){
								$(item).prop("checked","true");
							} 
						});
						
					</c:forEach>
				</script>
	            
	        </div>
    	</div>
    	
		<jsp:include page="../common/footer.jsp"/>
	</div>
	
	<script>
		// 게시글 상세보기 기능 (jquery를 통해 작업)
		$(function(){
			$("#list-table td").click(function(){
				var boardNo = $(this).parent().children().eq(0).text();
				// 쿼리스트링을 이용하여 get 방식으로 글 번호를 server로 전달
				<c:url var="detailUrl" value="detail">
              		<c:if test="${!empty param.searchzKey }">
	        		<c:param name="searchKey" value="${param.searchKey}"/>
		        	</c:if>
		        	<c:if test="${!empty param.searchValue }">
		        		<c:param name="searchValue" value="${param.searchValue}"/>
		        	</c:if>
		        	
		        	<c:if test="${!empty paramValues.searchCategory }">
			       		<c:forEach var="ct" items="${paramValues.searchCategory}" varStatus="vs">
			       			<c:param name="searchCategory" value="${ct}"/>
			       		</c:forEach>
		       		</c:if>
		        	
                 	<c:param name="currentPage" value="${pInf.currentPage}"/>
               	</c:url>
				
				location.href="${detailUrl}&no="+boardNo;
				
				//location.href="${contextPath}/board/detail?no=" + boardNo +"&currentPage="+${pInf.currentPage};
			
			}).mouseenter(function(){
				$(this).parent().css("cursor", "pointer");
			
			});
			
		});
		
		
	</script>
	
	
	
</body>
</html>
