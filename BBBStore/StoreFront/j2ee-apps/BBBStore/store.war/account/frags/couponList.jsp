<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	
	<dsp:getvalueof var="couponsWelcomeMsg" bean="SessionBean.couponsWelcomeMsg" />
	<dsp:getvalueof var="couponEmail" bean="SessionBean.couponEmail" />

	<dsp:getvalueof var="walletId" param="WALLET_ID" />
	<dsp:getvalueof var="action" param="ACTION" />
	<dsp:getvalueof var="offerId" param="OFFER_ID" />
	
	<c:if test="${!empty param.offerId}">
	<c:set var="offerId" value="${param.offerId}" />
	</c:if>

	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<c:choose>
		<c:when test="${!isTransient}">
		<dsp:getvalueof var="email" bean="Profile.email" />
		</c:when>
		<c:otherwise>
		<dsp:getvalueof var="email" value="${couponEmail}" />
		</c:otherwise>		
	</c:choose>
	<%--Uncommenting  Lines ,  as discussed with Shivam , Coupon Upload functionality is not required in CA --%>
	 <%-- <c:if test="${appid ne 'BedBathCanada'}"> 
	   <dsp:include page="/account/frags/addACoupon.jsp" >
	           <dsp:param name="walletId" value="${walletId}"/>	          
	   </dsp:include>
	 </c:if>  --%>
	 
	  <!--  commented above condition because coupon banner message keep on displaying after refreshing canada site -->
	<c:choose>
	<c:when test="${appid ne 'BedBathCanada' }">
	 <dsp:include page="/account/frags/addACoupon.jsp" >
	           <dsp:param name="walletId" value="${walletId}"/>	          
	   </dsp:include>
	</c:when>
	<c:otherwise>
	   <c:if test="${couponsWelcomeMsg}">                    
   	<dsp:setvalue bean="SessionBean.couponsWelcomeMsg" value="false"/>
   </c:if>
	</c:otherwise>
	</c:choose>
	
		<dsp:getvalueof var="emailFlow" param="emailFlow" />
	<c:if test="${not empty emailFlow && emailFlow eq true}">
		<dsp:getvalueof var="email" value="${couponEmail}" />
	</c:if>
	
	
	
	
	
	<dsp:droplet name="GetCouponsDroplet">
		<dsp:param name="EMAIL_ADDR" value="${email}" />
		<dsp:param name="MOBILE_NUMBER" bean="Profile.mobileNumber" />
		<dsp:param name="walletId" value="${walletId}" />
		<dsp:param name="OFFER_ID" value="${offerId}" />
		<dsp:oparam name="output">
			<div class="">
				<h3 id="couponListsHeader">
					<bbbl:label key="lbl_coupon_title"
						language="${pageContext.request.locale.language}" />
				</h3>
				
				<c:if test="${appid ne 'BedBathCanada'&& !isTransient && (HarteHanksOn == 'TRUE' || HarteHanksOn == 'True')}">
					<input  tabindex="0"type="button" id="couponListsPreferences" class="button-Med btnSecondary" value="Manage Preferences" />
					</c:if>
	
				<dsp:getvalueof var="couponsMap" param="couponsMap" />
				<dsp:getvalueof var="couponCount" param="couponCount" />
				<dsp:getvalueof var="signupCount" param="signupCount" />
				<dsp:getvalueof var="expSecAvailable" param="expSecAvailable" />
				
				<dsp:getvalueof var="couponsMapSize"
					value="${fn:length(couponsMap)}" /> 

				<c:choose>
					<c:when test="${couponCount==0}">
						<p>
							<bbbl:label key="lbl_coupon_no_coupons_message" language="${pageContext.request.locale.language}"/>
						</p>
					</c:when>
					<c:otherwise>
						<p>
							<bbbt:textArea key="txtarea_coupons_description"
								language="${pageContext.request.locale.language}" />
						</p>
					</c:otherwise>
				</c:choose>
				
				
  
				<c:forEach var="entry" items="${couponsMap}"  varStatus="counter" >   
					<dsp:getvalueof var="couponList" value="${entry.value}" />
					<dsp:getvalueof var="couponType" value="${entry.key}" />
					
					<c:if test="${! empty couponList && couponType != 'expRedeemedCoupons' }">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${entry.value}" />
							<dsp:oparam name="outputStart">

								
								
								<div class="couponsSectionHeader clearfix">
									<a class="collapseLink" href="#"> <span
										class="icon-fallback-text"> <span class="icon-minus"
											aria-hidden="true"></span> <span class="icon-text">close</span>

									</span></a>
									<c:choose>
										<c:when
											test="${fn:containsIgnoreCase(couponType, 'useAnywhereCoupons')}">
											<bbbl:label key="lbl_coupon_useanywhere"
												language="${pageContext.request.locale.language}" />
										</c:when>
										<c:when
											test="${fn:containsIgnoreCase(couponType, 'onlineCoupons')}">
											<bbbl:label key="lbl_coupon_useOnline"
												language="${pageContext.request.locale.language}" />
										</c:when>
										<c:when
											test="${fn:containsIgnoreCase(couponType, 'inStoreCoupons')}">
				  							<!--In-Store Only Coupons -->
				  							<bbbl:label key="lbl_coupon_inStoreOnly"
												language="${pageContext.request.locale.language}" />
				  							
				  						</c:when>
										
									</c:choose>


								</div>


								<div class="couponSectionCoupons ${couponType} clearfix">
							</dsp:oparam>
							<dsp:oparam name="output">
							
								<dsp:setvalue param="CouponListVo" paramvalue="element" />
								<dsp:getvalueof var="CouponListVo" vartype="java.lang.Object"
									param="CouponListVo" />
									<%-- code updates to read barcode from redemptionVO --%>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" value="${CouponListVo.redemptionCodesVO}" />
											<dsp:oparam name="output">
											<dsp:setvalue param="RedemptionCodeVO" paramvalue="element" />
											<dsp:getvalueof var="RedemptionCodeVO" vartype="java.lang.Object"
									param="RedemptionCodeVO" />
											<dsp:getvalueof param="count" var="count"/>
											<c:if test="${count ==1 }">
											<c:set var="uniqueCouponCd" value="${RedemptionCodeVO.uniqueCouponCd }" />
											</c:if>
											</dsp:oparam>
										</dsp:droplet>
									<%-- End code updates to read barcode from redemptionVO --%>
								<dsp:getvalueof var="count" param="count" />

								<c:set var="barcode" value="${uniqueCouponCd}" />
								<dsp:getvalueof var="promoId"	value="${CouponListVo.promoId}" />
								<c:if test="${!CouponListVo.disqualify}">
								<div class="coupon clearfix" 	data-barcode='${barcode}'
																		data-promoid='${promoId}'
																		data-expdate = '${CouponListVo.displayExpiryDate}'
																		data-couponid = '${CouponListVo.entryCd}'>
										<div class="couponImageWrap useAnywhere"
											data-barcode='${barcode}' data-count='${count}'>
											<div class="flipper">
												<div class="couponImage fl">
													<c:set var="count" value="${CouponListVo.expiryCount}" />
													<c:set var="onlineOfferCode" value="${RedemptionCodeVO.onlineOfferCode}" />
													<c:choose>													 
														<c:when test="${!empty param.offerId && (param.offerId == onlineOfferCode || param.offerId == uniqueCouponCd)}">
															<div class="couponImageLabel justAdded">																
																<bbbl:label key="lbl_coupon_justadded" language ="${pageContext.request.locale.language}"/>
															</div>
														</c:when>

														<c:when test="${(CouponListVo.preloaded)}">
															<div class="couponImageLabel justAdded">
																<bbbl:label key="lbl_coupon_justadded" language ="${pageContext.request.locale.language}"/>
															</div>
														</c:when>
														
														<c:otherwise>
															<c:if test="${count eq 1 }">
																<div class="couponImageLabel expToday">
																	<span> <bbbl:label key="lbl_coupon_expiresIn_0"
																			language="${pageContext.request.locale.language}" /> </span>
																</div>
															</c:if>
															<c:if test="${count eq 2 }">
																<div class="couponImageLabel expSoon">
																	<span> <bbbl:label key="lbl_coupon_expiresIn_1"
																			language="${pageContext.request.locale.language}" /> </span>
																</div>
															</c:if>
															<c:if test="${count eq 3 }">
																<div class="couponImageLabel expSoon">
																	<span> <bbbl:label key="lbl_coupon_expiresIn_2"
																			language="${pageContext.request.locale.language}" /> </span>
																</div>
															</c:if>
															<c:if test="${count eq 4 }">
																<div class="couponImageLabel expSoon">
																	<span> <bbbl:label key="lbl_coupon_expiresIn_3"
																			language="${pageContext.request.locale.language}" /> </span>
																</div>
															</c:if>
															<c:if test="${count eq 5 }">
																<div class="couponImageLabel expSoon">
																	<span> <bbbl:label key="lbl_coupon_expiresIn_4"
																			language="${pageContext.request.locale.language}" /> </span>
																</div>
															</c:if>
															<c:if test="${count eq 6 }">
																<div class="couponImageLabel expSoon">
																	<span> <bbbl:label key="lbl_coupon_expiresIn_5"
																			language="${pageContext.request.locale.language}" /> </span>
																</div>
															</c:if>
														</c:otherwise>
													</c:choose>
													<span class="border"></span>

													<dsp:getvalueof var="promoIMGURL"
														value="${CouponListVo.couponsImageUrl}" />
													<c:set var="barcode" value="${uniqueCouponCd}" />
													<c:choose>
														<c:when
															test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
															<dsp:getvalueof var="img"
																value="${CouponListVo.couponsImageUrl}" />
															<img src="${img}" height="100%" width="100%"
																alt="${CouponListVo.couponsDescription}" />
														</c:when>
														<c:otherwise>
															<dsp:getvalueof var="img1"
																value="${scene7Path}/${CouponListVo.couponsImageUrl}" />
															<img src="${img1}" height="100%" width="100%"
																alt="${CouponListVo.couponsDescription}" />
														</c:otherwise>
													</c:choose>
												</div>
												<div id="barcode${count}" class="barCode">
													<div class="closeBarcode"></div>
													<div class="barcodeInstructions">
														<%-- TODO - make BCC label--%>
														<bbbl:label key="lbl_coupon_presentbarcode" language ="${pageContext.request.locale.language}"/>
													</div>
													<div class="barcodeCanvas"></div>
													<div class="barcodeNum"></div>
												</div>
											</div>
										</div>
										<div class="couponInfo fl grid_2 omega">

											<c:choose>
												<c:when test="${fn:containsIgnoreCase(couponType, 'useAnywhereCoupons') || fn:containsIgnoreCase(couponType, 'inStoreCoupons')}">
													<input type="button" class="button-Small btnSecondary redeem" value="Redeem In-Store" onclick="javascript:couponWalletAction('${CouponListVo.couponsDescription}','redeem coupon')" />
													<input 	type="button" 																
																class="button-Small btnSecondary printCouponBtn" 
																value="Print Coupon" onclick="javascript:couponWalletAction('${CouponListVo.couponsDescription}','print coupon')" />
						  							
						  						</c:when>
						  						<c:when test="${fn:containsIgnoreCase(couponType, 'onlineCoupons')}">													
												<p class="couponOnline grid_2 alpha omega fl"><bbbt:textArea key="txt_online_coupon_desc" language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:when test="${fn:containsIgnoreCase(couponType, 'expRedeemedCoupons')}">						  							
						  							<!--Redeemed & Expired Coupons-->						  							
						  						</c:when>
											</c:choose>


                                            <c:set var="onlineOfferCode" value="${RedemptionCodeVO.onlineOfferCode}" />
                                            
											<c:if test="${!empty param.offerId && (param.offerId == onlineOfferCode || param.offerId == uniqueCouponCd)}">
												<c:set var="omniCoupondescription" value="${CouponListVo.couponsDescription}" />
	                                            <c:set var="omniAddCoupon" value="true" />											
											</c:if>

											<!-- <c:if test="${not empty CouponListVo.couponsDescription}">
													<p><dsp:valueof value="${CouponListVo.couponsDescription}"/></p>
												</c:if> -->
											<c:if test="${not empty CouponListVo.expiryDate}">
												<p class="couponExp grid_2 alpha omega fl">													
													<bbbl:label key="lbl_coupon_expires" language ="${pageContext.request.locale.language}"/>
													<dsp:valueof value="${CouponListVo.displayExpiryDate}" />
												</p>
											</c:if>
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value"
													value="${CouponListVo.couponsExclusions}" />
												<dsp:oparam name="false">
													
													<a
														href="${contextPath}/checkout/coupons/exclusions.jsp?item=${CouponListVo.entryCd}"
														class="popup" data="${CouponListVo.entryCd}" title="Exclusions"><bbbl:label
															key="lbl_coupon_exclusions_title"
															language="${pageContext.request.locale.language}" />
													</a>
												</dsp:oparam>
											</dsp:droplet>
										</div>
								</div>
								</c:if>
								<!-- <p id="barcode${count}"></p> -->
								

							</dsp:oparam>
							<dsp:oparam name="outputEnd">
			</div>
			<%-- end <div class="couponSectionCoupons"> --%>
		</dsp:oparam>
		<dsp:oparam name="empty">
			<p>
				<bbbt:label key="lbl_coupons_no_coupon"
					language="${pageContext.request.locale.language}" />
			</p>
		</dsp:oparam>
	</dsp:droplet>
	</c:if>
	
	<%--Email signup section --%>
	<c:if test="${appid ne 'BedBathCanada' &&((counter.index==signupCount && expSecAvailable) ||(counter.count==signupCount && !expSecAvailable)) }">
		<%-- out of scope 
		<dsp:include page="/account/frags/walletSignup.jsp" />
		--%>
	</c:if>
	<%--End Email signup section  --%>
	
	<c:if test="${! empty couponList && couponType == 'expRedeemedCoupons' }">
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${entry.value}" />
							<dsp:oparam name="outputStart">

								<div class="couponsSectionHeader clearfix">
									<a class="expandLink" href="#">
										<span class="icon-fallback-text"> 
											<span class="icon-plus" aria-hidden="true"></span> 
											<span class="icon-text">open</span>
										</span>
									</a>
				  							<bbbl:label key="lbl_coupon_redeemedandexpired"	language="${pageContext.request.locale.language}"/>

								</div>
								<div class="couponSectionCoupons couponSectionExpired clearfix" style="display:none;">
							</dsp:oparam>
							<dsp:oparam name="output">

								<dsp:setvalue param="CouponListVo" paramvalue="element" />
								<dsp:getvalueof var="CouponListVo" vartype="java.lang.Object"
									param="CouponListVo" />
								<dsp:getvalueof var="count" param="count" />
								
														
																					
								<%-- code updates to read barcode from redemptionVO --%>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" value="${CouponListVo.redemptionCodesVO}" />
											<dsp:oparam name="output">
											<dsp:setvalue param="RedemptionCodesVO" paramvalue="element" />
											<dsp:getvalueof var="RedemptionCodesVO" vartype="java.lang.Object"
									param="RedemptionCodesVO" />
											<c:set var="uniqueCouponCd" value="${RedemptionCodesVO.uniqueCouponCd }" />
											</dsp:oparam>
										</dsp:droplet>
									<%-- End code updates to read barcode from redemptionVO --%>
								<c:set var="barcode" value="${uniqueCouponCd}" />

								<c:if test="${!CouponListVo.disqualify}">
								<div class="coupon clearfix expired" >				
										<div class="couponImageWrap useAnywhere"
											data-barcode='${barcode}' data-count='${count}'>
											<div class="flipper">
												<div class="couponImage fl">
														<div class="couponImageLabel expSoon">
															<c:choose>
											<c:when test="${not empty CouponListVo.expiryDate && empty CouponListVo.lastRedemptionDate}">
												<bbbl:label key="lbl_coupon_expired" language ="${pageContext.request.locale.language}"/>				  					
											</c:when>
											<c:when test="${not empty CouponListVo.lastRedemptionDate}">
												<bbbl:label key="lbl_coupon_redeemed" language ="${pageContext.request.locale.language}"/>	  					
											</c:when>
											</c:choose>
														</div>
													<span class="border"></span>

													<dsp:getvalueof var="promoIMGURL"
														value="${CouponListVo.couponsImageUrl}" />
													<c:set var="barcode" value="${CouponListVo.uniqueCouponCd}" />
													<c:choose>
														<c:when
															test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
															<dsp:getvalueof var="img"
																value="${CouponListVo.couponsImageUrl}" />
															<img class="bw" src="${img}" height="100%" width="100%"
																alt="${CouponListVo.couponsDescription}" />
														</c:when>
														<c:otherwise>
															<dsp:getvalueof var="img1"
																value="${scene7Path}/${CouponListVo.couponsImageUrl}" />
															<img src="${img1}" class="bw" height="100%" width="100%"
																alt="${CouponListVo.couponsDescription}" />
														</c:otherwise>
													</c:choose>
												</div>
												<div id="barcode${count}" class="barCode">
													<div class="barcodeInstructions">
														<%-- TODO - make BCC label--%>
														<bbbl:label key="lbl_coupon_presentbarcode" language ="${pageContext.request.locale.language}"/>
														<!-- Present barcode to be scanned at register -->
													</div>
													<div class="barcodeCanvas"></div>
													<div class="barcodeNum"></div>
												</div>
											</div>
										</div>
										<div class="couponInfo fl grid_2 omega">
											<%--
											<input type="button" id="redeemInStore" class="button-Small btnSecondary" value="Redeem In-Store" />
											<input type="button" id="redeemInStore" class="button-Small btnSecondary marTop_10" value="Print Coupon" />
											--%>
											<c:if test="${not empty CouponListVo.description}">
											</c:if>
											<!-- <c:if test="${not empty CouponListVo.couponsDescription}">
													<p><dsp:valueof value="${CouponListVo.couponsDescription}"/></p>
												</c:if> -->

											<c:choose>
											<c:when test="${not empty CouponListVo.expiryDate && empty CouponListVo.lastRedemptionDate}">
												<p class="couponExp grid_2 alpha omega fl">
													<bbbl:label key="lbl_coupon_expiredon" language ="${pageContext.request.locale.language}"/>
													<dsp:valueof value="${CouponListVo.displayExpiryDate}" />
												</p>
											</c:when>
											<c:when test="${not empty CouponListVo.lastRedemptionDate}">
												<p class="couponExp grid_2 alpha omega">
													<bbbl:label key="lbl_coupon_redeemeddate" language ="${pageContext.request.locale.language}"/>
													<dsp:valueof value="${CouponListVo.lastRedemptionDate}" />
												</p>
											</c:when>
											</c:choose>
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value"
													value="${CouponListVo.couponsExclusions}" />
												<dsp:oparam name="false">
													<dsp:getvalueof var="promoId"
														value="${CouponListVo.promoId}" />
													<a
														href="${contextPath}/checkout/coupons/exclusions.jsp?item=${CouponListVo.entryCd}"
														class="popup" data="${CouponListVo.entryCd}" title="Exclusions"><bbbl:label
															key="lbl_coupon_exclusions_title"
															language="${pageContext.request.locale.language}" />
													</a>
												</dsp:oparam>
											</dsp:droplet>
											
											
										</div>
								</div>
							</c:if>
								<!-- <p id="barcode${count}"></p> -->

							</dsp:oparam>
							<dsp:oparam name="outputEnd">
			</div>
			<%-- end <div class="couponSectionCoupons"> --%>
		</dsp:oparam>
		</dsp:droplet>
	</c:if>
	</c:forEach>
	</div>
	</dsp:oparam>
	<dsp:oparam name="error">
		<div class="grid_10">
			<dsp:getvalueof var="varSystemSrror" param="systemerror"></dsp:getvalueof>
			<c:choose>
				<c:when test="${varSystemSrror=='err_mycoupons_system_error' }">
					<label class="error"> <bbbe:error
							key="err_mycoupons_system_error"
							language="${pageContext.request.locale.language}" /> </label>
				</c:when>
			</c:choose>
		</div>
	</dsp:oparam>
	</dsp:droplet>

	<div id="couponPrintDialog" class="hidden">
		<iframe id="couponPrintFrame" width="90%" height="100%" src="about:blank"></iframe>
	</div>

	<script>
	window.onload = function(){		
		 var couponDescriptionOmninature ='${omniCoupondescription}';
		 var omniAddCoupon='${omniAddCoupon}'; 	
 		 if(omniAddCoupon == 'true'){			 
 			couponWalletAdd('${omniCoupondescription}'); 			
 	     }
	}
	
	</script>

</dsp:page>
