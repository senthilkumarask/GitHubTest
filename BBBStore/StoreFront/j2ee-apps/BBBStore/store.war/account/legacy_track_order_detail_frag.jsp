<dsp:importbean bean="/com/bbb/account/BBBOrderTrackingFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var ="securityStatus" bean="Profile.securityStatus"/>
<dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
	
	<dsp:getvalueof var="BBBTrackOrderVO" bean="BBBOrderTrackingFormHandler.BBBTrackOrderVO"/>
	<dsp:getvalueof var="OrderNum" bean="BBBOrderTrackingFormHandler.OrderId"/>
	<dsp:getvalueof bean="BBBOrderTrackingFormHandler.userTokenBVRR" var="userTokenBVRR" scope="request"/>
	<c:set var="TrackOrderVO" value="${BBBTrackOrderVO}"/>
	<c:if test="${not empty TrackOrderVO.legacyOrderVO}">
		<dsp:getvalueof var="order" value="${TrackOrderVO.legacyOrderVO}" scope="session"/>
		<c:set var="order" value="${order}" scope="session"/>
		<c:set var="orderId" value="${OrderNum}" scope="session"/>
		
	</c:if>
	<c:set var="orderType" value="legacy"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="trackOrderPage" value="true"/>
		<div id="content" class="container_12 clearfix newCustomer" role="main">
			<div class="grid_12 clearfix">
				<h1><bbbl:label key="lbl_trackorder_header" language='${pageContext.request.locale.language}'/></h1>
			</div>
			<div class="grid_12 clearfix trackOrderDetail">
				<div class="grid_3 alpha" >
					<h2><bbbl:label key="lbl_trackorder_another_order" language='${pageContext.request.locale.language}'/></h2>
					<dsp:form id="trackOrder" method="post" action="track_order_guest_legacy.jsp">					
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
				   				<dsp:param name="value" bean="BBBOrderTrackingFormHandler.errorMap"/>
					   			<dsp:oparam name="false">
					   				<ul class="error">
										<li class="error"> <dsp:valueof bean="BBBOrderTrackingFormHandler.errorMap.error"/></li>
									</ul>
				   				</dsp:oparam>
				   			</dsp:droplet>
							<dsp:input bean="BBBOrderTrackingFormHandler.trackOrderErrorURL" type="hidden" value="track_order_guest_legacy.jsp"/>
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
									<label id="lblemailOrderTrack" class="textLgray12 block" for="emailOrderTrack"><bbbl:label key="lbl_trackorder_email" language='${pageContext.request.locale.language}'/> <span class="required">*</span></label>
								</div>	
								<div class="text">
									<dsp:input bean="BBBOrderTrackingFormHandler.emailId" type="text" iclass="block escapeHTMLTag" value="" name="email" id="emailOrderTrack">
										<dsp:tagAttribute name="aria-required" value="true"/>
	                                    <dsp:tagAttribute name="aria-labelledby" value="lblemailOrderTrack erroremailOrderTrack"/>
									</dsp:input>
								</div>
							</div>
							<dsp:input bean="BBBOrderTrackingFormHandler.trackOrderErrorURL" type="hidden" value="track_order_guest_legacy.jsp"/>
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
								<dsp:a page="/account/Login" title="${lbl_createaccount_button}"><bbbl:label key="lbl_trackorder_createUser_button" language='${pageContext.request.locale.language}'/></dsp:a>
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
									<p><fmt:formatDate value="${order.orderInfo.orderHeaderInfo.orderDt}" pattern="MMMM d, yyyy" /></p>
								</td>
								<td class="width_3" scope="col"><p>${orderId}</p></td>
								<td class="width_1" scope="col"><p>$<dsp:valueof value="${order.orderInfo.orderHeaderInfo.totalAmt}" number="0.00"/></p></td>
								<td class="width_2 talign-rt" scope="col"><p>${order.orderInfo.orderHeaderInfo.orderStatus}</p></td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="grid_9 omega clearfix">
					<dsp:include page="frags/track_order_frag_legacy.jsp">
						<dsp:param name="orderDetails" value="${order}"/>
						<dsp:param name="orderId" value="${orderId}"/>
					</dsp:include>
				</div>
			</div> 	
		</div> 