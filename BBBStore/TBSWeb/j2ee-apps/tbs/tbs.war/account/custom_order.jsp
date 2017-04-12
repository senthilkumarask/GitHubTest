<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/userprofiling/PropertyManager" />
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<c:set var="pageWrapper" value="login myAccount useFB" scope="request" />
<c:set var="pageNameFB" value="login" scope="request" />
<bbb:pageContainer section="accounts">
<dsp:form id="customOrder" action="custom_order.jsp" method="post">

<c:set var="isLoggedIn" value="false"/>

<dsp:droplet name="Compare">
  <dsp:param bean="Profile.securityStatus" name="obj1"/>
  <dsp:param bean="PropertyManager.securityStatusLogin" name="obj2"/>
  <dsp:oparam name="lessthan">
     <c:set var="isLoggedIn" value="false"/>
  </dsp:oparam>
  <dsp:oparam name="default">
     <c:set var="isLoggedIn" value="true"/>
  </dsp:oparam>
</dsp:droplet>


    <dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
	<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
	<c:if test="${(empty showLegacyPwdPopup) and (empty showMigratedPopup)}">
		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" bean="ProfileFormHandler.errorMap.loginPasswordError"/>
			<dsp:oparam name="false">
				<p><dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"></dsp:valueof></p>
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:include page="/global/gadgets/errorMessage.jsp">
					<dsp:param name="formhandler" bean="ProfileFormHandler"/>
				</dsp:include>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<div id="service-images" class="row">
		<div class="small-12 columns">
			<h2><bbbl:label key="tbs_customorderpageheader" language ="${pageContext.request.locale.language}"/></h2>
		</div>
		<div class="service-image-panel">
		<%-- was commented for 37386, need to re-enabled after Nov release --%>
		<c:if test="${currentSiteId != TBS_BuyBuyBabySite}">
			<div class="small-12 medium-6 columns">
				<h2><bbbl:label key="tbs_kirschheader" language ="${pageContext.request.locale.language}"/></h2>
				<br/>
				<div class="small-12 large-8 columns">
					<p><bbbl:textArea key="tbs_kirschcopy" language ="${pageContext.request.locale.language}"/></p>
				</div>
				<div class="small-6 medium-12 large-4 columns">
					<bbbl:textArea key="tbs_kirschimage" language ="${pageContext.request.locale.language}"/>
				</div>
				<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
				
				<div class="small-6 medium-12 large-6 columns">
				<c:choose>
					<c:when test="${isLoggedIn}">
					<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="createKrishOrder" />
						<!--<dsp:input type="hidden" bean="ProfileFormHandler.createKrischOrderSuccessURL" value="${contextPath}/cart/cart.jsp" />
						<dsp:input type="hidden" bean="ProfileFormHandler.createKrischOrderErrorURL" value="${contextPath}/account/custom_order.jsp" />-->	
						<dsp:input type="submit" iclass="small button service" value="Complete Order" bean="ProfileFormHandler.createKirschOrder"/>
					</c:when>
					<c:otherwise>
							<a href="${contextPath}/account/login.jsp?customOrderKey=kirsch" class="small button service">
								Complete Order
							</a>
					</c:otherwise>
				</c:choose>
				</div>
				
				<div class="small-6 columns">&nbsp;</div>
			</div>
		</c:if>
		<div class="small-12 medium-6 columns">
			<h2><bbbl:label key="tbs_cmoheader" language ="${pageContext.request.locale.language}"/></h2>
			<br/>
			<div class="small-12 large-8 columns">
			    <p><bbbl:textArea key="tbs_cmocopy" language ="${pageContext.request.locale.language}"/></p>
			</div>
			<div class="small-6 medium-12 large-4 columns">
			    <bbbl:textArea key="tbs_cmoimage" language ="${pageContext.request.locale.language}"/>
			</div>
			<div class="small-6 medium-12 large-6 columns">
			
			<c:choose>
				<c:when test="${isLoggedIn}">
				<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="createCMOOrder" />
					<!--<dsp:input type="hidden" bean="ProfileFormHandler.createCMOOrderSuccessURL" value="${contextPath}/cart/cart.jsp" />
					<dsp:input type="hidden" bean="ProfileFormHandler.createCMOOrderErrorURL" value="${contextPath}/account/custom_order.jsp" />-->
					<dsp:input type="submit" iclass="small button service" value="Complete Order" bean="ProfileFormHandler.createCMOOrder"/>
				</c:when>
				<c:otherwise>
						<a href="${contextPath}/account/login.jsp?customOrderKey=cmo" class="small button service">
							Complete Order
						</a>
				</c:otherwise>
			</c:choose>
				
			</div>
			<div class="small-6 columns">&nbsp;</div>
		</div>
		<div class="small-12 medium-6 columns">
			<h2><bbbl:label key="tbs_custominvitationsheader" language ="${pageContext.request.locale.language}"/></h2>
			<br/>
			<div class="small-12 large-8 columns">
				<p><bbbl:textArea key="tbs_custominvitationscopy" language ="${pageContext.request.locale.language}"/></p>
			</div>
			<div class="small-6 medium-12 large-4 columns">
				<bbbl:textArea key="tbs_custominvitationsimage" language ="${pageContext.request.locale.language}"/>
			</div>
			<div class="small-6 medium-12 large-6 columns">
				<a target="_blank" class="small button service" href="https://invitations.bedbathandbeyond.com" onclick="s_objectID=&quot;https://invitations.bedbathandbeyond.com/_1&quot;;return this.s_oc?this.s_oc(e):true">Shop Now</a>
			</div>
			<div class="small-6 columns">&nbsp;</div>
		</div>
		<div class="small-12 medium-6 columns">
			<h2><bbbl:label key="tbs_guidedsellingheader" language ="${pageContext.request.locale.language}"/></h2>
			<br/>
			<div class="small-12 large-8 columns">
				<p><bbbl:textArea key="tbs_guidedsellingcopy" language ="${pageContext.request.locale.language}"/></p>
			</div>
			<div class="small-6 medium-12 large-4 columns">
				<bbbl:textArea key="tbs_guidedsellingimage" language ="${pageContext.request.locale.language}"/>
			</div>
			<input name="orderId" id="orderId" type="hidden" value='<dsp:valueof param="gsOrderId"/>'/>
			<div class="small-6 medium-12 large-6 columns">
				<input type="submit" id="guided_selling" class="small button service" value="Complete Order" />
			</div>
			<div class="small-6 columns">&nbsp;</div>
		</div>
	</div>
	</div>
</dsp:form>
<dsp:include page="/account/idm/idm_login_custom_order.jsp" />
</bbb:pageContainer>
</dsp:page>