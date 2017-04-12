<dsp:page>
<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
  	<c:set var="pageWrapper" value="trackOrder useBazaarVoice" scope="request" />
	<bbb:pageContainer section="accounts" bodyClass="">
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

    <jsp:body>

	
	<dsp:importbean bean="/com/bbb/account/BBBOrderTrackingFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var ="securityStatus" bean="Profile.securityStatus"/>
	<dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
	
	<%--R2.2 517C start - Process parameters from BV/pie email link --%>
	<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>
	<c:if test="${not empty emailIdMD5}">
		<dsp:getvalueof var="productIdBVRR" param="productId" scope="request"/>
		<dsp:getvalueof var="orderId" param="orderId" scope="request"/>
		<dsp:setvalue bean="BBBOrderTrackingFormHandler.emailIdMD5" value="${emailIdMD5 }"/>
		<dsp:setvalue bean="BBBOrderTrackingFormHandler.orderId" value="${orderId }"/>
		<dsp:setvalue bean="BBBOrderTrackingFormHandler.trackOrderErrorURL" value="track_order.jsp"/>
		<dsp:setvalue bean="BBBOrderTrackingFormHandler.trackOrder" />
	</c:if>
	<c:set var="userTokenBVRR" value="${sessionScope.userTokenBVRR}" />
	<%-- R2.2 517C end --%>
	<dsp:getvalueof var="BBBTrackOrderVO" bean="BBBOrderTrackingFormHandler.BBBTrackOrderVO"/>
	<c:choose>
		<c:when test="${BBBTrackOrderVO.legacyOrderFlag }">
			<dsp:include page="legacy_track_order_detail_frag.jsp"></dsp:include>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="OrderNum" bean="BBBOrderTrackingFormHandler.OrderId"/>
			<dsp:getvalueof var="OrderEmail" bean="BBBOrderTrackingFormHandler.EmailId"/>
	
	<c:set var="TrackOrderVO" value="${BBBTrackOrderVO}" scope="session"/>
	<c:if test="${not empty TrackOrderVO.bbbOrderVO}">
		<dsp:getvalueof var="order" value="${TrackOrderVO.bbbOrderVO}" scope="session"/>
		<c:set var="order" value="${order}" scope="session"/>
		<c:set var="orderId" value="${OrderNum}" scope="session"/>
		<c:set var="orderEmailId" value="${OrderEmail}" scope="session"/>
		<c:set var="orderStatus" value="${order.orderStatus}" scope="session"/>
		<c:set var="orderType" value="${orderType}" scope="session"/>
		<c:set var="shippingGroups" value="${order.shippingGroups}" scope="session"/>
		<c:set var="registryMap" value="${order.registryMap}" scope="session"/>
		<c:set var="commerceItemVOList" value="${order.commerceItemVOList}" scope="session"/>
		<c:set var="carrierUrlMap" value="${order.carrierUrlMap}" scope="session"/>
		<c:set var="trackingInfos" value="${order.trackingInfos}" scope="session"/>
		<c:set var="orderPriceInfoDisplayVO" value="${order.orderPriceInfoDisplayVO}" scope="session"/>
		<c:set var="orderType" value="bopus" scope="session"/>
		<c:if test="${OrderNum eq order.onlineOrderNumber}">
			<c:set var="orderType" value="online" scope="session"/>
		</c:if>	
	</c:if>

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="trackOrderPage" value="true"/>
		<div id="content" class="container_12 clearfix newCustomer" role="main">
			<div class="grid_12 clearfix">
				<h1><bbbl:label key="lbl_trackorder_header" language='${pageContext.request.locale.language}'/></h1>
			</div>
			<div class="grid_12 clearfix trackOrderDetail">
				<div class="grid_3 alpha" >
					<h2><bbbl:label key="lbl_trackorder_another_order" language='${pageContext.request.locale.language}'/></h2>
					<dsp:form id="trackOrder" method="post" action="track_order_guest.jsp">						
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
				   				<dsp:param name="value" bean="BBBOrderTrackingFormHandler.errorMap"/>
					   			<dsp:oparam name="false">
					   				<ul class="error">
										<li class="error"> <dsp:valueof bean="BBBOrderTrackingFormHandler.errorMap.error"/></li>
									</ul>
				   				</dsp:oparam>
				   			</dsp:droplet>
	                		<div class="formTrackOrder clearfix">
							<div class="input">
								<div class="label">
									<label id="lblorderId" class="textLgray12 block" for="orderId"><bbbl:label key="lbl_trackorder_orderId" language='${pageContext.request.locale.language}'/> <span class="required">*</span></label>
								</div>
								<div class="text">
									<c:choose>
										<c:when test="${currentSiteId == 'BedBathUS'}">
											<dsp:input bean="BBBOrderTrackingFormHandler.orderId" type="text" iclass="block escapeHTMLTag" value="" name="orderIdUS" id="orderId">
												<dsp:tagAttribute name="aria-required" value="true"/>
			                                    <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
											</dsp:input>
										</c:when>
										<c:when test="${currentSiteId == 'BuyBuyBaby'}">
											<dsp:input bean="BBBOrderTrackingFormHandler.orderId" type="text" iclass="block escapeHTMLTag" value="" name="orderIdBB" id="orderId">
												<dsp:tagAttribute name="aria-required" value="true"/>
			                                    <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
											</dsp:input>
										</c:when>
										<c:otherwise>
											<dsp:input bean="BBBOrderTrackingFormHandler.orderId" type="text" iclass="block escapeHTMLTag" value="" name="orderIdCA" id="orderId">
												<dsp:tagAttribute name="aria-required" value="true"/>
			                                    <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
											</dsp:input>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="input">
								<div class="label">
									<label id="lblemailOrderTrack" class="textLgray12 block" for="emailOrderTrack"><bbbl:label key="lbl_trackorder_email" language='${pageContext.request.locale.language}'/><span class="required">*</span></label>
								</div>	
								<div class="text">
									<dsp:input bean="BBBOrderTrackingFormHandler.emailId" type="text" iclass="block escapeHTMLTag" value="" name="email" id="emailOrderTrack">
										<dsp:tagAttribute name="aria-required" value="true"/>
	                                    <dsp:tagAttribute name="aria-labelledby" value="lblemailOrderTrack erroremailOrderTrack"/>
									</dsp:input>
								</div>
							</div>
							<div class="button button_active button_light_blue">
								<dsp:input type="submit" value="FIND ORDER" bean="BBBOrderTrackingFormHandler.trackOrder" id="find_order_btn">
									<dsp:tagAttribute name="aria-pressed" value="false"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="find_order_btn"/>
	                                <dsp:tagAttribute name="role" value="button"/>
								</dsp:input>
							</div>							
						</div>	
					</dsp:form>
					<c:if  test="${!isLoggedIn and requestScope.recognizedUserFlag eq false}" >
						<c:set var="lbl_createaccount_button">
							<bbbl:label key='lbl_trackorder_createUser_button' language='${pageContext.request.locale.language}' />
						</c:set>
						<div class="account-area clearfix alert">
							<h3><bbbt:textArea key="txtarea_trackorder_createUser_head" language='${pageContext.request.locale.language}'/></h3>
							<p><bbbt:textArea key="txtarea_trackorder_createUser_body" language='${pageContext.request.locale.language}'/></p>
							<div class="button">
								<dsp:a page="/account/Login?emid=${orderEmailId}" title="${lbl_createaccount_button}"><bbbl:label key="lbl_trackorder_createUser_button" language='${pageContext.request.locale.language}'/></dsp:a>
							</div>
						</div>
					</c:if>
					<c:set var="trackOrderMsgOn"><bbbc:config key="Track_Order_Cancelled_Msg_Tag" configName="ContentCatalogKeys" /></c:set>
					<c:if test="${trackOrderMsgOn}">
						<div class="alert alert-info">
							<h3><bbbt:textArea key="txtarea_trackorder_cancelOrder_head" language='${pageContext.request.locale.language}'/></h3>
							<p><bbbt:textArea key="txtarea_trackorder_cancelOrder_body" language='${pageContext.request.locale.language}'/></p>
						</div>					
					</c:if>
				</div>	
				<div class="grid_9 omega clearfix">
  					<table title="Track Order Search Results">
  					   <thead>
							<tr>
								<th class="width_3" scope="col"><bbbl:label key="lbl_trackorder_date" language='${pageContext.request.locale.language}'/></th>
								<th class="width_3" scope="col"><bbbl:label key="lbl_trackorder_orderId" language='${pageContext.request.locale.language}'/></th>
								<th class="width_1" scope="col"><bbbl:label key="lbl_trackorder_total" language='${pageContext.request.locale.language}'/></th>
								<th class="width_2 talign-rt" scope="col"><bbbl:label key="lbl_trackorder_status" language='${pageContext.request.locale.language}'/></th>
							</tr>
						</thead>
						<tbody>	
							<tr class="padtop-10">								
								<td class="width_3" scope="col">
									<p><fmt:formatDate value="${order.submittedDate}" pattern="MMMM d, yyyy" /></p>
								</td>
								<td class="width_3" scope="col"><p>${orderId}</p></td>
								<c:choose>
									<c:when test="${orderType eq 'bopus'}">
										<td class="width_1" scope="col"><p>$<dsp:valueof value="${order.orderPriceInfoDisplayVO.storeAmount}" number="0.00"/></p></td>
									</c:when>
									<c:otherwise>
										<td class="width_1" scope="col"><p>$<dsp:valueof value="${order.orderPriceInfoDisplayVO.onlineTotal}" number="0.00"/></p></td>
									</c:otherwise>
								</c:choose>
								<td class="width_2 talign-rt" scope="col"><p>${fn:toUpperCase(fn:substring(order.orderStatus, 0, 1))}${fn:toLowerCase(fn:substring(order.orderStatus, 1, -1))}</p></td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="grid_9 omega clearfix">
					<dsp:include page="frags/track_order_frag.jsp">
						<dsp:param name="orderStatus" value="${orderStatus}"/>
						<dsp:param name="orderType" value="${orderType}"/>
						<dsp:param name="shippingGroups" value="${shippingGroups}"/>
						<dsp:param name="registryMap" value="${registryMap}"/>
						<dsp:param name="commerceItemVOList" value="${commerceItemVOList}"/>
						<dsp:param name="carrierUrlMap" value="${carrierUrlMap}"/>
						<dsp:param name="trackingInfos" value="${trackingInfos}"/>
						<dsp:param name="orderPriceInfoDisplayVO" value="${orderPriceInfoDisplayVO}"/>
					</dsp:include>
				</div>
			</div> 	
		</div> 
		</c:otherwise>
	</c:choose>
	<div id="sa_s22_instagram" data-saSrc="${saSrc}"></div>	
	</jsp:body>
	<jsp:attribute name="footerContent">
	<script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.channel = 'My Account';
			s.pageName='My Account>My Orders';
			s.prop1='My Account';
			s.prop2='My Account'; 
			s.prop3='My Account';
			s.prop6='${pageContext.request.serverName}'; 
			s.eVar9='${pageContext.request.serverName}';
		var s_code = s.t();
		if (s_code)
			document.write(s_code);
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