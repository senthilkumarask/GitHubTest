<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<style type="text/css">
	element.style {
	    table-layout: fixed;
	}
 table {
 	border: 0 none;
    border-collapse: collapse;
    border-spacing: 0;
 }
 td {
    word-wrap: break-word;
}
</style>
</head>
<body>
<table width="100%" border="1" cellpadding="0" cellspacing="0"
    style="table-layout: fixed;">
    <colgroup>
        <col width="100">
        <col width="500">        
    </colgroup>
    <tr>
        <th colspan="2">
        <h3>Session Properties</h3>
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
    %>
    <tr>
        <td width="20%"><%="weblogic.Name"%></td>
        <td width="80%"><%=p.getProperty("weblogic.Name") %></td>
    </tr>
    <tr>
        <td width="20%"><%="java.rmi.server.codebase"%></td>
        <td width="80%"><%=p.getProperty("java.rmi.server.codebase") %></td>
    </tr>
        <tr>
            <td width="20%"><c:out value="atg.servlet.request" /></td>
            <td width="80%"><pre><%=request.getAttribute("atg.servlet.request") %></pre></td>
        </tr>
     <tr>
         <td width="20%"><c:out value="javax.servlet.jsp.jspRequest" /></td>
         <td width="80%"><pre><%=pageContext.getAttribute("javax.servlet.jsp.jspRequest") %></pre></td>
     </tr>
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
</table>
</body>
</html>