<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>
<html>
<head>
	<title>Sucess !!</title>
	<link rel="stylesheet" href="/qas/qas.css" />
	</head>
	
	
	<dsp:importbean bean="/com/bbb/commerce/porch/formhandler/PorchOrderProcessFormHandler"/>
	<dsp:getvalueof var="porchOrders" bean="PorchOrderProcessFormHandler.porchProjectIdList"/>
 
	<table border="noborder" style="width: 600px;">
	<tr>
	<th>Order Id</th>
	<th>Job Status</th>
	<th>Porch ProjectId</th>
	<th>BookAJob Request</th>
	<th>BookAJob Response</th>

	</tr>
   <c:forEach var="porchOrder" items="${porchOrders}">
    <dsp:getvalueof var="porchCommItems" value="${porchOrder.value}"/>
	 
      
		<%
		if(null!=pageContext.getAttribute("porchCommItems")){
			java.util.List<atg.commerce.order.CommerceItem> commItems = (java.util.List<atg.commerce.order.CommerceItem>) pageContext.getAttribute("porchCommItems");
			int numberofitems=commItems.size();
			pageContext.setAttribute("numberofitems", numberofitems);
			
			%>
			
			<tr>
	<td rowspan="${numberofitems}">
		OrderId: ${porchOrder.key}
   </td>
			<%
			for (atg.commerce.order.CommerceItem comerceItem : commItems) {

				com.bbb.order.bean.BaseCommerceItemImpl citem = (com.bbb.order.bean.BaseCommerceItemImpl) comerceItem;
				
				if(citem.isPorchService()){
					   atg.repository.RepositoryItem serviceRefItem = citem.getPorchServiceRef();
					   java.lang.String bookAJobResponse = (java.lang.String)serviceRefItem.getPropertyValue("bookAJobJsonResp");
					   java.lang.String bookAJobRequest = (java.lang.String)serviceRefItem.getPropertyValue("bookAJobJsonReq");
					   java.lang.String porchProjectId = (java.lang.String)serviceRefItem.getPropertyValue("porchProjectId");
					   java.lang.String jobStatus = (java.lang.String)serviceRefItem.getPropertyValue("jobStatus");
					   
					   pageContext.setAttribute("bookAJobResponse", bookAJobResponse);
					   pageContext.setAttribute("bookAJobRequest", bookAJobRequest);
					   pageContext.setAttribute("porchProjectId", porchProjectId);
					   pageContext.setAttribute("jobStatus", jobStatus);
					 
					 
					 
					 %>
					 <td>
	jobStatus : ${jobStatus}
	</td>
	<td>
	porchProjectId : ${porchProjectId}
	</td>
	<td>
	bookAJobRequest : ${bookAJobRequest}
	</td>
	 <td>
	bookAJobResponse : ${bookAJobResponse}
	</td>
	</tr>
					 <%}
				
					 
			}
		}
	 else{
	 pageContext.setAttribute("noOrder", "true");
	 }
	%>
	<c:if test="${empty noOrder}">
	 
	
	
	</c:if>
	<c:if test="${!empty noOrder}">
	<td colspan="4">Order Not found </td>
	</c:if>
   </tr>
</c:forEach>


 </table>
</html>
</dsp:page>
