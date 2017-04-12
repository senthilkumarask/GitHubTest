<dsp:page>
	<bbb:pageContainer index="false" follow="false">
		<dsp:importbean bean="atg/userprofiling/ProfileFormHandler"/>
		<dsp:importbean bean="atg/userprofiling/Profile"/>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<c:set var="lbl_cancel_button"><bbbl:label key="lbl_profile_Cancel" language="${pageContext.request.locale.language}"/></c:set>
		<c:set var="lbl_profile_private_policy"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></c:set>
		<dsp:form method="post" id="createAccount" action="login.jsp">
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="true">
					<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
						<dsp:oparam name="output">
							<dsp:valueof param="message"/>
						</dsp:oparam>
					</dsp:droplet>
					<div id="themeWrapper" class="by">
						<div id="content" class="createAccount container_12 clearfix" role="main">
							<div class="grid_9">
								<h1><bbbl:label key="lbl_place_order" language="${pageContext.request.locale.language}" /></h1>
								<div class="grid_6 alpha">
									<div class="formRow">
										<label id="lblemail" for="email" class="textLgray12"><bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}"/><span class="starMandatory">*</span></label>
										<dsp:input bean="ProfileFormHandler.value.email" type="text" id="email" iclass="input_large211 block" name="email">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                        </dsp:input>
									</div>
									<div class="formRow">
										<label id="lblfname" for="fname" class="textLgray12"><bbbl:label key="lbl_profile_firstname" language="${pageContext.request.locale.language}"/><span class="starMandatory">*</span></label>
										<dsp:input bean="ProfileFormHandler.value.firstName" type="text" id="fname" iclass="input_large211 block" name="fname">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblfname errorfname"/>
                                        </dsp:input>
									</div>
									<div class="formRow">
										<label id="lbllname" for="lname" class="textLgray12"><bbbl:label key="lbl_profile_lastname" language="${pageContext.request.locale.language}"/><span class="starMandatory">*</span></label>
										<dsp:input bean="ProfileFormHandler.value.lastName" type="text" id="lname" iclass="input_large211 block" name="lname">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbllname errorlname"/>
                                        </dsp:input>
									</div>
									<div class="formRow">  
										<dsp:input  bean="ProfileFormHandler.placeOrder" type="submit" value="PLACE ORDER" iclass="submitBtn submitBtnMarginRight" id="createAccontBtn">
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="createAccontBtn"/>
                                            <dsp:tagAttribute name="role" value="button"/>
                                        </dsp:input>
										<a href="#" class="generalBold" title="${lbl_cancel_button}">${lbl_cancel_button}</a>
									</div>
									<div class="formRow">
										<ul class="viewPrivacyPolicy">
											<li><a href="#" class="general" title="${lbl_profile_private_policy}">${lbl_profile_private_policy}</a></li>
										</ul>
									</div>
								</div>
							</div>
							<div class="grid_3">
								<div class="teaser_229 benefitsAccountTeaser">
									<h3><span><bbbl:label key="lbl_benefits" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_of_account" language="${pageContext.request.locale.language}" /></h3>
									<ul>
									<li><bbbl:label key="lbl_checkout_speed" language="${pageContext.request.locale.language}" /></li>
									<li><bbbl:label key="lbl_prod_reviews" language="${pageContext.request.locale.language}" /></li>
									<li><bbbl:label key="lbl_track_order_history" language="${pageContext.request.locale.language}" /></li>
									<li><bbbl:label key="lbl_maintain_billing_address" language="${pageContext.request.locale.language}" /></li>
									<li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>
									<li>Pellentesque sapien nisi,</li>
									<li>Commodo non feugiat ac dignis non lacus.</li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</dsp:oparam>
				<dsp:oparam name="false">
				<bbbl:label key="lbl_profile_loggedIn" language="${pageContext.request.locale.language}" />
				 <dsp:a page="/login.jsp">
				<dsp:property bean="ProfileFormHandler.logoutSuccessURL" value="account/login.jsp"/>
				<dsp:property bean="ProfileFormHandler.logout" value="true"/>
				<bbbl:label key="lbl_profile_logout" language="${pageContext.request.locale.language}" />
				</dsp:a>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:form>
	</bbb:pageContainer>
</dsp:page>