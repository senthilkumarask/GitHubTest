<dsp:page>

    <%-- Imports --%>
    <%@ page import="com.bbb.constants.BBBCheckoutConstants"%>
    <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
<%--     <dsp:droplet name="RepriceOrderDroplet"> --%>
<%--         <dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/> --%>
<%--     </dsp:droplet> --%>
    <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
    <dsp:importbean bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet"/>
    <dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>

    <%-- Page Variables --%>
    <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof var="cartState" value="${0}"/>
    <c:if test="${currentState ne cartState}">
        <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
    </c:if>
    <dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
    <dsp:getvalueof id="applicationId" bean="Site.id" />
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
    <dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
    <c:set var="lbl_checkout_checkout" scope="page">
        <bbbl:label key="lbl_checkout_checkout" language="${pageContext.request.locale.language}" />
    </c:set>
    <c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <c:set var="bopusOnlyText" value="<%=BBBCheckoutConstants.BOPUS_ONLY %>" scope="page" />
    <c:set var="lbl_cartdetail_movetowishList" scope="page">
        <bbbl:label key="lbl_cartdetail_movetowishList" language="${pageContext.request.locale.language}" />
    </c:set>
    <c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
    <c:set var="CertonaContext" value="" scope="request"/>
    <c:set var="RegistryContext" value="" scope="request"/>
    <c:set var="registryFlag" value="false"/>
    <c:set var="skuFlag" value="false"/>
	<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
    <c:set var="TBS_BedBathUSSite">
        <bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="TBS_BuyBuyBabySite">
        <bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="TBS_BedBathCanadaSite">
        <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>
   	<c:choose>
	<c:when test="${applicationId eq 'TBS_BedBathUS'}">
		<c:set var="mapQuestOn" scope="request">
			<tpsw:switch tagName="MapQuestTag_tbs_us" />
		</c:set>
	</c:when>
	<c:when test="${applicationId eq 'TBS_BuyBuyBaby'}">
		<c:set var="mapQuestOn" scope="request">
			<tpsw:switch tagName="MapQuestTag_tbs_baby" />
		</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="mapQuestOn" scope="request">
			<tpsw:switch tagName="MapQuestTag_tbs_ca" />
		</c:set>
	</c:otherwise>
	</c:choose>
    <dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>

    <c:set var="language" value="<c:out param='${language}' />" />

    <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
       <dsp:oparam name="output">
          <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
       </dsp:oparam>
    </dsp:droplet>

	<dsp:droplet name="BBBSaveForLaterDisplayDroplet">
                        <dsp:oparam name="empty">
                        <div id="sflContentWrapper">
                           <div id="saveForLaterHeader" class=" alpha omega hidden">
                                <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                            </div>

                            <div id="saveForLaterBody" class="savedItems  alpha omega hidden saved-item hide-for-medium-down">
                                <div id="saveForLaterEmptyMessaging" class="alpha omega">
                                </div>
                            </div>
							</div>
                        </dsp:oparam>
                        <dsp:oparam name="output">
                         <div id="sflContentWrapper">
                            <div id="saveForLaterHeader" class=" alpha omega">
                                <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                            </div>

                            <div id="saveForLaterBody" class="savedItems  alpha omega saved-item hide-for-medium-down">
                                <div id="saveForLaterContent" class="alpha omega productListWrapper">
                                    <div class="row productsListHeader noBorderBot  hide-for-small">
                                        <div class="medium-5 columns wishlistItemDetails "><h3><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></h3></div>
                                        <div class="medium-4 columns wishlistQuantityDetails "><h3><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></h3></div>
                                        <div class="medium-3 columns wishlistTotalDetails "><h3><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></h3></div>
                                    </div>

									</br>
                                    <dsp:include page="saveForlaterCartForms.jsp"/>
                                    <dsp:form formid="frmSavedItems" iclass="frmAjaxSubmit  frmSavedItems" method="post" action="ajax_handler_cart.jsp">
                                    <div class="productsListContent ">

                                        <dsp:droplet name="CertonaDroplet">
                                             <dsp:param name="scheme" value="fc_lmi"/>
                                             <dsp:param name="context" value="${CertonaContext}"/>
                                             <dsp:param name="exitemid" value="${RegistryContext}"/>
                                             <dsp:param name="userid" value="${userId}"/>
                                             <dsp:param name="number" value="${lastMinItemsMax}"/>
                                             <dsp:param name="siteId" value="${applicationId}"/>
                                             <dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
                                             <dsp:oparam name="output">
                                             <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" scope="request"/>
                                             </dsp:oparam>
                                        </dsp:droplet>
                                        <dsp:droplet name="ForEach">
                                            <dsp:param name="array" param="giftList" />
                                            <dsp:param name="reverseOrder" value="true" />
                                            <dsp:oparam name="outputStart">
                                            </dsp:oparam>
                                            <dsp:oparam name="output">
                                                <c:set var="flagOff" value="${false}"/>
                                                <c:set var="bopusoff" value="${false}"/>
                                                <dsp:getvalueof var="arraySize" param="size" />
                                                <dsp:getvalueof var="currentCount" param="count" />
                                                <c:set var="lastRow" value="" />

                                                <c:if test="${arraySize eq currentCount}">
                                                <c:set var="lastRow" value="lastRow" />
                                                </c:if>

                                                <dsp:getvalueof var="registryId" param="element.registryID" />
                                                <dsp:getvalueof var="skuID" param="element.skuID" />
                                                <dsp:getvalueof var="quantity" param="element.quantity" />
                                                <dsp:getvalueof var="wishListItemId" param="element.wishListItemId" />
                                                <dsp:getvalueof var="commerceItemId" param="element.commerceItemId" />
                                                <dsp:getvalueof var="priceMessageVO" param="element.priceMessageVO" />
                                                <dsp:getvalueof var="prodID" param="element.prodID" />
                                                <dsp:getvalueof var="giftListId" param="element.giftListId" />
                                                <dsp:getvalueof var="btsValue" param="element.bts" />
                                                <dsp:getvalueof var="referenceNumber" param="element.referenceNumber" />
                								<dsp:getvalueof var="personalizePrice" param="element.personalizePrice" />
                								<dsp:getvalueof var="personalizationDetails" param="element.personalizationDetails" />
                								<dsp:getvalueof var="personalizationOptions" param="element.personalizationOptions" />
                								<dsp:getvalueof var="personalizationOptionsDisplay" param="element.personalizationOptionsDisplay" />
                								<dsp:getvalueof var="personalizationStatus" param="element.personalizationStatus" />
                								<dsp:getvalueof var="fullImagePath" param="element.fullImagePath" />
                								<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath" />
                								<c:choose>
                									<c:when test="${not empty referenceNumber && personalizationStatus eq 'saved'}">
                										<c:set var="isPersonalizationIncomplete" value="true" />
                									</c:when>
                									<c:otherwise>
                										<c:set var="isPersonalizationIncomplete" value="false" />
                									</c:otherwise>
                								</c:choose>
                								<c:choose>
												<c:when test="${not empty referenceNumber}">
													<c:set var="isPersonalized" value="true" />
												</c:when>

												<c:otherwise>
													<c:set var="isPersonalized" value="false" />
												</c:otherwise>
											</c:choose>
                                                <c:if test="${not empty priceMessageVO}">
                                                    <c:set var="bopusoff" value="${not priceMessageVO.bopus}"/>
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${not empty priceMessageVO and priceMessageVO.flagOff}">
                                                        <c:set var="flagOff" value="${true}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="flagOff" value="${false}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:set var="displaySize" >
                                                    <bbbc:config key="sfl_display_size" configName='CartAndCheckoutKeys' />
                                                 </c:set>
                                                 <c:if test="${empty displaySize}">
                                                    <c:set var="displaySize" >
                                                        2
                                                     </c:set>
                                                 </c:if>
                                                <c:if test="${currentCount == displaySize+1 && arraySize > displaySize}">
                                                    <div id="savedItemID_BTN" class="savedItemRow showAllBtn">
                                                        <div class="prodItemWrapper ">
                                                            <div class="button">
                                                                <input id="showAllBtn" type="button" value="Show all ${arraySize} items" />
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>


                                                <div id="savedItemID_${wishListItemId}" class=" row ${lastRow} <c:if test="${not empty registryId}">registeryItem</c:if> savedItemRow saved-item changeStoreItemWrap  <c:if test="${currentCount > displaySize && arraySize > displaySize}">hidden</c:if>">
                                                <c:if test="${not empty personalizationOptions}">
             								       <c:set var="cusDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>
             								       <input type="hidden" value="${cusDet}" id="customizationDetails"/>
             							        </c:if>
                                                <input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="prodId" name="productId">
                                                    <input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
                                                    <c:if test="${flagOff}">
                                                        <input type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
                                                    </c:if>

                                                            <dsp:getvalueof var="productVO" param="element.productVO" />
                                                            <dsp:droplet name="ForEach">
                                                        <dsp:param param="element.productVO.rollupAttributes" name="array" />
                                                                <dsp:oparam name="output">
                                                                    <dsp:getvalueof var="menu" param="key"/>
                                                                    <c:if test="${menu eq 'FINISH'}">
                                                                        <c:set var="rollupAttributesFinish" value="true" />
                                                                    </c:if>
                                                                </dsp:oparam>
                                                    </dsp:droplet>
                                                    <dsp:droplet name="SKUWishlistDetailDroplet">
                                                        <dsp:param name="siteId" value="${applicationId}"/>
                                                        <dsp:param name="skuId" value="${skuID}"/>
                                                        <dsp:oparam name="output">
                                                            <dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
                                                            <dsp:getvalueof var="skuImage" param="pSKUDetailVO.skuImages.mediumImage"/>
                                                            <dsp:getvalueof var="skuDisplayName" param="pSKUDetailVO.displayName" />
                                                            <dsp:getvalueof var="skuImageURL" param="pSKUDetailVO.skuImages.mediumImage" />
                                                            <dsp:getvalueof var="skuColor" param="pSKUDetailVO.color" />
                                                            <dsp:getvalueof var="skuSize" param="pSKUDetailVO.size" />
                                                            <dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
                                                            <dsp:getvalueof var="customizableRequired" param="pSKUDetailVO.customizableRequired" />
                										    <dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType" />
                										    <dsp:getvalueof var="customizableCodes" param="pSKUDetailVO.customizableCodes" />
															<dsp:getvalueof var="inCartFlag" param="pSKUDetailVO.inCartFlag" />
                                                        </dsp:oparam>
                                                    </dsp:droplet>

                                                    <dsp:getvalueof var="prodImage" value="${skuImageURL}" />
                                                    <dsp:getvalueof var="pName" value="${skuDisplayName}" />

                                                    <dsp:include page="itemLinkWishlist.jsp">
                                                        <dsp:param name="id" value="${wishListItemId}"/>
                                                        <dsp:param name="registryIdSavedItem" value="${registryId}"/>
                                                        <dsp:param name="image" value="${prodImage}"/>
                                                        <dsp:param name="displayName" value="${pName}"/>
                                                        <dsp:param name="priceMessageVO" value="${priceMessageVO}"/>
                                                        <dsp:param name="isPersonalized" value="${isPersonalized}" />
        												<dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}" />
                                                    </dsp:include>


                                                        <c:if test="${not empty registryId}">
                                                            <c:set var="registryUrl" value="../giftregistry/view_registry_guest.jsp"/>
                                                            <c:if test='${fn:contains(userActiveRegList, registryId)}'>
                                                                <c:set var="registryUrl" value="../giftregistry/view_registry_owner.jsp"/>
                                                            </c:if>

                                                              <dsp:getvalueof var="registryVO" param="element.registryVO"/>
                                                                          <dsp:droplet name="GetRegistryTypeNameDroplet">
                                                                            <dsp:param name="siteId" value="${applicationId}"/>
                                                                            <dsp:param name="registryTypeCode" value="${registryVO.registryType.registryTypeName}"/>
                                                                            <dsp:oparam name="output">
                                                                                <c:set var="registryTypeName"><dsp:valueof param="registryTypeName"/></c:set>
                                                                            </dsp:oparam>
                                                                          </dsp:droplet>
                                                                          <div class="registeryItemHeader">
                                                                            <h3>
                                                                                 <span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
                                                                                <dsp:a href="${registryUrl}">
                                                                                <dsp:param name="registryId" value="${registryId}"/>
                                                                                <dsp:param name="eventType" value="${registryTypeName}" />
                                                                                ${registryVO.primaryRegistrant.firstName}<c:if test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
                                                                                </dsp:a>
                                                                                <span>${registryTypeName}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
                                                                            </h3>

                                                                          </div>
                                                       </c:if>

                                                        <dsp:getvalueof var="ltlShipMethod"	param="element.ltlShipMethod" />
                                                        <%-- LTL Alert  --%>
                                                        <c:if test="${isLtlFlag && empty ltlShipMethod}" >

                                                            <div class="ltlItemAlert alert alert-info">
                                                                <bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
                                                            </div>
                                                        </c:if>

                                                        <%-- LTL Alert  --%>
                                                        <div class="small-12 columns  no-padding">
                                                            <div class="">
																<div  class="small-12 large-5 columns no-padding">
                                                                <div class="wishlistItemDetails  large-12 columns no-padding">
                                                                <dsp:droplet name="CanonicalItemLink">
                                                                    <dsp:param name="id" value="${prodID}" />
                                                                    <dsp:param name="itemDescriptorName" value="product" />
                                                                    <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                                                    <dsp:oparam name="output">
                                                                        <c:choose>
                                                                            <c:when test="${not flagOff}">
                                                                                <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="#" />
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </dsp:oparam>
                                                                </dsp:droplet>


                                                                    <c:choose>
                                                                        <c:when test="${not flagOff}">
                                                                            <dsp:a iclass="small-4 large-5 columns prodImg  block" title="${pName}" page="${finalUrl}?skuId=${skuID}">
                                                                            <c:choose>
                    														<c:when test="${not empty referenceNumber}">
                    															<c:choose>
                    																<c:when test="${not empty thumbnailImagePath && enableKatoriFlag}">
                    																	  <img class="fl productImage" src="${thumbnailImagePath}" alt="${pName}" title="${pName}" height="146" width="146" />
                    																 </c:when>
                    																 <c:otherwise>
                    																	 <img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${pName}" title="${pName}" height="146" width="146" />
                    																 </c:otherwise>
                    															</c:choose>
                    														</c:when>
                    														<c:otherwise>
                    															<c:choose>
                    																<c:when test="${empty prodImage}">
                    																	<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage"/>
                    																</c:when>
                    																<c:otherwise>
                    																	<img src="${scene7Path}/${prodImage}" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage noImageFound"/>
                    																</c:otherwise>
                    															</c:choose>
                    														</c:otherwise>
                    													</c:choose>
                                                                            </dsp:a>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="fl padLeft_10 block">
                                                                            <c:choose>
                    														<c:when test="${not empty referenceNumber}">
                    															<c:choose>
                    																<c:when test="${not empty thumbnailImagePath && enableKatoriFlag}">
                    																	  <img class="fl productImage" src="${thumbnailImagePath}" alt="${pName}" title="${pName}" height="146" width="146" />
                    																 </c:when>
                    																 <c:otherwise>
                    																	 <img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${pName}" title="${pName}" height="146" width="146" />
                    																 </c:otherwise>
                    															</c:choose>
                    														</c:when>
                    														<c:otherwise>
                    															<c:choose>
                    																<c:when test="${empty prodImage}">
                    																	<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage"/>
                    																</c:when>
                    																<c:otherwise>
                    																	<img src="${scene7Path}/${prodImage}" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage noImageFound"/>
                    																</c:otherwise>
                    															</c:choose>
                    														</c:otherwise>
                    													</c:choose>
                                                                            </span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <c:if test="${empty pName}">
                                                                        <dsp:getvalueof var="pName" value="${skuDisplayName}" />
                                                                    </c:if>

                                                                    <span class="small-8 large-7 columns prodInfo block breakWord">
                                                                        <div class="prodName product-name">
                                                                           <c:choose>
                                                                                <c:when test="${not flagOff}">
                                                                                    <dsp:a page="${finalUrl}?skuId=${skuID}" iclass="productName" title="${productVO.name}">${pName}</dsp:a>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <span class="productName disableText">${pName}</span>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                            <dsp:getvalueof var="pName" value="" />
                                                                            <dsp:getvalueof var="prodImage" value="" />
                                                                        </div>
                                                                        <div class="prodAttribsSavedItem facet">
                                                                            <c:if test='${not empty skuColor}'><c:choose><c:when test="${rollupAttributesFinish == 'true'}"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : </c:when><c:otherwise><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : </c:otherwise></c:choose><dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
                                                                            <c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
                                                                            <c:set var="rollupAttributesFinish" value="false" />
                                                                        </div>
                                                                        <div class="skuNum facet"><bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${skuID}</div>

                                                                        <div class="prodDeliveryInfo squareBulattedList facet">

                                                                         <dsp:droplet name="IsProductSKUShippingDroplet">
                                                                          <dsp:param name="siteId" value="${applicationId}"/>
                                                                          <dsp:param name="skuId" value="${skuID}"/>
                                                                          <dsp:param name="prodId" value="${prodID}"/>
                                                                          <dsp:oparam name="true">

                                                                              <dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
                                                                              <c:forEach var="item" items="${restrictedAttributes}">
                                                                                  <div>
                                                                                      <c:choose>
                                                                                          <c:when test="${null ne item.actionURL}">
                                                                                              <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
                                                                                          </c:when>
                                                                                          <c:otherwise>
                                                                                              ${item.attributeDescrip}
                                                                                          </c:otherwise>
                                                                                      </c:choose>
                                                                                  </div>
                                                                              </c:forEach>
                                                                          </dsp:oparam>
                                                                          <dsp:oparam name="false">

                                                                          </dsp:oparam>
                                                                      </dsp:droplet>
                                                                      </div>
                                                                      <c:if test='${enableKatoriFlag && not empty personalizationOptions && personalizationOptions != null}'>
                                                                      <div class="personalizationAttributes">
                                                                       <span class="exim-code">
                                                                            ${eximCustomizationCodesMap[personalizationOptions]} :
                                                                       </span>
                                                                        <c:if test="${isPersonalizationIncomplete eq 'false'}">
      																	      <input type="hidden" class="editPersonalization"  data-custom-vendor="${productVO.vendorInfoVO.vendorName}" data-sku="${skuVO.skuId}" data-refnum="${referenceNumber}" data-quantity="${quantity}" aria-hidden="true"></input>
      																	</c:if>	
                                                                        <span class="exim-detail">
                                                                        	${personalizationDetails}
                                                                        </span>
																		<c:choose>
																			<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
																				<c:set var="editPersonalizeTxt">
																					<bbbl:label key="lbl_sfl_customization_edit" language="<c:out param='${language}'/>"/>
																				</c:set>
																			</c:when>
																			<c:otherwise>
																				<c:set var="editPersonalizeTxt">
																					<bbbl:label key="lbl_sfl_personalization_edit" language="<c:out param='${language}'/>"/>
																				</c:set>
																			</c:otherwise>
																		</c:choose>

                                                                           <dsp:setvalue beanvalue="Profile.wishlist"
      	        																param="wishlist" />
      	        																<dsp:getvalueof id="wishlistId" param="wishlist.id" />
      	        																<c:if test="${isPersonalizationIncomplete}">
      	        															  <div class="personalizeLinks">
      	        																	<a href="#" class="editPersonalizationSfl bold" title="${editPersonalizeTxt}"
      	        	                                                                   	data-sku="${skuVO.skuId}" data-custom-vendor="${productVO.vendorInfoVO.vendorName}" data-refnum="${referenceNumber}" data-quantity="${quantity}" data-wishlistId="${wishlistId}" data-wishlistitemId="${wishListItemId}">
      	        																	   ${editPersonalizeTxt}</a>
      	        	                                                           </div>
      	        																</c:if>
                                                                      </div>
																	  </c:if>
                                                                        <c:if test="${BazaarVoiceOn}">
                                                                            <dsp:getvalueof var="reviews" value="${productVO.bvReviews.totalReviewCount}"/>
                                                                            <dsp:getvalueof var="ratings" value="${productVO.bvReviews.averageOverallRating}" vartype="java.lang.Integer"/>
                                                                            <dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>

                                                                            <c:choose>
                                                                                <c:when test="${empty productVO}">
                                                                                    <dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <dsp:getvalueof var="totalReviewCount" value="${productVO.bvReviews.totalReviewCount}"></dsp:getvalueof>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                            <c:if test = "${not flagOff}">
                                                                                <c:choose>
                                                                                    <c:when test="${ratings ne null && ratings ne '0' && (reviews eq '1' || reviews gt '1') }">
                                                                                        <div class="prodReview facet  prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
                                                                                        <dsp:a  page="${finalUrl}?skuId=${skuID}&categoryId=${CategoryId}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
                                                                                        </div>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <div class="prodReview facet  prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
                                                                                        <dsp:a  page="${finalUrl}?skuId=${skuID}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
                                                                                        </div>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </c:if>
                                                                        </c:if>


                                                                        <c:set var="itemPrice" />
                                                                        <dsp:droplet name="ListPriceSalePriceDroplet">
        																<dsp:param name="productId" param="element.prodID" />
        																<dsp:param name="skuId" param="element.skuID" />
        																<dsp:oparam name="output">
        																	<dsp:getvalueof var="listPriceD" param="listPrice" />
        																	<dsp:getvalueof var="salePriceD" param="salePrice" />
        																	<dsp:getvalueof var="inCartPriceD" param="inCartPrice" />
        																</dsp:oparam>
        															</dsp:droplet>
        															   <c:choose>
        																<c:when test="${(not empty salePriceD) && (salePriceD gt 0.0)}">
        																	<dsp:getvalueof var="itemPriceD" value="${salePriceD}"
        																		vartype="java.lang.Double" />
        																	<c:set var="itemPrice" value="${salePriceD}"/>
        																</c:when>
        																<c:otherwise>
        																	<dsp:getvalueof var="itemPriceD" value="${listPriceD}"
        																		vartype="java.lang.Double" />
        																	<c:set var="itemPrice" value="${listPriceD}"/>
        																</c:otherwise>
        															</c:choose>

																	<%--BBBH-2890:start --%>
																	<c:if test="${inCartFlag}">
																		<c:set var="itemPrice" value="${inCartPriceD}"/>
																	</c:if>
																	<%-- end --%>

        															<dsp:getvalueof var="itemPriceD" value="${itemPrice}" vartype="java.lang.Double"/>

                                                                        <c:choose>
        																  <c:when test="${personalizationType eq 'PY'}">
        																	<dsp:getvalueof var="PersonalPrice"
        																		value="${personalizePrice + itemPriceD}"
        																		vartype="java.lang.Double" />
        																  </c:when>
        																<c:when test="${personalizationType eq 'CR'}">
        																	<dsp:getvalueof var="PersonalPrice"
        																		value="${personalizePrice}" vartype="java.lang.Double" />
        																</c:when>
        																<c:otherwise>
        																	<dsp:getvalueof var="PersonalPrice"
        																		value="${itemPriceD}" vartype="java.lang.Double" />
        																</c:otherwise>
        															</c:choose>


        															 <c:set var="PersonalizedPrice" value="${PersonalPrice}" />


        															<c:choose>
        															<c:when test="${not empty referenceNumber}">
																	 <c:choose>
																	 		<c:when test="${!enableKatoriFlag}">
																	 		<span class="prodPrice">
																	 			<bbbl:label key="lbl_cart_tbd" language ="${pageContext.request.locale.language}"/>
																	 		</span>
																	 		</c:when>
																	 		<c:when test="${isPersonalizationIncomplete eq 'true' && not empty personalizationType &&  personalizationType eq 'CR' && PersonalizedPrice <= 0.01}">
																			<span class="prodPrice">
																			   <bbbl:label key='lbl_price_is_tbd_tbs' language="${pageContext.request.locale.language}"/>
																			</span>
																			</c:when>
																			<c:otherwise>
																			<div class="personalizationAttributes">
																				<span class="prodPrice"><dsp:valueof value="${PersonalizedPrice}" number="0.00" converter="currency"/></span>

																				<c:if test="${isPersonalizationIncomplete eq 'true' &&  personalizationType != 'PB'}">
																					<span class= "pricePersonalization"><bbbl:label key="lbl_sfl_personalization_price" language="<c:out param='${language}'/>"/></span>
																				</c:if>
																			</div>
																			</c:otherwise>
																	 	</c:choose>
															           </c:when>
															         <c:otherwise>
																        <span class="prodPrice"><dsp:valueof value="${PersonalizedPrice}" number="0.00" converter="currency"/></span>
															         </c:otherwise>
														      </c:choose>
        															<c:if test="${enableKatoriFlag &&  not empty referenceNumber && referenceNumber != null}">
      			                                                            	<div class="personalizationAttributes">
      	        																  <c:if test="${not empty personalizationOptions && personalizationOptions != null}">
      	        																<div class="pricePersonalization">
      	        																  <c:choose>
      	        														              <c:when test='${isPersonalizationIncomplete eq false  && not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
      	        																				<dsp:valueof value="${personalizePrice}" number="0.00" converter="currency"/>&nbsp;<bbbl:textArea key="txt_sfl_personal_price_msg" language="<c:out param='${language}'/>"/>
      	        																			</c:when>
      	        																			<c:when test='${isPersonalizationIncomplete eq false  && not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
      	        																				<dsp:valueof value="${personalizePrice}" number="0.00" converter="currency"/>&nbsp;<c:choose>
																																														<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
																																															<bbbl:textArea key="txt_pdp_cr_customize_katori_price"
																																																	language="<c:out param='${language}'/>" />
																																														</c:when>
																																														<c:otherwise>
																																															<bbbl:textArea key="txt_pdp_cr_katori_price"
																																																	language="<c:out param='${language}'/>" />
																																														</c:otherwise>
																																													</c:choose>
      	        																			</c:when>

      	        																			<c:when test='${not empty personalizationType && personalizationType == "PB"}'>
      	        																				<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
      	        																			</c:when>
      	        																		</c:choose>
      	        																	</div>
      	        																</c:if>
      	        																</div>
      	        															</c:if>
                                                                </span>
                                                                </div>
                                                            </div>



																<div class="small-6 large-4 columns">
                                                                    <div class="row prodDeliveryInfo padLeft_10 padTop_10">
                                                                    <c:if test="${not flagOff}">
                                                                            <div class="small-9 large-5 columns quantity">
																			<div class="qty-spinner quantityBox">
																			<a title="Decrease Quantity" class="button minus secondary">
																			<span></span></a>
                                                                            <input data-max-value="99" name="${wishListItemId}" aria-required="true" aria-labelledby="lblquantity_text_${skuID}" type="text" value="${quantity}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" id="quantity_text_${skuID}" maxlength="2" class="moveToCartData itemQuantity fl quantity-input"/>
																			<a title="Increase Quantity" class="button plus secondary">
																			<span></span></a>
																			</div>
																			<div class="small-7 large-5 columns qty-actions no-padding">
																			<a title="Update" data-parent-row="#cartItemID_DC1ci408000046" data-submit-button="#btnUpdateDC1ci408000046" class="qty-update no-padding" href="#">Update</a>
																			<input type="hidden" value="DC1ci408000046" name="/atg/commerce/order/purchase/CartModifierFormHandler.commerceItemId">
																			<input type="hidden" value=" " name="_D:/atg/commerce/order/purchase/CartModifierFormHandler.commerceItemId">
																			<input type="hidden" value=" " name="_D:/atg/commerce/order/purchase/CartModifierFormHandler.setOrderSuccessURL">
																				<%-- <input type="hidden" value="/tbs/cart/ajax_handler_cart.jsp" name="/atg/commerce/order/purchase/CartModifierFormHandler.setOrderSuccessURL">
																			<input type="hidden" value="/tbs/cart/ajax_handler_cart.jsp" name="/atg/commerce/order/purchase/CartModifierFormHandler.setOrderErrorURL">  --%>
																			<dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="cartBodySFL" />
																			<input type="hidden" value=" " name="_D:/atg/commerce/order/purchase/CartModifierFormHandler.setOrderErrorURL">
																			<input type="submit" class="hidden" name="btnUpdate" id="btnUpdateDC1ci408000046">
																			<input type="hidden" value=" " name="_D:btnUpdate"> <a title="Remove" onclick="omniRemove('1041235058','41235058')" data-parent-row="#cartItemID_DC1ci408000046" data-ajax-frmid="frmCartItemRemove" class="remove-item no-padding" href="#">Remove</a>
																			</div>
																			</div>
                                                                    </c:if>
                                                                        <dsp:input bean="GiftlistFormHandler.fromCartPage" type="hidden" value="true" />
                                                                        <dsp:input bean="GiftlistFormHandler.productId" type="hidden" value="${prodID}" />
                                                                        <dsp:input bean="GiftlistFormHandler.catalogRefIds" type="hidden" value="${skuID}" />
                                                                        <%-- Client DOM XSRF | Part -2
                                                                        <dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="/tbs/cart/ajax_handler_cart.jsp" />
                                                                        <dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="/tbs/cart/ajax_handler_cart.jsp" /> --%>
                                                                        <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="updateGiftlist" />
                                                                        <dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
                                                                        <dsp:setvalue paramvalue="wishlist.id" param="giftlistId" />
                                                                        <dsp:input bean="GiftlistFormHandler.giftlistId" value="${giftListId}" type="hidden" />
                                                                        <dsp:input bean="GiftlistFormHandler.currentItemId" value="${wishListItemId}" type="hidden" />
                                                                        <c:if test="${not flagOff}">
                                                                        </c:if>
                                                                        <dsp:getvalueof id="giftlistId" param="wishlist.id"/>
                                                                        <input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                                        <input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
                                                                    </div>

                                                                    <dsp:input bean="GiftlistFormHandler.updateGiftlistItems" name="btnUpdate" id="btnUpdateSFL0${currentCount}" type="submit" value="Update" iclass="hidden" />
                                                                </div>

                                                                <div class="small-6 large-3 columns wishlistTotalDetails ">
                                                                        <div class="prodInfo">
                                                                            <h3 class="prodPrice hide-for-small">
                                                                            <c:set var="Price">
        																     	<dsp:valueof value="${PersonalPrice}" number="0.00" />
        																    </c:set>
        																    <c:choose>
        		                                                               <c:when test="${(not empty referenceNumber && enableKatoriFlag) || empty referenceNumber}">
        		                                                                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
        																			    <dsp:param name="price" value="${Price * quantity}" />
        																		    </dsp:include>
        																		    <c:set var="totalPriceOmni"> <dsp:valueof value="${Price}" number="0.00" converter="unformattedCurrency"/></c:set>
        				                                                             <input type="hidden" name="totalPriceOmni" class="frmAjaxSubmitData" value="${totalPriceOmni}" />
        		                                                               </c:when>
        		                                                               <c:otherwise>
        		                                                                  <bbbl:label key="lbl_cart_tbd" language ="${pageContext.request.locale.language}"/>
        		                                                               </c:otherwise>
        		                                                          </c:choose>
        		                                                          <div class="clear"></div>
        																	</h3>
                                                                        </div>
																		<c:if test="${isLtlFlag}">
																  <dsp:getvalueof var="ltlShipMethodDesc" param="element.ltlShipMethodDesc"/>
																	<dsp:getvalueof var="deliverySurcharge" param="element.deliverySurcharge"/>
																	<dsp:getvalueof var="assemblyFee" param="element.assemblyFees"/>
																	<span class="ltlPriceInfo">+
																	<c:choose>
														              <c:when test="${ltlShipMethod == null || empty ltlShipMethod}">

																	<span class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/></span>
																	<span class="deliverypriceLtl">
																		<span class="deliverypriceLtlClass"> TBD <img src="/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="Kyle Schuneman for Apt2B Logan Mini Apartment Sofa in Chicago Blue" class="dslInfo" /> </span>
																	</span>

																		</c:when>
																		 <c:otherwise>

																		 <span class="deliveryLtl">Incl ${ltlShipMethodDesc}</span>
																		 <span class="deliverypriceLtl">
																		 <span class="deliverypriceLtlClass">
																		 <c:if test="${deliverySurcharge eq 0.0}"> FREE <img src="/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="Kyle Schuneman for Apt2B Logan Mini Apartment Sofa in Chicago Blue" class="dslInfo" /> </c:if>
		                                                                    <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
		                                                                    </span>
																		</span>

																		</c:otherwise>
																		  </c:choose>
																		  <c:if test="${assemblyFee gt 0.0 }">

																		  <span class="deliveryLtl">  <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/></span>
																		  <span class="deliverypriceLtl"><span class="deliverypriceLtlClass">  <dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/>
																		   </span>
																			</span>

			                                                                </c:if>
			                                                                </span>
			                                                         </c:if>
                                                                    <dsp:getvalueof id="cartQuantity" param="element.quantity"/>
                                                            <dsp:getvalueof id="wishListItemId" param="element.wishListItemId"/>
                                                            <dsp:getvalueof var="ltlShipMethodToSend" param="element.ltlShipMethod" />
                                                            <input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />

                                                            <input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
                                                            <input type="hidden" name="cartQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
                                                            <input type="hidden" name="cartWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                            <input type="hidden" name="iCount" class="frmAjaxSubmitData" value="${currentCount}" />
                                                            <input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />
                                                            <input type="hidden" name="saveBts"  class="frmAjaxSubmitData" value="${btsValue}" />
                                                            <input type="hidden" name="cartUndoQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
                                                            <input type="hidden" name="cartUndoCount" class="frmAjaxSubmitData" value="${currentCount}" />
                                                            <input type="hidden" name="cartUndoCommerceId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                            <input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
                                                            <input type="hidden" name="personalizationType"  class="frmAjaxSubmitData" value="${personalizationType}" />
                                                            <input type="hidden" name="customizableCodes"  class="frmAjaxSubmitData" value="${customizableCodes}" />
															<c:if test="${not empty referenceNumber}">
																<c:if test="${not empty personalizationOptions}">
																	<c:set var="persDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>
																	<input type="hidden" name="cusDet"  class="frmAjaxSubmitData" value="${persDet}" id="customizationDetails"/>
																</c:if>
															</c:if>
                                                            <c:if test="${ltlShipMethodToSend eq 'LWA'}">
															<dsp:getvalueof var="ltlShipMethodToSend" value="LW" />
															<input type="hidden" name="whiteGloveAssembly"
																class="frmAjaxSubmitData" value="true" />
														</c:if>
														<c:if test="${ltlShipMethodToSend eq 'LW'}">
															<input type="hidden" name="whiteGloveAssembly"
																class="frmAjaxSubmitData" value="false" />
														</c:if>

																<input type="hidden" name="prevLtlShipMethod"
																	class="frmAjaxSubmitData"
																	value="${ltlShipMethodToSend}" />

																<input type="hidden" name="ltlShipMethod"
																	class="frmAjaxSubmitData"
																	value="${ltlShipMethodToSend}" />
																	<input type="hidden" name="ltlDeliveryServices"  class="frmAjaxSubmitData" value="" />

                                                            <c:set var="mveToCart" value="${false}"/>
                                                             <c:if test="${(empty priceMessageVO) or ((not empty priceMessageVO) and (priceMessageVO.inStock))}">
                                                                <c:set var="mveToCart" value="${true}"/>
                                                                    <div class="<c:if test='${isPersonalizationIncomplete or (not empty referenceNumber && !enableKatoriFlag)}'>button_disabled</c:if>">
                                                                        <dsp:droplet name="IsEmpty">
                                                                            <dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
                                                                            <dsp:oparam name="false">
                                                                                <input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="button tiny service moveToCart" onclick="additemtoorder(';${prodID};;;;event72=${quantity}|event73=${totalPriceOmni};eVar30=${skuID}|eVar15=Wish List','scAdd,,event72,event73',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}')" aria-pressed="false" role="button" <c:if test='${isPersonalizationIncomplete or (not empty referenceNumber && !enableKatoriFlag)}'>disabled="disabled"</c:if>/>
                                                                            </dsp:oparam>
                                                                            <dsp:oparam name="true">
                                                                                <input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="button tiny service btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;event72=${quantity}|event73=${totalPriceOmni};eVar30=${skuID}|eVar15=Wish List','scOpen,scAdd,event72,event73',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}')" aria-pressed="false" role="button" <c:if test='${isPersonalizationIncomplete or (not empty referenceNumber && !enableKatoriFlag)}'>disabled="disabled"</c:if>/>
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>
                                                                    </div>
                                                            </c:if>
                                                                        <dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
                                                                        <c:if test="${bopusoff and mapQuestOn and (not mveToCart)}">
                                                                               <%-- <div class="button "> --%>
                                                                                    <dsp:getvalueof id="id" param="element.wishListItemId" />
                                                                                    <input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
                                                                                    <input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
                                                                                    <input type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
                                                                                    <input type="hidden" name="count" value="${currentCount}" data-dest-class="count" data-change-store-submit="count" />
                                                                                    <input type="hidden" name="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL"/>
                                                                                    <c:if test="${not empty registryId}">
                                                                                        <input type="hidden" value="${registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
                                                                                    </c:if>

                                                                                    <input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
                                                                                    <input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
                                                                                  <%--  <input type="button hidden" class="changeStore nearby-store" value="Find in Store" role="button" aria-pressed="false" /> ---%>
                                                                                <%-- </div> ---%>
                                                                                <dsp:a iclass="nearbyStores nearby-stores in-store tiny button secondary" href="/tbs/selfservice/find_tbs_store.jsp">
																					<dsp:param name="id" value="${prodID}" />
																					<dsp:param name="siteId" value="${currentSiteId}" />
																					<dsp:param name="registryId" value="${registryId}" />
																					<dsp:param name="skuid" value="${skuID}"/>
																					<dsp:param name="itemQuantity" value="${quantity}"/>
																					<dsp:param name="wishlistItemId" value="${id}"/>
																					<bbbl:label key='lbl_pdp_product_find_store' language="${pageContext.request.locale.language}" />
																				</dsp:a>

                                                                       </c:if>
                                                                            <div>
                                                                                <input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
                                                                                <input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
                                                                                <input type="hidden" name="regQuantity" class="frmAjaxSubmitData" value="${quantity}" />
                                                                                <input type="hidden" name="reqWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                                                <input type="hidden" name="currentCount" class="frmAjaxSubmitData" value="${currentCount}" />
                                                                                <dsp:getvalueof var="appid" bean="Site.id" />
                                                                            <c:if test="${not flagOff}">
                                                                                <dsp:getvalueof var="transient" bean="Profile.transient" />
                                                                                <c:choose>
                                                                                    <c:when test="${transient == 'false'}">
                                                                                        <dsp:droplet name="AddItemToGiftRegistryDroplet">
                                                                                        <dsp:param name="siteId" value="${appid}"/>
                                                                                        <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_move_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                                                                                        <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
                                                                                        <dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
                                                                                        <dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
                                                                                            <c:choose>
                                                                                            <c:when test="${sizeValue>1 && (isPersonalizationIncomplete|| (not empty referenceNumber && !enableKatoriFlag))}">
                                                                                            <div class="btnAddToRegistrySel">
                                                                                            <div class="upperCase addToRegistry addToRegistrySel">
                                                                                            <div class="select button_select">
                                                                                                    <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
                                                                                                    <dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
                                                                                                    <dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                                                                            <dsp:option> <bbbl:label key="lbl_move_to_registry" language="${pageContext.request.locale.language}" /></dsp:option>
                                                                                                                <dsp:droplet name="ForEach">
                                                                                                                    <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                                                                                    <dsp:oparam name="output">
                                                                                                                        <dsp:param name="futureRegList" param="element" />
                                                                                                                        <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                        <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                                                                        <dsp:option value="${regId}"  iclass="${event_type}">
                                                                                                                            <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                                                                        </dsp:option>
                                                                                                                    </dsp:oparam>
                                                                                                                </dsp:droplet>
                                                                                                                <dsp:tagAttribute name="disabled" value="disabled" />
                                                                                                    </dsp:select>

                                                                                                    <input class="btnAjaxSubmitSFL hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" aria-pressed="false" />
                                                                                            </div>
                                                                                            </div>
                                                                                            </div>
                                                                                            </c:when>
                                                                                                <c:when test="${sizeValue>1}">
                                                                                                <div class="btnAddToRegistrySel">
                                                                                                <div class="upperCase addToRegistry addToRegistrySel">
                                                                                                <div class="select button_select">
                                                                                                        <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
                                                                                                        <dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
                                                                                                        <dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                                                                                                <dsp:option> <bbbl:label key="lbl_move_to_registry" language="${pageContext.request.locale.language}" /></dsp:option>
                                                                                                                    <dsp:droplet name="ForEach">
                                                                                                                        <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                                                                                        <dsp:oparam name="output">
                                                                                                                            <dsp:param name="futureRegList" param="element" />
                                                                                                                            <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                            <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                                                                            <dsp:option value="${regId}"  iclass="${event_type}">
                                                                                                                                <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                                                                            </dsp:option>
                                                                                                                        </dsp:oparam>
                                                                                                                    </dsp:droplet>
                                                                                                        </dsp:select>

                                                                                                        <input class="btnAjaxSubmitSFL hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" aria-pressed="false" />
                                                                                                </div>
                                                                                                </div>
                                                                                                </div>
                                                                                                </c:when>
                                                                                                <c:when test="${sizeValue==1}">
                                                                                                        <dsp:droplet name="ForEach">
                                                                                                                        <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                                                                                        <dsp:oparam name="output">
                                                                                                                            <dsp:param name="futureRegList" param="element" />
                                                                                                                            <dsp:getvalueof var="regId"
                                                                                                                                param="futureRegList.registryId" />
                                                                                                                                <dsp:getvalueof var="registryName"
                                                                                                                                param="futureRegList.eventType" />
                                                                                                                            <input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
                                                                                                                            <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${regId}" />
                                                                                                                            <input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis omniRegName" />
                                                                                                                            <dsp:setvalue bean="GiftRegistryFormHandler.registryName" value="${registryName}" />
                                                                                                                        </dsp:oparam>
                                                                                                                    </dsp:droplet>
                                                                                                <div class="addToRegistry <c:if test='${isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}'>button_disabled</c:if>">
                                                                                                    <input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData omniRegId" value="${regId}" />
                                                                                                    <%-- <input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/> --%>
                                                                                                    <input class="button moveToReg tiny secondary " name="123reqRegistryId" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}');" data-ajax-frmID="frmRegSaveForLater" aria-pressed="false" role="button" <c:if test='${isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}'>disabled="disabled"</c:if>/>
                                                                                                    </div>
                                                                                            </c:when>
                                                                                    </c:choose>
                                                                                    </dsp:droplet>


                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.certonaAppId" value="${appIdCertona}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.itemId" value="${prodID}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.quantity" value="${quantity}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.customerId" value="${userId}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.pageId" value="${pageIdCertona}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.price" value="${itemPrice * quantity}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.certonaParameter.registryName" value="${registryName}" type="hidden" />
                                                                                </c:when>
                                                                            </c:choose>
                                                                        </c:if>
                                                                        </div>
                                                            </div>
                                                            </div>
                                                            <input type="submit" name="btnPickUpInStore" id="btnPickUpInStore${skuID}" class="hidden pickUpInStoreSFL" value="PickUpInStore" role="button" aria-pressed="false" />
                                                        </div>
                                                </div>
                                            </dsp:oparam>
                                            <dsp:oparam name="outputEnd">
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </div>
                                    </dsp:form>
                                </div>
                            </div>
                            </div>
                        </dsp:oparam>
                    </dsp:droplet>
                    <%--<dsp:include page="/account/idm/idm_login_checkout.jsp" />--%>


</dsp:page>
