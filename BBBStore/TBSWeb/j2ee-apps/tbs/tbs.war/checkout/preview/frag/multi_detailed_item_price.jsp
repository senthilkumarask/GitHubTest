<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>
 

	<%-- Variables --%>
	<dsp:getvalueof var="cID" param="commerceItemRelationship.commerceItem.id" />
	<dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem" />
	<dsp:getvalueof var="skuDetailVO" param="skuDetailVO" />
	<dsp:getvalueof var="isConfirmation" param="isConfirmation"/>
	<c:set var="parentId">
		<dsp:valueof param='commerceItemRelationship.commerceItem.id'/><dsp:valueof param='commerceItemRelationship.commerceItem.registryId'/>_<dsp:valueof param='shippingGroup.id'/>
	</c:set>
	<dsp:droplet name="BBBPriceDisplayDroplet">
		<dsp:param name="priceObject" param="commerceItemRelationship.commerceItem" />
		<dsp:param name="orderObject" param="order"/>
		<dsp:param name="profile" bean="Profile"/>
		<dsp:oparam name="output">

			<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
			<dsp:getvalueof var="commerceItemRelationship" param="commerceItemRelationship"/>
			<dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
			<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
			<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
			<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
			<dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage"/>
			<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
			<dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
			<dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
			<dsp:getvalueof var="isSkuLtl" param="isSkuLtl"/>
			<dsp:getvalueof var="clearancePrice" param="clearancePrice"/>
			<dsp:getvalueof var="shippingMethodDescription" param="shippingMethodDescription"/>
			<dsp:getvalueof var="skuName" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" /> 
			

			<dsp:droplet name="BBBPriceDisplayDroplet">
				<dsp:param name="priceObject" param="commerceItemRelationship" />
				<dsp:oparam name="output"> 
					<%-- <dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/> --%>
					<dsp:getvalueof var="undiscountedItemsCount" param="commerceItemRelationship.quantity"/>
					<div class="small-12 large-6 print-8 columns">
						<div class="row">

							<%-- quantity --%>
							<div class="small-6 large-3 columns quantity">
								<h3><bbbl:label key="lbl_cartdetail_quantity" language="${language}"/>: </h3>
								<c:out value="${undiscountedItemsCount}" />
							</div>

							<%-- prices --%>
							<div class="small-6 medium-9 columns pricing">
								<div class="row">

									<%-- our price --%>
									<div class="small-12 large-4 columns print-3">
										<h3><bbbl:label key="lbl_cartdetail_unitprice" language="${language}"/>: </h3>
										<c:choose>
											<c:when test="${isConfirmation}">
												<dsp:valueof value="${unitListPrice}" converter="currency"/>
											</c:when>
											<c:otherwise>
												<a href="#" class="priceOverrideModal" data-reveal-id="priceOverrideModal_${parentId}"><dsp:valueof value="${unitListPrice}" converter="currency"/></a>
											</c:otherwise>
										</c:choose>
									</div>
									<div id="priceOverrideModal_${parentId}" class="reveal-modal medium price-override" data-reveal="">
										<div class="row">
											<div class="small-12 columns no-padding">
												<h1>Price Override</h1>
												<p class="note">* required field</p>
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label class="right inline">Item Description</label>
											</div>
											<div class="small-12 large-8 columns">
												<input type="text" value="<c:out value='${skuName}' escapeXml='false' />" disabled />
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label class="right inline">Quantity</label>
											</div>
											<div class="small-12 large-8 columns">
												<input type="text" value="<c:out value='${undiscountedItemsCount} 'escapeXml='false' />" disabled />
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label class="right inline">Price per Unit</label>
											</div>
											<div class="small-12 large-8 columns">
												<c:choose>
													<c:when test="${unitSalePrice gt 0.0}">
														<input type="text" class="unitListPrice" value="<dsp:valueof value='${unitSalePrice}' converter='currency' />" disabled />
													</c:when>
													<c:otherwise>
														<input type="text" class="unitListPrice" value="<dsp:valueof value='${unitListPrice}' converter='currency' />" disabled />
													</c:otherwise>
												</c:choose>
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label class="right inline">Quantity to Override</label>
											</div>
											<div class="small-12 large-8 columns quantity">
												<div class="qty-spinner">
													<a class="button minus secondary"><span></span></a>
													<input id="qtyToOverride_${cID}" class="quantity-input" type="text" maxlength="2" value="${undiscountedItemsCount}" data-max-value="${undiscountedItemsCount}" maxlength="2" />
													<a class="button plus secondary"><span></span></a>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label for="reasonList_${cID}" class="right inline">Reason *</label>
											</div>
											<div class="small-12 large-8 columns">
												<select name="reasonList_${cID}" id="reasonList_${cID}" class="reasonList">
													<dsp:droplet name="TBSOverrideReasonDroplet">
														<dsp:param name="OverrideType" value="item" />
														<dsp:oparam name="output">
															<option value="">Select Reason</option>
															<dsp:droplet name="ForEach">
																<dsp:param name="array" param="reasons" />
																<dsp:param name="elementName" value="elementVal" />
																<dsp:oparam name="output">
																<option value="<dsp:valueof param='key'/>">
					                                              <dsp:valueof param="elementVal" />
					                                            </option>
                                   								     </dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
													</dsp:droplet>
												</select>
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label for="competitorList_${cID}" class="right inline">Competitor</label>
											</div>
											<div class="small-12 large-8 columns">
												<select name="competitorList_${cID}" id="competitorList_${cID}" class="competitorList">
													<dsp:droplet name="TBSOverrideReasonDroplet">
														<dsp:param name="OverrideType" value="competitors" />
														<dsp:oparam name="output">
															<option value="">Select Competitor</option>
															<dsp:droplet name="ForEach">
																<dsp:param name="array" param="reasons" />
																<dsp:param name="elementName" value="elementVal" />
																<dsp:oparam name="output">
																<option value="<dsp:valueof param='key'/>">
					                                              <dsp:valueof param="elementVal" />
					                                            </option>
                                   								     </dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
													</dsp:droplet>
												</select>
                                                <small for="competitorList_${cID}" class="error hidden" id="errorComp">Please select a value for Competitor</small>
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-4 columns">
												<label for="newPrice_${cID}" class="right inline">New Price *</label>
											</div>
											<div class="small-12 large-8 columns">
												<span class="dollarSignOverride">$</span>
												<input type="tel" value="0.00" id="newPrice_${cID}" name="newPrice_${cID}" class="priceOverride newPrice" maxlength="11"/>
											</div>
										</div>
										<div class="row">
											<div class="small-12 large-offset-4 large-4 columns">
												<a href='javascript:void(0);' class="button small service expand submit-price-override" data-parent="#priceOverrideModal_${parentId}" data-shipping-group="<dsp:valueof param='shippingGroup.id'/>" data-cid="${cID}">Override</a>
											</div>
											<div class="small-12 large-2 columns left">
                                                <a href='javascript:void(0);' class="close-modal small button secondary expand">Cancel</a>
                                            </div>
										</div>
										<a class="close-reveal-modal">&times;</a>
									</div>

									<%-- your price --%>
									<div class="small-12 large-5 columns your-price no-padding-right print-6">
										<h3><bbbl:label key="lbl_you_pay" language="${language}"/>: </h3>
										<dsp:include page="/cart/cart_includes/your_price.jsp">
											<dsp:param name="priceInfoVO" value="${priceInfoVO}"/>
											<dsp:param name="cItem" param="commerceItemRelationship.commerceItem" />
										</dsp:include>
										<dsp:getvalueof param="commerceItemRelationship.commerceItem.TBSItemInfo" var="itemInfo"/>
										<c:if test="${not empty itemInfo && itemInfo.overridePrice > 0}">
											<div class="dl-wrap">
											<span class="overrides"><bbbl:label key="lbl_price_override_to" language="<c:out param='${language}'/>"/> 
												<dsp:valueof param="commerceItemRelationship.commerceItem.TBSItemInfo.overridePrice" converter="currency"/>
											</span>
											</div>
										</c:if>
										<c:if test="${unitSavedAmount gt 0.0 || totalSavedAmount gt 0.0 || couponApplied eq 'true'}">
											<div class="dl-wrap">
												<dt class="savings">
													<bbbl:label key="lbl_cartdetail_yousave" language="<c:out param='${language}'/>"/>
												<!-- </dt>
												<dd class="savings large-7 left"> -->
													<c:choose>
														<c:when test="${couponApplied ne 'true'}">
															<c:if test="${totalSavedAmount gt 0.0}">
																<dsp:valueof value="${totalSavedAmount}" number="0.00" converter="currency"/>
															</c:if>
														</c:when>
														<c:otherwise>
															<c:choose> 
																<c:when test="${unitSavedAmount gt 0.0 && totalSavedAmount gt 0.0 && totalSavedAmount ne unitSavedAmount}">
																	<dsp:valueof value="${totalSavedAmount}" number="0.00" converter="currency"/>
																</c:when>
																<c:otherwise>
																	<dsp:valueof value="${couponDiscountAmount + totalSavedAmount}" number="0.00" converter="currency"/>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</dd>
											</div>
										</c:if>
											<dsp:droplet name="ForEach">
											<dsp:param value="${skuDetailVO.skuAttributes}" name="array" />
											<dsp:param name="elementName" value="attributeVOList"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="placeholder" param="key"/>
												<c:if test="${placeholder eq 'CRSL'}">
													<dsp:droplet name="ForEach">
													<dsp:param param="element" name="array" />
													<dsp:param param="attributeVOList" name="array" />
													<dsp:param name="sortProperties" value="+priority"/>
													<dsp:param name="elementName" value="attributeVO"/>
													<dsp:oparam name="output">
														<div class="dl-wrap">
															<dt></dt>
															<dd class="productAttributes prodAttribWrapper">
																<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
															</dd>
														</div>
													</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</dsp:oparam>
											</dsp:droplet>
										
										
										<%-- Additional info for LTL items --%>
					                    <c:if test="${deliverySurcharge gt 0.0}">
					                        <div class="dl-wrap">
					                            <dt class="ltlsavings">
					                               ${shippingMethodDescription}:
					                                <dsp:droplet name="TBSCommerceItemLookupDroplet">
					                                    <dsp:param name="id" param="commerceItemRelationship.commerceItem.deliveryItemId"/>
					                                    <dsp:param name="order" param="order"/>
					                                    <dsp:param name="elementName" value="surchargeItem"/>
					                                        <dsp:oparam name="output">
					                                            <dsp:param param="surchargeItem" name="surchargeItem"/>
					                                            <dsp:getvalueof param="surchargeItem.quantity" var="surchargeQty"/>
					                                            <dsp:getvalueof param="listPrice" var="surchargeListPrice"/>
					                                            <dsp:getvalueof param="displayName" var="SurchargeSkuName"/>
					                                        </dsp:oparam>
					                                </dsp:droplet>
					                                <a href="#" class="surchargeOverrideModal" data-reveal-id="surchargeOverrideModal_${commItem.deliveryItemId}">
					                                    <dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/>
					                                </a>
					                            </dt>
					                        </div>
					                    </c:if>
					                    
					                    <c:if test="${assemblyFee gt 0.0}">
					                        <div class="dl-wrap">
					                            <dt class="ltlsavings">
					                                 <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:		                            
					                                <dsp:droplet name="TBSCommerceItemLookupDroplet">
					                                    <dsp:param name="id"  param="commerceItemRelationship.commerceItem.assemblyItemId"/>
					                                    <dsp:param name="order" param="order"/>
					                                    <dsp:param name="elementName" value="assemblyItem"/>
					                                        <dsp:oparam name="output">
					                                            <dsp:param param="assemblyItem" name="assemblyItem"/>
					                                            <dsp:getvalueof param="assemblyItem.quantity" var="assemblyQty"/>
					                                            <dsp:getvalueof param="listPrice" var="assemblyListPrice"/>
					                                            <dsp:getvalueof param="displayName" var="assemblySkuName"/>
					                                        </dsp:oparam>
					                                </dsp:droplet>
					                                <a href="#" class="assemblyFeeOverrideModal" data-reveal-id="assemblyFeeOverrideModal_${commItem.assemblyItemId}">
					                                    <dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/>
					                                </a>
					                           </dt>
					                        </div>
					                    </c:if>
					                    <%-- /Additional info for LTL items --%>
										
									</div>

									<%-- total price --%>
									<div class="small-12 large-3 columns total-price print-right">
										<h3><bbbl:label key="lbl_cartdetail_totalprice" language="${language}"/>: </h3>
										<dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="currency"/>
									</div>

								</div>
							</div>
						</div>
					</div>
					
					<div id="surchargeOverrideModal_${commItem.deliveryItemId}" class="reveal-modal medium surcharge-override" data-reveal="">
			            <div class="row">
			                <div class="small-12 columns no-padding">
			                    <h1>Surcharge Override</h1>
			                    <p class="note">* required field</p>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Item Description</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <input type="text" value="<c:out value='${SurchargeSkuName}' escapeXml='false' />" disabled />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Quantity</label>
			                </div>
			                <div class="small-12 large-8 columns surchargeQty">
			                     <input type="text" value="<c:out value='${surchargeQty}' escapeXml='false' />" disabled />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Price per Unit</label>
			                </div>
			                <div class="small-12 large-8 columns price">
			                    <input type="text" value="<dsp:valueof value='${surchargeListPrice}' converter='currency' />" class="surchargeListPrice" disabled />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Quantity to Override</label>
			                </div>
			                <div class="small-12 large-8 columns quantity">
			                    <div class="qty-spinner">
			                        <input id="qtyToOverride_${commItem.deliveryItemId}" class="quantity-input" type="text" data-max-value="99" maxlength="2" value="${surchargeQty}" data-max-value="${surchargeQty}" disabled="disabled" />
			                    </div>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label for="reasonList_${commItem.deliveryItemId}" class="right inline">Reason *</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <select name="reasonList_${commItem.deliveryItemId}" id="reasonList_${commItem.deliveryItemId}" class="surchargeReasonList">
			                        <dsp:droplet name="TBSOverrideReasonDroplet">
			                            <dsp:param name="OverrideType" value="surcharge" />
			                            <dsp:oparam name="output">
			                                <option value="">Select Reason</option>
			                                <dsp:droplet name="ForEach">
			                                    <dsp:param name="array" param="reasons" />
			                                	<dsp:param name="elementName" value="elementVal" />
			                                    <dsp:oparam name="output">
			                                    <option value="<dsp:valueof param='key'/>">
	                                              <dsp:valueof param="elementVal" />
	                                            </option>
                                             </dsp:oparam>
			                                </dsp:droplet>
			                            </dsp:oparam>
			                        </dsp:droplet>
			                    </select>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label for="competitorList_${commItem.deliveryItemId}" class="right inline">Competitor</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <select name="competitorList_${commItem.deliveryItemId}" id="competitorList_${commItem.deliveryItemId}" class="surchargeCompetitorList">
			                        <dsp:droplet name="TBSOverrideReasonDroplet">
			                            <dsp:param name="OverrideType" value="competitors" />
			                            <dsp:oparam name="output">
			                                <option value="">Select Competitor</option>
			                                <dsp:droplet name="ForEach">
			                                    <dsp:param name="array" param="reasons" />
			                                	<dsp:param name="elementName" value="elementVal" />
			                                    <dsp:oparam name="output">
			                                    <option value="<dsp:valueof param='key'/>">
	                                              <dsp:valueof param="elementVal" />
	                                            </option>
                                   			</dsp:oparam>
			                                </dsp:droplet>
			                            </dsp:oparam>
			                        </dsp:droplet>
			                    </select>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label for="newPrice_${commItem.deliveryItemId}" class="right inline">New Price *</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                	<span class="dollarSignOverride">$</span>
			                    <input type="tel" value="0.00" id="newPrice_${commItem.deliveryItemId}" name="newPrice_${commItem.deliveryItemId}" class="priceOverride newSurchargePrice" maxlength="7" value=""/>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-offset-4 large-4 columns">
			                    <a href='javascript:void(0);' class="button small service expand submit-surcharge-override" data-parent="#priceOverrideModal_${commItem.deliveryItemId}" data-cid="${commItem.deliveryItemId}">Override</a>
			                </div>
			                <div class="small-12 large-4 columns">
			                    <a href='javascript:void(0);' class="close-modal button secondary">Cancel</a>
			                </div>
			            </div>
			            <a class="close-reveal-modal">&times;</a>
			        </div>
			
			        <div id="assemblyFeeOverrideModal_${commItem.assemblyItemId}" class="reveal-modal medium assemblyFee-override" data-reveal="">
			            <div class="row">
			                <div class="small-12 columns no-padding">
			                    <h1>Assembly Fee Override</h1>
			                    <p class="note">* required field</p>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Item Description</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <input type="text" value="<c:out value='${assemblySkuName}' escapeXml='false' />" disabled />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Quantity</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <input type="text" value="<c:out value='${assemblyQty}' escapeXml='false' />" disabled />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Price per Unit</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <input type="text" value="<dsp:valueof value='${assemblyListPrice}' converter='currency' />" class="assemblyListPrice" disabled />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label class="right inline">Quantity to Override</label>
			                </div>
			                <div class="small-12 large-8 columns quantity">
			                    <div class="qty-spinner">
			                        <input id="qtyToOverride_${commItem.assemblyItemId}" class="quantity-input" 
			                            type="text" maxlength="2" value="${assemblyQty}" data-max-value="${assemblyQty}" class="button plus secondary" disabled="disabled" />
			                    </div>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label for="reasonList_${commItem.assemblyItemId}" class="right inline">Reason *</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <select name="reasonList_${commItem.assemblyItemId}" id="reasonList_${commItem.assemblyItemId}" class="assemblyReasonList">
			                        <dsp:droplet name="TBSOverrideReasonDroplet">
			                            <dsp:param name="OverrideType" value="surcharge" />
			                            <dsp:oparam name="output">
			                                <option value="">Select Reason</option>
			                                <dsp:droplet name="ForEach">
			                                    <dsp:param name="array" param="reasons" />
			                                	<dsp:param name="elementName" value="elementVal" />
			                                   <dsp:oparam name="output">
			                                   <option value="<dsp:valueof param='key'/>">
	                                              <dsp:valueof param="elementVal" />
	                                            </option>
                                   				</dsp:oparam>
			                                </dsp:droplet>
			                            </dsp:oparam>
			                        </dsp:droplet>
			                    </select>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label for="competitorList_${commItem.assemblyItemId}" class="right inline">Competitor</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                    <select name="competitorList_${commItem.assemblyItemId}" id="competitorList_${commItem.assemblyItemId}" class="assemblyCompetitorList">
			                        <dsp:droplet name="TBSOverrideReasonDroplet">
			                            <dsp:param name="OverrideType" value="competitors" />
			                            <dsp:oparam name="output">
			                                <option value="">Select Competitor</option>
			                                <dsp:droplet name="ForEach">
			                                    <dsp:param name="array" param="reasons" />
			                                	<dsp:param name="elementName" value="elementVal" />
			                                    <dsp:oparam name="output">
			                                    <option value="<dsp:valueof param='key'/>">
	                                              <dsp:valueof param="elementVal" />
	                                            </option>
                                   			  </dsp:oparam>
			                                </dsp:droplet>
			                            </dsp:oparam>
			                        </dsp:droplet>
			                    </select>
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-4 columns">
			                    <label for="newPrice_${commItem.assemblyItemId}" class="right inline">New Price *</label>
			                </div>
			                <div class="small-12 large-8 columns">
			                	<span class="dollarSignOverride">$</span>
			                    <input type="tel" value="0.00" id="newPrice_${commItem.assemblyItemId}" name="newPrice_${commItem.assemblyItemId}" class="priceOverride assemblyNewPrice" maxlength="7" />
			                </div>
			            </div>
			            <div class="row">
			                <div class="small-12 large-offset-4 large-4 columns">
			                    <a href='javascript:void(0);' class="button small service expand submit-assemblyFee-override" data-parent="#priceOverrideModal_${commItem.assemblyItemId}" data-cid="${commItem.assemblyItemId}">Override</a>
			                </div>
			                <div class="small-12 large-4 columns">
			                    <a href='javascript:void(0);' class="close-modal">Cancel</a>
			                </div>
			            </div>
			            <a class="close-reveal-modal">&times;</a>
			        </div>
					

				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
