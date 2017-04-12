<dsp:page>
	<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath"bean="/OriginatingRequest.contextPath" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="coupons myAccount" scope="request" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="optinURL">
			<bbbc:config key="optinURL" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="btnWalletLook">
	   <bbbl:label key="lbl_lookup_wallet" language="${pageContext.request.locale.language}"/>
	</c:set>
	<c:set var="create_wallet">
		<bbbl:label key="lbl_create_wallet" language="${pageContext.request.locale.language}"/>
	</c:set>

	 <c:set var="facebook_createWallet"><bbbc:config key="pixel_facebook_createWallet" configName="ContentCatalogKeys" /></c:set>
	 <c:set var="twitter_createWallet"><bbbc:config key="pixel_twitter_createWallet" configName="ContentCatalogKeys" /></c:set>
	 <dsp:droplet name="Switch">
			<dsp:param name="value" bean="Profile.transient"/>
			
			<dsp:oparam name="false">
					<dsp:droplet name="/atg/dynamo/droplet/Redirect">
					  <dsp:param name="url" value="${contextPath}/account/coupons.jsp"/>
					</dsp:droplet>
					
				</dsp:oparam>
		
		</dsp:droplet>
	
	<bbb:pageContainer section="accounts">
	<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
	<jsp:attribute name="PageType">WalletRegistration</jsp:attribute>
	<jsp:body>
	
		
		
	
		<div id="content" class="container_12 clearfix">
			<div class="grid_12">
				<h1 class="account fl"><bbbl:label key="lbl_coupon_wallet" language ="${pageContext.request.locale.language}"/></h1>
			</div>
			<div class="grid_12" id="walletRegistration">
				<%-- <img src="/_assets/global/images/couponWallet/walletReg.jpg" width="100%" />--%>
				<div class="grid_4 marTop_10">
				<%-- <img src="/_assets/global/images/couponWallet/walletReg1.jpg" width="100%" />--%>
				<bbbt:textArea key="txt_couponWallet_registration" language ="${pageContext.request.locale.language}"/>
				</div>
				<div class="grid_7 fr">
					 <bbbl:label key="lbl_walletlook_hdr" language="${pageContext.request.locale.language}" />
					 <div class="frmlookupText"><bbbt:textArea key="txt_wallet_lookup" language="${pageContext.request.locale.language}" />
					 <h3><bbbl:label key="lbl_coupon_lookupcouponwallet" language ="${pageContext.request.locale.language}"/></h3>
					 </div>
					<dsp:form id="frmwalletLook" action="${contextPath}/couponWallet/frags/wallet_reg_json.jsp" method="post" iclass="flatform fl">
						<div class="cpnForm flatform">
							<div class="clearfix">
								<div class="input_wrap">
									<label class="popUpLbl" for="lookupEmail"><bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}"/></label>						
										<dsp:input id="lookupEmail" name="email" bean="WalletFormHandler.email"  type="email" iclass="fl lookupEmail">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
										</dsp:input>
								</div>
								<dsp:input id="btnWalLook" iclass="button-Large btnPrimary" bean="WalletFormHandler.lookupWallet" type="submit" value="${btnWalletLook}">
								<dsp:tagAttribute name="aria-pressed" value="false"/>					 
								</dsp:input>
							</div>
						</div>
						<label id="errorlookup" class="error" style="display:none;"><dsp:valueof bean="WalletFormHandler.errorMap.emailError"/></label>
						 <!--<dsp:input bean="WalletFormHandler.lookupCouponSuccessURL" type="hidden" value="${contextPath}/couponWallet/frags/wallet_reg_json.jsp"/>
						 <dsp:input bean="WalletFormHandler.lookupCouponErrorURL" type="hidden" value="${contextPath}/couponWallet/walletRegistration.jsp"/>
						 <dsp:input bean="WalletFormHandler.lookupCouponErrorURL" type="hidden" value="${contextPath}/couponWallet/frags/wallet_reg_json.jsp"/>-->
						 <dsp:input bean="WalletFormHandler.fromPage" type="hidden" value="lookupwalletreg" />
					</dsp:form>
					<div class="fl createone">
						<p><bbbl:label key="lbl_coupon_needacouponwallet" language ="${pageContext.request.locale.language}"/><a href="#" id="btnwalletReg"><bbbl:label key="lbl_coupon_createone_now" language ="${pageContext.request.locale.language}"/></a></p>
						</div>
				</div>
			</div>
		</div>
				<div id="walletRegModal" title="<h3><bbbl:label key="lbl_coupon_createcouponwallet" language ="${pageContext.request.locale.language}"/></h3>">
	        	<strong><bbbl:label key="lbl_coupon_enteryouremailaddress" language ="${pageContext.request.locale.language}"/></strong>
	        	<dsp:form id="frmwalletRegSignup" action="${contextPath}/couponWallet/frags/wallet_regcreate_json.jsp" method="post" iclass="flatform fl">
					<div class="flatform cpnForm">
						<div class="clearfix">
							<div class="input_wrap">
							<label class="popUpLbl" for="email"><bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}"/></label>
								<dsp:input tabindex="3" id="createEmail" name="email" bean="WalletFormHandler.email" type="email" iclass="fl" 
								 onkeyup="if(this.value.length > 0) document.getElementById('btnwalletRegSignup').disabled = false; else document.getElementById('btnwalletRegSignup').disabled = true;">
									<dsp:tagAttribute name="aria-required" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
								</dsp:input>
							</div>
							
							<dsp:input id="btnwalletRegSignup" tabindex="-1" iclass="btnForm btnPrimary" bean="WalletFormHandler.createWallet" onclick="javascript:couponWalletCreate();rkgMicropixelevent();addTwtPixel('${appid}');addFbPixel();" type="submit" value="${create_wallet}">
									<dsp:tagAttribute name="aria-pressed" value="false"/>
							</dsp:input>
							<input type="hidden" name="patch" value="true">
							
							<dsp:getvalueof var="createWalletID"bean="WalletFormHandler.createWalletID" />
						</div>
						<input type="Checkbox" name="walletOptin" id="walletOptin" checked="true" data-submit-optinurl="${optinURL}"/>
						<div class="walletSignUpText"><label for="walletOptin" class="lblwalletOptin"><bbbl:label key="lbl_coupon_walletOptin" language ="${pageContext.request.locale.language}"/></label>
							<strong><bbbt:textArea key="txt_coupon_walletoptin_privacy" language ="${pageContext.request.locale.language}"/></strong></div>
					</div>
                <label id="errorwalsignup" class="error" style="display:none;"><dsp:valueof bean="WalletFormHandler.errorMap.emailError"/></label>
				<!--<dsp:input bean="WalletFormHandler.regSuccessURL" type="hidden" value="${contextPath}/couponWallet/frags/wallet_regcreate_json.jsp"/>
				<dsp:input bean="WalletFormHandler.regErrorURL" type="hidden" value="${contextPath}/couponWallet/frags/wallet_regcreate_json.jsp"/>-->
				<dsp:input bean="WalletFormHandler.fromPage" type="hidden" value="walletreg" />
				
	        </dsp:form>
	        
	        </div>
	        
	<script>
	
	function rkgMicropixelevent()
	{			
		var isrkgMicropixel='${createWalletID}';			
	    if (isrkgMicropixel === 'false')
	    {
		  rkg_micropixel('${appid}','couponwallet');
	    }			
	}
	
	</script>
	
	<script>
	
	function createCouponWallet()
	{			
		var isCreateWalletInvoked ='${createWalletID}';			
	    if (isCreateWalletInvoked === true)
	    {
	    	couponWalletCreate();
	    }			
	}
		
	</script>
	
	<script>
	
	function addFbPixel()
	{			
		var fbPixelTrkId = $("#fbPixelTrkId").val();
		var _fbq = window._fbq || (window._fbq = []);
	 	if (!_fbq.loaded) {
		    var fbds = document.createElement('script');
		    fbds.async = true;
		    fbds.src = '//connect.facebook.net/en_US/fbds.js';
		    var s = document.getElementsByTagName('script')[0];
		    s.parentNode.insertBefore(fbds, s);
		    _fbq.loaded = true;
	    }			
	  	window._fbq = window._fbq || [];
		window._fbq.push(['track', fbPixelTrkId, {'value':'0.00','currency':'USD'}]);
		var href ='https://www.facebook.com/tr?ev='+fbPixelTrkId+'&amp;cd[value]=0.00&amp;cd[currency]=USD&amp;noscript=1';	
		var img ='<img height="1" width="1" style="display: none" src="' + href + '"/>';
		$(document.body).append(img);			
	}
	
	</script>
	
	<script>
	
	function addTwtPixel(siteId)
	{			
		var twtPixelTrkId = $("#twtPixelTrkId").val();
		twttr.conversion.trackPid(twtPixelTrkId);
		var href1 ='https://analytics.twitter.com/i/adsct?txn_id='+twtPixelTrkId+'&p_id=Twitter';
		var href2 = '//t.co/i/adsct?txn_id='+twtPixelTrkId+'&p_id=Twitter';
		var img1 ='<img height="1" width="1" style="display: none" src="' + href1 + '"/>';
		var img2 ='<img height="1" width="1" style="display: none" src="' + href2 + '"/>';
		$(document.body).append(img1);
		$(document.body).append(img2);			
	}
		
	</script>
	</jsp:body>
<jsp:attribute name="footerContent">
	<script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.pageName = 'My Account>Coupon Wallet Registration Page';
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