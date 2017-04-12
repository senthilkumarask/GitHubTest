<dsp:page>
  <dsp:importbean bean="/com/bbb/account/GetCouponsDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <div class="spacer clearfix">
		<h2 class="noMarTop"><bbbl:label key="lbl_account_offers" language ="${pageContext.request.locale.language}"/></h2>
		<div class="info">
			<div class="clearfix">
			<dsp:droplet name="GetCouponsDroplet">
            	<dsp:param name="EMAIL_ADDR" bean="Profile.email"/>
   				<dsp:param name="MOBILE_NUMBER" bean="Profile.mobileNumber"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="couponList" param="couponCount"/>
					<div class="count">${couponList}</div>
					<div class="msg"><bbbl:label key="lbl_overview_coupons" language ="${pageContext.request.locale.language}"/></div>			
				</dsp:oparam>
				
				<dsp:oparam name="error">
					<div class="msg"><bbbl:error key="err_mycoupons_system_error" language ="${pageContext.request.locale.language}"/></div>
				</dsp:oparam>
			</dsp:droplet>
				
			</div>
		</div>
		<a href="${contextPath}/account/coupons.jsp" title="${overview_coupons_view_title}" class="itemLink"><bbbl:label key="lbl_overview_coupons_view" language ="${pageContext.request.locale.language}"/></a> 
	<div class="clear"></div>
	</div>
</dsp:page>