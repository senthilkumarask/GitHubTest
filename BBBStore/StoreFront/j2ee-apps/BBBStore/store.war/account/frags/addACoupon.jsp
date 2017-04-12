<dsp:page>

<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="couponsWelcomeMsg" bean="SessionBean.couponsWelcomeMsg" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="walletId" value="${walletId}"/>


<dsp:getvalueof var="walletIDfromProfile" bean="Profile.walletId"/>

        <c:choose>
		<c:when test="${empty walletId}">
        <c:set var="walletId" value="${walletIDfromProfile}"/>
        </c:when>
		<c:otherwise>
		<c:set var="walletId" value="${walletId}"/>							
		</c:otherwise>
		</c:choose>


  	<%-- position add coupon form based on welcome msg --%>
  	<c:choose>
      <%-- <c:when test="${appid ne 'BedBathCanada' && couponsWelcomeMsg}">    --%>                 
      <c:when test="${couponsWelcomeMsg}">   
         <c:set var="under" value="" />
      </c:when>
      <c:otherwise>
      	<c:set var="under" value="under" />                    
      </c:otherwise>
   </c:choose>



   <%-- add a coupon form depends on the original session value for display rules. 
      will set the value to false on first view      
   --%>
   <c:if test="${couponsWelcomeMsg}">                    
   	<dsp:setvalue bean="SessionBean.couponsWelcomeMsg" value="false"/>
   </c:if>

	<div class="widgetContainer">
	<div class="addCpnIcon" aria-label="upload a coupon" tabindex="0" role="button"></div>
	   <div class="formWrap ${under}">
	      <div class="inner-formWrap">
	         <h3><bbbl:label key="lbl_coupon_addacoupon" language ="${pageContext.request.locale.language}"/></h3>
	        	<p><bbbl:label key="lbl_coupon_entercode" language ="${pageContext.request.locale.language}"/></p>


	        	<c:set var="enter_coupon_placeholder"><bbbl:label key="lbl_coupon_addacoupon_placeholder" language ="${pageContext.request.locale.language}"/></c:set>
	        	
            <dsp:form id="addCouponForm" iclass="flatform" action="${contextPath}/account/frags/add_a_coupon_json.jsp" method="post">

	            <dsp:input tabindex="0" id="couponCode" name="couponCode" bean="WalletFormHandler.couponCode"  type="text" iclass="fl couponCode">
			  			<dsp:tagAttribute name="aria-required" value="true"/>
                  <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                  <dsp:tagAttribute name="placeholder" value="${enter_coupon_placeholder}"/>

		    		</dsp:input>
				
					<dsp:input bean="WalletFormHandler.walletID" type="hidden" value="${walletId}"/>
										
					<dsp:input id="addCoupon" iclass="button-Large btnPrimary" tabindex="0" bean="WalletFormHandler.addCoupon" type="submit" value="ADD">
						<dsp:tagAttribute name="aria-pressed" value="false"/>
					</dsp:input>

					<label for="couponCode" generated="true" class="error errorLabel" id="errorcouponCode" style="display: none; clear: both;"></label>
				<label id="errorCoupon" class="error" style="display:none;"></label>
				
					<span id="addCouponLoader" style="display:none;"><img src="/_assets/global/images/widgets/small_loader.gif" /></span>	
	          	<%-- <a class="button-Med btnPrimary fr">ADD</a> --%>
		        
	         </dsp:form>
	      </div>
	   </div>
	   
	   <div class="popup popIn start">
	   	<span><bbbl:label key="lbl_coupon_addacoupon" language ="${pageContext.request.locale.language}"/></span>
	   </div>
	   
	     
	</div>
	
</dsp:page>
<script>
$(document).ready(function(){
   $('.addCpnIcon').keypress(function(e){
       if((e.keyCode ? e.keyCode : e.which)==13){
             $(this).trigger('click');
       }
    });
});
</script>
