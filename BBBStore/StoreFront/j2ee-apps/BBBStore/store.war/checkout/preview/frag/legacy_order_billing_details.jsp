<%-- Billing information Starts here --%>
<%-- Billing address and method details start here --%>
<div
	class="billingInformation grid_8 alpha omega marTop_20 marBottom_20">
	<dsp:getvalueof var="orderDetails" param="orderDetails" />
	<dsp:getvalueof var="billingAddress" param="orderDetails.billing.address" />
	<dsp:getvalueof id="payments" param="orderDetails.payments" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <c:set var="bedBathCanadaSiteCode"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
		<h3 class="sectionHeading noBorder">
			<bbbl:label key="lbl_preview_billinginformation"
				language="<c:out param='${language}'/>" />
		</h3>
		<div id="summary" class="spclSectionBox">
			<div class="spclSection clearfix">				
				<div class="grid_4 omega">
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" value="${payments.creditCard}" />
						<dsp:oparam name="false">
							<p class="previewText">
								<strong><bbbl:label key="lbl_preview_billingmethod"
										language="<c:out param='${language}'/>" /> </strong>
							</p>
							<dl class="clearfix">
								<dt>${payments.creditCard.cardType}</dt>
								<dd>${payments.creditCard.cardNum}</dd>

								<dt><bbbl:label key="lbl_preview_exp" language="<c:out param='${language}'/>" /></dt>
								<dd>${payments.creditCard.expMo}/${payments.creditCard.expYr}</dd>
								<dt></dt>
								<dd>&nbsp;</dd>
							</dl>
						</dsp:oparam>
						<dsp:oparam name="true">
							<p class="marBottom_10" />
						</dsp:oparam>
					</dsp:droplet>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" value="${payments.giftCards}" />
						<dsp:oparam name="false">
							<dsp:getvalueof var="giftCards" value="${payments.giftCards}" />
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${giftCards}" />
								<dsp:param name="elementName" value="giftcard" />
								<dsp:oparam name="output">
								<dsp:getvalueof id="count" param="count"/>
									<dl class="clearfix">
										<dt><bbbl:label key="lbl_preview_giftcard" language="<c:out param='${language}'/>"/></dt>
										<dd><dsp:valueof param="giftcard.cardNum"/></dd>
										<dt><bbbl:label key="lbl_preview_giftcardamount" language="<c:out param='${language}'/>"/></dt>
										<dd><dsp:valueof param="giftcard.debitAmt" converter="currency"/></dd>
									</dl>
									<p class="marBottom_10"></p>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
						<dsp:oparam name="true">
						</dsp:oparam>
					</dsp:droplet>
					<div class="grid_4 alpha omega marBottom_20">
						<c:if test="${not empty billingAddress.city}">
							<p class="previewText">
								<strong><bbbl:label key="lbl_preview_billingaddress"
										language="<c:out param='${language}'/>" /> </strong>
							</p>
							<p class="marTop_5">
								${billingAddress.firstNm}
								${billingAddress.lastNm}
							</p>
							<p>
								${billingAddress.addr1}
							</p>
							<p>
								${billingAddress.city}
								,
								${billingAddress.state}
								${billingAddress.zip}
							</p>
						</c:if>
					</div>
				</div>
				
				<div class="grid_4 omega">
				<h3 class="subHeading"><bbbl:label key="lbl_preview_delivery" language="<c:out param='${language}'/>"/></h3>
					<%-- Total Billing details Fragement -like shipping amt, txt amt and total amount-- starts here --%>
					<dsp:getvalueof var="totalQty" param="totalQty" />
					<dsp:getvalueof var="totalExtPrice" param="totalExtPrice" />
					<dsp:getvalueof var="totalUnitPrice" param="totalUnitPrice" />
					<dsp:getvalueof var="totalDisAmt" param="totalDisAmt" />
					<dsp:getvalueof var="totalAmt"
						param="orderDetails.orderInfo.orderHeaderInfo.totalAmt" />
					<dsp:getvalueof var="totalShippingAmount"
						param="orderDetails.orderInfo.orderHeaderInfo.shipAmt" />
					<dsp:getvalueof var="giftWrapPrice"
						param="orderDetails.orderInfo.orderHeaderInfo.giftPkgAmt" />
					<dsp:getvalueof var="totalTax"
						param="orderDetails.orderInfo.orderHeaderInfo.taxAmt" />
					<dsp:getvalueof var="preTaxAmout" param="orderDetails.orderInfo.orderHeaderInfo.subTotalAmt" />

					<dsp:getvalueof var="originCd" param="orderDetails.orderInfo.orderHeaderInfo.originCd" />
					<dsp:getvalueof var="stateLevelTax" param="orderDetails.orderInfo.orderHeaderInfo.stateTax" />
					<dsp:getvalueof var="countyLevelTax" param="orderDetails.orderInfo.orderHeaderInfo.countyTax" />
					

					<dsp:getvalueof var="ECOFee" value="0.0" />

					<dl
						class="grid_4 billingDetailsList alpha omega marTop_15 clearfix summaryNew">
						<dt>
							<c:if test="${totalQty gt 0}">
								<span class="totalItems bold"><bbbl:label key="lbl_order_subtotal" language="<c:out param='${language}'/>"/> 
								</span>
								<span class="summaryCount"><dsp:valueof value="${totalQty}" />
								<bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></span>
							</c:if>
							
						</dt>

						<c:if test="${totalUnitPrice gt 0.0}">
							<dd class="bold itemsPrice">
								<dsp:valueof value="${totalUnitPrice}" converter="currency" />
							</dd>
						</c:if>

						<c:set value="0" var="count" scope="page" />
						<jsp:useBean id="footNotesMap" class="java.util.HashMap"
							scope="request" />

						<c:if test="${ECOFee gt 0.0}">
							<dt class="bold">
								<bbbl:label key="lbl_preview_ecofee"
									language="<c:out param='${language}'/>" />
								<c:set var="ecofeeFootNote" scope="page">
									<bbbl:label key="lbl_footnote_ecofee"
										language="<c:out param='${language}'/>" />
								</c:set>
								<c:if test="${not empty ecofeeFootNote}">
									<c:set value="${count+1}" var="count" scope="page" />
									<sup>${count}</sup>
									<c:set target="${footNotesMap}" property="ecofeeFootNoteCount"
										value="${count}" />
								</c:if>
							</dt>
							<dd class="bold">
								<%-- TODO: ECO fee price display once CR is closed --%>
							</dd>
						</c:if>
						<c:if test="${totalShippingAmount gt 0.0}">
							<dt><span class="bold">
								<bbbl:label key="lbl_cartdetail_estimatedshipping"
									language="<c:out param='${language}'/>" />:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a> 
								<c:set var="shippingFootNote" scope="page">
									<bbbl:label key="lbl_footnote_shipping"
										language="<c:out param='${language}'/>" />
								</c:set>
								<c:if test="${not empty shippingFootNote}">
									<c:set value="${count+1}" var="count" scope="page" />
									<sup>${count}</sup>
									<c:set target="${footNotesMap}"
										property="shippingFootNoteCount" value="${count}" />
								</c:if>
							</dt>
							<dd class="bold">
								<dsp:valueof value="${totalShippingAmount}" converter="currency" />
							</dd>
						</c:if>

						<c:if test="${giftWrapPrice gt 0.0}">
							<dt class="bold">
								<bbbl:label key="lbl_preview_giftpackaging"
									language="<c:out param='${language}'/>" />
								<c:set var="giftWrapFootNote" scope="page">
									<bbbl:label key="lbl_footnote_giftWrap"
										language="<c:out param='${language}'/>" />
								</c:set>
								<c:if test="${not empty giftWrapFootNote}">
									<c:set value="${count+1}" var="count" scope="page" />
									<sup>${count}</sup>
									<c:set target="${footNotesMap}"
										property="giftWrapFootNoteCount" value="${count}" />
								</c:if>
							</dt>
							<dd class="bold">
								<dsp:valueof value="${giftWrapPrice}" converter="currency" />
							</dd>
						</c:if>

						<%-- TO-DO: Foot notes display --%>
						<%-- <dt>Coupon: Free Shipping <sup>1</sup></dt><dd class="highlight">($ 7.99)</dd> --%>
					
						<%-- <c:if test="${totalTax gt 0.0}">
							<dt>
								<bbbl:label key="lbl_preview_tax"
									language="<c:out param='${language}'/>" />
							</dt>
							<dd>
								<dsp:valueof value="${totalTax}" converter="currency" />
							</dd>
						</c:if> --%>
					<c:choose>
						<c:when test="${currentSiteId eq bedBathCanadaSiteCode}">
							<c:choose>
								<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
									<dt class="bold">
										<bbbl:label key="lbl_preview_state_tax"
											language="<c:out param='${language}'/>" />
									</dt>
									<dd class="bold">
										<dsp:valueof value="${countyLevelTax}" converter="currency" />
									</dd>
									<dt class="bold">
										<bbbl:label key="lbl_preview_county_tax"
											language="<c:out param='${language}'/>" />
									</dt>
									<dd class="bold">
										<dsp:valueof value="${stateLevelTax}" converter="currency" />
									</dd>
								</c:when>
								<c:otherwise>
									<dt class="bold">
										<bbbl:label key="lbl_preview_state_tax"
											language="<c:out param='${language}'/>" />
									</dt>
									<dd class="bold">
										<dsp:valueof value="${stateLevelTax}" converter="currency" />
									</dd>
									<dt class="bold">
										<bbbl:label key="lbl_preview_county_tax"
											language="<c:out param='${language}'/>" />
									</dt>
									<dd class="bold">
										<dsp:valueof value="${countyLevelTax}" converter="currency" />
									</dd>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:if test="${totalTax gt 0.0}">
								<dt class="bold">
									<bbbl:label key="lbl_preview_tax"
										language="<c:out param='${language}'/>" />
								</dt>
								<dd class="bold">
									<dsp:valueof value="${totalTax}" converter="currency" />
								</dd>
							</c:if>
						</c:otherwise>
					</c:choose>
					<%-- <c:if test="${preTaxAmout gt 0.0}">
							<dt class="preTax">
								<strong><bbbl:label key="lbl_preview_pretaxtotal"
										language="<c:out param='${language}'/>" /> </strong>
							</dt>
							<dd class="preTax">
								<strong><dsp:valueof value="${preTaxAmout}"
										converter="currency" /> </strong>
							</dd>
						</c:if> --%>
					</dl>

					<div class="fl">
						<c:if test="${storePickupShippingGroupItemCount gt 0}">
							<h3 class="subHeading borderTop">
								<bbbl:label key="lbl_shipping_store_pickup"
									language="<c:out param='${language}'/>" />
							</h3>
						</c:if>
						<dl
							class="grid_4 billingDetailsList alpha omega marTop_15 clearfix summaryNew">
							<c:if test="${storePickupShippingGroupItemCount gt 0}">
								<dt>
									<dsp:valueof value="${storePickupShippingGroupItemCount}" />
									<bbbl:label key="lbl_preview_items"
										language="<c:out param='${language}'/>" />
								</dt>
								<dd>
									<dsp:valueof value="${totalAmt}" converter="currency" />
								</dd>
							</c:if>

							<dt class="total">
								<div class="totalAllign">
								<strong><bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>:</strong>
								<c:set var="totalFootNote" scope="page">
									<bbbl:label key="lbl_footnote_total"
										language="<c:out param='${language}'/>" />
								</c:set>
								<c:if test="${not empty totalFootNote}">
									<c:set value="${count+1}" var="count" scope="page" />
									<sup>${count}</sup>
									<c:set target="${footNotesMap}" property="totalFootNoteCount"
										value="${count}" />
								</c:if>
								<span class="bold"><dsp:valueof value="${totalAmt}" converter="currency"/></span>
								</div>
							</dt>

						<%--	<dt class="fr highlight bold totalSaving"><div class="fr"><bbbl:label key="lbl_preview_totalsavings" language="<c:out param='${language}'/>"/>:
								<span class="highlight bold"><dsp:valueof value="${totalDisAmt}" converter="currency"/></span></div></dt> --%>
						
							
						</dl>
					</div>

					<%-- Total Billing details Fragement -like shipping amt, txt amt and total amount-- ends here --%>
				</div>
			</div>
		</div>

		<div class="clearfix marTop_20">
			<c:if test="${not empty showLinks}">
			<div class="placeOrderInformation grid_6 omega marLeft_25">
				<bbbl:label key="lbl_preview_placeordercaption"
					language="<c:out param='${language}'/>" />
			</div>
			</c:if>
			<div class="grid_8 footNotes marLeft_25">
				<ol>
					<c:if test="${not empty ecofeeFootNote}">
						<li class="smallText"><bbbl:label key="lbl_footnote_ecofee"
								language="<c:out param='${language}'/>"
								placeHolderMap="footNotesMap" />
						</li>
					</c:if>
					<c:if test="${not empty shippingFootNote}">
						<li class="smallText"><bbbl:label key="lbl_footnote_shipping"
								language="<c:out param='${language}'/>"
								placeHolderMap="footNotesMap" />
						</li>
					</c:if>
					<c:if test="${not empty giftWrapFootNote}">
						<li class="smallText"><bbbl:label key="lbl_footnote_giftWrap"
								language="<c:out param='${language}'/>"
								placeHolderMap="footNotesMap" />
						</li>
					</c:if>
					<c:if test="${not empty totalFootNote}">
						<li class="smallText"><bbbl:label key="lbl_footnote_total"
								language="<c:out param='${language}'/>"
								placeHolderMap="footNotesMap" />
						</li>
					</c:if>
				</ol>
			</div>
		</div>	
</div>
<%-- Billing information Ends here --%>
