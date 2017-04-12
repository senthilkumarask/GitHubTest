<dsp:page>
	<bbb:pageContainer index="false" follow="false">
		 <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		 <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
		 <dsp:getvalueof id="status" value="status"/>
		 <dsp:droplet name="Switch">
		 	<dsp:param name="value" param="get"/>
		 	<dsp:oparam name="activate">
		 		<c:set var="status" value="activate"/>
		 	</dsp:oparam>
		 </dsp:droplet>
		<c:set var="startShopping">
			<bbbl:label key='lbl_getcoupon_start_shopping' language='${pageContext.request.locale.language}' />
		</c:set>	
		 <div id="content" class="container_12 clearfix" role="main">
			<dsp:droplet name="/com/bbb/selfservice/ProcessCouponDroplet">
				<dsp:param name="requestType" value="${status}"/>
				<dsp:param name="serviceName" value="processCoupon"/>
				<dsp:param name="entryCd" param="entryCd"/>
				<dsp:param name="emailAddr" param="email"/>			
				<dsp:param name="siteId" bean="/atg/multisite/Site.id"/>	
				<dsp:oparam name="output">					
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="couponStatus"/>
						<dsp:oparam name="Not Activated">
							 <div class="grid_12">
			                    <h2><bbbl:textArea key="txt_getcoupon_activation_coupon" language="${pageContext.request.locale.language}" /></h2>
			                 </div>
			                 <div class="grid_12">
			                 	<span class="error">
				                 	<dsp:getvalueof id="cmsContent" param="cmsContent" />
							 		<c:if test="${empty cmsContent}">
							 			<bbbl:label key='lbl_getcoupon_something_wrong' language='${pageContext.request.locale.language}' />
									</c:if>
								</span>
			                 	<dsp:valueof param="cmsContent" valueishtml="true"/>
			                 	<c:set var="accept">
									<bbbl:label key='lbl_getcoupon_accept_terms' language='${pageContext.request.locale.language}' />
								</c:set>									
			                 	<dsp:include page="frags/couponinfo.jsp">
			                 		<dsp:getvalueof id="email" param="email"/>
			                 		<dsp:param name="email" param="email"/>
			                 		<dsp:getvalueof id="entryCd" param="entryCd"/>
			                 		<dsp:param name="buttonHref" value="${contextPath}/account/getcoupon.jsp?email=${email}&entryCd=${entryCd}&get=activate"/>
			                 		<dsp:param name="buttonValue" value="${accept}"/>
			                 		<dsp:param name="cssClass" value="grid_7 push_3"/>
			                 	</dsp:include>
							</div>	
						</dsp:oparam>
						<dsp:oparam name="Activated">
							<div class="grid_12">
			                    <h2><bbbl:textArea key="txt_getcoupon_online_activated" language="${pageContext.request.locale.language}" /></h2>
			                </div>
			                <div class="grid_12">
			                	<span class="error">
				                	<dsp:getvalueof id="cmsContent" param="cmsContent"/>
							 		<c:if test="${empty cmsContent}">
										<bbbl:label key='lbl_getcoupon_something_wrong' language='${pageContext.request.locale.language}' />
									</c:if>
								</span>
								<dsp:valueof param="cmsContent" valueishtml="true"/>
								<dsp:include page="frags/couponinfo.jsp">			                	
			                 		<dsp:param name="email" param="email"/>
				                	<dsp:param name="buttonHref" value="${contextPath}"/>
				                	<dsp:param name="buttonValue" value="${startShopping}"/>
				                	<dsp:param name="cssClass" value="grid_6 push_5"/>
				                </dsp:include>
			                </div>
						</dsp:oparam>
						<dsp:oparam name="Already Activated">
							<div class="grid_12">
			                    <h2><bbbl:textArea key="txt_getcoupon_online_allready_activated" language="${pageContext.request.locale.language}" /></h2>
			                    <p>
			                    	<bbbl:textArea key="txt_getcoupon_online_allready_activated_info" language="${pageContext.request.locale.language}" />
			                    </p>	
			                 </div>
			                <div class="grid_12">
			                	<span class="error">
				                	<dsp:getvalueof id="cmsContent" param="cmsContent"/>
							 		<c:if test="${empty cmsContent}">
										<bbbl:label key='lbl_getcoupon_something_wrong' language='${pageContext.request.locale.language}' />
									</c:if>
								</span>
								<dsp:valueof param="cmsContent" valueishtml="true"/>
								<dsp:include page="frags/couponinfo.jsp">			                	
			                 		<dsp:param name="email" param="email"/>
				                	<dsp:param name="buttonHref" value="${contextPath}"/>
				                	<dsp:param name="buttonValue" value="${startShopping}"/>
				                	<dsp:param name="cssClass" value="grid_6 push_5"/>
				                </dsp:include>
			                </div>
						</dsp:oparam>
						<dsp:oparam name="Pre Activated">
							<div class="grid_12">
			                    <h2><bbbl:textArea key="txt_getcoupon_online_pre_activated" language="${pageContext.request.locale.language}" /></h2>
			                </div>
			                <div class="grid_12">
			                	<span class="error">
				                	<dsp:getvalueof id="cmsContent" param="cmsContent"/>
							 		<c:if test="${empty cmsContent}">
										<bbbl:label key='lbl_getcoupon_something_wrong' language='${pageContext.request.locale.language}' />
									</c:if>
								</span>
								<dsp:valueof param="cmsContent" valueishtml="true"/>
								<dsp:include page="frags/couponinfo.jsp">			                	
			                 		<dsp:param name="email" param="email"/>
				                	<dsp:param name="buttonHref" value="${contextPath}"/>
				                	<dsp:param name="buttonValue" value="${startShopping}"/>
				                	<dsp:param name="cssClass" value="grid_6 push_5"/>
				                </dsp:include>
			                </div>
						</dsp:oparam>
						<dsp:oparam name="Redeemed">
							<div class="grid_12">
			                    <h2><bbbl:textArea key="txt_getcoupon_online_redeemed" language="${pageContext.request.locale.language}" /></h2>
			                    <p>
			                        <bbbl:textArea key="txt_getcoupon_already_redeemed" language="${pageContext.request.locale.language}" />
			                    </p>
			                </div>
			                <dsp:include page="frags/couponinfoerror.jsp">			                	
				               	<dsp:param name="buttonHref" value="${contextPath}"/>
				               	<dsp:param name="buttonValue" value="${startShopping}"/>				               	
				            </dsp:include>
						</dsp:oparam>
						<dsp:oparam name="Expired">
							<dsp:getvalueof id="cmsContent" param="cmsContent"/>							
							<div class="grid_12">
			                    <h2><bbbl:textArea key="txt_getcoupon_expired" language="${pageContext.request.locale.language}" /></h2>
			                    <p>
			                      <bbbl:textArea key="txt_getcoupon_coupon_expired" language="${pageContext.request.locale.language}" /><dsp:valueof value=" "/><dsp:valueof param="cmsContent" valueishtml="true"/>
			                    </p>
			                </div>
			              	<dsp:include page="frags/couponinfoerror.jsp">		                	
				               	<dsp:param name="buttonHref" value="${contextPath}"/>
				               	<dsp:param name="buttonValue" value="${startShopping}"/>				               	
				            </dsp:include>
						</dsp:oparam>							
					</dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="cmsError"/>
						<dsp:oparam name="EmailError">
							<div class="grid_12">
								<h2><bbbl:textArea key="txt_getcoupon_activation_error1" language="${pageContext.request.locale.language}" /></h2>
			                    <p>
			                        <bbbl:textArea key="txt_getcoupon_unableto_activate" language="${pageContext.request.locale.language}" />
			                    </p>
			                    <dsp:include page="frags/couponinfoerror.jsp">			                	
				                	<dsp:param name="buttonHref" value="${contextPath}"/>
				                	<dsp:param name="buttonValue" value="${startShopping}"/>
				                </dsp:include>
				             </div>
						</dsp:oparam>
						<dsp:oparam name="CouponActivateError">
							<div class="grid_12">
								<h2><bbbl:textArea key="txt_getcoupon_activation_error2" language="${pageContext.request.locale.language}" /></h2>
		                    	<p>
		                        	<bbbl:textArea key="txt_getcoupon_error_activation1" language="${pageContext.request.locale.language}" />
		                    	</p>
		                    </div>
		                    <dsp:include page="frags/couponinfoerror.jsp">			                	
			                	<dsp:param name="buttonHref" value="${contextPath}"/>
			                	<dsp:param name="buttonValue" value="${startShopping}"/>
			                </dsp:include>
						</dsp:oparam>
						<dsp:oparam name="CouponUnknownError">
							<div class="grid_12">
								<h2><bbbl:textArea key="txt_getcoupon_activation_error3" language="${pageContext.request.locale.language}" /></h2>
		                    	<p>
		                        	<bbbl:textArea key="txt_getcoupon_error_activation2" language="${pageContext.request.locale.language}" />
		                    	</p>
								<dsp:include page="frags/couponinfoerror.jsp">			                	
				                	<dsp:param name="buttonHref" value="${contextPath}"/>
				                	<dsp:param name="buttonValue" value="${startShopping}"/>
				                </dsp:include>
			                </div>
						</dsp:oparam>
						<dsp:oparam name="default">
							<div class="grid_12">
								<h2><bbbl:textArea key="txt_getcoupon_activation_error3" language="${pageContext.request.locale.language}" /></h2>
		                    	<p>
		                        	<bbbl:textArea key="txt_getcoupon_error_activation2" language="${pageContext.request.locale.language}" />
		                    	</p>
								<dsp:include page="frags/couponinfoerror.jsp">			                	
				                	<dsp:param name="buttonHref" value="${contextPath}"/>
				                	<dsp:param name="buttonValue" value="${startShopping}"/>
				                </dsp:include>
			                </div>
						</dsp:oparam>
					</dsp:droplet>					
				</dsp:oparam>
			</dsp:droplet>
		</div>		
	</bbb:pageContainer>
</dsp:page>

