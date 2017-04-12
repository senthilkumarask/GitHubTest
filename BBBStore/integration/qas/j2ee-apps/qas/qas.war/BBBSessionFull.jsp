<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<style type="text/css">
td {
    word-wrap: break-word;
}
</style>
</head>
<body>
<table width="100%" border="1" cellpadding="0" cellspacing="0"
    style="table-layout: fixed;">
    <colgroup>
        <col width="500">
    </colgroup>
    <tr>
        <th colspan="2">
        <h3>System Properties</h3>
        </th>
    </tr>
    <tr>
        <th>Key</th>
        <th>Value</th>
    </tr>
    <%@page import="java.util.Map"%>
    <%@page import="java.util.Set"%>
    <%@page import="java.util.Properties"%>
    <%@page import="java.util.Arrays"%>
    <%
        Properties p = System.getProperties();
        Set<Map.Entry<Object, Object>> set = p.entrySet();
        for (Map.Entry<Object, Object> e : set) {
    %>
    <tr>
        <td><%=e.getKey()%></td>
        <td><%="".equals(e.getValue()) ? "&nbsp;" : e.getValue()%></td>
        <%
            }
        %>
    </tr>
	<tr>
        <td width="20%">Active Promotions</td>
        <td width="80%">
        	<dsp:getvalueof var="profileId" bean="Profile.id"/>
        	<pre>
        	<%	atg.commerce.promotion.PromotionTools promotionTools = (atg.commerce.promotion.PromotionTools)atg.nucleus.Nucleus.getGlobalNucleus().resolveName("/atg/commerce/promotion/PromotionTools");
        		java.util.Collection promotions = promotionTools.getPromotions((String)pageContext.getAttribute("profileId"));
     			if(promotions!= null
     					&& promotions.size() >0) {
     				for(Object promotion: promotions) {
     					out.println(((atg.repository.RepositoryItem)promotion).getRepositoryId()+"-"+((atg.repository.RepositoryItem)promotion).getPropertyValue("displayName")+"-"+((atg.repository.RepositoryItem)promotion).getPropertyValue("description"));
     				}
     			}
        	%>
        	</pre>
       </td>
    </tr>
    <tr>
        <th colspan="2">
        <h3>attributes in $headerValues</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${headerValues}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><c:forEach var="item" items="${entry.value}"
                varStatus="status">
                <pre><c:out value="${item}" /></pre>
                <c:if test="${not status.last}">
                    <br />
                </c:if>
            </c:forEach></td>
        </tr>
    </c:forEach>

	<tr>
        <th colspan="2">
        <h3>attributes in $paramValues</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${paramValues}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><c:forEach var="item" items="${entry.value}"
                varStatus="status">
                <pre><c:out value="${item}" /></pre>
                <c:if test="${not status.last}">
                    <br />
                </c:if>
            </c:forEach></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
        <h3>attributes in $requestScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${requestScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
        <h3>attributes in $sessionScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${sessionScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
        <h3>attributes in $pageScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${pageScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
        <h3>attributes in $applicationScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${applicationScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>