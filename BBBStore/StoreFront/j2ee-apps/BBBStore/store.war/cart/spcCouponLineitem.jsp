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

	<c:set var="applyCoupon"><bbbl:label key="lbl_coupon_apply_coupon" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="unApplyCoupon"><bbbl:label key="lbl_coupon_unapply_coupon" language="${pageContext.request.locale.language}" /></c:set>


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
	    <div class="couponsSectionHeader fl grid_8 alpha omega"><bbbl:label key="lbl_coupon_useanywhere" language="${pageContext.request.locale.language}"/></div>
	</c:if>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="useAnywhereCouponList" />
		<dsp:oparam name="output">

			<div class="coupon clearfix grid_4 alpha omega">
				<dsp:setvalue param="appliedCouponListVO" paramvalue="element" />
				<dsp:getvalueof var="appliedCouponListVO" vartype="java.lang.Object"
					param="appliedCouponListVO" />
					<dsp:getvalueof var="count" param="count" />
				<dsp:getvalueof var="size" param="size" />
				<c:set var="couponId" value="${appliedCouponListVO.couponId}" />
				<c:set var="couponType"
					value="${appliedCouponListVO.couponsImageUrl}" />
				<c:set var="uniqueCouponCd"
					value="${appliedCouponListVO.uniqueCouponCd}" />
				<c:set var="expiryDate" value="${appliedCouponListVO.expiryDate}" />
				<c:set var="displayExpiryDate" value="${appliedCouponListVO.displayExpiryDate}" />
				<c:set var="expiryCount" value="${appliedCouponListVO.expiryCount}" />
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
        <div class="couponInfo fl marTop_10 grid_2 ">
           
        		<%--
            <div class="label grid_2 alpha omega">
            	<label class="couponHeader grid_2 alpha omega">
            		<strong>${appliedCouponListVO.displayName}</strong>
            	</label>
            </div>
				--%>
            
				

				<div class="fl couponBtnWrap grid_2 alpha omega">
		            <c:choose>
		                <c:when test="${couponId == 'schoolPromo'}">
		                	<c:choose>
				                <c:when test="${selected}">
				                    <c:set var="applied" value="applied"/>
				                    
			                    		<input type="button" data-couponid="${couponId}" class="button-Small btnPrimary unapply" value="${unApplyCoupon}" />

			                    		<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}" iclass="couponIdVal" value="${couponId}" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                               </dsp:input>
			                    		--%>


										<dsp:input bean="CartModifierFormHandler.couponList.${count}"
											checked="true" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>

				                </c:when>
				                <c:otherwise>
				                    <c:set var="applied" value=""/>
				                    
				                    <input type="button" data-couponid="${couponId}" class="button-Small btnSecondary apply" value="${applyCoupon}" />

											<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}"  iclass="couponIdVal" value="" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                              </dsp:input>
			                    		--%>


										<dsp:input bean="CartModifierFormHandler.couponList.${count}"
											checked="false" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>

				                </c:otherwise>
		            		</c:choose>
		                </c:when>
		                <c:otherwise>
		                	<c:choose>
				                <c:when test="${selected}">
				                    <c:set var="applied" value="applied"/>
				                  
				                    	<input type="button" data-couponid="${couponId}" class="button-Small btnPrimary unapply" value="${unApplyCoupon}" />

				                    	<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}"  iclass="couponIdVal" value="${couponId}" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                               </dsp:input>
											--%>

										<dsp:input bean="CartModifierFormHandler.couponList.${count}"
											checked="true" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>
			                    	

				                </c:when>
				                <c:otherwise>
				                    <c:set var="applied" value=""/>
				                  
				                    <input type="button" data-couponid="${couponId}" class="button-Small btnSecondary apply" value="${applyCoupon}" />

<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}"  iclass="couponIdVal" value="" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                              </dsp:input>
--%>

										<dsp:input bean="CartModifierFormHandler.couponList.${count}"
											checked="false" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>

				                </c:otherwise>
		            		</c:choose>
		                </c:otherwise>
		            </c:choose>
		        </div>


           	<p class="couponExp fl grid_2 alpha omega"><bbbl:label key="lbl_coupon_expires" language ="${pageContext.request.locale.language}"/> ${displayExpiryDate }</p>
             <c:set var="title"><bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/></c:set>
             <c:set value="${appliedCouponListVO.mCouponsExclusions}" var="mCouponsExclusions"/>
             <c:set var="promoId" value="${appliedCouponListVO.promoId}" />
					<dsp:droplet name="IsEmpty">
						<dsp:param name="value" value="${appliedCouponListVO.mCouponsExclusions}" />
						<dsp:oparam name="false">
							<c:set var="coupon_exclusions_title">
								<bbbl:label
									key="lbl_coupon_exclusions_title"
									language="${pageContext.request.locale.language}" />
							</c:set>

							<dsp:a 	iclass="popup grid_2 alpha omega" 
										page="/checkout/coupons/exclusions.jsp?item=${appliedCouponListVO.couponId}" 
										title='${coupon_exclusions_title}'>        						
        						${coupon_exclusions_title}
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
        <div class="clear"></div><br/>
         <c:if test="${not empty couponErrorList && not selected}">
        	<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList.${couponId}"/>
        	<c:choose>
	        	<c:when test="${not empty errorMap}">
	        			<div class="couponErrorMsg cb clearfix">
							<h3><bbbe:error key="err_coupon_cannot_apply" language="${pageContext.request.locale.language}"/></h3>
							<p><bbbe:error key="${errorMap}" language="${pageContext.request.locale.language}"/></p>
							<a class="dsmiss button-Small btnSecondary"><bbbl:label key="lbl_coupon_wallet_dismiss" language ="${pageContext.request.locale.language}"/></a>
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

    <%--end use anywhere coupon list --%>
    <%-- start online coupon list --%>
    <c:if test="${fn:length(onlineCouponList)>0}">
	    <div class="couponsSectionHeader fl grid_8 alpha omega"><bbbl:label key="lbl_coupon_useOnline" language="${pageContext.request.locale.language}"/></div>
    </c:if>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="onlineCouponList" />
		<dsp:oparam name="output">

			<div class="coupon clearfix grid_4 alpha omega">
				<dsp:setvalue param="appliedCouponListVO" paramvalue="element" />
				<dsp:getvalueof var="appliedCouponListVO" vartype="java.lang.Object"
					param="appliedCouponListVO" />
				<c:set var="couponId" value="${appliedCouponListVO.couponId}" />
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
				<div class="couponInfo fl marTop_10 grid_2 ">

					<%--
					<div class="label grid_2 alpha omega">
						<label class="couponHeader grid_2 alpha omega">
							<strong>${appliedCouponListVO.displayName}</strong>
						</label>
					</div>
					--%>


					<div class=" fl couponBtnWrap grid_2 alpha omega">
						 <c:choose>
		                <c:when test="${couponId == 'schoolPromo'}">
		                	<c:choose>
				                <c:when test="${selected}">
				                    <c:set var="applied" value="applied"/>
				                    
			                    		<input type="button" data-couponid="${couponId}" class="button-Small btnPrimary unapply" value="${unApplyCoupon}" />

			                    		<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}" iclass="couponIdVal" value="${couponId}" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                               </dsp:input>
			                    		--%>


										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											checked="true" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>

				                </c:when>
				                <c:otherwise>
				                    <c:set var="applied" value=""/>
				                    
				                    <input type="button" data-couponid="${couponId}" class="button-Small btnSecondary apply" value="${applyCoupon}" />

											<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}"  iclass="couponIdVal" value="" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                              </dsp:input>
			                    		--%>


										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											checked="false" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>

				                </c:otherwise>
		            		</c:choose>
		                </c:when>
		                <c:otherwise>
		                	<c:choose>
				                <c:when test="${selected}">
				                    <c:set var="applied" value="applied"/>
				                  
				                    	<input type="button" data-couponid="${couponId}" class="button-Small btnPrimary unapply" value="${unApplyCoupon}" />

				                    	<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}"  iclass="couponIdVal" value="${couponId}" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                               </dsp:input>
											--%>

										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											checked="true" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>
			                    	

				                </c:when>
				                <c:otherwise>
				                    <c:set var="applied" value=""/>
				                  
				                    <input type="button" data-couponid="${couponId}" class="button-Small btnSecondary apply" value="${applyCoupon}" />

<%--
			                        <dsp:input bean="CartModifierFormHandler.couponList.${count }" type="hidden"  id="${couponId}"  iclass="couponIdVal" value="" >
	                                 <dsp:tagAttribute name="data-couponid" value="${couponId}" />
	                              </dsp:input>
--%>

										<dsp:input bean="CartModifierFormHandler.couponList.${count+size}"
											checked="false" type="checkbox" id="offer1_off20${couponId }"
											value="${couponId }" iclass="noUniform couponIdVal">
											<dsp:tagAttribute name="aria-checked" value="false" />
										</dsp:input>

				                </c:otherwise>
		            		</c:choose>
		                </c:otherwise>
		            </c:choose>
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
							<c:set var="coupon_exclusions_title">
								<bbbl:label
									key="lbl_coupon_exclusions_title"
									language="${pageContext.request.locale.language}" />
							</c:set>	

							<dsp:a 	iclass="popup grid_2 alpha omega" 
										page="/checkout/coupons/exclusions.jsp?item=${appliedCouponListVO.couponId}" 
										title='${coupon_exclusions_title}'>        						
        						${coupon_exclusions_title}
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
				<c:if test="${not empty couponErrorList && not selected}">
					<dsp:getvalueof id="errorMap"
						bean="CartModifierFormHandler.couponErrorList.${couponId}" />
					<c:choose>
						<c:when test="${not empty errorMap}">
							<div class="couponErrorMsg cb clearfix">
								<h3>
									<bbbe:error key="err_coupon_cannot_apply"
										language="${pageContext.request.locale.language}" />
								</h3>
								<p>
									<bbbe:error key="${errorMap}"
										language="${pageContext.request.locale.language}" />
								</p>
								<a class="dsmiss button-Small btnSecondary"><bbbl:label key="lbl_DISMISS" language="${pageContext.request.locale.language}" /></a>
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

        <%--End online coupons list --%>

        <%-- Below If block is for R2 requirement --%>



</dsp:page>
