<dsp:page>
<bbb:pageContainer index="false" follow="false">
	
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<div class="headerPlaceholder clearfix">
			<img src="login_files/masthead_by.htm" width="1280" height="200" />
		</div>

		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_9 alpha">				
				<div class="clearfix"></div>
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
					<dsp:param name="value" bean="Profile.transient"/>
					<dsp:oparam name="true">
						<div class="grid_9 loginWindow">
							<div id="newCustomer" class="grid_4">
								<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
									<dsp:oparam name="output">
										<dsp:valueof param="message"/>
									</dsp:oparam>
								</dsp:droplet>
							
								<h3><bbbl:label key="lbl_createaccountmain_main_header" language="${pageContext.request.locale.language}"/></h3>
								<h6><bbbl:label key="lbl_createaccount_main_header" language="${pageContext.request.locale.language}"/></h6>
								<dsp:form id="newAccount" method="post" action="create_account.jsp">
									<div class="formRow">										
									</div>
									<div>
										<dsp:input bean="ProfileFormHandler.checkoutLoginRegister" type="submit" iclass="buttonWrapper" value="Check Out As Guest">
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="role" value="button"/>
                                        </dsp:input>
									</div>
									
									
								</dsp:form>
		
							</div>
		
							<div id="existingCustomer" class="grid_4">
								<h3><bbbl:label key="lbl_login_frag__existing_customers" language="${pageContext.request.locale.language}" /></h3>
								<h6><bbbl:label key="lbl_login_frag__sign_in" language="${pageContext.request.locale.language}" /></h6>
								
								<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
									<dsp:oparam name="output">
										<dsp:valueof param="message"/><br>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:form action="login.jsp" method="post">
									<div class="formRow">
										<label id="lblemail" class="textLgray12 block" for="email"><bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}" /></label> 
											<dsp:input id="email" bean="ProfileFormHandler.value.login" type="text" iclass="input_large211 block">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                            </dsp:input>
									</div>
									<div class="formRow">
										<label id="lblpassword" class="textLgray12 block" for="password"><bbbl:label key="lbl_login_frag__password" language="${pageContext.request.locale.language}" /></label>
										<dsp:input bean="ProfileFormHandler.value.password" id="password" value="" iclass="input_large211 block showpassCheckoutLogin" type="password" autocomplete="off">
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                                        </dsp:input>
									</div>		
									<div>
										<dsp:input bean="ProfileFormHandler.checkoutLogin" type="submit" iclass="buttonWrapper" value="SIGN IN">
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="role" value="button"/>
                                        </dsp:input>
									</div>
								</dsp:form>
							</div>
						</div>
			</dsp:oparam>
			<dsp:oparam name="false">
				<b><bbbl:label key="lbl_success_login" language="${pageContext.request.locale.language}" /> <dsp:a page="/login.jsp">
	       		<dsp:property bean="ProfileFormHandler.logoutSuccessURL" value="account/login.jsp"/>
	       		<dsp:property bean="ProfileFormHandler.logout" value="true"/> 
	       		<bbbl:label key="lbl_profile_logout" language="${pageContext.request.locale.language}" />
	     	</dsp:a></b>
								
			</dsp:oparam>
			</dsp:droplet>
			</div>
			<div class="grid_3">
				<div class="teaser_229 benefitsAccountTeaser">
					<h3>
						<span><bbbl:label key="lbl_benefits" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_of_account" language="${pageContext.request.locale.language}" />
					</h3>
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
			<div class="clearfix"></div>
			<div id="bottomSpacing" class="grid_12"></div>
		</div>

</bbb:pageContainer>
</dsp:page>