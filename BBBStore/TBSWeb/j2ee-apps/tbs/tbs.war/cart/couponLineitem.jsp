<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  couponLineItems.jsp
 *
 *  DESCRIPTION: fragment for coupons form displaying individual coupons
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    
    <dsp:getvalueof var="selected" param="select"/>
    <dsp:getvalueof var="noForm" param="noForm"/>
    <dsp:getvalueof var="fullPage" param="couponPage"/>
    <dsp:getvalueof var="existingPromoCode" bean="/atg/commerce/ShoppingCart.current.SchoolCoupon" />
    <dsp:getvalueof var="couponId" param="key"/>
    <c:set var="showPromoError" value="${false}" scope="page" />
    <dsp:droplet name="Switch">
		<dsp:param name="value" bean="CartModifierFormHandler.formError"/>
		<dsp:oparam name="true">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="CartModifierFormHandler.formExceptions"/>
				<dsp:oparam name="output">
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="element.errorCode"/>
						<dsp:oparam name="err_schoolPromoCode_BLANK">
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoError" scope="page" value="error"/>
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoErrorMsg" scope="page" param="element.message"/>
							<c:set var="showPromoError" value="${true}" scope="page" />
						</dsp:oparam>
						<dsp:oparam name="err_schoolPromoCode_UNMATCH">
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoError" scope="page" value="error"/>
							<dsp:getvalueof vartype="java.lang.String" var="schoolPromoErrorMsg" scope="page" param="element.message"/>
							<c:set var="showPromoError" value="${true}" scope="page" />
						</dsp:oparam>
						<dsp:oparam name="default">
							<dsp:getvalueof var="couponId" param="key"/>	
							<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList.${couponId}"/>
							<c:if test="${not empty errorMap && not selected}">
								<dsp:getvalueof var="couponNotApplied" scope="page" value="couponNotApplied"/>
								<dsp:getvalueof var="couponErrorList" scope="page" bean="CartModifierFormHandler.couponErrorList"/>					
							</c:if>	
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	                            
    <div class="coupon ${couponNotApplied} clearfix">   
        <div class="checkboxItem fl input">          
            <dsp:getvalueof var="couponId" param="key"/>
            <dsp:getvalueof var="index" param="index"/>
            <c:choose>
                <c:when test="${couponId == 'schoolPromo'}">
                	<c:choose>
		                <c:when test="${selected}">
		                    <c:set var="applied" value="applied"/>
		                    <div class="checkbox">
		                        <dsp:input bean="CartModifierFormHandler.couponList.${index }" type="checkbox" checked="true" id="${couponId }" value="${couponId}" >
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                </dsp:input>
		                    </div>
		                    <div class="label applied"><label for="${couponId }"><bbbl:label key="lbl_coupon_applied_text" language="${language}"/></label></div>
		                </c:when>
		                <c:otherwise>           
		                    <c:set var="applied" value=""/>         
		                    <div class="checkbox">
		                        <dsp:input bean="CartModifierFormHandler.couponList.${index }" checked="false" type="checkbox" id="${couponId }" value="${couponId }" >
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                </dsp:input>
		                    </div>
		                    <div class="label"><label for="${couponId }"><bbbl:label key="lbl_coupon_select_text" language="${language}"/></label></div>
		                </c:otherwise>
            		</c:choose>
                </c:when>
                <c:otherwise>
                	<c:choose>
		                <c:when test="${selected}">
		                    <c:set var="applied" value="applied"/>
		                    <div class="checkbox">
		                        <dsp:input bean="CartModifierFormHandler.couponList.${index }" type="checkbox" checked="false" id="offer1_off20${couponId }" value="${couponId }" >
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                </dsp:input>
		                    </div>
		                    <div class="label applied"><label for="offer1_off20${couponId }"><bbbl:label key="lbl_coupon_applied_text" language="${language}"/></label></div>
		                </c:when>
		                <c:otherwise>           
		                    <c:set var="applied" value=""/>         
		                    <div class="checkbox">
		                        <dsp:input bean="CartModifierFormHandler.couponList.${index }" checked="false" type="checkbox" id="offer1_off20${couponId }" value="${couponId }" >
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                </dsp:input>
		                    </div>
		                    <div class="label"><label for="offer1_off20${couponId }"><bbbl:label key="lbl_coupon_select_text" language="${language}"/></label></div>
		                </c:otherwise>
            		</c:choose>   
                </c:otherwise>
            </c:choose>
        </div>
        <c:set var="scene7Path">
            <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
        </c:set>
        <div class="couponImage fl">
            <span class="border ${applied}"></span>
            <dsp:getvalueof var="promoIMGURL" param="item.media.mainImage.url"></dsp:getvalueof>
            
				<c:choose>
                   	<c:when test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
                   		<img src='<dsp:valueof param="item.media.mainImage.url"/>' height="74" width="398" alt='<dsp:valueof param="item.displayName"/>' />
                   	</c:when>
                   	<c:otherwise>
                   		<img src='${scene7Path}/<dsp:valueof param="item.media.mainImage.url"/>' height="74" width="398" alt='<dsp:valueof param="item.displayName"/>' />
                   	</c:otherwise>
				</c:choose>
        </div>
        <div class="couponInfo fl">            
            <div class="label"><label><strong><dsp:valueof param="item.displayName" valueishtml="true"/></strong></label></div>
            <p><dsp:valueof param="item.description" valueishtml="true"/></p>
            
             <c:set var="title"><bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/></c:set>
             <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="item.tandc"/>
                <dsp:oparam name="false">
                	 <dsp:a iclass="popup" page="/checkout/coupons/exclusions.jsp" title='${title}' onclick="return false"><dsp:param name="item" value="${couponId}"/>
									<bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/>
					 </dsp:a>                       
                </dsp:oparam>
             </dsp:droplet>
            
        </div>
        <c:if test="${empty existingPromoCode}">   
        <c:if test="${couponId == 'schoolPromo'}">
         <div class="clear"></div>
       	 <div class="couponPromoCode fl">
				<div class="input clearfix">
					<div class="label fl marRight_5">
						<label id="lblcouponPromoCode" for="couponPromoCode"><bbbl:label key="lbl_coupon_promo_code" language="${language}"/></label>
					</div>
					<div class="text fl width_2">
						<dsp:input id="couponPromoCode" iclass="${schoolPromoError}" name="couponPromoCode" type="text"  bean="CartModifierFormHandler.couponClaimCode" disabled="true">
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcouponPromoCode errorcouponPromoCode"/>
                        </dsp:input>
						<label id="errorcouponPromoCode" class="error" for="couponPromoCode" generated="true" <c:if test="${showPromoError}"> style="display: block;" </c:if> ><dsp:valueof value="${schoolPromoErrorMsg}"/></label>						
						<label for="couponPromoCode" class="offScreen"><bbbl:label key="lbl_coupon_promo_code" language="${language}"/></label>
					</div>
                    <div class="clear"></div>
				</div>
			</div>
        </c:if>
        </c:if>
        <%-- Below If block is for R2 requirement --%>	
        <c:if test="${not empty couponErrorList && not selected}">
        	<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList.${couponId}"/>
        	<c:choose>
	        	<c:when test="${not empty errorMap}">
	        			<div class="couponErrorMsg cb clearfix">
							<h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3>
							<p><bbbe:error key="${errorMap}" language="${pageContext.request.locale.language}"/></p>
						 </div>
	        	</c:when>
	        	<%-- <c:otherwise>
	        		<div class="couponErrorMsg cb clearfix">
	        			<dsp:getvalueof id="genericErrorMap" bean="CartModifierFormHandler.couponErrorList.error"/>
						<h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3>
						<p><bbbe:error key="${genericErrorMap}" language="${pageContext.request.locale.language}"/></p>
					</div>
	        	</c:otherwise> --%>
        	</c:choose>
		</c:if>
		<div class="clear"></div>
    </div>
</dsp:page>