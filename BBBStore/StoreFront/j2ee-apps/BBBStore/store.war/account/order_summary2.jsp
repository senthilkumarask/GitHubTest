<dsp:page>
<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
<dsp:importbean bean="com/bbb/account/OrderHistoryDroplet" />
<dsp:importbean bean="com/bbb/account/OrderSummaryDetails" />
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:attribute name="pageWrapper">orderDetailWrapper myAccount useBazaarVoice</jsp:attribute>
    
    <%-- use url param to show all orders, for debugging only --%>
    <c:set var="showAll" scope="request" value="false"/>
	<c:if test="${not empty param.showAll}">
		<c:set var="showAll" scope="request" value="${param.showAll}"/>
	</c:if>
    
    <jsp:body>
    
   		<%@ page import="com.bbb.constants.BBBAccountConstants"%>
		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12">
				<h1 class="account fl">
					<bbbl:label key="lbl_personalinfo_myaccount"
						language="${pageContext.request.locale.language}" />
				</h1>
				<h3 class="subtitle fl"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></h3>
                <div class="clear"></div>
			</div>
			<div class="grid_2">
				<c:import url="/account/left_nav.jsp">
				  <c:param name="currentPage"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
			</div>
			<div class="grid_10 ordersSummary trackOrderDetail">
				<div class="clearfix prefix_1">
				
					<div id="ordersSummaryTabs" class="alpha omega grid_9 clearfix">
						<ul class="categoryProductTabsLinks noprint"> 
							<li><a href="#onlineOrders">Online</a></li>
							<li><a href="#instoreOrders">In-Store</a></li>
						</ul>
						<div id="onlineOrders">
							<dsp:droplet name="OrderHistoryDroplet">
							<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:param name="orderType" value="ONL"/>
							<dsp:oparam name="orderOutputStart">
								<div class="grid_9 alpha omega clearfix ordersHeader">
									<table title="Track Order Search Results"> 
										<thead> 
											<tr> 
												<th class="width_3" scope="col">Order Date</th> 
												<th class="width_3" scope="col">Order Number</th> 
												<th class="width_1" scope="col">Total</th> 
												<th class="width_2 talign-rt" scope="col">Order Status</th> 
											</tr> 
										</thead>
									</table>
								</div>
							</dsp:oparam>
							<dsp:oparam name="orderOutput">
							
							
								<c:set var="onlineCounter" value="0" />			
							
								<%-- Need 2 loops for online. 
									First loop show only ATG
									Second loop show only legacy --%>
									
								<div id="onlineOrdersContent">
								
									<%-- this loop is for atg orders only (legacyOrder==false) --%>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="OrderList" />
										<dsp:param name="elementName" value="Orders" />
										<dsp:oparam name="output">
										
											<dsp:getvalueof var="legacyOrder" param="Orders.wsOrder" />
											<c:if test="${legacyOrder != true}">
										
												<dsp:getvalueof var="orderType" param="Orders.orderType" />
												<dsp:getvalueof	var="orderId" param="Orders.orderNumber" />
												<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
												<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
												<dsp:getvalueof var="totalAmt" param="Orders.totalAmt" />
												<dsp:getvalueof var="orderDate" param="Orders.orderDate" />
												<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
												<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />
												
															
												
												<c:choose>
													<c:when test="${legacyOrder}">
														<c:set var="splitOrder" value="false"/>																								
														<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
														<c:set var="orderType" value="online"/>
													</c:when>
													<c:when test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
														<c:set var="splitOrder" value="true"/>
														<c:set var="orderNum" value="${onlineOrderNumber }"/>
														<c:set var="orderType" value="online"/>												
														<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
														<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
													</c:when>											
													<c:otherwise>
														<c:set var="splitOrder" value="false"/>
														<c:if test="${not empty onlineOrderNumber}">
															<c:set var="orderNum" value="${onlineOrderNumber }"/>
															<c:set var="orderType" value="online"/>												
															<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
															<c:set var="bopusJsonData" value=''/>													
														</c:if>
														<c:if test="${not empty bopusOrderNumber}">
															<c:set var="orderNum" value="${bopusOrderNumber }"/>
															<c:set var="orderType" value="bopus"/>																					
															<c:set var="onlineJsonData" value=''/>				
															<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
														</c:if>
													</c:otherwise>
												</c:choose>
																		
												
												<div class="grid_9 alpha omega clearfix orderHeader" > 
												<%-- 
												orderType ${orderType}<br />
												legacyOrder:${legacyOrder}<br />										
												onlineCounter${onlineCounter}<br />
												showAll:${showAll}<br />
												--%>
													<div class="grid_3 alpha omega " >												
														<c:choose>
															<c:when test="${legacyOrder || (onlineCounter >= 10 && showAll != 'true')}">														
																<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}'  href="#">+</a>
															</c:when>
															<c:otherwise>
																<a class="collapseLink" href="#">-</a>
															</c:otherwise>
														</c:choose>												
														<fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
													</div> 
													<div class="grid_3 alpha omega " >
														${orderNum }
														<c:if test="${splitOrder == 'true'}">
															<br />${bopusOrderNumber }
														</c:if>
														
													</div> 
													<div class="grid_1 alpha omega " ><dsp:valueof converter="currency" value="${totalAmt}" /></div> 
													<div class="grid_2 alpha omega talign-rt noMarRight" >${orderStatus }</div> 
												</div>
																	
												<div class="grid_9 alpha omega clearfix orderContent">  
													<c:if test="${onlineCounter >= 10 && showAll != 'true'}">
														style="display:none"
													</c:if>
												
												
													<%-- 
													onlineOrderNumber::${onlineOrderNumber }<br />
													bopusOrderNumber::${bopusOrderNumber }<br />
													--%>							
													
													<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>																						
													
													<%--only show first 10 ATG order details, or showAll param was passed --%>												
													<c:if test="${onlineCounter < 10 || showAll == 'true'}">															  
														<dsp:include page="frags/order_summary_frag.jsp">
															<dsp:param name="orderNum" value="${orderNum}"/>
															<dsp:param name="orderType" value="${orderType}"/>
															<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>														
														</dsp:include>
														
														<c:if test="${splitOrder == 'true' }">																
															<hr class="clearfix" />															
															<dsp:include page="frags/order_summary_frag.jsp">
																<dsp:param name="orderNum" value="${bopusOrderNumber}"/>
																<dsp:param name="orderType" value="bopus"/>
																<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>														
															</dsp:include>
														</c:if>
														
														<c:set var="onlineCounter" value="${onlineCounter+1}" />
													</c:if>
												</div><%-- end  <div class="orderContent ">  --%>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>			
									
									<%-- this loop is for legacy orders only (legacyOrder==true) --%>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="OrderList" />
										<dsp:param name="elementName" value="Orders" />
										<dsp:oparam name="output">
										
											<dsp:getvalueof var="legacyOrder" param="Orders.wsOrder" />
											<c:if test="${legacyOrder == true}">
											
										
												<dsp:getvalueof var="orderType" param="Orders.orderType" />
												<dsp:getvalueof	var="orderId" param="Orders.orderNumber" />
												<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
												<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
												<dsp:getvalueof var="totalAmt" param="Orders.totalAmt" />
												<dsp:getvalueof var="orderDate" param="Orders.orderDate" />
												<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
												<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />
												<dsp:getvalueof var="legacyOrder" param="Orders.wsOrder" />
															
												
												<c:set var="splitOrder" value="false"/>																								
												<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
												<c:set var="orderType" value="online"/>
																		
												
												<div class="grid_9 alpha omega clearfix orderHeader" > 
												<%-- 
												orderType ${orderType}<br />
												legacyOrder:${legacyOrder}<br />										
												onlineCounter${onlineCounter}<br />
												showAll:${showAll}<br />
												--%>
													<div class="grid_3 alpha omega " >												
														<c:choose>
															<c:when test="${legacyOrder || showAll != 'true'}">														
																<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}'  href="#">+</a>
															</c:when>
															<c:otherwise>
																<a class="collapseLink" href="#">-</a>
															</c:otherwise>
														</c:choose>												
														<fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
													</div> 
													<div class="grid_3 alpha omega " >
														${orderNum }
														<c:if test="${splitOrder == 'true'}">
															<br />${bopusOrderNumber }
														</c:if>
														
													</div> 
													<div class="grid_1 alpha omega " ><dsp:valueof converter="currency" value="${totalAmt}" /></div> 
													<div class="grid_2 alpha omega talign-rt noMarRight" >${orderStatus }</div> 
												</div>
																	
												<div class="grid_9 alpha omega clearfix orderContent" <c:if test="${showAll != 'true'}">style="display:none"</c:if>	> 
												
													<%-- 
													onlineOrderNumber::${onlineOrderNumber }<br />
													bopusOrderNumber::${bopusOrderNumber }<br />
													--%>							
													
													<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>																						
													
													<%--only show first 10 ATG order details, or showAll param was passed --%>												
													<c:if test="${showAll == 'true'}">															  
														<dsp:include page="frags/order_summary_frag.jsp">
															<dsp:param name="orderNum" value="${orderNum}"/>
															<dsp:param name="orderType" value="${orderType}"/>
															<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>														
														</dsp:include>														
													</c:if>
												</div><%-- end  <div class="orderContent ">  --%>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>		
									
								</div><%-- end <div id="onlineOrdersContent"> --%>							
							</dsp:oparam>
							<dsp:oparam name="orderEmpty">
								<bbbe:error key="err_orderhistory_noorder"
											language="${pageContext.request.locale.language}" />
							</dsp:oparam>
							<dsp:oparam name="error">
								<bbbe:error key="err_orderhistory_techerr"
											language="${pageContext.request.locale.language}" />
							</dsp:oparam>
						</dsp:droplet>
						</div>						
						<div id="instoreOrders">
												
						 	<dsp:droplet name="OrderHistoryDroplet">
							<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:param name="orderType" value="TBS"/>
							<dsp:oparam name="orderOutputStart">
								<div class="grid_9 alpha omega clearfix ordersHeader">
									<table title="Track Order Search Results"> 
										<thead> 
											<tr> 
												<th class="width_3" scope="col">Order Date</th> 
												<th class="width_3" scope="col">Order Number</th> 
												<th class="width_1" scope="col">Total</th> 
												<th class="width_2 talign-rt" scope="col">Order Status</th> 
											</tr> 
										</thead>
									</table>
								</div>
							</dsp:oparam>
							<dsp:oparam name="orderOutput">
								<div id="instoreOrdersContent">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="OrderList" />
									<dsp:param name="elementName" value="Orders" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="orderType" param="Orders.orderType" />
										<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
										<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
										<dsp:getvalueof var="totalAmt" param="Orders.totalAmt" />
										<dsp:getvalueof var="orderDate" param="Orders.orderDate" />
										<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
										<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />
													
										
										<c:choose>
											<c:when test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
												<c:set var="splitOrder" value="true"/>
												<c:set var="orderNum" value="${onlineOrderNumber }"/>
												<c:set var="orderType" value="online"/>
												<c:set var="legacyOrder" value="false"/>
												<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
												<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
											</c:when>											
											<c:otherwise>
												<c:set var="splitOrder" value="false"/>
												<c:if test="${not empty onlineOrderNumber}">
													<c:set var="orderNum" value="${onlineOrderNumber }"/>
													<c:set var="orderType" value="online"/>
													<c:set var="legacyOrder" value="false"/>
													<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>													
												</c:if>
												<c:if test="${not empty bopusOrderNumber}">
													<c:set var="orderNum" value="${bopusOrderNumber }"/>
													<c:set var="orderType" value="bopus"/>
													<c:set var="legacyOrder" value="false"/>													
													<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
												</c:if>
											</c:otherwise>
										</c:choose>
											
										<div class="grid_9 alpha omega clearfix orderHeader"> 
											<div class="grid_3 alpha omega " >
												<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}' data-legacyOrder='' href="#">+</a>
												<fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
											</div>											
											<div class="grid_3 alpha omega " >
												${orderNum }
												<c:if test="${splitOrder == 'true'}">
													<br />${bopusOrderNumber }
												</c:if>
											</div>  
											<div class="grid_1 alpha omega " ><dsp:valueof converter="currency" value="${totalAmt}" /></div> 
											<div class="grid_2 alpha omega talign-rt noMarRight" >${orderStatus }</div> 
										</div>
															
										<div class="grid_9 alpha omega clearfix orderContent" style="display: none;">	
										
											
											<%-- 
											onlineOrderNumber::${onlineOrderNumber }<br />
											bopusOrderNumber::${bopusOrderNumber }<br />
											--%> 
										
											<%-- Need droplet here that will pass orderID, and bring back  TrackOrderVO.bbbOrderVO --%>
											<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>
											
											<c:if test="${showAll == 'true'}">
												<%-- --%>  
												<dsp:include page="frags/order_summary_frag.jsp">
													<dsp:param name="orderNum" value="${orderNum}"/>
													<dsp:param name="orderType" value="${orderType}"/>
													<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>														
												</dsp:include>
												
												<c:if test="${splitOrder == 'true' }">											
													<hr />
												
													<dsp:include page="frags/order_summary_frag.jsp">
														<dsp:param name="orderNum" value="${bopusOrderNumber}"/>
														<dsp:param name="orderType" value="bopus"/>
														<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>														
													</dsp:include>
												</c:if>												
											</c:if>	
										</div><%-- end  <div class="orderContent ">  --%>
									</dsp:oparam>
								</dsp:droplet>			
								</div>							
							</dsp:oparam>
							<dsp:oparam name="orderEmpty">
								<bbbe:error key="err_orderhistory_noorder"
											language="${pageContext.request.locale.language}" />
							</dsp:oparam>
							<dsp:oparam name="error">
								<bbbe:error key="err_orderhistory_techerr"
											language="${pageContext.request.locale.language}" />
							</dsp:oparam>
						</dsp:droplet>
						
								
						</div>
					</div>
				</div>
			</div>
		</div>
		</jsp:body>
		<jsp:attribute name="footerContent">
			<script type="text/javascript">
			       if(typeof s !=='undefined') {
			        	s.pageName='My Account > My Orders';
			        	s.channel='My Account';
			        	s.prop1='My Account';
			        	s.prop2='My Account';
			        	s.prop3='My Account';
			                
			            var s_code=s.t();
			            if(s_code)document.write(s_code);           
			           }
			           function omnitureExternalLinks(data){
			             	if (typeof s !== 'undefined') {
			             	externalLinks(data);
			             	}
			             }
			</script>
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>