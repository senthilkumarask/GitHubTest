<%@page contentType="application/json"%>
<dsp:page>
<dsp:importbean bean="/com/bbb/social/facebook/FBConnectFormHandler" />
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
			
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	<dsp:getvalueof var="basicInfo" param="0" />
	<dsp:getvalueof var="page" param="page" />
	<dsp:getvalueof var="pageSection" param="pageSection" />
	
	<c:if test="${page == 'myAccountOverview' && pageSection == 'overview'}">
		<dsp:setvalue bean="FBConnectFormHandler.pageSection" value="${page}" />
	</c:if>	
	<dsp:setvalue bean="FBConnectFormHandler.fbBasicInfo" value="${basicInfo}" />
	<dsp:setvalue bean="FBConnectFormHandler.checkFBConnect" />
	
	<dsp:getvalueof bean="FBConnectFormHandler.event" var="event" />
	<dsp:getvalueof bean="FBConnectFormHandler.formError" var="formError"/>

		<c:choose>
			<c:when test="${formError == true}">
				<json:object>
					<json:property name="errorMessage">
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" bean="FBConnectFormHandler.errorMap" />
							<dsp:oparam name="false">	
								<dsp:valueof bean="FBConnectFormHandler.errorMap.error"/>
							</dsp:oparam>
						</dsp:droplet>  
					</json:property>
					<json:property name="email"></json:property>
					<json:property name="modalDialog"></json:property>
					<json:property name="fcEnabled">false</json:property>
					<json:property name="profileFound">false</json:property>
					<json:property name="enableClose">false</json:property>
					<json:property name="pageSection">${pageSection}</json:property>
					<json:property name="page">${page}</json:property>
				</json:object>	
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${event == 'EVENT_MIGRATED_USER' && pageSection == 'loginRegistration'}">
						<json:object>
							<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
							<json:property name="modalDialog">${contextPath}/account/frags/changepasswordmodel.jsp</json:property>
							<json:property name="errorMessage"></json:property>
							<json:property name="fcEnabled">false</json:property>
							<json:property name="profileFound">true</json:property>
							<json:property name="enableClose">true</json:property>
							<json:property name="pageSection">${pageSection}</json:property>
							<json:property name="page">${page}</json:property>
						</json:object>
					</c:when>
					<c:when test="${(page == 'guestLogin' || page == 'trackOrder') && pageSection == 'loginSignin'}">
						<c:choose>
							<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT'}">
								<c:choose>
									<c:when test="${page == 'guestLogin'}">
										<dsp:setvalue bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
										<dsp:setvalue bean="ProfileFormHandler.loginSuccessURL" value="${contextPath}/checkout/shipping/shipping.jsp"/>
										<dsp:setvalue bean="ProfileFormHandler.loginErrorURL" value="${contextPath}/checkout/guest_checkout.jsp"/>
									</c:when>
									<c:when test="${page == 'trackOrder'}">
										<dsp:setvalue bean="ProfileFormHandler.loginSuccessURL" value="${contextPath}/account/order_summary.jsp"/>
										<dsp:setvalue bean="ProfileFormHandler.loginErrorURL" value="${contextPath}/checkout/TrackOrder"/>
									</c:when>
									<c:otherwise>
										<dsp:setvalue bean="ProfileFormHandler.loginSuccessURL" value="${contextPath}/account/login.jsp"/>
										<dsp:setvalue bean="ProfileFormHandler.loginErrorURL" value="${contextPath}/account/login.jsp"/>
									</c:otherwise>
								</c:choose>
								<c:if test="${page == 'guestLogin'}">		
									<dsp:setvalue bean="ProfileFormHandler.userCheckingOut" value="true" />
								</c:if>		
								<dsp:setvalue bean="ProfileFormHandler.fbLogin"/>
								<json:object>
								</json:object>
							</c:when>
							<c:otherwise>
								<dsp:setvalue bean="FBConnectFormHandler.bbbProfileExist" />
								<dsp:getvalueof bean="FBConnectFormHandler.event" var="event" />
								<c:choose>
									<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE'}">
										<json:object>
											<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
											<json:property name="modalDialog"></json:property>
											<json:property name="errorMessage"><bbbl:error key="err_fb_not_enable_bbb_profile_found" language ="${pageContext.request.locale.language}"/></json:property>
											<json:property name="fcEnabled">false</json:property>
											<json:property name="profileFound">true</json:property>
											<json:property name="enableClose">false</json:property>
											<json:property name="pageSection">${pageSection}</json:property>
											<json:property name="page">${page}</json:property>
										</json:object>
									</c:when>
									<c:otherwise>
										<json:object>
											<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
											<json:property name="modalDialog"></json:property>
											<json:property name="errorMessage"><bbbl:error key="err_fb_not_enable_during_login" language ="${pageContext.request.locale.language}"/></json:property>
											<json:property name="fcEnabled">false</json:property>
											<json:property name="profileFound">false</json:property>
											<json:property name="enableClose">false</json:property>
											<json:property name="pageSection">${pageSection}</json:property>
											<json:property name="page">${page}</json:property>
										</json:object>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>	
					</c:when>
					<c:when test="${page == 'myAccountOverview' && pageSection == 'overview'}">
						<dsp:setvalue bean="FBConnectFormHandler.successURL" value="${contextPath}/account/myaccount.jsp"/>
						<dsp:setvalue bean="FBConnectFormHandler.errorURL" value="${contextPath}/account/myaccount.jsp"/>
						<dsp:setvalue bean="FBConnectFormHandler.linkingWithoutLogin" />
						<json:object>
						</json:object>	
					</c:when>
					<c:when test="${page == 'registrationEdit' && pageSection == 'registrationEdit'}">
						<c:choose>
							<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT'}">
								<json:object>
									<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
									<json:property name="modalDialog">${contextPath}/account/frags/fc_enabled_current_site.jsp</json:property>
									<json:property name="errorMessage"></json:property>
									<json:property name="fcEnabled">true</json:property>
									<json:property name="profileFound">true</json:property>
									<json:property name="enableClose">false</json:property>
									<json:property name="pageSection">${pageSection}</json:property>
									<json:property name="page">${page}</json:property>
								</json:object>
							</c:when>
							<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP_WITH_FB_CONNECT'}">
								<json:object>
									<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
									<json:property name="modalDialog">${contextPath}/account/fc_enabled_sister_site.jsp</json:property>
									<json:property name="errorMessage"></json:property>
									<json:property name="fcEnabled">true</json:property>
									<json:property name="profileFound">true</json:property>
									<json:property name="enableClose">true</json:property>
									<json:property name="pageSection">${pageSection}</json:property>
									<json:property name="page">${page}</json:property>
								</json:object>
							</c:when>
							<c:otherwise>
								<dsp:setvalue bean="FBConnectFormHandler.successURL" value="${contextPath}/account/create_account.jsp?load_fb_info=true"/>
								<dsp:setvalue bean="FBConnectFormHandler.fbRedirect" />
								<json:object>
								</json:object>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT'}">
								<json:object>
									<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
									<json:property name="modalDialog">${contextPath}/account/frags/fc_enabled_current_site.jsp</json:property>
									<json:property name="errorMessage"></json:property>
									<json:property name="fcEnabled">true</json:property>
									<json:property name="profileFound">true</json:property>
									<json:property name="enableClose">false</json:property>
									<json:property name="pageSection">${pageSection}</json:property>
									<json:property name="page">${page}</json:property>
								</json:object>
							</c:when>
							<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP_WITH_FB_CONNECT'}">
								<json:object>
									<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
									<json:property name="modalDialog">${contextPath}/account/fc_enabled_sister_site.jsp</json:property>
									<json:property name="errorMessage"></json:property>
									<json:property name="fcEnabled">true</json:property>
									<json:property name="profileFound">true</json:property>
									<json:property name="enableClose">true</json:property>
									<json:property name="pageSection">${pageSection}</json:property>
									<json:property name="page">${page}</json:property>
								</json:object>
							</c:when>
							<c:otherwise>
								<dsp:setvalue bean="FBConnectFormHandler.successURL" value="${contextPath}/account/create_account.jsp?load_fb_info=true"/>
								<dsp:setvalue bean="FBConnectFormHandler.pageSection" value="${pageSection}"/>
								<dsp:setvalue bean="FBConnectFormHandler.bbbProfileExist" />
								<dsp:getvalueof bean="FBConnectFormHandler.event" var="event" />
								<c:choose>	
									<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE'}">
										<json:object>
											<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
											<json:property name="modalDialog"></json:property>
											<json:property name="errorMessage"><bbbl:error key="err_fb_email_already_exist_register" language ="${pageContext.request.locale.language}"/></json:property>
											<json:property name="fcEnabled">false</json:property>
											<json:property name="profileFound">true</json:property>
											<json:property name="enableClose">false</json:property>
											<json:property name="pageSection">${pageSection}</json:property>
											<json:property name="page">${page}</json:property>
										</json:object>
									</c:when>
									<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP'}">
										<json:object>
											<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
											<json:property name="modalDialog">${contextPath}/account/extend_profile.jsp</json:property>
											<json:property name="errorMessage"></json:property>
											<json:property name="fcEnabled">false</json:property>
											<json:property name="profileFound">true</json:property>
											<json:property name="enableClose">true</json:property>
											<json:property name="pageSection">${pageSection}</json:property>
											<json:property name="page">${page}</json:property>
										</json:object>
									</c:when>
									<c:otherwise>
										<json:object>
											<json:property name="email">${sessionScope.FB_BASIC_INFO.email}</json:property>
											<json:property name="modalDialog"></json:property>
											<json:property name="errorMessage"><bbbl:error key="err_fb_email_already_exist_register" language ="${pageContext.request.locale.language}"/></json:property>
											<json:property name="fcEnabled">false</json:property>
											<json:property name="profileFound">true</json:property>
											<json:property name="enableClose">false</json:property>
											<json:property name="pageSection">${pageSection}</json:property>
											<json:property name="page">${page}</json:property>
										</json:object>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>	
					</c:otherwise>
				</c:choose>	
			</c:otherwise>
		</c:choose>
		
	
</dsp:page>