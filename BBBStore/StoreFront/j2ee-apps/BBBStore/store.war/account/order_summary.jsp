<dsp:page>
<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
<dsp:importbean bean="com/bbb/account/OrderHistoryDroplet" />
<dsp:importbean bean="com/bbb/account/OrderSummaryDetails" />
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:attribute name="pageWrapper">orderDetailWrapper myAccount useBazaarVoice orderSummary usePorch</jsp:attribute>
    
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
				<%--START International shipping Order Tracking link--%>
					 <c:if test="${internationalShippingOn}">
						<a href="international_order_summary.jsp" class="fr intOrderLinkPos">
							<bbbl:label key="lbl_international_track_order" language="${pageContext.request.locale.language}" />
						</a>  
					</c:if>
				 <%--END International shipping Order Tracking link--%>
                <div class="clear"></div>
			</div>
			<div class="grid_3">
				<c:import url="/account/left_nav.jsp">
				  <c:param name="currentPage"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
				
				<c:set var="trackOrderMsgOn"><bbbc:config key="Track_Order_Cancelled_Msg_Tag" configName="ContentCatalogKeys" /></c:set>
				<c:if test="${trackOrderMsgOn}">
					<div class="clearfix"></div>
					<div id="cancledOrderMsg" class="alert alert-info">
						<h3><bbbt:textArea key="txtarea_trackorder_cancelOrder_head" language='${pageContext.request.locale.language}'/></h3>
						<p><bbbt:textArea key="txtarea_trackorder_cancelOrder_body" language='${pageContext.request.locale.language}'/></p>
					</div>					
				</c:if>
			</div>
			<div class="grid_9 ordersSummary trackOrderDetail">
				<div class="clearfix ">
				
					<div id="ordersSummaryTabs" class="alpha omega grid_9 clearfix">
						<ul class="categoryProductTabsLinks noprint"> 
							<li><a href="#onlineOrders"><bbbl:label key="lbl_header_orders" language="${pageContext.request.locale.language}"/></a></li>
							<li id="storeBlank"><a href="#instoreOrders" ><bbbl:label key="lbl_more_orders" language="${pageContext.request.locale.language}"/>  </a> 
							<a href="${contextPath}/static/moreOrderInformation" class="fl moreOrderinfo" data-reveal-id="infoModal" data-reveal-ajax="true">
							<!-- <a href="/store/static/moreOrderInformation" class="popup moreOrderinfo"> -->
							<img class="quesMark marBottom_5" width="13" height="13" src="/_assets/global/images/LTL/images.jpg" alt="Question Mark" /> 
							</a></li>
						</ul>
						<div id="onlineOrders">
							<dsp:droplet name="OrderHistoryDroplet">
							<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:param name="orderType" value="ONL"/>
							<dsp:oparam name="orderOutputStart">
								<div class="grid_9 alpha omega clearfix ordersHeader">
									<div class="grid_3 alpha  orderDate" ><bbbl:label key="lbl_trackorder_date" language="${pageContext.request.locale.language}" /></div> 
									<div class="grid_2 orderNumber" ><bbbl:label key="lbl_trackorder_orderId" language="${pageContext.request.locale.language}" /></div> 
									<div class="grid_2 orderTotal" ><bbbl:label key="lbl_trackorder_total" language="${pageContext.request.locale.language}" /></div> 
									<div class="grid_2 omega orderStatus" ><bbbl:label key="lbl_trackorder_status" language="${pageContext.request.locale.language}" /></div>
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
													<c:when test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
														<c:set var="splitOrder" value="true"/>
														<c:set var="orderNum" value="${onlineOrderNumber }"/>
														<c:set var="orderType" value="online"/>												
														<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}","orderType":"online","frag":"atg","wsOrder":"${legacyOrder}"}'/>
														<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}","orderType":"bopus","frag":"atg","wsOrder":"${legacyOrder}"}'/>
													</c:when>											
													<c:otherwise>
														<c:set var="splitOrder" value="false"/>
														<c:if test="${not empty onlineOrderNumber}">
															<c:set var="orderNum" value="${onlineOrderNumber }"/>
															<c:set var="orderType" value="online"/>												
															<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}","orderType":"online","frag":"atg","wsOrder":"${legacyOrder}"}'/>
															<c:set var="bopusJsonData" value=''/>													
														</c:if>
														<c:if test="${not empty bopusOrderNumber}">
															<c:set var="orderNum" value="${bopusOrderNumber }"/>
															<c:set var="orderType" value="bopus"/>																					
															<c:set var="onlineJsonData" value=''/>				
															<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}","orderType":"bopus","frag":"atg","wsOrder":"${legacyOrder}"}'/>
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
													<div class="grid_3 alpha orderDate" >												
														<c:choose>
															<c:when test="${onlineCounter >= 10 && showAll != 'true'}">														
																<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}'  href="#">
																	<span class="icon-fallback-text"> 
																		<span class="icon-plus" aria-hidden="true"></span> 
																		<span class="icon-text"><bbbl:label key="lbl_open_small" language="${pageContext.request.locale.language}" /></span>
																	</span>
																</a>
															</c:when>
															<c:otherwise>
																<a class="collapseLink" href="#">
																	<span class="icon-fallback-text"> 
																		<span class="icon-minus" aria-hidden="true"></span> 
																		<span class="icon-text"><bbbl:label key="lbl_close_small" language="${pageContext.request.locale.language}" /></span>
																	</span>
																</a>
															</c:otherwise>
														</c:choose>												
														<fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
													</div> 
													<div class="grid_2 orderNumber" >
														${orderNum }
														<c:if test="${splitOrder == 'true'}">
															<br />${bopusOrderNumber }
														</c:if>
														
													</div> 
													<div class="grid_2 orderTotal" ><dsp:valueof converter="defaultCurrency" value="${totalAmt}" /></div> 
													<div class="grid_2 omega orderStatus" >													
														<c:choose>
															<c:when test="${orderStatus == 'SUBMITTED'}">
																Submitted
															</c:when>
															<c:when test="${orderStatus == 'Order being processed.'}">
																Processing
															</c:when>
															<c:otherwise>
																${orderStatus }			
															</c:otherwise>				
														</c:choose>
													</div> 
												</div>
																	
												<div class="grid_9 alpha omega clearfix orderContent" <c:if test="${onlineCounter >= 10 && showAll != 'true'}">style="display:none"</c:if> >
												
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
															<dsp:param name="wsOrder" value="${legacyOrder}"/>																	
														</dsp:include>
														
														<c:if test="${splitOrder == 'true' }">																
															<hr class="clearfix" />															
															<dsp:include page="frags/order_summary_frag.jsp">
																<dsp:param name="orderNum" value="${bopusOrderNumber}"/>
																<dsp:param name="orderType" value="bopus"/>
																<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
																<dsp:param name="wsOrder" value="${legacyOrder}"/>														
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

												<c:set var="splitOrder" value="false"/>																								
												<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}","orderType":"online","frag":"legacy","wsOrder":"${legacyOrder}"}'/>
												<c:set var="orderType" value="online"/>
																		
												
												<div class="grid_9 alpha omega clearfix orderHeader" > 
												<%-- 
												orderType ${orderType}<br />
												legacyOrder:${legacyOrder}<br />										
												onlineCounter${onlineCounter}<br />
												showAll:${showAll}<br />
												--%>
													<div class="grid_3 alpha orderDate " >												
														<c:choose>
															<c:when test="${showAll != 'true'}">
																<a class="expandLink" data-onlineOrder='${onlineJsonData}'  href="#">
																	<span class="icon-fallback-text"> 
																		<span class="icon-plus" aria-hidden="true"></span> 
																		<span class="icon-text"><bbbl:label key="lbl_open_small" language="${pageContext.request.locale.language}" /></span>
																	</span>
																</a>
															</c:when>
															<c:otherwise>
																<a class="collapseLink" href="#">
																	<span class="icon-fallback-text"> 
																		<span class="icon-minus" aria-hidden="true"></span> 
																		<span class="icon-text"><bbbl:label key="lbl_close_small" language="${pageContext.request.locale.language}" /></span>
																	</span>
																</a>
															</c:otherwise>
														</c:choose>												
														<fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
													</div> 
													<div class="grid_2 orderNumber" >
														${orderNum }
														<c:if test="${splitOrder == 'true'}">
															<br />${bopusOrderNumber }
														</c:if>
														
													</div> 
													<div class="grid_2 orderTotal" ><dsp:valueof converter="defaultCurrency" value="${totalAmt}" /></div> 
													<div class="grid_2 omega orderStatus" >
														<c:choose>
															<c:when test="${orderStatus == 'SUBMITTED'}">
																Submitted
															</c:when>
															<c:otherwise>
																${orderStatus }			
															</c:otherwise>				
														</c:choose>
													</div> 
												</div>
																	
												<div class="grid_9 alpha omega clearfix orderContent" <c:if test="${showAll != 'true'}">style="display:none"</c:if>	> 
												
													<%-- 
													onlineOrderNumber::${onlineOrderNumber }<br />
													bopusOrderNumber::${bopusOrderNumber }<br />
													--%>							
													
													<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>																						
													
													<%--only show first 10 ATG order details, or showAll param was passed --%>												
													<c:if test="${showAll == 'true'}">														  
														<dsp:include page="frags/legacy_order_summary_frag.jsp">
															<dsp:param name="orderNum" value="${orderNum}"/>
															<dsp:param name="orderType" value="${orderType}"/>
															<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>														
															<dsp:param name="wsOrder" value="${legacyOrder}"/>	
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
									<div class="grid_3 alpha  orderDate" >Order Date</div> 
									<div class="grid_2 orderNumber" >Order Number</div> 
									<div class="grid_2 orderTotal" >Total</div> 
									<div class="grid_2 omega orderStatus" >Order Status</div>									
								</div>
							</dsp:oparam>
							<dsp:oparam name="orderOutput">
								<div id="instoreOrdersContent">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="OrderList" />
									<dsp:param name="elementName" value="Orders" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="wsOrder" param="Orders.wsOrder" />
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
												<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}","orderType":"online","frag":"legacy","wsOrder":"${wsOrder}"}'/>
												<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}","orderType":"bopus","frag":"legacy","wsOrder":"${wsOrder}"}'/>
											</c:when>											
											<c:otherwise>
												<c:set var="splitOrder" value="false"/>
												<c:if test="${not empty onlineOrderNumber}">
													<c:set var="orderNum" value="${onlineOrderNumber }"/>
													<c:set var="orderType" value="online"/>
													<c:set var="legacyOrder" value="false"/>
													<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}","orderType":"online","frag":"legacy","wsOrder":"${wsOrder}"}'/>													
												</c:if>
												<c:if test="${not empty bopusOrderNumber}">
													<c:set var="orderNum" value="${bopusOrderNumber }"/>
													<c:set var="orderType" value="bopus"/>
													<c:set var="legacyOrder" value="false"/>													
													<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}","orderType":"bopus","frag":"legacy","wsOrder":"${wsOrder}"}'/>
												</c:if>
											</c:otherwise>
										</c:choose>
											
										<div class="grid_9 alpha omega clearfix orderHeader"> 
											<div class="grid_3 alpha orderDate " >
												<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}' href="#">
													<span class="icon-fallback-text"> 
														<span class="icon-plus" aria-hidden="true"></span> 
														<span class="icon-text"><bbbl:label key="lbl_open_small" language="${pageContext.request.locale.language}" /></span>
													</span>
												</a>
												<fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
											</div>											
											<div class="grid_2 orderNumber " >
												${orderNum }
												<c:if test="${splitOrder == 'true'}">
													<br />${bopusOrderNumber }
												</c:if>
											</div>  
											<div class="grid_2 orderTotal" ><dsp:valueof converter="defaultCurrency" value="${totalAmt}" /></div> 
											<div class="grid_2 omega orderStatus" >
												<c:choose>
													<c:when test="${orderStatus == 'SUBMITTED'}">
														Submitted
													</c:when>
													<c:when test="${orderStatus == 'Order being processed.'}">
														Processing
													</c:when>
													<c:otherwise>
														${orderStatus }			
													</c:otherwise>				
												</c:choose>
											</div> 
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
												<dsp:include page="frags/legacy_order_summary_frag.jsp">
													<dsp:param name="orderNum" value="${orderNum}"/>
													<dsp:param name="orderType" value="${orderType}"/>
													<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
													<dsp:param name="wsOrder" value="${wsOrder}"/>														
												</dsp:include>
												
												<c:if test="${splitOrder == 'true' }">											
													<hr />
												
													<dsp:include page="frags/legacy_order_summary_frag.jsp">
														<dsp:param name="orderNum" value="${bopusOrderNumber}"/>
														<dsp:param name="orderType" value="bopus"/>
														<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
														<dsp:param name="wsOrder" value="${wsOrder}"/>															
													</dsp:include>
												</c:if>												
											</c:if>	
										</div><%-- end  <div class="orderContent ">  --%>
									</dsp:oparam>
								</dsp:droplet>			
								</div>							
							</dsp:oparam>
							<dsp:oparam name="orderEmpty">
								 <script type="text/javascript">
									$('#storeBlank').addClass('hidden');
								</script> 
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
		
		<div id="sa_s22_instagram" data-saSrc="${saSrc}"></div>	
		</jsp:body>
		<jsp:attribute name="footerContent">
			<script type="text/javascript">
			       if(typeof s !=='undefined') {
			        	s.pageName='My Account>My Orders';
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
			       	
			           var compare_list_array = [];
			           var compare_list = Array.prototype.map.call(document.querySelectorAll('div[id^=currentProduct]'),
			        	function (div) { 
			           var compare_var = div.getAttribute("data-trackproduct"); 
			           compare_var = compare_var.split(':'); 
			           compare_list_array[compare_var[0]] = compare_var[1];
			          
			           }); 
			         
			           var sa_multiple_products = compare_list_array; 
			           console.log(sa_multiple_products);
			           
			    	var sa_page="9";
			    	

			    	(function() {
			    	var sa = document.createElement('script');
			    	sa.type = 'text/javascript';
			    	sa.async = true;
			    	sa.src = '${saSrc}';
			    	var sax = document.getElementsByTagName('script')[0];
			    	sax.parentNode.insertBefore(sa, sax);
			    	 })();       
			    

			</script>
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>