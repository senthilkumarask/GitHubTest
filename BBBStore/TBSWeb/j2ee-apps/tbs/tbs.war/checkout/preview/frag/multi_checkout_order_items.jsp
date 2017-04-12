<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBCartDisplayDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GroupCommerceItemsByShiptimeDroplet"/>

	<%-- Variables --%>
	<c:set var="skuIds" scope="request"/>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isLoggedIn" bean="Profile.transient"/>
	<dsp:getvalueof var="isConfirmation" param="isConfirmation"/>
	<dsp:getvalueof var="shipMethod" param="shippingGroup.shippingMethod"/>
	<dsp:getvalueof var="shippingState" param="shippingGroup.shippingAddress.state"/>
	
	<div class="row hide-for-medium-down productsListHeader">
		<div class="medium-6 print-4 columns">
			<h3><bbbl:label key="lbl_cartdetail_item" language="${language}"/></h3>
		</div>
		<div class="medium-6  print-8 columns">
			<div class="row">
				<div class="medium-3 columns">
					<h3><bbbl:label key="lbl_cartdetail_quantity" language="${language}"/></h3>
				</div>
				<div class="medium-3 columns print-2 no-padding">
					<h3><bbbl:label key="lbl_cartdetail_unitprice" language="${language}"/></h3>
				</div>
				<div class="medium-4 columns print-5">
					<h3><bbbl:label key="lbl_you_pay" language="${language}"/></h3>
				</div>
				<div class="medium-2 columns print-center">
					<h3><bbbl:label key="lbl_cartdetail_totalprice" language="${language}"/></h3>
				</div>
			</div>
		</div>
	</div>

	<dsp:droplet name="BBBCartDisplayDroplet">
		<dsp:param name="order" param="order" />
		
		<dsp:oparam name="output">

			<dsp:droplet name="GroupCommerceItemsByShiptimeDroplet">
				<dsp:param name="commerceItemList" param="commerceItemList"/>
				<dsp:param name="order" param="order" />
				<dsp:param name="shipGroup" param="shippingGroup"/>
				<dsp:oparam name="output">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="itemsByShipTime" />
						<dsp:param name="elementName" value="groupedItemsList" />
						<dsp:oparam name="output">

							<c:set var="headerIsShowing" value="false" scope="request" />
                            <div class="cart-items">
							<%-- display each item in the shipping group, grouped by shiptime --%>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="groupedItemsList" />
								<dsp:param name="elementName" value="commerceItem" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="ciSkuID" param="commerceItem.BBBCommerceItem.id" />
									<dsp:getvalueof var="skuDetailVO" param="commerceItem.skuDetailVO" />
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
										<dsp:param name="elementName" value="commerceItemRelationship" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="skuId" param="commerceItemRelationship.commerceItem.id" />
											<c:if test="${ciSkuID eq skuId}">
												<c:if test="${not headerIsShowing}">
													<div class="row collapse no-padding print-gray-panel">
														<div class="small-12 columns">
															<dsp:getvalueof param="key" var="shiptime"/>
                                              				<c:set var="tbs_ship_time">
                                              					<c:choose>
																	<c:when test="${shiptime eq '0001'}">
			                                                           	<bbbl:label key="lbl_tbs_ship_time_0001" language="${pageContext.request.locale.language}" />
			                                                        </c:when>
			                                                        <c:when test="${shiptime eq '0002'}">
		                                                            	<bbbl:label key="lbl_tbs_ship_time_0002" language="${pageContext.request.locale.language}" />
			                                                        </c:when>
			                                                        <c:when test="${shiptime eq '0003'}">
			                                                            <bbbl:label key="lbl_tbs_ship_time_0003" language="${pageContext.request.locale.language}" />
			                                                        </c:when>
			                                                        <c:when test="${shiptime eq '0004'}">
			                                                            <bbbl:label key="lbl_tbs_ship_time_0004" language="${pageContext.request.locale.language}" />
			                                                        </c:when>
			                                                        <c:when test="${shiptime eq '0005'}">
			                                                            <bbbl:label key="lbl_tbs_ship_time_0005" language="${pageContext.request.locale.language}" />
			                                                        </c:when>
			                                                        <c:otherwise>
			                                                        	<c:out value="${shiptime}"></c:out>
			                                                        </c:otherwise>
			                                                    </c:choose> 
			                                                </c:set>
															<p class="divider">
                                                       			${tbs_ship_time}
                                                       			<c:if test="${HolidayMessagingOn}">
                                                        			<dsp:include src="/tbs/common/holidayMessaging.jsp">
														 				<dsp:param name="timeframe" value="${shiptime}"/>
														 				<dsp:param name="tbsShipTime" value="${tbs_ship_time}"/>
														 				<dsp:param name="appendtoLeadTime" value="true"/>
														 				<dsp:param name="shipMethod" value="${shipMethod}"/>
														 			</dsp:include>
                                                       			</c:if>
									 						</p>
														</div>
													</div>
													<c:set var="headerIsShowing" value="true" scope="request" />
												</c:if>
												<div class="small-12 columns no-padding cart-item" id="cartItemID_<dsp:valueof param='commerceItemRelationship.commerceItem.id'/><dsp:valueof param='commerceItemRelationship.commerceItem.registryId'/>_<dsp:valueof param='shippingGroup.id'/>">
													<dsp:include page="/checkout/preview/frag/multi_sku_details.jsp" flush="true">
														<dsp:param name="commerceItemRelationship" param="commerceItemRelationship"/>
														<dsp:param name="skuDetailVO" value="${skuDetailVO}" />
														<dsp:param name="isFromOrderDetail" value="${isFromOrderDetail}"/>
														<dsp:param name="isConfirmation" param="isConfirmation"/>
														<dsp:param name="order" param="order"/>
														<dsp:param name="shipMethod" value="${shipMethod}"/>
														<dsp:param name="shippingState" value="${shippingState}"/>
													</dsp:include>

													<dsp:include page="/checkout/preview/frag/multi_detailed_item_price.jsp" flush="true">
														<dsp:param name="commerceItemRelationship" param="commerceItemRelationship"/>
														<dsp:param name="shippingGroup" param="shippingGroup"/>
														<dsp:param name="promoExclusionMap" param="promoExclusionMap"/>
														<dsp:param name="order" param="order"/>
														<dsp:param name="isConfirmation" param="isConfirmation"/>
														<dsp:param name="skuDetailVO" value="${skuDetailVO}" />
													</dsp:include>
												</div>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>

								</dsp:oparam>
							</dsp:droplet>
							<%-- for each cart item --%>
                            </div>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>

		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
