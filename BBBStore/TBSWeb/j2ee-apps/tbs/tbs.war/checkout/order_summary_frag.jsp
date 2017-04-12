<dsp:page>


	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>
	<dsp:getvalueof var="displayShippingDisclaimer" param="displayShippingDisclaimer"/>
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
	<div class="grid_4 omega" id="rightCol">
		<div id="summary" class="spclSectionBox">
			<div class="spclSection">
				<h3><bbbl:label key="lbl_preview_ordersummary" language="<c:out param='${language}'/>"/></h3>
				<dsp:include page="order_summary_data.jsp"/>
			</div>
		</div>
		<p class="infoMsg"><bbbl:label key="lbl_preview_securedtransactionmesg" language="<c:out param='${language}'/>"/></p>
		<div class="footNotes">
			<ol>
				<c:if test="${not empty taxFailureFootNote}">
					<li class="infoMsg noMarTop">* . ${taxFailureFootNote}</li>
				</c:if>
				<c:if test="${not empty ecofeeFootNote}">
					<li class="infoMsg noMarTop"><bbbl:label key="lbl_footnote_ecofee" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
				</c:if>
				<c:if test="${not empty giftWrapFootNote}">
					<li class="infoMsg noMarTop"><bbbl:label key="lbl_footnote_giftWrap" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
				</c:if>
				<c:if test="${not empty shippingFootNote}">
					<li class="infoMsg noMarTop"><bbbl:label key="lbl_footnote_shipping" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
				</c:if>
				<c:if test="${not empty totalFootNote}">
					<li class="infoMsg noMarTop"><bbbl:label key="lbl_footnote_total" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
				</c:if>
			</ol>
				<c:if test="${not empty displayShippingDisclaimer}">
				<ol>
					<li class="infoMsg noMarTop"><bbbl:label key="lbl_checkout_ship_disclaimer" language="<c:out param='${language}'/>"/></li>
				</ol>
				</c:if>
		</div>
		<!-- Start R2.2  Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal -->

		<dsp:getvalueof var="PageType" param="PageType"/>
		<c:if test="${PageType eq 'Payment'}">
									<div class="clear"></div>
										<c:choose>
										<c:when test="${visaLogoOn || masterCardLogoOn}">
										<div class="visaMasterCardContainer infoMsg">
												<bbbl:label key="lbl_vbv_payment_secure" language="${pageContext.request.locale.language}" />

													<c:if test="${visaLogoOn}">
													<bbbt:textArea key="txt_vbv_logo_visa" language="<c:out param='${language}'/>"/>
													</c:if>
													<c:if test="${visaLogoOn && masterCardLogoOn}">
													<span class="visaLogoSep"></span>
													</c:if>
													<c:if test="${masterCardLogoOn}">
													<bbbt:textArea key="txt_vbv_logo_masterCard" language="<c:out param='${language}'/>"/>
													</c:if>

											</div>
											</c:when>
											</c:choose>
	</c:if>
	<!-- End R2.2  Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal -->
	</div>

</dsp:page>
