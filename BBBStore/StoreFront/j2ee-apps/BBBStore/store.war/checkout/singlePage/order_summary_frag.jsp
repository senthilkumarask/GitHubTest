<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/droplet/AppliedCouponsDroplet" />
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>

	<%-- adding these 2 flags for ajax requested fragments  --%>
	<c:set var="visaLogoOn" scope="request"><bbbc:config key="visaLogoFlag" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="masterCardLogoOn" scope="request"><bbbc:config key="masterCardLogoFlag" configName="FlagDrivenFunctions" /></c:set>

	<dsp:getvalueof var="couponsAvailable" param="couponsAvailable"/>
     <dsp:getvalueof var="displayShippingDisclaimer" param="displayShippingDisclaimer"/>
   <dsp:droplet name="/com/bbb/commerce/checkout/droplet/SuperScriptDroplet">
	        <dsp:param name="order" param="order"/>
	        <dsp:oparam name="output">
	           <dsp:tomap var="placeHolderMap" param="SuperScriptMap"/>
	        </dsp:oparam>
        </dsp:droplet>
     	<c:set var="totalStateTax" value="0" scope="request" />
	    <c:set var="totalCountyTax" value="0" scope="request" />
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="order.shippingGroups" />
		<dsp:param name="elementName" value="shippingGroup" />
		<dsp:oparam name="output">
			<dsp:droplet
				name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
				<dsp:param name="priceObject" param="shippingGroup" />
				<dsp:param name="orderObject" param="order" />
				<dsp:oparam name="output">

					<dsp:getvalueof var="countyLevelTax"
						param="priceInfoVO.shippingcountyLevelTax" />
					<dsp:getvalueof var="stateLevelTax"
						param="priceInfoVO.shippingStateLevelTax" />
					<c:choose>
						<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">

							<c:set var="totalStateTax"
								value="${totalStateTax+countyLevelTax}" scope="request" />
							<c:set var="totalCountyTax"
								value="${totalCountyTax+stateLevelTax}" scope="request" />
						</c:when>
						<c:otherwise>

							<c:set var="totalStateTax" value="${totalStateTax+stateLevelTax}"
								scope="request" />
							<c:set var="totalCountyTax"
								value="${totalCountyTax+countyLevelTax}" scope="request" />
						</c:otherwise>
						</c:choose>
				</dsp:oparam>
				
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	
        <div id="summary" class="spclSectionBox">
            <div class="spclSection">
                <h3><bbbl:label key="lbl_spc_preview_ordersummary" language="<c:out param='${language}'/>"/></h3>
                    <dsp:include page="order_summary_data.jsp">
					   <dsp:param name="placeHolderMap" value="${placeHolderMap}"/>
                     </dsp:include>					
            </div>
            <div class="SPCordSumCoupon">
              	<dsp:droplet name="AppliedCouponsDroplet">
				<dsp:param name="order" bean="ShoppingCart.current" />
				<dsp:oparam name="output">
				<dsp:getvalueof var="couponList" param="couponList" />
		</dsp:oparam>
		</dsp:droplet>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" value="${couponList}" />
				<dsp:oparam name="output">
				<dsp:setvalue param="CouponListVo" paramvalue="element" />
											<dsp:getvalueof var="CouponListVo" vartype="java.lang.Object"
									param="CouponListVo" />
				<dsp:getvalueof var="promoIMGURL" value="${CouponListVo.couponsImageUrl}" />
				<h3 class="subHeading marBottom_10"><bbbl:label key="lbl_spc_appliedcoupon" language="<c:out param='${language}'/>"/></h3>
				<span class="SPCcouponImg">
													<c:choose>
														<c:when
															test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
															<dsp:getvalueof var="img"
																value="${CouponListVo.couponsImageUrl}" />
															<img src="${img}" width="100%"
																alt="${CouponListVo.couponsDescription}" />
														</c:when>
														<c:otherwise>
															<dsp:getvalueof var="img1"
																value="${scene7Path}/${CouponListVo.couponsImageUrl}" />
															<img src="${img1}" width="100%"
																alt="${CouponListVo.couponsDescription}" />
														</c:otherwise>
													</c:choose>
													</span>
													<%-- <c:if test="${not empty couponsAvailable && couponsAvailable eq 'true'}"> --%>
														<span class="SPCcouponDesc">
														${CouponListVo.couponsDescription }
														<p><a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?isFromPreview=true#spcCoupons" title="<bbbl:label key="lbl_spc_preview_edit_coupon" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit_coupon" language="<c:out param='${language}'/>"/></strong></a></p>
														</span>	
													<%-- </c:if>--%>
				</dsp:oparam>
				</dsp:droplet>
            
            </div>

        <p class="infoMsg"><bbbl:label key="lbl_spc_preview_securedtransactionmesg" language="<c:out param='${language}'/>"/></p>
        <div class="footNotes">
            <ol>
                <c:if test="${not empty taxFailureFootNote}">
                    <li class="infoMsg noMarTop">${taxFailureFootNote}</li>
                </c:if>
                <c:if test="${not empty ecofeeFootNote}">
                    <li class="infoMsg noMarTop"><bbbl:label key="lbl_spc_footnote_ecofee" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
                </c:if>
                <c:if test="${not empty giftWrapFootNote}">    
                    <li class="infoMsg noMarTop"><bbbl:label key="lbl_spc_footnote_giftWrap" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
                </c:if>
                <c:if test="${not empty shippingFootNote}">
                    <li class="infoMsg noMarTop"><bbbl:label key="lbl_spc_footnote_shipping" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
                </c:if>                
                <c:if test="${not empty totalFootNote}">
                    <li class="infoMsg noMarTop"><bbbl:label key="lbl_spc_footnote_total" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>                        
                </c:if>
            </ol>
            	<c:if test="${not empty displayShippingDisclaimer}">
            	<ol>
                    <li class="infoMsg noMarTop"><bbbl:label key="lbl_spc_checkout_ship_disclaimer" language="<c:out param='${language}'/>"/></li>
                </ol>
                </c:if>
        </div>
        </div>
        <%-- Start R2.2  Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal --%> 
        
         <dsp:getvalueof var="PageType" param="PageType"/>
         <c:if test="${PageType eq 'Payment'}">
                                       <div class="clear"></div>
										   <c:choose>
										   <c:when test="${visaLogoOn || masterCardLogoOn}">
                                           <div class="visaMasterCardContainer infoMsg"> 
												<bbbl:label key="lbl_spc_vbv_payment_secure" language="${pageContext.request.locale.language}" />
												    
													<c:if test="${visaLogoOn}">
													<bbbt:textArea key="txt_spc_vbv_logo_visa" language="<c:out param='${language}'/>"/>
													</c:if>
													<c:if test="${visaLogoOn && masterCardLogoOn}">		
													<span class="visaLogoSep"></span>
													</c:if>
													<c:if test="${masterCardLogoOn}">
													<bbbt:textArea key="txt_spc_vbv_logo_masterCard" language="<c:out param='${language}'/>"/>
													</c:if>
													
											</div> 
											</c:when>
											</c:choose>
	   </c:if>
	<%-- End R2.2  Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal --%>
    
  </dsp:page>
