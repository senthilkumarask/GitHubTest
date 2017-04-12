<dsp:page>

    
    <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    
    <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
    <c:choose>
        <c:when test="${transientUser == 'true'}">
            <bbb:pageContainer index="false" follow="false">
                <div class="headerPlaceholder clearfix">    
                    <img src="login_files/masthead_by.htm" width="1280" height="200">
                </div>
    
                <div id="content" class="container_12 clearfix">
                    <div class="grid_9 alpha">              
                        <div class="clearfix"></div>                    
                                <div class="grid_9 loginWindow">
                                    <div id="newCustomer" class="grid_4">
									    <dsp:include page="/global/gadgets/errorMessage.jsp" />
                                    
                                        <h3><bbbl:label key="lbl_createaccountmain_main_header" language="${pageContext.request.locale.language}"/></h3>
                                                <dsp:a page="/checkout/shipping/shipping.jsp"><bbbl:label key="lbl_createaccountmain_main_header" language="${pageContext.request.locale.language}"/></dsp:a>
                
                                    </div>
                
                                    <div id="existingCustomer" class="grid_4">
                                        <h3><bbbl:label key="lbl_Existing_Customers" language="${pageContext.request.locale.language}"/></h3>
                                        <h6><bbbl:label key="lbl_spc_login_frag__sign_in" language="${pageContext.request.locale.language}"/></h6>
                                        
                                        <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
                                            <dsp:oparam name="output">
                                                <dsp:valueof param="message"/><br>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <dsp:form action="login.jsp" method="post">
                                            <div class="formRow">
                                                <label id="lblemail" class="textLgray12 block" for="email"><bbbl:label key="lbl_forgot_pwd_email" language="${pageContext.request.locale.language}"/></label> 
                                                    <dsp:input id="email" bean="ProfileFormHandler.value.login" type="text" iclass="input_large211 block">
                                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                                    </dsp:input>
                                            </div>
					    <div class="formRow marBottom_5">
                                                <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassCheckoutUser" class="showPassword" id="showPassword" aria-labelledby="lblshowPassword" />
                                                <label id="lblshowPassword" for="showPassword" class="textDgray11 bold"><bbbl:label key="lbl_spc_show_password" language="${pageContext.request.locale.language}"/></label>
                                            </div>
                                            <div class="formRow">
                                                <label id="lblpassword" class="textLgray12 block" for="password"><bbbl:label key="lbl_spc_login_frag__password" language="${pageContext.request.locale.language}"/></label>
                                                <dsp:input bean="ProfileFormHandler.value.password" id="password" value="" iclass="input_large211 block" type="password" autocomplete="off">
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                                                </dsp:input>
                                            </div>      
                                            <div>
                                                <dsp:input bean="ProfileFormHandler.checkoutUserLogin" type="submit" iclass="buttonWrapper" value="SIGN IN">
                                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                    <dsp:tagAttribute name="role" value="button"/>
                                                </dsp:input>
                                            </div>
                                            
                                        </dsp:form>
                                    </div>
                                </div>
                    </div>
                    <div class="grid_3">
                        <div class="teaser_229 benefitsAccountTeaser">
                            <h3>
                                <span>Benefits</span> of an account
                            </h3>
                            <ul>
                                <li>Speed up the check out process</li>
                                <li>Write product reviews</li>
                                <li>Track order history</li>
                                <li>Maintain billing addresses and billing info</li>
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
        </c:when>
        <c:otherwise>
            <dsp:include page="/checkout/shipping/shipping.jsp"/>
        </c:otherwise>
    </c:choose>
</dsp:page>