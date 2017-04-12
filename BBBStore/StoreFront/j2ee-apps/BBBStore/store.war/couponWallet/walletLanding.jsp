<dsp:page>    

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/com/bbb/account/ExistingProfileDroplet" />
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />

<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="section" value="accounts" scope="request" />
<c:set var="pageWrapper" value="coupons myAccount couponWallet" scope="request" />


<bbb:pageContainer index="false" follow="false">
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">${section}</jsp:attribute>
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:attribute name="PageType">WalletLanding</jsp:attribute>
    <jsp:body>

	<c:set var="scene7Path">
        <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
    </c:set>


    <%-- Reading url parameters --%>
	<c:set var="walletId" value="${param.walletId}"/>
	<c:set var="action" value="${param.action}"/>
	<c:set var="offerId" value="${param.offerId}"/>
	

	<%-- End reading url parameters --%>
	<dsp:getvalueof var="couponEmail" bean="SessionBean.couponEmail" />	 
	<dsp:droplet name="ExistingProfileDroplet">
		      		<dsp:param name="EMAIL_ADDR" value="${couponEmail}"/>
		      		<dsp:param name="wallet_id" value="${walletId}"/>
                	<dsp:oparam name="output">
	   	<dsp:getvalueof var="existingProfile" param="existingProfile" />
	   	<dsp:getvalueof var="walletIdShallowProfile" param="walletIdShallowProfile" />
		</dsp:oparam>
 	 </dsp:droplet>
	<dsp:getvalueof var="couponsWelcomeMsg" bean="SessionBean.couponsWelcomeMsg" />
   
    <div id="content" class="container_12 clearfix" role="main">        
        <div class="grid_12">		  
			  <h3 class="subtitle"><bbbl:label key="lbl_myaccount_couponWallet" language ="${pageContext.request.locale.language}"/></h3>
            <div class="clear"></div>
        </div>


      <%--  <c:if test="${appid ne 'BedBathCanada' && couponsWelcomeMsg}"> --%>        	
      <c:if test="${couponsWelcomeMsg}">
            <div class="grid_12" id="couponWalletBanner">
            <c:choose>
                <c:when test="${appid eq 'BuyBuyBaby'}">                    
                    <bbbt:textArea key="txt_coupons_welcomePromo_baby"  language="${pageContext.request.locale.language}" />
                </c:when>
                <c:when test="${appid eq 'BedBathCanada'}">                    
                    <bbbt:textArea key="txt_coupons_welcomePromo_CA"  language="${pageContext.request.locale.language}" />
                </c:when>
                <c:otherwise>
                    <bbbt:textArea key="txt_coupons_welcomePromo"  language="${pageContext.request.locale.language}" />
                </c:otherwise>
            </c:choose>

            <%-- can't have this here, add a coupon form depends on the original session value for display rules. 
            will set the value on addAcoupon.jsp 
            <dsp:setvalue bean="SessionBean.couponsWelcomeMsg" value="false"/>
            --%>
			</div>
        </c:if>

        
		
        <div class="grid_12" >		
		     <div class="grid_3 alpha">
					 	
               
			      <dsp:droplet name="/atg/dynamo/droplet/Switch">
					 <dsp:param bean="Profile.transient" name="value"/>
					 <dsp:oparam name="false">
					  <div class="walletLoginWrap" style="background-color: transparent;padding: 125px 20px 135px;">
					  </div>
					 </dsp:oparam>
					 <dsp:oparam name="true">
                <div class="walletLoginWrap">
               
				<dsp:form id="frmCreateWallet" method="post">
                    <c:choose>
                        <c:when test="${existingProfile}">
                            
                            <h3><bbbl:label key="lbl_couponWallet_login_header_existing_profile" language ="${pageContext.request.locale.language}"/></h3>
                            <p>
                                <bbbt:textArea key="txt_couponWallet_login_text_existing_profile"  language="${pageContext.request.locale.language}" />
                            </p>

                            <c:set var="loginBtnText">
                                <bbbl:label key="lbl_couponWallet_login_button_existing_profile" language ="${pageContext.request.locale.language}"/>
                            </c:set >
							 <dsp:setvalue bean="SessionBean.frmWalletRegPage" value="true"/>
                            <dsp:a  page="/account/Login" 
                                    title="${loginBtnText}"
                                    iclass="button-Med btnPrimary">
                                    ${loginBtnText}
                            </dsp:a>
                                
                        </c:when>
                        <c:otherwise>
                            <h3><bbbl:label key="lbl_couponWallet_login_header_no_profile" language ="${pageContext.request.locale.language}"/></h3>
                            <p>
                                <bbbt:textArea key="txt_couponWallet_login_text_no_profile"  language="${pageContext.request.locale.language}" />
                            </p>
                            
                            <c:set var="loginBtnText">
                                <bbbl:label key="lbl_couponWallet_login_button_no_profile" language ="${pageContext.request.locale.language}"/>
                            </c:set >

                            <!--<dsp:input 	bean="WalletFormHandler.regErrorURL" 
								type="hidden"
								value="${contextPath}/couponWallet/walletLanding.jsp" />
								<dsp:input 	bean="WalletFormHandler.regSuccessURL" 
								type="hidden"
								value="${contextPath}/account/coupons.jsp" />-->
								<dsp:input bean="WalletFormHandler.fromPage" type="hidden" value="walletlanding"/>
								<dsp:setvalue bean="SessionBean.frmWalletRegPage" value="true"/>
								 <dsp:input bean="WalletFormHandler.loginToManageActiveCouponWallet" id="btnCreateWallet" type="submit" onclick="javascript:couponWalletLoginOmniture();" iclass="button-Med btnPrimary" value="${loginBtnText}">
								<dsp:tagAttribute name="aria-pressed" value="false"/>
						    </dsp:input>


                        </c:otherwise>
                    </c:choose>
                    <div class="clear"></div>
					</dsp:form>
					
                </div>
                 </dsp:oparam>
		  	</dsp:droplet>
            </div>
         
            	<c:choose>
             		<c:when test="${not empty walletId}">
             			<c:set var="walletId" value="${walletId}" scope="request" />
             		</c:when>
     				<c:otherwise>
     					<c:set var="walletId" value="${walletIdShallowProfile}" scope="request" />
     				</c:otherwise>
    			</c:choose>
    			
            <div class="grid_9 omega couponLists">
	           <dsp:include page="/account/frags/couponList.jsp">
	           <dsp:param name="WALLET_ID" value="${walletId}"/>
	           <dsp:param name="ACTION" value="${action}"/>
	           <dsp:param name="OFFER_ID" value="${offerId}"/>
	           </dsp:include>
	              
           </div>
        </div>
    </div>



</jsp:body>
<jsp:attribute name="footerContent">
		<script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.pageName = 'My Account>Coupon Wallet';
			s.channel = 'My Account';
			s.prop1='My Account';
			s.prop2='My Account';
			s.prop3='My Account';
			s.prop6='${pageContext.request.serverName}'; 
			s.eVar9='${pageContext.request.serverName}';
			var s_code = s.t();
			if (s_code)
				document.write(s_code);
		}
	</script>
</jsp:attribute>
</bbb:pageContainer>
</dsp:page>