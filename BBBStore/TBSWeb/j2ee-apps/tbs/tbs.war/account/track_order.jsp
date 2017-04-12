<dsp:page>
  <c:set var="pageWrapper" value="login trackOrder useFB" scope="request" />
  <c:set var="pageNameFB" value="trackOrder" scope="request" />
  <bbb:pageContainer section="accounts" bodyClass="">
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:body>

		<dsp:importbean bean="/com/bbb/account/BBBOrderTrackingFormHandler"/>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="trackOrderPage" value="true"/>
		<dsp:getvalueof var="BBBTrackOrderVO" bean="BBBOrderTrackingFormHandler.BBBTrackOrderVO"/>
		<dsp:getvalueof var="OrderNum" bean="BBBOrderTrackingFormHandler.OrderId"/>

		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12 clearfix">
				<h1><bbbl:label key="lbl_trackorder_header" language='${pageContext.request.locale.language}'/></h1>
			</div>
			<p class="error cb fcConnectErrorMsg hidden"></p>
			<div class="grid_12">
           		<div id="newCustomer" class="grid_4 alpha" >
               		<h3><bbbl:label key="lbl_trackorder_dont_have_account" language='${pageContext.request.locale.language}'/></h3>
               		<p><bbbt:textArea key="txtarea_trackorder_track" language='${pageContext.request.locale.language}'/></p>
               		<dsp:form id="trackOrder" method="post" action="track_order.jsp">
			   			<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			   				<dsp:param name="value" bean="BBBOrderTrackingFormHandler.errorMap"/>
			   				<dsp:oparam name="false">
				   				<ul class="error">
									<li class="error"> <dsp:valueof bean="BBBOrderTrackingFormHandler.errorMap.error"/></li>
								</ul>
				   			</dsp:oparam>
				   		</dsp:droplet>
						<dsp:input bean="BBBOrderTrackingFormHandler.trackOrderErrorURL" type="hidden" value="track_order.jsp"/>
                		
                		<%--START International shipping Order Tracking link--%>
						             <c:if test="${internationalShippingOn}">
										<a href="international_order_summary.jsp">
											<bbbl:label key="lbl_international_track_order" language="${pageContext.request.locale.language}" />
										</a>  
									</c:if>
						 <%--END International shipping Order Tracking link--%>
						 
               			<div class="formRow clearfix">
							<div class="input <c:if test="${not empty track_order_email_err}">inputError</c:if>">
								<div class="label">
									<label id="lblemailOrderTrack" class="textLgray12 block" for="emailOrderTrack"><bbbl:label key="lbl_trackorder_email" language='${pageContext.request.locale.language}'/> <span class="required">*</span></label>
								</div>	
								<div class="text width_3">
								<dsp:input bean="BBBOrderTrackingFormHandler.emailId" type="text" id="emailOrderTrack" name="email" iclass="block" >
                                       <dsp:tagAttribute name="aria-required" value="true"/>
                                       <dsp:tagAttribute name="aria-labelledby" value="lblemailOrderTrack erroremailOrderTrack"/>
                                   </dsp:input>
								</div>
							</div>
						
							<div class="input <c:if test="${not empty track_order_orderId_err}">inputError</c:if>">
							<c:choose>
								<c:when test="${currentSiteId == 'BedBathUS'}">
									<div class="label">
										<label id="lblorderId" class="textLgray12 block" for="orderId"><bbbl:label key="lbl_trackorder_orderId" language='${pageContext.request.locale.language}'/> <span class="required">*</span></label>
									</div>
									<div class="text width_3">
										<dsp:input bean="BBBOrderTrackingFormHandler.orderId" type="text" id="orderId" name="orderIdUS" iclass="block" >
                                               <dsp:tagAttribute name="aria-required" value="true"/>
                                               <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
                                           </dsp:input>
									</div>
								</c:when>
								<c:when test="${currentSiteId == 'BuyBuyBaby'}">
									<div class="label">
										<label id="lblorderId" class="textLgray12 block" for="orderId"><bbbl:label key="lbl_trackorder_orderId" language='${pageContext.request.locale.language}'/> <span class="required">*</span></label>
									</div>
									<div class="text width_3">
										<dsp:input bean="BBBOrderTrackingFormHandler.orderId" type="text" id="orderId" name="orderIdBB" iclass="block" >
                                               <dsp:tagAttribute name="aria-required" value="true"/>
                                               <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
                                           </dsp:input>
									</div>
								</c:when>
								<c:otherwise>
									<div class="label">
										<label id="lblorderId" class="textLgray12 block" for="orderId"><bbbl:label key="lbl_trackorder_orderId" language='${pageContext.request.locale.language}'/> <span class="required">*</span></label>
									</div>
									<div class="text width_3">
										<dsp:input bean="BBBOrderTrackingFormHandler.orderId" type="text" id="orderId" name="orderIdCA" iclass="block" >
                                               <dsp:tagAttribute name="aria-required" value="true"/>
                                               <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
                                           </dsp:input>
									</div>
								</c:otherwise>
							</c:choose>
							</div>	
						</div>	
							
               			<div class="button_active">
               				<c:set var="lbl_trackorder_button">
								<bbbl:label key='lbl_trackorder_button' language='${pageContext.request.locale.language}' />
							</c:set>
               				<dsp:input iclass="small expand button primary" bean="BBBOrderTrackingFormHandler.trackOrder" type="submit" onclick="javascript:externalLinks('Track Your Order')"; id="newEmailBtn" value="${lbl_trackorder_button}">
                                   <dsp:tagAttribute name="aria-pressed" value="false"/>
                                   <dsp:tagAttribute name="aria-labelledby" value="newEmailBtn"/>
                                   <dsp:tagAttribute name="role" value="button"/>
                               </dsp:input>
               				<dsp:input bean="BBBOrderTrackingFormHandler.trackOrder" type="hidden" value="${lbl_trackorder_button}"/>
               			</div>
               		</dsp:form>
           		</div>

				<dsp:include page="frags/login_frag.jsp">
					<dsp:param name="trackOrder" value="track_order"/>
				</dsp:include>
			</div>
		</div> 
	</jsp:body>
	<jsp:attribute name="footerContent">
						<script type="text/javascript">
							if (typeof s !== 'undefined') {
								s.pageName = 'My Account>Log In';
								s.channel = 'My Account';
								s.prop1 = 'My Account';
								s.prop2 = 'My Account';
								s.prop3 = 'My Account';
								var s_code = s.t();
								if (s_code)
									document.write(s_code);
							}
						</script>					
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
