<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="bedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="displayTax" scope="request">
		<dsp:valueof param="displayTax"/>
	</c:set>
	<dsp:getvalueof var="order" param="order" scope="request"/>
	<c:if test="${empty order}">
		<dsp:getvalueof var="order" bean="ShoppingCart.current" scope="request"/>
	</c:if>

	<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
		<dsp:param name="priceObject" value="${order}"  />
		<dsp:param name="profile" bean="Profile"/>
		<dsp:oparam name="output">

			<dsp:getvalueof var="hardgoodShippingGroupItemCount" param="priceInfoVO.hardgoodShippingGroupItemCount"/>
			<dsp:getvalueof var="totalShippingAmount" param="priceInfoVO.totalShippingAmount"/>
			<dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
			<dsp:getvalueof var="totalTax" param="priceInfoVO.totalTax"/>
			<dsp:getvalueof var="storePickupShippingGroupItemCount" param="priceInfoVO.storePickupShippingGroupItemCount"/>
			<dsp:getvalueof var="onlineEcoFeeTotal" param="priceInfoVO.onlineEcoFeeTotal"/>
			<dsp:getvalueof var="storeEcoFeeTotal" param="priceInfoVO.storeEcoFeeTotal"/>
			<dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
			<dsp:getvalueof var="storeAmount" param="priceInfoVO.storeAmount"/>
			<dsp:getvalueof var="countyLevelTax" param="priceInfoVO.shippingCountyLevelTax"/>
			<dsp:getvalueof var="stateLevelTax" param="priceInfoVO.shippingStateLevelTax"/>
			<dsp:getvalueof var="onlinePurchaseTotal" param="priceInfoVO.onlinePurchaseTotal"/>
			<dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
			<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
			<dsp:getvalueof var="freeShipping" param="priceInfoVO.freeShipping"/>
			<dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
			<dsp:getvalueof var="onlineTotal" param="priceInfoVO.onlineTotal"/>
			<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount" scope="request"/>
			<dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
			<dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
			<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
			<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
			<dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
			<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
			<dsp:getvalueof var="orderHasLtl" param="orderHasLtl"/>
			<dsp:getvalueof var="orderContainsEmptySG" param="orderContainsEmptySG"/>

			<dl class="clearfix <c:if test='${storePickupShippingGroupItemCount gt 0}'>inStorePickup</c:if> noBorder">

				<dt>${hardgoodShippingGroupItemCount} <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></dt>
				<dd><dsp:valueof value="${onlinePurchaseTotal}" converter="currency"/></dd>

				<c:if test="${hardgoodShippingGroupItemCount gt 0.0}">
					<!-- hardgoodShippingGroupItemCount gt 0.0 -->
					<c:set value="0" var="count" scope="request"/>
					<c:if test="${onlineEcoFeeTotal gt 0.0}">
						<!-- onlineEcoFeeTotal gt 0.0 -->
						<dt><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/>
							<c:set var="ecofeeFootNote" scope="request">
								<bbbl:label key="lbl_footnote_ecofee" language="<c:out param='${language}'/>"/>
							</c:set>
							<c:if test="${not empty ecofeeFootNote}">
								<c:set value="${count+1}" var="count" scope="request"/>
								<sup>${count}</sup>
								<c:set target="${placeHolderMap}" property="ecofeeFootNoteCount" value="${count}"/>
							</c:if>
						</dt>
						<dd>
							<dsp:valueof value="${onlineEcoFeeTotal}" converter="currency"/>
						</dd>
					</c:if>

					<c:if test="${giftWrapTotal gt 0.0}">
						<!-- giftWrapTotal gt 0.0 -->
						<dt>
							<bbbl:label key="lbl_preview_giftpackaging" language="<c:out param='${language}'/>"/>
							<c:set var="giftWrapFootNote" scope="request">
								<bbbl:label key="lbl_footnote_giftWrap" language="<c:out param='${language}'/>"/>
							</c:set>
							<c:if test="${not empty giftWrapFootNote}">
								<c:set value="${count+1}" var="count" scope="request"/>
								<sup>${count}</sup>
								<c:set target="${placeHolderMap}" property="giftWrapFootNoteCount" value="${count}"/>
							</c:if>
						</dt>
						<dd><dsp:valueof value="${giftWrapTotal}" converter="currency"/></dd>
					</c:if>

					<c:choose>
						<c:when test="${freeShipping ne true}">
							<!-- freeShipping ne true -->
							<c:choose>
								<c:when test ="${orderHasLtl and orderContainsEmptySG}">
									<!-- orderHasLtl and orderContainsEmptySG -->
									<dt>
									<bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/>
										<c:set var="shippingFootNote" scope="request">
											<bbbl:label key="lbl_footnote_shipping" language="<c:out param='${language}'/>"/>
										</c:set>
										<c:if test="${not empty shippingFootNote}">
											<c:set value="${count+1}" var="count" scope="request"/>
											<sup>${count}</sup>
											<c:set target="${placeHolderMap}" property="shippingFootNoteCount" value="${count}"/>
										</c:if>
										<dsp:include page="/common/shipping_method_description.jsp"/>
									</dt>
									<dd>TBD</dd>
								</c:when>
								<c:otherwise>
									<!-- otherwise -->
									<dt>
										<bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/>
										<c:set var="shippingFootNote" scope="request">
											<bbbl:label key="lbl_footnote_shipping" language="<c:out param='${language}'/>"/>
										</c:set>
										<c:if test="${not empty shippingFootNote}">
											<c:set value="${count+1}" var="count" scope="request"/>
											<sup>${count}</sup>
											<c:set target="${placeHolderMap}" property="shippingFootNoteCount" value="${count}"/>
										</c:if>
										<dsp:include page="/common/shipping_method_description.jsp"/>
									</dt>
									<dd><dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/></dd>
								</c:otherwise>
							</c:choose>

							<dsp:getvalueof var="shippingAdjustments" param="priceInfoVO.shippingAdjustments"/>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="priceInfoVO.shippingAdjustments" />
								<dsp:param name="elementName" value="shippingPromoDiscount" />
								<dsp:oparam name="outputStart">
									<!-- shipping adjustments -->
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
									<!-- /shipping adjustments -->
								</dsp:oparam>
								<dsp:oparam name="output">
									<!-- upperCase highlight WHEN -->
									<dt class="upperCase highlight">
										<c:set var="shipPromo" value="true" />
										<dsp:getvalueof var="count" param="count" />
										<dsp:getvalueof var="size" param="size" />
										<c:choose>
											<c:when test="${count lt size}" >
												<dsp:valueof param="key.displayName"/>
											</c:when>
											<c:otherwise>
												<dsp:valueof param="key.displayName"/>
											</c:otherwise>
										</c:choose>
									</dt>
									<dsp:getvalueof var="keyVar" param="key"></dsp:getvalueof>
									<dd class="highlight">(<dsp:valueof value="${shippingAdjustments[keyVar]}" converter="currency" number="0.00"/>)</dd>
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<!-- upperCase highlight OTHERWISE -->
							<dt class="highlight upperCase"><strong><bbbl:label key="lbl_cartdetail_freeshipping" language="<c:out param='${language}'/>"/></strong></dt>
							<dd></dd>
						</c:otherwise>
					</c:choose>
					<c:if test="${totalSurcharge gt 0.0}">
						<!-- totalSurcharge gt 0.0 -->
						<dt><bbbl:label key="lbl_preview_surcharge" language="<c:out param='${language}'/>"/></dt>
						<dd><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
					</c:if>
					<c:if test="${surchargeSavings gt 0.0}">
						<!-- surchargeSavings gt 0.0 -->
						<dt class="highlight"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
						<dd class="highlight">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
					</c:if>

					<!-- Additional info for LTL items summary -->
					<dl class="LTLDetails clearfix">
						<c:if test="${orderHasLtl and orderContainsEmptySG}">
							<dt class="highlightBlack"><bbbl:label key="ltl_delivery_surcharge_may_apply" language="<c:out param='${language}'/>"/></dt>
							<dd class="highlightBlack">TBD</dd>
						</c:if>
						<c:if test="${totalDeliverySurcharge gt 0.0 and !orderContainsEmptySG and orderHasLtl}">
							<dt class="highlightBlack"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/></dt>
							<dd class="highlightBlack"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></dd>
						</c:if>
						<c:if test="${maxDeliverySurchargeReached}">
							<c:choose>
								<c:when test="${orderHasLtl and orderContainsEmptySG}">
									<dt class="highlightRed">
										<bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/> <br>
										<a class="highlightBlue newOrPopup maxSurcharges" href="/tbs/cart/static/max_surcharges_info.jsp"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
									</dt>
									<dd class="parentheses highlightRed">TBD</dd>
								</c:when>
								<c:otherwise>
									<dt class="highlightRed">
										<bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/> <br>
										<a class="highlightBlue newOrPopup maxSurcharges" href="/tbs/cart/static/max_surcharges_info.jsp"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
									</dt>
									<dd class="parentheses highlightRed"><dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="currency"/></dd>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${totalAssemblyFee gt 0.0}">
							<dt class="highlightBlack"><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/></dt>
							<dd class="highlightBlack"><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="currency"/></dd>
						</c:if>
					</dl>

					<!-- Additional info for LTL items summary -->
					<c:if test="${not empty order.specialInstructions['CYBERSOURCE_TAX_FAILURE']}">
						<c:set var="taxFailureFootNote" scope="request">
							<bbbl:label key="lbl_preview_tax_unavailable" language="<c:out param='${language}'/>"/>
						</c:set>
					</c:if>
displayTax: <c:out value="${displayTax}"/><br>
					<c:choose>
						<c:when test="${displayTax}">
							<!-- displayTax WHEN -->
							<c:choose>
								<c:when test="${not empty taxFailureFootNote}">
									<!-- not empty taxFailureFootNote -->
									<dt><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></dt>
									<dd class="smallText">*</dd>
								</c:when>
								<c:when test="${currentSiteId eq bedBathCanadaSiteCode}">
									<!-- currentSiteId eq bedBathCanadaSiteCode -->
									<dt><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
									<dd><dsp:valueof value="${totalStateTax}" converter="currency"/></dd>
									<dt><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
									<dd><dsp:valueof value="${totalCountyTax}" converter="currency"/></dd>
									
								</c:when>
								<c:otherwise>
									<!-- currentSiteId eq bedBathCanadaSiteCode OTHERWISE -->
									<dt><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></dt>
									<dd><dsp:valueof value="${totalTax}" converter="currency"/></dd>
								</c:otherwise>
							</c:choose>
							<dt><strong><bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/></strong></dt>
							<dd><strong><dsp:valueof value="${onlineTotal}" converter="currency"/> </strong></dd>
							<dsp:getvalueof value="${onlineTotal}" var="barcodeTotalAmount" scope="session"/>
						</c:when>
						<c:otherwise>
							<!-- displayTax OTHERWISE -->
							<c:set var="totalAmount" value="${totalAmount - totalTax}"/>
							<dt><strong><bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/></strong></dt>
							<dd><strong><dsp:valueof value="${orderPreTaxAmout}" converter="currency"/> </strong></dd>
							<dsp:getvalueof value="${onlineTotal}" var="barcodeTotalAmount" scope="session"/>
						</c:otherwise>
					</c:choose>
				</c:if>

				<dsp:getvalueof param="priceInfoVO.totalSavedAmount" var="totalSavedAmount"/>
				<c:if test="${totalSavedAmount gt 0}">
					<c:choose>
						<c:when test ="${orderHasLtl and orderContainsEmptySG}">
							<dt class="highlight"><bbbl:label key="lbl_preview_totalsavings" language="<c:out param='${language}'/>"/></dt>
							<dd class="highlight">TBD</dd>
						</c:when>
						<c:otherwise>
							<dt class="highlight"><bbbl:label key="lbl_preview_totalsavings" language="<c:out param='${language}'/>"/></dt>
							<dd class="highlight"><dsp:valueof value="${totalSavedAmount}" converter="currency"/></dd>
						</c:otherwise>
					</c:choose>
				</c:if>
			</dl>

			<c:if test="${storePickupShippingGroupItemCount gt 0}">
				<!-- storePickupShippingGroupItemCount gt 0 -->
				<h3 class="subHeading"><bbbl:label key="lbl_shipping_store_pickup" language="<c:out param='${language}'/>"/> <span class="smallText"><bbbl:label key="lbl_store_amountdue" language="<c:out param='${language}'/>"/></span></h3>
				<dl class="clearfix">
					<dt>${storePickupShippingGroupItemCount} <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></dt>
					<dd><dsp:valueof value="${storeAmount }" converter="currency"/></dd>
					<c:if test="${storeEcoFeeTotal gt 0.0}">
						<dt><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/></dt>
						<dd><dsp:valueof value="${storeEcoFeeTotal}" converter="currency"/></dd>
					</c:if>
				</dl>
			</c:if>

		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
