<%@ page language="java" import="javax.naming.InitialContext,javax.sql.DataSource,javax.naming.NameNotFoundException"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="startTime" value="<%=System.currentTimeMillis()%>" scope="page" />
<%
	String jndi = "MiscDS"; // getServletContext().getInitParameter("JNDI");

	DataSource ds = null;

	InitialContext ctx = new InitialContext();
	try {
		ds = (DataSource) ctx.lookup(jndi);	
		pageContext.setAttribute("ds", ds);																		
	} catch (NameNotFoundException e) {
		ds = (DataSource) ctx.lookup("java:comp/env/jdbc/" + jndi);
		pageContext.setAttribute("ds", ds);	
	}
%>
<c:set var="id" value="0" scope="page" />
<c:catch var="error">
	<sql:query dataSource="${ds}" var="rs">
        select * from bbb_list
        where (list_id = ? or 0 = ?)
	<sql:param value="${id}" />
	<sql:param value="${id}" />
	</sql:query>
</c:catch>
<html>
<body>
<c:choose>
    <c:when test="${error == null}">
	<table width="100%" border="1">
	<tr>
		<c:set var="colcount" value="0" scope="page" />
		<c:forEach var="columnName" items="${rs.columnNames}">
			<th><c:out value="${columnName}"/></th>
			<c:set var="colcount" value="${colcount + 1}" scope="page"/> 
		</c:forEach>
	</tr>
	<c:set var="rowcount" value="0" scope="page" />
	<c:forEach var="row" items="${rs.rowsByIndex}">
   		<c:choose>
        	<c:when test="${rowcount % 2 == 0}">
            		<tr>
        	</c:when>
        	<c:otherwise>
            		<tr class="altrow">
        	</c:otherwise>
    		</c:choose>
		<c:set var="colcount" value="0" scope="page" />
		<c:forEach var="column" items="${row}">
			<td><c:out value="${column}"/></td>
			<c:set var="colcount" value="${colcount + 1}" scope="page"/> 
		</c:forEach>
	</tr>
	<c:set var="rowcount" value="${rowcount + 1}" scope="page"/> 
	</c:forEach>
	<c:set var="endTime" value="<%=System.currentTimeMillis()%>" scope="page" />
	<tr class="lastrow"><td colspan="<c:out value="${colcount}" />"><c:out value="${rowcount}" />&nbsp;row(s) returned in&nbsp;<c:out value="${(endTime-startTime) / 1000}"/>&nbsp;second(s)</td></tr>
	</table>

    </c:when>
    <c:otherwise>
        <div class="error"><c:out value="${error.message}"></c:out></div>
    </c:otherwise>
</c:choose>
</body>
</html>