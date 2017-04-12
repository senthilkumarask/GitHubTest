<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBCartRegistryDisplayDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBCartDisplayDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GroupCommerceItemsByShiptimeDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>

	<%-- Variables --%>
	<c:set var="skuIds" scope="request"/>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="isLoggedIn" bean="Profile.transient"/>
	<dsp:getvalueof var="isConfirmation" param="isConfirmation"/>
	<dsp:getvalueof var="shipMethod" param="shippingGroup.shippingMethod"/>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<div class="row hide-for-medium-down productsListHeader">
		<div class="medium-6 columns print-4">
			<h3><bbbl:label key="lbl_cartdetail_item" language="${language}"/></h3>
		</div>
		<div class="medium-6 columns print-8">
			<div class="row">
				<div class="medium-3 columns">
					<h3><bbbl:label key="lbl_cartdetail_quantity" language="${language}"/></h3>
				</div>
				<div class="medium-3 columns">
					<h3><bbbl:label key="lbl_cartdetail_unitprice" language="${language}"/></h3>
				</div>
				<div class="medium-4 columns">
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
				<dsp:oparam name="output">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="itemsByShipTime" />
						<dsp:param name="elementName" value="groupedItemsList" />
						<dsp:oparam name="output">

							<%-- shipping group header --%>
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
							<%-- KP COMMENT END --%>
                            <div class="cart-items">
							<%-- display each item in the shipping group --%>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="groupedItemsList" />
								<dsp:param name="elementName" value="commerceItem" />
								<dsp:oparam name="output">

									<c:set var="itemFlagoff" value="${false}"/>
									<c:set var="disableLink" value="${false}"/>
									<dsp:getvalueof var="commItem" param="commerceItem"/>
									<dsp:getvalueof var="arraySize" param="size" />
									<dsp:getvalueof var="currentCount" param="count" />
									<c:set var="lastRow" value="" />
									<c:if test="${arraySize eq currentCount}">
										<c:set var="lastRow" value="lastRow" />
									</c:if>
									<c:if test="${commItem.stockAvailability ne 0}">
										<c:set var="isOutOfStock" value="${true}"/>
									</c:if>
									<dsp:getvalueof id="priceMessageVO" value="${commItem.priceMessageVO}" />

									<c:if test="${not empty priceMessageVO}">
										<c:set var="itemFlagoff" value="${priceMessageVO.flagOff}"/>
										<c:set var="disableLink" value="${priceMessageVO.disableLink}"/>
									</c:if>
									<dsp:getvalueof var="count" param="count"/>
									<dsp:param name="bbbItem" param="commerceItem.BBBCommerceItem.shippingGroupRelationships"/>
									<dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
									<dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
									<dsp:getvalueof id="productIdsCertona" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
									 <c:if test="${commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem'}">
                                		<dsp:getvalueof var="kirsch" value="${commItem.BBBCommerceItem.kirsch}"/>
                                   		<dsp:getvalueof var="cmo" value="${commItem.BBBCommerceItem.CMO}" scope="request"/>
                                   		<dsp:getvalueof var="tbsItemInfo" value="${commItem.BBBCommerceItem.TBSItemInfo }"/>
                                    </c:if>
									<c:set var="ship_method_avl" value="${true}"/>
									<dsp:getvalueof var="referenceNumber" param="commerceItem.BBBCommerceItem.referenceNumber" />
                                    <dsp:getvalueof var="personalizationOptions" param="commerceItem.BBBCommerceItem.personalizationOptions" />
                                	<dsp:getvalueof var="personalizePrice" param="commerceItem.BBBCommerceItem.personalizePrice" />
                                	<dsp:getvalueof var="personalizationDetails" param="commerceItem.BBBCommerceItem.personalizationDetails" />
                                	<dsp:getvalueof var="fullImagePath" param="commerceItem.BBBCommerceItem.fullImagePath" />
                                	<dsp:getvalueof var="thumbnailImagePath" param="commerceItem.BBBCommerceItem.thumbnailImagePath" />
                                	<dsp:getvalueof var="personalizationType" param="commerceItem.BBBCommerceItem.auxiliaryData.catalogRef.personalizationType" />

                                	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
                                		<dsp:param name="personalizationOptions" value="${personalizationOptions}"/>
                                		<dsp:oparam name="output">
                                			 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
                                		</dsp:oparam>
                                	 </dsp:droplet>

									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="bbbItem"/>
										<dsp:param name="elementName" value="shipGrp" />
										<dsp:oparam name="output">
											<dsp:getvalueof id="oldShippingId" param="shipGrp.shippingGroup.id"/>
											<dsp:getvalueof var="shippingMethod" param="shipGrp.shippingGroup.shippingMethod"/>
											<dsp:getvalueof var="shippingState" param="shipGrp.shippingGroup.shippingAddress.state"/>
											<c:if test="${empty shippingMethod}" >
												<c:set var="ship_method_avl" value="${false}"/>
												<c:set var="displayDeliverySurMayApply" value="${true}"/>
												<c:set var="shipmethodAvlForAllLtlItem" value="${false}"/>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>

									<div class="small-12 columns no-padding cart-item" id="cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" data-ship-group="${key}">

										<%-- item --%>
										<div class="small-12 large-6 columns print-4">
											<div class="small-12 columns no-padding-left">
												<c:if test="${not empty commItem.BBBCommerceItem.registryId}">
													<span>From
														<dsp:getvalueof param="order.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
														<c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
															${registratantVO.primaryRegistrantFirstName}
															<c:if test="${not empty registratantVO.coRegistrantFirstName}"> &amp; ${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
														</c:if>
													</span>
													<dsp:droplet name="BBBCartRegistryDisplayDroplet">
														<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}" />
														<dsp:param name="order" param="order" />
														<dsp:oparam name="regOutput">
															<dsp:getvalueof var="registrantEmail" param="registrantEmail"/>
															<span><dsp:valueof param="registryType"/></span>
															<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</div>
											<div class="row">

												<dsp:getvalueof var="image" param="commerceItem.skuDetailVO.skuImages.mediumImage"/>
												<dsp:getvalueof var="skuUpc" value="${commItem.skuDetailVO.upc}" />
												<dsp:getvalueof var="skuColor" value="${commItem.skuDetailVO.color}" />
												<dsp:getvalueof var="skuSize"  value="${commItem.skuDetailVO.size}" />

												<c:set var="CertonaContext" scope="request">${CertonaContext}<dsp:valueof param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>;</c:set>
												<dsp:droplet name="CanonicalItemLink">
													<dsp:param name="id" param="commerceItem.BBBCommerceItem.repositoryItem.productId" />
													<dsp:param name="itemDescriptorName" value="product" />
													<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
													<dsp:oparam name="output">

														<dsp:droplet name="BBBPriceDisplayDroplet">
															<dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
															<dsp:param name="profile" bean="Profile"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
																<dsp:getvalueof var="clearancePrice" param="clearancePrice"/>
															</dsp:oparam>
														</dsp:droplet>

														<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
														<c:if test="${itemFlagOff or disableLink}">
															<c:set var="finalUrl" value="#"/>
														</c:if>

														<%-- image --%>
														<div class="small-6 columns left hide-for-print">
															<div class="category-prod-img">
																<c:choose>
																	<c:when test="${itemFlagoff or disableLink}">
																		<c:choose>
																		  <c:when test="${not empty referenceNumber}">
																		     <c:choose>
																			     <c:when test="${not empty fullImagePath}">
																			     <img src="${fullImagePath}" width="146" height="146" alt="${commItem.skuDetailVO.displayName}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                                 <div class="zoomin-link-wrapper">
																						<a href="#" alt="<bbbl:label key="lbl_click_larger_image" language ="${pageContext.request.locale.language}"/>" class="zoomin-link" data-reveal-id="previewImageModalPopup_${commItem.BBBCommerceItem.id}">
	   																						<span class="zoomin-icon"></span>
																							<span class="zoomin-label"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></span>
																						</a>
																					 </div>
																				<div id="previewImageModalPopup_${commItem.BBBCommerceItem.id}" class="reveal-modal small previewImageModalPopup" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal="">
                                                                                           <img src="${fullImagePath}" alt="${commItem.skuDetailVO.displayName}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"/>
                                                                                               <a class="close-reveal-modal" aria-label="Close">&#215;</a>
                                                                                 </div>

																				</c:when>
																				<c:otherwise>
																						<img id="katoriFlag" width="63" height="63" title="${skuName}" alt="${skuName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="fl" />
																				</c:otherwise>
																			</c:choose>
																	        </c:when>
																			<c:when test="${empty image || 'null' == image}">
																				<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																			</c:when>
																			<c:otherwise>
																				<img src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																		 <c:when test="${not empty referenceNumber}">
																	     <c:choose>
																		     <c:when test="${not empty fullImagePath}">
																		     <img src="${fullImagePath}" width="146" height="146" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                            <div class="zoomin-link-wrapper">
																						 <a href="#" alt="<bbbl:label key="lbl_click_larger_image" language ="${pageContext.request.locale.language}"/>" class="zoomin-link" data-reveal-id="previewImageModalPopup_${commItem.BBBCommerceItem.id}">
			 																					<span class="zoomin-icon"></span>
																								<span class="zoomin-label"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></span>
																							</a>
																			 </div>
                                                                             <div id="previewImageModalPopup_${commItem.BBBCommerceItem.id}" class="reveal-modal small previewImageModalPopup" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal="">
                                                                                             <img src="${fullImagePath}" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"/>
                                                                                             <a class="close-reveal-modal" aria-label="Close">&#215;</a>
                                                                             </div>

																			</c:when>
																			<c:otherwise>
																					<img id="katoriFlag" width="63" height="63" title="${skuName}" alt="${skuName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="fl" />
																			</c:otherwise>
																		</c:choose>
																        </c:when>
																			<c:when test="${kirsch or cmo}">
																				<%--<c:choose>
																					<c:when test="${tbsItemInfo ne null}">
																						<dsp:getvalueof var="kirschCmoImg" value="${tbsItemInfo.productImage}"/>
																						<img class="fl productImage noImageFound" src="${kirschCmoImg}" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																					</c:when>
																					<c:otherwise> --%>
																						<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																					<%--</c:otherwise>
																				</c:choose> --%>
																			</c:when>
																			<c:otherwise>
																				<dsp:a iclass="prodImg padLeft_10 block fl" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																				<c:choose>
																					<c:when test="${empty image || 'null' == image}">
																						<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																					</c:when>
																					<c:otherwise>
																						<img class="fl productImage noImageFound" src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName} $${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																					</c:otherwise>
																				</c:choose>
																				</dsp:a>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</div>
														</div>

														<%-- capitalize facet labels --%>
														<c:set var="labelColor"><bbbl:label key='lbl_item_color' language='${pageContext.request.locale.language}'/></c:set>
														<c:set var="labelSize"><bbbl:label key='lbl_item_size' language='${pageContext.request.locale.language}'/></c:set>
														<c:set var="labelColorCapitalize" value="${fn:toUpperCase(fn:substring(labelColor, 0, 1))}${fn:toLowerCase(fn:substring(labelColor, 1,fn:length(labelColor)))}" />
														<c:set var="labelSizeCapitalize" value="${fn:toUpperCase(fn:substring(labelSize, 0, 1))}${fn:toLowerCase(fn:substring(labelSize, 1,fn:length(labelSize)))}" />

														<%-- description --%>
														<div class="small-6 columns print-12 left">
															<c:choose>
																<c:when test="${itemFlagoff or disableLink}">
																	<div class="product-name"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></div>
																	<c:if test="${not empty commItem.BBBCommerceItem.catalogRefId}">
																		<div class="facet hide show-for-print">
																			<strong>SKU: <dsp:valueof value="${commItem.BBBCommerceItem.catalogRefId}" valueishtml="true" /></strong>
																		</div>
																	</c:if>
																	<c:if test='${not empty skuColor}'>
																		<div class="facet">
																			<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
																		</div>
																	</c:if>
																	<c:if test='${not empty skuSize}'>
																		<div class="facet">
																			<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
																		</div>
																	</c:if>
																</c:when>
																<c:otherwise>
																<c:choose>
                                                                  	<c:when test="${kirsch or cmo}">
																		<c:choose>
                                                                           	<c:when test="${cmo}">
	                                                                           	<div class="product-name-cmo">
	                                                                           		<c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" />
	                                                                           	</div>
                                                                           	</c:when>
                                                                           	<c:otherwise>
                                                                           		<div class="product-name">
	                                                                           		<c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" />
	                                                                           	</div>
                                                                           	</c:otherwise>
                                                                        </c:choose>


                                                                  	</c:when>
                                                                  	<c:otherwise>
                                                                  		<div class="product-name">
                                                                         	<dsp:a page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																				<c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" />
																			</dsp:a>
																		</div>
                                                                  	</c:otherwise>
                                                                </c:choose>



																	<c:choose>
                                                                      	<c:when test="${kirsch or cmo}">
                                                                      		<c:if test="${tbsItemInfo  ne null}">
                                                                      			<div class="facet">
                                                                      				<dsp:valueof value="${tbsItemInfo.productDesc}" valueishtml="true"/>

                                                                      			</div>
                                                                      			<c:if test='${not isConfirmation}'>
                                                                      				<a href="#" id="collapsedFacet">Hide Info</a>
                                                                      			</c:if>
                                                                      		</c:if>
                                                                      	</c:when>
                                                                      	<c:otherwise>
                                                                      		<c:if test="${not empty commItem.BBBCommerceItem.catalogRefId}">
																				<div class="facet hide show-for-print">
																					<strong>SKU: <dsp:valueof value="${commItem.BBBCommerceItem.catalogRefId}" valueishtml="true" /></strong>
																				</div>
																			</c:if>
                                                                      		 <c:if test='${not empty skuColor}'>
																				<div class="facet">
																					<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
																				</div>
																			</c:if>
																			<c:if test='${not empty skuSize}'>
																				<div class="facet">
																					<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
																				</div>
																			</c:if>
                                                                      	</c:otherwise>
                                                                      </c:choose>
																</c:otherwise>
															</c:choose>

														<%-- TBXPS-2371 | Now expected delivery date will be displayed for non VDC items as well --%>
															<dsp:getvalueof var="isVdcSku" value="${commItem.BBBCommerceItem.vdcInd}"/>
															<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
																	<dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}"/>
																	<dsp:param name="siteId" value="${currentSiteId}"/>
																	<dsp:param name="shippingMethodCode" value="${shippingMethod}"/>
																	<dsp:param name="shippingState" value="${shippingState}"/>
																	<dsp:param name="requireMsgInDates" value="true"/>
																	<dsp:param name="isFromOrderDetail" value="${isFromOrderDetail}"/>
																	<dsp:param name="order" param="order" />
																	<dsp:param name="isVdcSku" value="${isVdcSku}"/>
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
																		<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
																		<c:set var="vdcOffsetFlag">
																			<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
																		</c:set>
																	</dsp:oparam>
																	<dsp:oparam name="error">
																	</dsp:oparam>
																</dsp:droplet>
																<c:if test="${not empty vdcDelTime }">
																	<div class="facet">Expected delivery <br>${vdcDelTime}</div>
																</c:if>
																<c:if test="${vdcOffsetFlag && not empty offsetDateVDC && !isConfirmation}">
																	<c:if test="${ !commItem.skuDetailVO.ltlItem && commItem.BBBCommerceItem.vdcInd}">
																		<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
																		<jsp:useBean id="placeHolderMapVdcLearnMore" class="java.util.HashMap" scope="request" />
																		<c:set target="${placeHolderMapVdcLearnMore}" property="shipMethod" value="${shippingMethod}" />
																		<c:set target="${placeHolderMapVdcLearnMore}" property="skuId" value="${commItem.skuDetailVO.skuId}" />
																		<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
																			<div class="facet highlightRed vdcOffsetMsg bold">
																				<br>
																				<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
																				<c:set var="lbl_offset_learn_more_link" scope="request"><bbbl:label key="lbl_offset_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/></c:set>
																				<c:set var="lbl_offset_learn_moreTBS" value="${fn:replace(lbl_offset_learn_more_link, '/store/includes','/tbs/includes')}" />
																				<dsp:valueof value="${lbl_offset_learn_moreTBS}" valueishtml="true"/>
																			</div>
																	</c:if>
																</c:if>
															<c:choose>
																<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
																	<c:set var="customizeTxt" value="true"/>
																</c:when>
																<c:otherwise>
																	<c:set var="customizeTxt" value="false"/>
																</c:otherwise>
															</c:choose>
															<c:if test='${not empty referenceNumber && not empty personalizationOptions}'>
															  <div class="personalizationAttributes">
															      ${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
														     <div class="pricePersonalization">
															   <c:choose>
													                <c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
																		<dsp:valueof value="${personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
																	</c:when>
																	<c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
																		<dsp:valueof value="${personalizePrice}" converter="currency"/> <c:choose>
																																	 		<c:when test="${customizeTxt eq true}">
																																	 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																																								language="${pageContext.request.locale.language}"/>
																																	 		</c:when>
																																	 		<c:otherwise>
																																	 			<bbbl:label key="lbl_exim_cr_added_price"
																																								language="${pageContext.request.locale.language}"/>
																																	 		</c:otherwise>
																																	 	</c:choose> 
																	</c:when>
																	<c:when test='${not empty personalizationType && personalizationType == "PB"}'>
																	  <bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																	</c:when>
																</c:choose>
																</div>
											            	</div>
														</c:if>

															<c:if test="${not empty commItem.BBBCommerceItem.registryId}">
																<div class="facet registrant">
																	This is a gift for a Registrant
																</div>
																<div class="facet">
																	 <dsp:getvalueof param="order.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
																	<c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
																		${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName}<c:if test="${not empty registratantVO.coRegistrantFirstName}"> &amp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}</c:if>
																	</c:if>
																</div>
															</c:if>
															<dsp:getvalueof var="orderSiteId" param="order.siteId" />
															<div class="facet">
															<c:if test="${fn:contains(orderSiteId,'TBS') && not empty commItem.BBBCommerceItem.autoWaiveClassification}">
																	<bbbl:label key="lbl_autowaive_item_code" language="<c:out param='${language}'/>"/> ${commItem.BBBCommerceItem.autoWaiveClassification}
															</c:if>
															</div>
														</div>
													</dsp:oparam>
												</dsp:droplet>
											</div>
										</div>

										<div class="small-6 columns print-8">
											<div class="row">

												<%-- quantity --%>
												<div class="small-12 large-3 columns quantity">
													<h3 class="show-for-small"><bbbl:label key="lbl_cartdetail_quantity" language="${language}"/>:&nbsp;<c:out value="${commItem.BBBCommerceItem.quantity}"/></h3>
													<div class="show-for-medium-up"><c:out value="${commItem.BBBCommerceItem.quantity}"/></div>
												</div>

												<%-- prices --%>
												<div class="small-12 medium-9 columns pricing">
													<div class="row">
														<dsp:droplet name="BBBPriceDisplayDroplet">
															<dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
															<dsp:param name="profile" bean="Profile"/>
															<dsp:oparam name="output">

																<dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
																<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
																<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
																<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
																<dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
																<dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
																<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
																<dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
																<dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
																<dsp:getvalueof var="shippingMethodAvl" param="shippingMethodAvl"/>

																<%-- our price --%>
																<div class="small-12 large-4 columns">
																	<h3 class="show-for-small"><bbbl:label key="lbl_cartdetail_unitprice" language="${language}"/>:
																	<c:choose>
																		<c:when test="${isConfirmation}">
																			<fmt:formatNumber value="${unitListPrice}"  type="currency"/>
																		</c:when>
																		<c:otherwise>
																			<a href="#" class="priceOverrideModal" data-reveal-id="priceOverrideModal_${commItem.BBBCommerceItem.id}"><fmt:formatNumber value="${unitListPrice}"  type="currency"/></a>
																		</c:otherwise>
																	</c:choose>&nbsp;</h3>
																	<div class="show-for-medium-up">
																		<c:choose>
																			<c:when test="${isConfirmation}">
																				<fmt:formatNumber value="${unitListPrice}"  type="currency"/>
																			</c:when>
																			<c:otherwise>
																				<a href="#" class="priceOverrideModal" data-reveal-id="priceOverrideModal_${commItem.BBBCommerceItem.id}"><fmt:formatNumber value="${unitListPrice}"  type="currency"/></a>
																			</c:otherwise>
																		</c:choose>
																	</div>
																</div>

																<c:if test="${not isConfirmation}">
																	<div id="priceOverrideModal_${commItem.BBBCommerceItem.id}" class="reveal-modal medium price-override" data-reveal="">
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
																				<input type="text" value="<c:out value='${commItem.skuDetailVO.displayName}' escapeXml='false' />" disabled />
																			</div>
																		</div>
																		<div class="row">
																			<div class="small-12 large-4 columns">
																				<label class="right inline">Quantity</label>
																			</div>
																			<div class="small-12 large-8 columns">
																				<input type="text" value="<c:out value='${commItem.BBBCommerceItem.quantity} 'escapeXml='false' />" disabled />
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
																					<input id="qtyToOverride_${commItem.BBBCommerceItem.id}" class="quantity-input" type="text" maxlength="2" value="${commItem.BBBCommerceItem.quantity}" data-max-value="${commItem.BBBCommerceItem.quantity}" maxlength="2">
																					<a class="button plus secondary"><span></span></a>
																				</div>
																			</div>
																		</div>
																		<div class="row">
																			<div class="small-12 large-4 columns">
																				<label for="reasonList_${commItem.BBBCommerceItem.id}" class="right inline">Reason *</label>
																			</div>
																			<div class="small-12 large-8 columns">
																				<select name="reasonList_${commItem.BBBCommerceItem.id}" id="reasonList_${commItem.BBBCommerceItem.id}" class="reasonList">
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
																				<label for="competitorList_${commItem.BBBCommerceItem.id}" class="right inline">Competitor</label>
																			</div>
																			<div class="small-12 large-8 columns">
																				<select name="competitorList_${commItem.BBBCommerceItem.id}" id="competitorList_${commItem.BBBCommerceItem.id}" class="competitorList">
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
                                                                                <small for="competitorList_${commItem.BBBCommerceItem.id}" class="error hidden" id="errorComp">Please select a value for Competitor</small>
																			</div>
																		</div>
																		<div class="row">
																			<div class="small-12 large-4 columns">
																				<label for="newPrice_${commItem.BBBCommerceItem.id}" class="right inline">New Price *</label>
																			</div>
																			<div class="small-12 large-8 columns">
																				<span class="dollarSignOverride">$</span>
																				<input type="tel" value="0.00" id="newPrice_${commItem.BBBCommerceItem.id}" name="newPrice_${commItem.BBBCommerceItem.id}" class="priceOverride newPrice" maxlength="7" />
																			</div>
																		</div>
																		<div class="row">
																			<div class="small-12 large-offset-4 large-4 columns">
																				<a href='javascript:void(0);' class="button small service expand submit-price-override" data-parent="#priceOverrideModal_${commItem.BBBCommerceItem.id}" data-cid="${commItem.BBBCommerceItem.id}">Override</a>
																			</div>
																			<div class="small-12 large-4 columns">
																				<a href='javascript:void(0);' class="close-modal button small secondary expand">Cancel</a>
																			</div>
																		</div>
																		<a class="close-reveal-modal">&times;</a>
																	</div>
																</c:if>

																<%-- your price --%>
																<div class="small-12 large-5 columns your-price no-padding-right">
																	<h3><bbbl:label key="lbl_you_pay" language="${language}"/>: </h3>
																	<dsp:include page="/cart/cart_includes/your_price.jsp">
																		<dsp:param name="priceInfoVO" param="priceInfoVO"/>
																		<dsp:param name="cItem" value="${commItem.BBBCommerceItem}" />
																	</dsp:include>
																		<c:if test="${commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem' && not empty commItem.BBBCommerceItem.TBSItemInfo && commItem.BBBCommerceItem.TBSItemInfo.overridePrice > 0}">
																			<span class="overrides"><bbbl:label key="lbl_price_override_to" language="<c:out param='${language}'/>"/>
																				<fmt:formatNumber value="${commItem.BBBCommerceItem.TBSItemInfo.overridePrice}"  type="currency"/>
																			</span>
																		</c:if>
																	<c:if test="${unitSavedAmount gt 0.0 || totalSavedAmount gt 0.0 || couponApplied eq 'true'}">
																		<div class="dl-wrap">
																			<dt class="savings">
																				<bbbl:label key="lbl_cartdetail_yousave" language="<c:out param='${language}'/>"/>
																			<!-- </dt>
																			<dd class="savings"> -->
																				<c:choose>
																					<c:when test="${couponApplied ne 'true'}">
																						<c:if test="${totalSavedAmount gt 0.0}">
																							<fmt:formatNumber value="${totalSavedAmount}"  type="currency"/>
																						</c:if>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${unitSavedAmount gt 0.0 && totalSavedAmount gt 0.0 && totalSavedAmount ne unitSavedAmount}">
																								<fmt:formatNumber value="${totalSavedAmount}"  type="currency"/>
																							</c:when>
																							<c:otherwise>
																							    <fmt:formatNumber value="${couponDiscountAmount + totalSavedAmount}"  type="currency"/>
																							</c:otherwise>
																						</c:choose>
																					</c:otherwise>
																				</c:choose>
																			</dt>
																		</div>
																		</c:if>
																		<dsp:droplet name="ForEach">
																		<dsp:param value="${commItem.skuDetailVO.skuAttributes}" name="array" />
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
																	
                                                                      </div>

																<%-- total price --%>
																<div class="small-12 large-3 columns total-price print-right">
																	<h3><bbbl:label key="lbl_cartdetail_totalprice" language="${language}"/>: </h3>
																	<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
																	<fmt:formatNumber value="${totalAmount}"  type="currency"/>
																	<!-- <c:choose>
																		<c:when test="${undiscountedItemsCount eq commItem.BBBCommerceItem.quantity}">
																			<dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/>
																		</c:when>
																		<c:otherwise>
																			<dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/>
																		</c:otherwise>
																	</c:choose> -->
																</div>

															</dsp:oparam>
														</dsp:droplet>
													</div>
												</div>
											</div>
										</div>
									</div>

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
