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
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
   <%--<dsp:getvalueof var="selected" param="select"/> --%>
    <dsp:getvalueof var="noForm" param="noForm"/>
    <dsp:getvalueof var="fullPage" param="couponPage"/>
    <dsp:getvalueof var="existingPromoCode" bean="/atg/commerce/ShoppingCart.current.SchoolCoupon" />
    <%--<dsp:getvalueof var="couponId" param="key"/> --%>
    <dsp:getvalueof var="onlineCouponList" param="onlineCouponList"/>
    <dsp:getvalueof var="useAnywhereCouponList" param="useAnywhereCouponList"/>
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
							<dsp:getvalueof id="appliedCouponMap" bean="CartModifierFormHandler.appliedCouponMap.${couponId}"/>
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
    <%-- Adding omniture tag--%>
	<c:forEach var="entry" items="${appliedCouponMap}">
		 <c:set var="appliedCoupon" value="${entry.key}" />
		<c:set var="couponName" value="${entry.value}" /> 
		 
	<c:if test="${!empty couponName }">
			<script type="text/javascript">
	 			couponWalletAction('${couponName}','apply coupon');
	 		</script>
		</c:if>
	</c:forEach>
	<%-- End Adding omniture tag --%>

	<%--Start use anywhere coupon list --%>
    <c:if test="${fn:length(useAnywhereCouponList)>0}">
	    <h2 class="couponsHeader fl grid_8 alpha omega"><bbbl:label key="lbl_coupon_useanywhere" language="${pageContext.request.locale.language}"/></h2>
	</c:if>
	<div class="grid_10 alpha omega">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="useAnywhereCouponList" />
		<dsp:oparam name="output">
			<div class="coupon clearfix grid_4 alpha omega">
				<dsp:setvalue param="appliedCouponListVO" paramvalue="element" />
				<dsp:getvalueof var="appliedCouponListVO" vartype="java.lang.Object"
					param="appliedCouponListVO" />
				<c:set var="couponId" value="${appliedCouponListVO.couponId}" />
				<input type="hidden" id="couponCode" value="${couponId}"/>
				<dsp:getvalueof var="count" param="count" />
				<dsp:getvalueof var="size" param="size" />
				<c:set var="couponType"
					value="${appliedCouponListVO.couponsImageUrl}" />
				<c:set var="uniqueCouponCd"
					value="${appliedCouponListVO.uniqueCouponCd}" />
				<c:set var="expiryDate" value="${appliedCouponListVO.expiryDate}" />
				<c:set var="expiryCount" value="${appliedCouponListVO.expiryCount}" />
				<c:set var="displayExpiryDate" value="${appliedCouponListVO.displayExpiryDate}" />
				<c:set var="selected" value="${appliedCouponListVO.selected}" />
				<c:if test="${not empty couponErrorList && not selected}">
					<dsp:getvalueof id="errorMap"
						bean="CartModifierFormHandler.couponErrorList.${couponId}" />
					<c:if test="${not empty errorMap}">
						<div class="coupon ${couponNotApplied} clearfix">
					</c:if>
				</c:if>
        <c:set var="scene7Path">
            <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
        </c:set>
        <div class="couponImage fl">
					<c:if test="${expiryCount eq 1 }">
						<div class="couponImageLabel expToday">
							<span> <bbbl:label key="lbl_coupon_expiresIn_0"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 2 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_1"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 3 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_2"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 4 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_3"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 5 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_4"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 6 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_5"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<%--<c:choose>
						<c:when test="${selected}">
							<c:set var="applied" value="applied" />
						</c:when>
						<c:otherwise>
							<c:set var="applied" value="" />
						</c:otherwise>
					</c:choose>
					<span class="border ${applied}"></span>  --%>
           <c:set var="promoIMGURL" value="${appliedCouponListVO.couponsImageUrl}"/>
				<c:choose>
                   	<c:when test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
                   		<img src="${promoIMGURL}" height="100%" width="100%" alt="${appliedCouponListVO.description}" />
                   	</c:when>
                   	<c:otherwise>
                   		<img src='${scene7Path}/${promoIMGURL}' height="100%" width="100%" alt='${appliedCouponListVO.description}' />
                   	</c:otherwise>
				</c:choose>
        </div>
        <div class="couponInfo fl marTop_10 grid_2 omega">
            <div class="label grid_2 alpha omega">
            	<label class="couponHeader grid_2 alpha omega">
            		<strong>${appliedCouponListVO.displayName}</strong>
            	</label>
            </div>

           		<p class="couponExp fl grid_2 alpha omega"><bbbl:label key="lbl_coupon_expires" language ="${pageContext.request.locale.language}"/> ${displayExpiryDate }</p>
             <c:set var="title"><bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/></c:set>
             <c:set value="${appliedCouponListVO.mCouponsExclusions}" var="mCouponsExclusions"/>
             <c:set var="promoId" value="${appliedCouponListVO.promoId}" />
					<dsp:droplet name="IsEmpty">
						<dsp:param name="value" value="${appliedCouponListVO.mCouponsExclusions}" />
						<dsp:oparam name="false">
							<a 
								href="${contextPath}/checkout/coupons/exclusions.jsp?item=${appliedCouponListVO.couponId}"
								class="popup grid_2 alpha omega"
								title="Exclusions"><bbbl:label
									key="lbl_coupon_exclusions_title"
									language="${pageContext.request.locale.language}" />
							</a>
						</dsp:oparam>
					</dsp:droplet>

             <div class="checkboxItem fl input grid_2 alpha omega <c:if test="${fullPage eq 'COUPONS'}">checkoutCoupon</c:if>">
		            <c:choose>
		                <c:when test="${couponId == 'schoolPromo'}">
		                	<c:choose>
				                <c:when test="${selected}">
				                    <c:set var="applied" value="applied"/>
				                    <div class="checkbox">
				                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="checkbox" checked="true" id="${couponId }" value="${couponId}" >
		                                    <dsp:tagAttribute name="aria-checked" value="true"/>
		                                </dsp:input>
				                    </div>
				                    <div class="label applied"><label for="${couponId }"><span class="txtOffScreen">Offer Applied for ${appliedCouponListVO.displayName}.</span><bbbl:label key="lbl_coupon_applied_text" language="${language}"/></label>
				                      <c:if test="${fullPage eq 'COUPONS' }"><span class="icon-checkmark"></span></c:if>
				                    </div>
				                </c:when>
				                <c:otherwise>
				                    <c:set var="applied" value=""/>
				                    <div class="checkbox">
				                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" checked="false" type="checkbox" id="${couponId }" value="${couponId }" >
		                                    <dsp:tagAttribute name="aria-checked" value="false"/>
		                                </dsp:input>
				                    </div>
				                    <div class="label checkboxShow"><label for="${couponId }">
									<span class="txtOffScreen">Select/apply ${appliedCouponListVO.displayName}</span>
				                    <c:choose>
				                       <c:when test="${fullPage eq 'COUPONS' }">
				                          <bbbl:label key="lbl_coupon_apply_text" language="${language}"/>
				                       </c:when>				                    
				                        <c:otherwise>
				                               <bbbl:label key="lbl_coupon_select_text" language="${language}"/>
				                         </c:otherwise>
				                      </c:choose>
				                    </label>
				                    </div>
				                </c:otherwise>
		            		</c:choose>
		                </c:when>
		                <c:otherwise>
		                	<c:choose>
				                <c:when test="${selected}">
				                    <c:set var="applied" value="applied"/>
				                    <div class="checkbox">
									<span class="couponSel visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
				                        <dsp:input bean="CartModifierFormHandler.couponList.${count}" type="checkbox" checked="true" id="offer1_off20${couponId }" value="${couponId }" >
		                                    <dsp:tagAttribute name="aria-checked" value="true"/>
		                                </dsp:input>
				                    </div>
				                    <div class="label applied" tabindex="0" aria-label="Your coupon has been applied"><label for="offer1_off20${couponId }"><span class="txtOffScreen">Offer Applied for ${appliedCouponListVO.displayName}.</span><bbbl:label key="lbl_coupon_applied_text" language="${language}"/></label>
				                    <c:if test="${fullPage eq 'COUPONS' }"><span class="icon-checkmark"></span></c:if></div>
				                </c:when>
				                <c:otherwise>
				                    <c:set var="applied" value=""/>
				                    <div class="checkbox">
									<span class="couponSel visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
				                        <dsp:input bean="CartModifierFormHandler.couponList.${count}" checked="false" type="checkbox" id="offer1_off20${couponId }" value="${couponId }" >
		                                    <dsp:tagAttribute name="aria-checked" value="false"/>
		                                </dsp:input>
				                    </div>
				                    <div class="label checkboxShow"><label for="offer1_off20${couponId }">
                                                    <span class="txtOffScreen">Select/apply ${appliedCouponListVO.displayName}</span>
				                     <c:choose>
				                       <c:when test="${fullPage eq 'COUPONS' }">
				                          <bbbl:label key="lbl_coupon_apply_text" language="${language}"/>
				                       </c:when>				                    
				                        <c:otherwise>
				                               <bbbl:label key="lbl_coupon_select_text" language="${language}"/>
				                         </c:otherwise>
				                      </c:choose>
				                    </label></div>
				                </c:otherwise>
		            		</c:choose>
		                </c:otherwise>
		            </c:choose>
		        </div>
	    </div>

        <div class="clear"></div><br/>
        
        <div class="couponErrorMsg cb clearfix grid_4 alpha omega hidden" id="couponErrorMsg${couponId}">
			<div role='alert'><h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3></div>
			<p class="errorMsg"></p>
			<a class="dsmiss button-Small btnSecondary"><bbbl:label key="lbl_coupon_wallet_dismiss" language ="${pageContext.request.locale.language}"/></a>
		</div>
        	
         <c:if test="${not empty couponErrorList && not selected}">
        	<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList.${couponId}"/>
        	<c:choose>
	        	<c:when test="${not empty errorMap}">
	        			<div class="couponErrorMsg cb clearfix">
							<div role='alert'><h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3></div>
							<p><bbbe:error key="${errorMap}" language="${pageContext.request.locale.language}"/></p></a>
							<a class="dsmiss button-Small btnSecondary"><bbbl:label key="lbl_coupon_wallet_dismiss" language ="${pageContext.request.locale.language}"/></a>
						 </div>
	        	</c:when>
	        	<%-- <c:otherwise>
	        		<div class="couponErrorMsg cb clearfix">
	        			<dsp:getvalueof id="genericErrorMap" bean="CartModifierFormHandler.couponErrorList.error"/>
						<div role='alert'><h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3></div>
						<p><bbbe:error key="${genericErrorMap}" language="${pageContext.request.locale.language}"/></p>
					</div>
	        	</c:otherwise> --%>
        	</c:choose>
		</c:if>
		<%--if div --%>
				<c:if test="${not empty couponErrorList && not selected}">
					<dsp:getvalueof id="errorMap"
						bean="CartModifierFormHandler.couponErrorList.${couponId}" />
					<c:if test="${not empty errorMap}">
			</div>
			</c:if>
			</c:if>
			<%--end if div --%>
        </div>
        </dsp:oparam>
        </dsp:droplet>
</div>
    <%--end use anywhere coupon list --%>
    <%-- start online coupon list --%>
    <c:if test="${fn:length(onlineCouponList)>0}">
	    <h2 class="couponsHeader fl grid_8 alpha omega"><bbbl:label key="lbl_coupon_useOnline" language="${pageContext.request.locale.language}"/></h2>
    </c:if>
    <div class="grid_10 alpha omega">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="onlineCouponList" />
		<dsp:oparam name="output">

			<div class="coupon clearfix grid_4 alpha omega">
				<dsp:setvalue param="appliedCouponListVO" paramvalue="element" />
				<dsp:getvalueof var="appliedCouponListVO" vartype="java.lang.Object"
					param="appliedCouponListVO" />
				<c:set var="couponId" value="${appliedCouponListVO.couponId}" />
				<input type="hidden" id="couponCode${couponId}" value="${couponId}"/>
				<dsp:getvalueof var="count" param="count" />
				<c:set var="couponType"
					value="${appliedCouponListVO.couponsImageUrl}" />
				<c:set var="uniqueCouponCd"
					value="${appliedCouponListVO.uniqueCouponCd}" />
				<c:set var="expiryDate" value="${appliedCouponListVO.expiryDate}" />
				<c:set var="displayExpiryDate" value="${appliedCouponListVO.displayExpiryDate}" />
				<c:set var="expiryCount" value="${appliedCouponListVO.expiryCount}" />
				<c:set var="selected" value="${appliedCouponListVO.selected}" />
				<c:set var="promoId" value="${appliedCouponListVO.promoId}" />
				<c:if test="${not empty couponErrorList && not selected}">
					<dsp:getvalueof id="errorMap"
						bean="CartModifierFormHandler.couponErrorList.${couponId}" />
					<c:if test="${not empty errorMap}">
						<div class="coupon ${couponNotApplied} clearfix grid_4 alpha omega">
					</c:if>
				</c:if>
				<c:set var="scene7Path">
					<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
				</c:set>
				<div class="couponImage fl">
					<c:if test="${expiryCount eq 1 }">
						<div class="couponImageLabel expToday">
							<span> <bbbl:label key="lbl_coupon_expiresIn_0"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 2 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_1"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 3 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_2"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 4 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_3"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 5 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_4"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
					<c:if test="${expiryCount eq 6 }">
						<div class="couponImageLabel expSoon">
							<span> <bbbl:label key="lbl_coupon_expiresIn_5"
									language="${pageContext.request.locale.language}" /> </span>
						</div>
					</c:if>
				<%--	<c:choose>
						<c:when test="${selected}">
							<c:set var="applied" value="applied" />
						</c:when>
						<c:otherwise>
							<c:set var="applied" value="" />
						</c:otherwise>
					</c:choose>
					<span class="border ${applied}"></span>  --%>
					<c:set var="promoIMGURL"
						value="${appliedCouponListVO.couponsImageUrl}" />
					<c:choose>
						<c:when
							test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
							<img src="${promoIMGURL}" height="100%" width="100%"
								alt="${appliedCouponListVO.description}" />
						</c:when>
						<c:otherwise>
							<img src='${scene7Path}/${promoIMGURL}' height="74" width="398"
								alt='${appliedCouponListVO.description}' />
						</c:otherwise>
					</c:choose>
				</div>
				<div class="couponInfo fl marTop_10 grid_2 omega">
					<div class="label grid_2 alpha omega">
						<label class="couponHeader grid_2 alpha omega">
							<strong>${appliedCouponListVO.displayName}</strong>
						</label>
					</div>
				 <c:if test="${not empty displayExpiryDate}">
					<p class="couponExp fl grid_2 alpha omega"><bbbl:label key="lbl_coupon_expires" language ="${pageContext.request.locale.language}"/> ${displayExpiryDate }</p>
					</c:if>
					
					<c:set var="title">
						<bbbl:label key="lbl_coupon_exclusions_title"
							language="${language}" />
					</c:set>
					<c:set value="${appliedCouponListVO.mCouponsExclusions}" var="mCouponsExclusions" />
					<c:set var="promoId" value="${appliedCouponListVO.promoId}" />
					<dsp:droplet name="IsEmpty">
						<dsp:param name="value" value="${appliedCouponListVO.mCouponsExclusions}" />
						<dsp:oparam name="false">
							<a 
								href="${contextPath}/checkout/coupons/exclusions.jsp?item=${appliedCouponListVO.couponId}"
								class="popup grid_2 alpha omega"
								title="Exclusions"><bbbl:label
									key="lbl_coupon_exclusions_title"
									language="${pageContext.request.locale.language}" />
							</a>
						</dsp:oparam>
					</dsp:droplet>

					<div class="checkboxItem fl input grid_2 alpha omega <c:if test="${fullPage eq 'COUPONS'}">checkoutCoupon</c:if>">

					<c:choose>
						<c:when test="${couponId == 'schoolPromo'}">
							<c:choose>
								<c:when test="${selected}">
									<c:set var="applied" value="applied" />
									<div class="checkbox fl">
										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											type="checkbox" checked="true" id="${couponId }"
											value="${couponId}">
											<dsp:tagAttribute name="aria-checked" value="true" />
										</dsp:input>
									</div>
									<div class="label applied">
										<label for="${couponId }"><span class="txtOffScreen">Offer Applied for ${appliedCouponListVO.displayName}.</span>
										<bbbl:label
												key="lbl_coupon_applied_text" language="${language}" />
										</label>
										 <c:if test="${fullPage eq 'COUPONS' }"><span class="icon-checkmark"></span></c:if>
									</div>
								</c:when>
								<c:otherwise>
									<c:set var="applied" value="" />
									<div class="checkbox">
										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											checked="false" type="checkbox" id="${couponId }"
											value="${couponId }">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>
									</div>
									<div class="label checkboxShow">
										<label for="${couponId }">
										<span class="txtOffScreen">Select/apply ${appliedCouponListVO.displayName}</span>
												<c:choose>
				                                    <c:when test="${fullPage eq 'COUPONS' }">
				                                                 <bbbl:label key="lbl_coupon_apply_text" language="${language}"/>
				                                    </c:when>				                    
				                                 <c:otherwise>
				                                      <bbbl:label
												        key="lbl_coupon_select_text" language="${language}" />
				                                   </c:otherwise>
				                                </c:choose>
										</label>
									</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${selected}">
									<c:set var="applied" value="applied" />
									<div class="checkbox">
										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											type="checkbox" checked="true" id="offer1_off20${couponId }"
											value="${couponId }">
											<dsp:tagAttribute name="aria-checked" value="true" />
										</dsp:input>
									</div>
									<div class="label applied">
										<label for="offer1_off20${couponId }"><span class="txtOffScreen">Offer Applied for ${appliedCouponListVO.displayName}.</span><bbbl:label
												key="lbl_coupon_applied_text" language="${language}" />
										</label>
										 <c:if test="${fullPage eq 'COUPONS' }"><span class="icon-checkmark"></span></c:if>
									</div>
								</c:when>
								<c:otherwise>
									<c:set var="applied" value="" />
									<div class="checkbox">
										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											checked="false" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>
									</div>
									<div class="label checkboxShow">
										<label for="offer1_off20${couponId }">
										<span class="txtOffScreen">Select/apply ${appliedCouponListVO.displayName}</span>
										 <c:choose>
				                       <c:when test="${fullPage eq 'COUPONS' }">
				                          <bbbl:label key="lbl_coupon_apply_text" language="${language}"/>
				                       </c:when>				                    
				                        <c:otherwise>
				                               <bbbl:label key="lbl_coupon_select_text" language="${language}"/>
				                         </c:otherwise>
				                      </c:choose>
										</label>
									</div>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</div>

				</div>
				<c:if test="${empty existingPromoCode}">
					<c:if test="${couponId == 'schoolPromo'}">
						<div class="clear"></div>
						<div class="couponPromoCode fl">
							<div class="input clearfix">
								<div class="label fl marRight_5">
									<label id="lblcouponPromoCode" for="couponPromoCode"><bbbl:label
											key="lbl_coupon_promo_code" language="${language}" />
									</label>
								</div>
								<div class="text fl width_2">
									<dsp:input id="couponPromoCode" iclass="${schoolPromoError}"
										name="couponPromoCode" type="text"
										bean="CartModifierFormHandler.couponClaimCode" disabled="true">
										<dsp:tagAttribute name="aria-required" value="false" />
										<dsp:tagAttribute name="aria-labelledby"
											value="lblcouponPromoCode errorcouponPromoCode" />
									</dsp:input>
									<label id="errorcouponPromoCode" class="error"
										for="couponPromoCode" generated="true"
										<c:if test="${showPromoError}"> style="display: block;" </c:if>><dsp:valueof
											value="${schoolPromoErrorMsg}" />
									</label> <label for="couponPromoCode" class="offScreen"><bbbl:label
											key="lbl_coupon_promo_code" language="${language}" />
									</label>
								</div>
								<div class="clear"></div>
							</div>
						</div>
					</c:if>
				</c:if>
				<div class="clear"></div>
				<br />
			<div class="couponErrorMsg cb clearfix grid_4 alpha omega hidden" id="couponErrorMsg${couponId}">
			<div role='alert'><h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3></div>
			<p class="errorMsg"></p>
			<a class="dsmiss button-Small btnSecondary"><bbbl:label key="lbl_coupon_wallet_dismiss" language ="${pageContext.request.locale.language}"/></a>
		</div>
				
				<c:if test="${not empty couponErrorList && not selected}">
					<dsp:getvalueof id="errorMap"
						bean="CartModifierFormHandler.couponErrorList.${couponId}" />
					<c:choose>
						<c:when test="${not empty errorMap}">
							<div class="couponErrorMsg cb clearfix">
								<div role='alert'><h3>
									<bbbe:error key="err_coupon_cannot_apply"
										language="${pageContext.request.locale.language}" />
								</h3></div>
								<p>
									<bbbe:error key="${errorMap}"
										language="${pageContext.request.locale.language}" />
								</p>
								<a class="dsmiss button-Small btnSecondary">DISMISS</a>
							</div>
						</c:when>
						<%-- <c:otherwise>
	        		<div class="couponErrorMsg cb clearfix">
	        			<dsp:getvalueof id="genericErrorMap" bean="CartModifierFormHandler.couponErrorList.error"/>
						<div role='alert'><h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3>
						<p><bbbe:error key="${genericErrorMap}" language="${pageContext.request.locale.language}"/></p></div>
					</div>
	        	</c:otherwise> --%>
					</c:choose>
				</c:if>
				<%--if div --%>
				<c:if test="${not empty couponErrorList && not selected}">
					<dsp:getvalueof id="errorMap"
						bean="CartModifierFormHandler.couponErrorList.${couponId}" />
					<c:if test="${not empty errorMap}">
			</div>
			</c:if>
			</c:if>
			<%--end if div --%>
        </div>
        </dsp:oparam>
        </dsp:droplet>
</div>
        <%--End online coupons list --%>

        <%-- Below If block is for R2 requirement --%>



</dsp:page>
<script>
$(document).ready(function(){
   $('a[href="#hero"]').attr('tabindex','-1');
   $('a[href="#department-navigation"]').attr('tabindex','-1');
   $(".applied").focus();
});
</script>
