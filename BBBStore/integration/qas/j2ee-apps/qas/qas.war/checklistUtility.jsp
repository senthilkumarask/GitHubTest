<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>
	<dsp:importbean
		bean="/com/bbb/integration/interactive/InteractiveChecklistFormHandler" />
		<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:getvalueof var="listOfSites"
		bean="InteractiveChecklistFormHandler.listOfSites" />
		 <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<html>
	<head>
	<title>Interactive Checklist Utility Page</title>
	<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css" rel="stylesheet" integrity="sha384-T8Gy5hrqNKT+hzMclPo118YTQO6cYprQmhrYwIiQ/3axmI1hQomh7Ud2hPOy8SP1" crossorigin="anonymous">
		<style type="text/css">
			body{
				font-family: Arial;
				padding-top: 50px;
			}
			i{
				color: #f00 !important;
   				margin-right: 5px;
			}
			.disable{
				color: #666;
    			cursor: not-allowed;;
			}
			.checklist-utility form{
			    margin:0 30px; 
			}
			h1,.checklist-utility form{
				margin-left: 100px;
			}
			.checklist-utility form .submitBtn{
			        margin-left: 238px;
				    height: 35px;
				    width: 100px;
				    background-color: #5b9bd5;
				    color: #fff;
				    font-size: 16px;
				    border: 1px solid #000;
				    text-transform: uppercase;
				    display: block;
			}
			.checklist-utility form label{
			    margin-bottom: 20px;
			    margin-top: 5px;
			}
			.divWrap{
			    display: block;
			    margin-bottom: 20px;
			    clear: both;
			}
			.checklist-utility table{
			    width: 100%;
			    margin: 0 auto;
			    table-layout: fixed; 
			}
			.checklist-utility table thead{
			    background-color: #5b9bd5;
			    color: #fff;
			    height: 40px;
			    font-size: 15px;
			}
			.checklist-utility table thead tr{
			    height: 40px;
			}

			.checklist-utility table th{
			    text-align: left;
			    border-bottom-width: 4px;
			    min-height: 50px;
			}
			.checklist-utility table td{
			    min-width: 28%;
			    font-family: Arial;
			    font-size: 14px;
			    padding: 10px 10px 10px 5px;
			    color: #000;
			}
			.checklist-utility table td,
			.checklist-utility table th{
			    padding: 10px;
			    word-wrap: break-word;
			}

			.checklist-utility table tbody tr{
			    background-color: #eaeff7;
			}    
			.checklist-utility table tbody tr:nth-child(odd) {
			    background: #d2deef;
			}

			.checklist-utility table tbody tr:nth-child(even) {
			    background: #eaeff7;
			}
			.checklist-utility table tbody tr td:first-child{
			    padding: 10px 10px 10px 5px;
			    color: #0047b6;
			    border-left:none;
			    text-decoration: underline;   
			}
			.checklist-utility tbody td:last-child{
			    border-right:none;   
			}
			.checklist-utility tbody span{
			    display: block;
			    margin-bottom: 5px;
			}

			.checklist-utility-form .divWrap label{
			    font-size: 15px;
			    width: 70px;
			    display: inline-table;
			    float: left;
			}

			.checklist-utility-results{
				margin-top: 20px;
			}
			select{
				background-color: #5b9bd5 !important;
    			border: 1px solid #000 !important;
    			color: #fff;
    			font-size: 15px;
    			display: inline-table;
			}
			
			select,
			#skuIds{
			    width: 270px;
			    height: 30px !important;
			}
			
			#skuIds,
			#skuIds:focus,
			#skuIds:active{
			    width: 268px;
			    background-color: #5b9bd5 !important;
			    border: 1px solid #000 !important;
			    color: #fff;
			    margin-right: 20px;
			    text-indent: 5px;
			    font-size: 16px;
			   	line-height: 1;
			   	display: inline-table;
			   	float: left;
			}
			.checklist-utility .note-content{
			    font-size: 14px;
			}
			::-webkit-input-placeholder {
			   color: #fff;
			}

			:-moz-placeholder { /* Firefox 18- */
			   color: #fff;  
			}

			::-moz-placeholder {  /* Firefox 19+ */
			   color: #fff;  
			}

			:-ms-input-placeholder {  
			   color: #fff;  
			}
			.clearfix:before,
			.clearfix:after {
			    content: '\0020';
			    display: block;
			    overflow: hidden;
			    visibility: hidden;
			    width: 0;
			    height: 0;
			}
			.clearfix:after {
			    clear: both;
			}
			.disabledElm {
				color: #949494;
			}
			.errorInfo{
				display:block !important;
				color:#f00;
				margin-top:20px;
			}
			.errorInfo .errorDesc{
				color: #000;
				margin-left:10px;
			}
			.hide{
				display:none;
			}
			.show{
				display:block;
			}
		</style>
	</head>
	<body>
		<div class="checklist-utility">
			<h1>Interactive Checklist Utility Page</h1>
			<div class="checklist-utility-form">
				<dsp:form id="icInfo"action="#" method="post">
					<div class="divWrap clearfix">
	                    <label for="siteIds">SITE ID:</label>
						<dsp:select bean="InteractiveChecklistFormHandler.currentSite">
							<dsp:tagAttribute name="id" value="siteIds"/>
							<dsp:tagAttribute name="class" value="siteIds"/>
							<c:forEach items="${listOfSites}" var="entry">
								<dsp:option value="${entry.key}">${entry.value}</dsp:option>
							</c:forEach>
						</dsp:select>
	                </div>
	                
	                <div class="divWrap clearfix">
	                    <label for="skuIds">SKU ID:</label>
	                    <dsp:input type="text" bean="InteractiveChecklistFormHandler.skuIds"  name="skuIds" id="skuIds">
	                    	<dsp:tagAttribute name="placeholder" value="Please enter a SKU ID"/>	
	                    </dsp:input>
	                     <span class="note-content">Note: For multiple SKUs enter comma separated values</span>
	                </div>
					
					<dsp:input bean="InteractiveChecklistFormHandler.fetchRegCatInfo"
									type="submit" value="submit" iclass='submitBtn'/>
					<dsp:input bean="InteractiveChecklistFormHandler.interactiveSuccessURL" type="hidden" value="${contextPath}/checklistUtility.jsp" />
					<dsp:input bean="InteractiveChecklistFormHandler.interactiveErrorURL" type="hidden" value="${contextPath}/checklistUtility.jsp" />
				</dsp:form>
			</div>
 	<dsp:getvalueof var ="utilityMap" bean="InteractiveChecklistFormHandler.utilityMap"/>
	  <dsp:getvalueof var ="invalidSkus" bean="InteractiveChecklistFormHandler.invalidSkus"/>
	    <dsp:getvalueof var ="skuNotMapped" bean="InteractiveChecklistFormHandler.skuNotMapped"/>
	    <dsp:getvalueof var ="skuURL" bean="InteractiveChecklistFormHandler.skuURL"/>
	    <dsp:getvalueof var ="serverNameUS" bean="InteractiveChecklistFormHandler.serverNameUS"/>
	    <dsp:getvalueof var ="serverNameBABY" bean="InteractiveChecklistFormHandler.serverNameBABY"/>
	    <dsp:getvalueof var ="serverNameCA" bean="InteractiveChecklistFormHandler.serverNameCA"/>
	  <c:if test="${fn:length(skuNotMapped) gt 0}">
		Skus not mapped to any category : ${skuNotMapped}
	</c:if>
	<br>
	<c:if test="${fn:length(invalidSkus) gt 0}">
		Invalid skus : ${invalidSkus}
	</c:if>
			<div class="checklist-utility-results">
				<table>
					<c:set var="count" value="1" />
					<c:forEach items="${utilityMap}" var="entry">
						<c:if test="${count == 1 }">
							<thead>
								<tr class="thead">
									<th>SKU ID</th>
									<c:forEach items="${entry.value}" var="list">
										<th>${list.registryTypeDescription}</th>
										<c:set var="count" value="${count + 1 }" />
									</c:forEach>
								</tr>
							</thead>
						</c:if>
					</c:forEach>
					<tbody>
						<c:forEach items="${utilityMap}" var="entry">
							<tr>
							
								<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
										<dsp:param name="skuId" value="${entry.key}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
										</dsp:oparam>
									</dsp:droplet>
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
						
						<c:choose>
						<c:when test="${skuURL eq 'BedBathUS'}">
						<td><a href="https://${serverNameUS}/store${finalUrl}?skuId=${entry.key}">${entry.key}</a></td>
						</c:when>
						<c:when test="${skuURL eq 'BuyBuyBaby'}">
						<td><a href="https://${serverNameBABY}/store${finalUrl}?skuId=${entry.key}">${entry.key}</a></td>
						</c:when>
						<c:when test="${skuURL eq 'BedBathCanada'}">
						<td><a href="https://${serverNameCA}/store${finalUrl}?skuId=${entry.key}">${entry.key}</a></td>
						</c:when>
						</c:choose>
								<c:forEach items="${entry.value}" var="list">
									<td>
										<span <c:if test="${list.c1Disabled || list.c1ShowOnChecklist eq 'false' || list.c1Deleted}"> class = "disable" </c:if>><c:if test="${list.c1Disabled}"><i class="fa fa-ban"></i></c:if><c:if test="${list.c1ShowOnChecklist eq 'false'}"><i class="fa fa-times"></i></c:if><c:if test="${list.c1Deleted}"><i class="fa fa-trash-o"></i></c:if>C1: ${list.c1Category}</span>
										<span <c:if test="${list.c2Disabled || list.c2ShowOnChecklist eq 'false' || list.c2Deleted}"> class = "disable" </c:if>><c:if test="${list.c2Disabled}"><i class="fa fa-ban"></i></c:if><c:if test="${list.c2ShowOnChecklist eq 'false'}"><i class="fa fa-times"></i></c:if><c:if test="${list.c2Deleted}"><i class="fa fa-trash-o"></i></c:if>C2: ${list.c2Category}</span>
										<span <c:if test="${list.c3Disabled || list.c3ShowOnChecklist eq 'false' || list.c3Deleted}"> class = "disable" </c:if>><c:if test="${list.c3Disabled}"><i class="fa fa-ban"></i></c:if><c:if test="${list.c3ShowOnChecklist eq 'false'}"><i class="fa fa-times"></i></c:if><c:if test="${list.c3Deleted}"><i class="fa fa-trash-o"></i></c:if>C3: ${list.c3Category}</span>
									</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<c:if test="${count > 1 }">
				<div>
				<div class="errorInfo fa fa-ban"><span class="errorDesc">indicates that category is disabled because IS_DISABLED flag is true</span></div>
				<div class="errorInfo fa fa-times"><span class="errorDesc">indicates that category is disabled because SHOW_ON_CHECKLIST flag is false</span></div>
				<div class="errorInfo fa fa-trash-o"><span class="errorDesc">indicates that category is disabled because IS_DELETED flag is true</span></div>
			</div>
			</c:if>
			</div>
			
		</div>
	</body>
</html>
</dsp:page>