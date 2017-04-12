<dsp:page>
 
 <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet" />
 <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
 <dsp:importbean bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet" />
 <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
 <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
 <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />
 <dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
 <dsp:importbean bean="/atg/multisite/Site" />
 <dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
 <dsp:importbean bean="/atg/multisite/Site" />
 <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet" />
 <dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet" />
 <dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
 <!-- LTL changes divided ajax_cart in two jsp's and passed required parameters for included file  -->
 <dsp:getvalueof id="movedCommerceItemId" param="movedCommerceItemId" />
 <dsp:getvalueof id="fromWishlist" param="fromWishlist" />
 <dsp:getvalueof id="quantityCart" param="quantityCart" />
 <dsp:getvalueof id="applicationId" bean="Site.id" />
 <c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
 <c:set var="customizeCTACodes">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
 <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
	<dsp:oparam name="output">
		 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	</dsp:oparam>
</dsp:droplet>


 <!-- LTL changes end  -->
 <dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
  <dsp:oparam name="empty">
   <dsp:getvalueof var="movedMap" bean="GiftRegistryFormHandler.movedItemMap" />
   <dsp:getvalueof id="storeIdCart" bean="CartModifierFormHandler.storeId" />
   <dsp:getvalueof id="wishlistItemIdCart" bean="CartModifierFormHandler.wishlistItemId" />
   <%-- KP comment - not required
   
    <div class="clearfix grid_10 alpha omega">
    <h2 class="account fl">
     <bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>" />
    </h2>
    <ul class="grid_2 alpha share omega">
     <li><a href="#" class="print avoidGlobalPrintHandler" id="printCart"
      title="<bbbl:label key="lbl_cartdetail_print" language="<c:out param='${language}'/>"/>">
                                            <bbbl:label key="lbl_cartdetail_print" language="<c:out param='${language}'/>"/>
                                        </a>
                                    </li>
                                    <li><a href="#" class="email avoidGlobalEmailHandler" id="openEmailCart" title="<bbbl:label key="lbl_cartdetail_emailcart" language="<c:out param='${language}'/>"/>">
                                            <bbbl:label key="lbl_cartdetail_emailcart" language="<c:out param='${language}'/>"/>
                                        </a>
                                    </li>
                                </ul                               
                            </div> --%>
                            <c:choose>
                                <c:when test="${(not empty movedCommerceItemId) and fromWishlist}">

                                    <div id="saveForLaterHeader" class="clearfix grid_10 alpha omega">
                                        <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                                        <%-- <ul class="grid_2 alpha share omega">
                                            <li><a href="#" class="print avoidGlobalPrintHandler" title="Print Saved Items" id="printSavedItems"><bbbl:label key="lbl_sfl_print_saved_items" language="<c:out param='${language}'/>"/></a></li>
                                            <li><a href="#" class="email avoidGlobalEmailHandler" title="Email Saved Items" id="openEmailSavedItems"><bbbl:label key="lbl_sfl_email_saved_items" language="<c:out param='${language}'/>"/></a></li>
                                        </ul> --%>
                                        
                                    </div>
                                    <div id="saveForLaterBody">
                                        <div id="saveForLaterContentWrapper">
                                            <div class="clearfix grid_10 alpha omega productListWrapper" id="saveForLaterContent">
                                                
                                                <div class="row  hide-for-medium-down productsListHeader saved-items-header">
                                                   <div class="wishlistItemDetails medium-5 columns"><h3><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></h3>
                                                    </div>
                                                    <div class="wishlistQuantityDetails medium-3 columns"><h3><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></h3>
                                                    </div>
                                                    <div class="wishlistTotalDetails medium-2 columns"><h3><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></h3>
                                                    </div>
                                                </div>
                                                
                                                <div class="productsListContent">
                                                <dsp:form formid="frmSaveUndoCart" id="frmSaveUndoCart" iclass="frmAjaxSubmit frmSaveUndoCart" method="post" action="ajax_handler_cart.jsp">
                                                    <dsp:input type="hidden" bean="GiftlistFormHandler.fromCartPage" iclass="undoMoveToCartData" value="true" />
                                                    <dsp:input type="hidden" bean="GiftlistFormHandler.undoComItemId" iclass="frmAjaxSubmitData">
                                                        <dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoCommerceId" />
                                                    </dsp:input>
                                                    <dsp:input bean="GiftlistFormHandler.countNo" type="hidden" iclass="frmAjaxSubmitData">
                                                        <dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoCount" />
                                                    </dsp:input>
                                                    <dsp:input bean="GiftlistFormHandler.undoOpt" type="hidden" value="true" iclass="frmAjaxSubmitData" />
                                                    <dsp:input bean="GiftlistFormHandler.quantity" iclass="frmAjaxSubmitData" type="hidden">
                                                        <dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoQuantity" />
                                                    </dsp:input>
                                                    <dsp:input bean="GiftlistFormHandler.successQueryParam"  type="hidden" value="showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
													<%-- Client DOM XSRF | Part -2
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCartLoginURL" iclass="frmAjaxSubmitData" type="hidden" value="/cart/ajax_handler_cart.jsp?showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCartLoginURL" iclass="frmAjaxSubmitData" type="hidden" value="/cart/ajax_handler_cart.jsp?showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCartErrorURL" iclass="frmAjaxSubmitData" type="hidden" value="/cart/ajax_handler_cart.jsp?showMoveToCartError=true" / --%>
													  <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="moveItemsFromCart" />
                                                    <dsp:input bean="GiftlistFormHandler.moveItemsFromCart"  type="submit" value="MOVE TO CART" id="btnUndoMoveToCart2" iclass="hidden" name="btnUndoMoveToCart" />
                                                </dsp:form>
                                                <dsp:include page="saveUndoLink.jsp">
                                                    <dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
                                                    <dsp:param name="fromWishlist" value="${fromWishlist}"/>
                                                    <dsp:param name="quantity" value="${quantityCart}"/>
                                                    <dsp:param name="currentCount" value="1"/>
                                                </dsp:include>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:when>
                                <c:when test="${not empty movedMap}">
                                    <div id="saveForLaterBody">
                                        <div id="saveForLaterContentWrapper">
                                        <div class="clearfix grid_10 alpha omega productListWrapper" id="saveForLaterContent">
                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                    <dsp:param name="array" bean="GiftRegistryFormHandler.movedItemMap" />
                                                    <dsp:oparam name = "outputStart">
                                                        
                                                        <div class="row  hide-for-medium-down productsListHeader saved-items-header">
                                                           <div class="wishlistItemDetails medium-5 columns"><h3><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></h3>
                                                            </div>
                                                            <div class="wishlistQuantityDetails medium-3 columns"><h3><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></h3>
                                                            </div>
                                                            <div class="wishlistTotalDetails medium-2 columns"><h3><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></h3>
                                                            </div>
                                                        </div>
                                                        
                                                        <div class="productsListContent">
                                                    </dsp:oparam>

                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof var="movedItemCount" param="key" />
                                                        <dsp:getvalueof var="productID" param="element" />
                                                        <dsp:getvalueof bean="GiftRegistryFormHandler.skuId" var="skuId" />
                                                        
                                                        
                                                        
                                                        <div id="savedItemID_${skuID}" class="savedItemRow saved-item movedItem movedToReg itemMovedToRegUndoLink">
                                                            <dsp:form name="frmSavedItems" iclass="frmAjaxSubmit frmSavedItems savedItemRow" action="ajax_handler_cart.jsp">
                                                                <div class="prodItemWrapper">
                                                                    <p class="fl prodInfo breakWord textLeft">
                                                                        <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                                            <dsp:param name="id" value="${productID}" />
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
                                                                        <dsp:droplet name="SKUWishlistDetailDroplet">
                                                                            <dsp:param name="siteId" value="${appid}"/>
                                                                            <dsp:param name="skuId" value="${skuId}"/>
                                                                            <dsp:oparam name="output">
                                                                                <dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName" />
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>
                                                                        <dsp:a iclass="prodName" title="${prodName}" page="${finalUrl}?skuId=${skuId}">${prodName}</dsp:a> has been moved to registry #<dsp:valueof bean="GiftRegistryFormHandler.movedItemRegistryId" />.
                                                                    </p>
                                                                    <p class="fr textRight">

                                                                                <dsp:droplet name="GetRegistryVODroplet">
                                                                                    <dsp:param name="siteId" value="${applicationId}"/>
                                                                                    <dsp:param bean="GiftRegistryFormHandler.movedItemRegistryId" name="registryId" />
                                                                                    <dsp:oparam name="output">
                                                                                          <dsp:getvalueof var="registryVO" param="registryVO"/>

                                                                                            <dsp:droplet name="GetRegistryTypeNameDroplet">
                                                                                                <dsp:param name="siteId" value="${applicationId}"/>
                                                                                                <dsp:param name="registryTypeCode" value="${registryVO.registryType.registryTypeName}"/>
                                                                                                <dsp:oparam name="output">
                                                                                                    <dsp:a iclass="lnkAction viewRegistry" href="/tbs/giftregistry/view_registry_owner.jsp" title="View your registry">View your registry
                                                                                                        <dsp:param name="registryId" bean="GiftRegistryFormHandler.movedItemRegistryId"/>
                                                                                                        <dsp:param name="eventType" param="registryTypeName"/>
                                                                                                    </dsp:a>
                                                                                                </dsp:oparam>
                                                                                            </dsp:droplet>

                                                                                    </dsp:oparam>
                                                                                </dsp:droplet>

                                                                    </p>
                                                                    
                                                                </div>
                                                                
                                                            </dsp:form>
                                                            
                                                        </div>
                                                    </dsp:oparam>

                                                    <dsp:oparam name = "outputEnd">
                                                        </div>
                                                    </dsp:oparam>
                                            </dsp:droplet>
                                        </div>
                                      </div>
                                    </div>
                                </c:when>
                                <c:when test="${not empty storeIdCart and not empty wishlistItemIdCart}">
                                    <div id="saveForLaterBody">
                                        <div id="saveForLaterContentWrapper">
                                            <div id="saveForLaterContent" class="clearfix grid_10 alpha omega productListWrapper">
                                                <div class="savedItemRow saved-item itemFindInStoreUndoLink"></div>
                                            </div>
                                        </div>
                                    </div>
                                </c:when>

                            </c:choose>
                        </dsp:oparam>
                    <dsp:oparam name="output">
                     <div id="saveForLaterHeader" class="clearfix grid_10 alpha omega">
                          <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                          <%-- <ul class="grid_2 alpha share omega">
                              <li><a href="#" class="print avoidGlobalPrintHandler" title="Print Saved Items" id="printSavedItems"><bbbl:label key="lbl_sfl_print_saved_items" language="<c:out param='${language}'/>"/></a></li>
                              <li><a href="#" class="email avoidGlobalEmailHandler" title="Email Saved Items" id="openEmailSavedItems"><bbbl:label key="lbl_sfl_email_saved_items" language="<c:out param='${language}'/>"/></a></li>
                          </ul> --%>
                     </div>
                     <div id="saveForLaterBody">

                        <div id="saveForLaterContentWrapper">

                        <div id="saveForLaterContent" class="clearfix grid_10 alpha omega productListWrapper">
                        
                            <div class="row  hide-for-medium-down productsListHeader saved-items-header">
                               <div class="wishlistItemDetails medium-5 columns"><h3><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></h3></div>
                                <div class="wishlistQuantityDetails medium-3 columns"><h3><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></h3></div>
                                <div class="wishlistTotalDetails medium-2 columns"><h3><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></h3></div>
                            </div>
                            
                            <dsp:include page="saveForlaterCartForms.jsp"/>
                            <dsp:form name="frmSavedItems" iclass="frmAjaxSubmit frmSavedItems savedItemRow" action="ajax_handler_cart.jsp">
                                <div class="productsListContent">
                                <c:set var="showMsg" value="${true}"/>
                                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param name="array" param="giftList" />
                                    <dsp:param name="reverseOrder" value="true" />
                                    <dsp:oparam name="outputStart">
                                    </dsp:oparam>
                                    <dsp:oparam name="output">
                                        <c:set var="flagOff" value="${false}"/>
                                        <c:set var="bopusoff" value="${false}"/>
                                        <dsp:getvalueof var="arraySize" param="size" />
                                        <dsp:getvalueof var="currentCount" param="count" />
                                        <dsp:getvalueof var="registryId" param="element.registryID" />
                                        <dsp:getvalueof var="skuID" param="element.skuID" />
                                        <dsp:getvalueof var="quantity" param="element.quantity" />
                                        <dsp:getvalueof var="wishListItemId" param="element.wishListItemId" />
                                        <dsp:getvalueof var="commerceItemId" param="element.commerceItemId" />
                                        <dsp:getvalueof var="prodID" param="element.prodID" />
                                        <dsp:getvalueof var="giftListId" param="element.giftListId" />
                                        
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
                                        <dsp:getvalueof var="priceMessageVO" param="element.priceMessageVO" />
                                        <dsp:getvalueof id="iCount" param="count"/>
                                        <dsp:getvalueof id="size" param="size"/>
                                        <dsp:getvalueof id="countNo" bean="CartModifierFormHandler.countNo"/>
                                        
                                        <c:if test="${arraySize eq currentCount}">
                                                    <c:set var="lastRow" value="lastRow" />
                                        </c:if>
                                        
                                        
                                        
                                        <c:if test="${(size eq iCount) and (size lt countNo)}">
                                            <c:set var="check" value="${true}"/>
                                        </c:if>

                                        <c:if test="${iCount eq countNo}">
                                            <dsp:getvalueof id="storeIdCart" bean="CartModifierFormHandler.storeId" />
                                            <dsp:getvalueof id="wishlistItemIdCart" bean="CartModifierFormHandler.wishlistItemId" />
                                            <c:if test="${not empty storeIdCart and not empty wishlistItemIdCart}">
                                                    <div class="savedItemRow saved-item itemFindInStoreUndoLink"></div>
                                            </c:if>
                                            <dsp:include page="saveUndoLink.jsp">
                                                <dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
                                                <dsp:param name="fromWishlist" value="${fromWishlist}"/>
                                                <dsp:param name="quantity" value="${quantityCart}"/>
                                                <dsp:param name="currentCount" value="${countNo}"/>
                                            </dsp:include>
                                        </c:if>

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

                                        <dsp:getvalueof var="movedItemIndex" bean="GiftRegistryFormHandler.movedItemIndex"/>
                                        <c:set var="displaySize" >
                                            <bbbc:config key="sfl_display_size" configName='CartAndCheckoutKeys' />
                                         </c:set>
                                         <c:if test="${empty displaySize}">
                                            <c:set var="displaySize" >
                                                2
                                             </c:set>
                                         </c:if>


                                        <c:choose>
                                            <c:when test="${movedItemIndex <= displaySize && currentCount == displaySize+1 && arraySize > displaySize}">

                                                    <div id="savedItemID_BTN" class="savedItemRow showAllBtn">
                                                        <div class="prodItemWrapper">
                                                                <input id="showAllBtn" type="button" value="Show all ${arraySize} items" role="button" aria-pressed="false" aria-labelledby="showAllBtn" />
                                                        </div>
                                                    </div>

                                            </c:when>
                                            <c:when test="${currentCount == displaySize+1 && arraySize > displaySize}">

                                                    <div id="savedItemID_BTN" class="savedItemRow showAllBtn">
                                                        <div class="prodItemWrapper">
                                                            <div class="button">
                                                                <input id="showAllBtn" type="button" value="Show all ${arraySize} items" role="button" aria-pressed="false" aria-labelledby="showAllBtn" />
                                                            </div>
                                                        </div>
                                                    </div>

                                            </c:when>
                                        </c:choose>
                                                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                    <dsp:param name="array" bean="GiftRegistryFormHandler.movedItemMap" />
                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof var="movedItemCount" param="key" />
                                                        <dsp:getvalueof var="productID" param="element" />
                                                        <dsp:getvalueof bean="GiftRegistryFormHandler.skuId" var="skuId" />
                                                        <c:if test="${movedItemCount eq currentCount}">
                                                        
                                                        
                                                        
                                                        
                                                        <div id="savedItemID_${skuID}" class="${lastRow} savedItemRow saved-item movedItem movedToReg itemMovedToRegUndoLink">

                                                                <div class="prodItemWrapper">
                                                                    <p class="fl prodInfo breakWord">
                                                                        <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                                            <dsp:param name="id" value="${productID}" />
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
                                                                        <dsp:droplet name="SKUWishlistDetailDroplet">
                                                                            <dsp:param name="siteId" value="${appid}"/>
                                                                            <dsp:param name="skuId" value="${skuId}"/>
                                                                            <dsp:oparam name="output">
                                                                                <dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName" />
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>
                                                                        <dsp:a iclass="prodName" title="${prodName}" page="${finalUrl}?skuId=${skuId}">${prodName}</dsp:a> has been moved to registry #<dsp:valueof bean="GiftRegistryFormHandler.movedItemRegistryId" />.
                                                                    </p>
                                                                    <p class="fr">

                                                                                <dsp:droplet name="GetRegistryVODroplet">
                                                                                    <dsp:param name="siteId" value="${applicationId}"/>
                                                                                    <dsp:param bean="GiftRegistryFormHandler.movedItemRegistryId" name="registryId" />
                                                                                    <dsp:oparam name="output">
                                                                                          <dsp:getvalueof var="registryVO" param="registryVO"/>

                                                                                            <dsp:droplet name="GetRegistryTypeNameDroplet">
                                                                                                <dsp:param name="siteId" value="${applicationId}"/>
                                                                                                <dsp:param name="registryTypeCode" value="${registryVO.registryType.registryTypeName}"/>
                                                                                                <dsp:oparam name="output">
                                                                                                    <dsp:a iclass="lnkAction viewRegistry" href="/tbs/giftregistry/view_registry_owner.jsp" title="View your registry">View your registry
                                                                                                        <dsp:param name="registryId" bean="GiftRegistryFormHandler.movedItemRegistryId"/>
                                                                                                        <dsp:param name="eventType" param="registryTypeName"/>
                                                                                                    </dsp:a>
                                                                                                </dsp:oparam>
                                                                                            </dsp:droplet>

                                                                                    </dsp:oparam>
                                                                                </dsp:droplet>

                                                                    </p>
                                                                    
                                                                </div>
                                                                

                                                            
                                                        </div>
                                                        </c:if>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <c:if test="${currentCount > displaySize && arraySize > displaySize}">
                                                    <c:set var="hidden" value="hidden"></c:set>
                                                </c:if>
                                                <c:if test="${movedItemIndex <= displaySize && currentCount > displaySize && arraySize > displaySize}">
                                                    <c:set var="hidden" value="hidden"></c:set>
                                                </c:if>

                                                <dsp:getvalueof bean="GiftlistFormHandler.itemIdJustMvBack" id="itemIdJustMvBackWish"/>
                                                <dsp:getvalueof bean="GiftlistFormHandler.undoOpt" id="undoCheckwish"/>
                                                <c:if test="${undoCheckwish and (itemIdJustMvBackWish eq wishListItemId)}">
                                                    <c:set var="itemMovedBackToSave" value="itemMovedBackToSave"/>
                                                </c:if>
                                                <dsp:getvalueof bean="GiftlistFormHandler.newItemAdded" id="newItemAddedwish"/>
                                                <c:if test="${newItemAddedwish and currentCount == 1}">
                                                    <c:set var="itemJustMovedToSave" value="itemJustMovedToSave"/>
                                                </c:if>
                                                <dsp:getvalueof var="ltlShipMethod" param="element.ltlShipMethod"/>
                                        <div id="savedItemID_${wishListItemId}" class="${itemJustMovedToSave} ${itemMovedBackToSave} ${lastRow} <c:if test="${not empty registryId}">registeryItem</c:if> savedItemRow saved-item changeStoreItemWrap ${hidden}">
                                        <c:set var="itemMovedBackToSave" value=""/>
                                        <c:set var="itemJustMovedToSave" value=""/>
                                        <input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="prodId" name="productId">
                                        <input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
                                        <c:if test="${flagOff}">
                                            <input id="quantity" type="hidden" data-dest-class="itemQuantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity" role="textbox" aria-required="true">
                                        </c:if>

                                        <dsp:getvalueof var="productVO" param="element.productVO"/>
                                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
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

                                            <dsp:include page="itemLinkWishlist.jsp?id=${wishListItemId}&registryIdSavedItem=${registryId}&image=${prodImage}">
                                                <dsp:param name="priceMessageVO" value="${priceMessageVO}"/>
                                                <dsp:param name="displayName" value="${pName}"/>
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
                                                                <span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>&nbsp;
                                                                <dsp:a href="${registryUrl}">
                                                                <dsp:param name="registryId" value="${registryId}"/>
                                                                <dsp:param name="eventType" value="${registryTypeName}" />
                                                                <strong>${registryVO.primaryRegistrant.firstName}<c:if test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
                                                                </dsp:a>
                                                                &nbsp;<span>${registryTypeName}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
                                                            </h3>
                                                                
                                                            </div>

                                                            
                                                    </c:if>

                                                    
                                                <!-- LTL Alert  -->
                                                        <c:if test="${isLtlFlag && empty ltlShipMethod}" >
                                                            
                                                            <div class="ltlItemAlert alert alert-info">
                                                                <bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
                                                            </div>
                                                        </c:if>
                                                        <!-- LTL Alert  -->
                                                <div class="prodItemWrapper">
                                                    <div class="row">
                                                        <div class="wishlistItemDetails small-12 large-5 columns">
                                                        <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
                                                     <div class="small-4 large-6 columns">
                                                        <c:choose>
                                                            <c:when test="${not flagOff}">
                                                                <dsp:a iclass="prodImg block" title="${pName}" page="${finalUrl}?skuId=${skuID}">
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
                                                     </div>
                                                     <div class="small-8 large-6 columns">
                                                            <c:if test="${empty pName}">
                                                                    <dsp:getvalueof var="pName" value="${skuDisplayName}" />
                                                            </c:if>
                                                            <span class="prodInfo block breakWord">
                                                            <span class="prodName">
                                                               <c:choose>
                                                                    <c:when test="${not flagOff}">
                                                                        <dsp:a page="${finalUrl}?skuId=${skuID}" iclass="productName" title="${productVO.name}">${pName}</dsp:a>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="productName disableText">${skuDisplayName}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <dsp:getvalueof var="pName" value="" />
                                                                <dsp:getvalueof var="prodImage" value="" />
                                                            </span>
                                                            <br/>
                                                            <span class="prodAttribsSavedItem facet">
                                                                <c:if test='${not empty skuColor}'><c:choose><c:when test="${rollupAttributesFinish == 'true'}"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : </c:when><c:otherwise><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : </c:otherwise></c:choose>&nbsp;<dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
                                                                <c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
                                                                <c:set var="rollupAttributesFinish" value="false" />
                                                            </span>
                                                            <br/>
                                                                <span class="skuNum facet"><bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${skuID}</span>
																
                                                                <div class="prodDeliveryInfo squareBulattedList  facet">

                                                                         <dsp:droplet name="IsProductSKUShippingDroplet">
                                                                          <dsp:param name="siteId" value="${applicationId}"/>
                                                                           <dsp:param name="skuId" value="${skuID}"/>
                                                                          <dsp:param name="prodId" value="${prodID}"/>
                                                                          <dsp:oparam name="true">

                                                                              <dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
                                                                              <c:forEach var="item" items="${restrictedAttributes}">
                                                                                      <c:choose>
                                                                                          <c:when test="${null ne item.actionURL}">
                                                                                              <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
                                                                                          </c:when>
                                                                                          <c:otherwise>
                                                                                              ${item.attributeDescrip}
                                                                                          </c:otherwise>
                                                                                      </c:choose>
                                                                              </c:forEach>
                                                                          </dsp:oparam>
                                                                          <dsp:oparam name="false">
                                                                          </dsp:oparam>
                                                                      </dsp:droplet>
                                                                      </div>
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
                                                                      <c:if test='${enableKatoriFlag && not empty personalizationOptions && personalizationOptions != null}'>
                                                                       <div class="personalizationAttributes">
                                                                       <span class="exim-code">${eximCustomizationCodesMap[personalizationOptions]} : 
                                                                       </span>
                                                                       <span class="exim-detail">${personalizationDetails}
                                                                       </span> 
                                                                          
                                                                       <c:if test="${isPersonalizationIncomplete eq 'false'}">
 																	        <input type="hidden" class="editPersonalization"  data-custom-vendor="${productVO.vendorInfoVO.vendorName}" data-sku="${skuVO.skuId}" data-refnum="${referenceNumber}" data-quantity="${quantity}" aria-hidden="true"></input>
 																	   </c:if>	
                                                                         <dsp:setvalue beanvalue="Profile.wishlist"
       	        																param="wishlist" />
       	        																<dsp:getvalueof id="wishlistId" param="wishlist.id" />
       	        																<c:if test="${isPersonalizationIncomplete}">
       	        															  <div class="personalizeLinks">
       	        																	<a href="#" class="editPersonalizationSfl bold" title="${editPersonalizeTxt}" data-sku="${skuVO.skuId}" data-custom-vendor="${productVO.vendorInfoVO.vendorName}" data-refnum="${referenceNumber}" data-quantity="${quantity}" data-wishlistId="${wishlistId}" data-wishlistitemId="${wishListItemId}">
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
    															
																<%-- BBBH-2890:start --%>
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
																																														<bbbl:textArea key="txt_pdp_cr_customize_katori_price" language="<c:out param='${language}'/>"/>
																																													</c:when>
																																													<c:otherwise>
																																														<bbbl:textArea key="txt_pdp_cr_katori_price" language="<c:out param='${language}'/>"/>
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
                                                        </span>
                                                        </div>
                                                        </div>
                                                         <div class="wishlistQuantityDetails small-6 large-2 columns quantity">
                                                            <div class="prodDeliveryInfo">
                                                            <c:if test="${not flagOff}">
                                                                   
                                                                    <div class="qty-spinner quantityBox">
                                                                            
                                                                                    <a class="button minus secondary" title="Decrease Quantity"><span></span></a>
                                                                                    <input data-max-value="99" name="${wishListItemId}" aria-required="true" aria-labelledby="lblquantity_text_${skuID}" type="text" value="${quantity}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" id="quantity_text_${skuID}" maxlength="2" class="moveToCartData itemQuantity fl quantity-input"/>
                                                                                    <a class="button plus secondary" title="Increase Quantity"><span></span></a>
                                                                                
                                                                        </div>
                                                            </c:if>
                                                                
                                                            </div>
                                                            <div class="qty-actions">
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
                                                                    <a href="#" class="lnkUpdate triggerSubmit" data-submit-button="btnUpdateSFL0${currentCount}" title="Update Quantity">
                                                                    <bbbl:label key="lbl_sfl_update" language="<c:out param='${language}'/>"/></a>
                                                                </c:if>
                                                                <dsp:getvalueof id="giftlistId" param="wishlist.id"/>
                                                                <input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                                <input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
                                                                <a href="#" data-ajax-frmID="frmSaveItemRemove" class="lnkRemove btnAjaxSubmitSFL remove-item" data-submit-button="btnRemoveSFL0${currentCount}" title="Remove Product from Save For Later" onclick="removeWishList('${prodID}','${skuID }');"><bbbl:label key="lbl_sfl_remove" language="<c:out param='${language}'/>"/></a>
                                                            </div>
                                                            <dsp:input bean="GiftlistFormHandler.updateGiftlistItems" name="btnUpdate" id="btnUpdateSFL0${currentCount}" type="submit" value="Update" iclass="hidden" />
                                                        </div>
                                                        <div class="wishlistTotalDetails small-6 large-2 columns no-padding-left">
                                                        <div class="prodInfo">
                                                        <h3 class="prodPrice hide-for-small">
                                                        <c:set var="totalPrice" value="${quantity * PersonalPrice}"/>	
                                                        <span class="prodPrice">
                                                         <c:choose>
                                                           <c:when test="${not empty referenceNumber && !enableKatoriFlag}">
                                                            <bbbl:label key="lbl_cart_tbd" language ="${pageContext.request.locale.language}"/>  
                                                           </c:when>
                                                           <c:when test="${isPersonalizationIncomplete eq 'true' && not empty personalizationType &&  personalizationType eq 'CR' && totalPrice <= 0.01}">
                                                              <bbbl:label key="lbl_dsk_pdp_price_is_tbd" language ="${pageContext.request.locale.language}"/>
                                                           </c:when>                                                                             
                                                           <c:otherwise>
                                                           <c:set var="totalPriceOmni"> <dsp:valueof value="${totalPrice}" number="0.00" converter="unformattedCurrency"/></c:set>
                                                            <dsp:valueof value="${totalPrice}" number="0.00" converter="currency" />
													         <input type="hidden" name="totalPriceOmni" class="frmAjaxSubmitData" value="${totalPriceOmni}" />
                                                        </c:otherwise>
                                                      </c:choose>
													
                                                   </span>
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
                                                                <!-- item exclusion droplet -->
                                                             <dsp:droplet name="TBSItemExclusionDroplet">
																<dsp:param name="siteId" value="${applicationId}"/>
																<dsp:param name="skuId" value="${skuID}"/>
																<dsp:oparam name="output">
																<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																</dsp:oparam>
															</dsp:droplet>

                                                            <dsp:getvalueof id="cartQuantity" param="element.quantity"/>
                                                            <dsp:getvalueof id="wishListItemId" param="element.wishListItemId"/>
                                                            <dsp:getvalueof var="ltlShipMethodToSend" param="element.ltlShipMethod"/>
                                                            <dsp:getvalueof id="undoComItemId" bean="CartModifierFormHandler.commerceItemId" />
                                                            <input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
                                                            <input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
                                                            <input type="hidden" name="cartQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
                                                            <input type="hidden" name="cartWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                            <input type="hidden" name="iCount" class="frmAjaxSubmitData" value="${currentCount}" />
                                                            <input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />
                                                            <input type="hidden" name="cartUndoQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
                                                            <input type="hidden" name="cartUndoCount" class="frmAjaxSubmitData" value="${currentCount}" />
                                                            <input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
                                                            <input type="hidden" name="personalizationType"  class="frmAjaxSubmitData" value="${personalizationType}" />
                                                            <input type="hidden" name="customizableCodes"  class="frmAjaxSubmitData" value="${customizableCodes}" />
                                                            <input type="hidden" name="cartUndoCommerceId" class="frmAjaxSubmitData" value="${undoComItemId}" />
                                                            <c:if test="${ltlShipMethodToSend eq 'LWA'}">
															<dsp:getvalueof var="ltlShipMethodToSend" value="LW"/>
															<input type="hidden" name="whiteGloveAssembly"  class="frmAjaxSubmitData" value="true" />
														</c:if>
														<c:if test="${ltlShipMethodToSend eq 'LW'}">
															<input type="hidden" name="whiteGloveAssembly"  class="frmAjaxSubmitData" value="false" />
														</c:if>
														
																	<input type="hidden" name="prevLtlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
												        	
																	<input type="hidden" name="ltlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
																	<input type="hidden" name="ltlDeliveryServices"  class="frmAjaxSubmitData" value="" />
															
                                                            <c:set var="mveToCart" value="${false}"/>
                                                            <c:choose>
																<c:when test="${isItemExcluded || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag) || caDisabled }">
																	<div class="button_disabled">
																		<input name="addItemToOrder" type="button" disabled="disabled" value="MOVE TO CART" class="button tiny service btnAjaxSubmitSFL moveToCart"  role="button" aria-pressed="false" />
																	</div>
																</c:when>
																<c:otherwise>
		                                                             <c:if test="${(empty priceMessageVO) or ((not empty priceMessageVO) and (priceMessageVO.inStock))}">
		                                                                <c:set var="mveToCart" value="${true}"/>
		                                                                    <div class="fr">
		                                                                        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		                                                                            <dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
		                                                                            <dsp:oparam name="false">
		                                                                                <input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="button tiny service btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;event72=${quantity}|event73=${totalPriceOmni};eVar30=${skuID}|eVar15=Wish List','scAdd,event72,event73','${isPersonalized}','${eximCustomizationCodesMap[personalizationOptions]}')" aria-pressed="false" role="button" />
		                                                                            </dsp:oparam>
		                                                                            <dsp:oparam name="true">
		                                                                                <input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="button tiny service btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;event72=${quantity}|event73=${totalPriceOmni};eVar30=${skuID}|eVar15=Wish List','scOpen,scAdd,event72,event73','${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}')" aria-pressed="false" role="button" />
		                                                                            </dsp:oparam>
		                                                                        </dsp:droplet>
		                                                                    </div>
		                                                            </c:if>
                                                            	</c:otherwise>
                                                            </c:choose>
                                                                        <dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
                                                                        <c:if test="${bopusoff and (defaultCountry eq 'US') and (not mveToCart)}">
                                                                                <div class="fr">
                                                                                    <dsp:getvalueof id="id" param="element.wishListItemId" />
                                                                                    <input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
                                                                                    <input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
                                                                                    <input id="quantity" type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
                                                                                    <c:if test="${not empty registryId}">
                                                                                        <input type="hidden" value="${registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
                                                                                    </c:if>
                                                                                    <input type="hidden" name="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL"/>
                                                                                    <input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
                                                                                    <input type="hidden" name="count" value="${currentCount}" data-dest-class="count" data-change-store-submit="count" />
                                                                                    <input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
                                                                                    <!-- <input type="button" class="changeStore" value="Find in Store"> -->
	                                                                               	<dsp:a iclass="nearbyStores nearby-stores in-store tiny button secondary" href="/tbs/selfservice/find_tbs_store.jsp">
																						<dsp:param name="id" value="${prodID}" />
																						<dsp:param name="siteId" value="${currentSiteId}" />
																						<dsp:param name="registryId" value="${registryId}" />
																						<dsp:param name="skuid" value="${skuID}"/>
																						<dsp:param name="itemQuantity" value="${quantity}"/>
																						<dsp:param name="wishlistItemId" value="${id}"/>
																						<bbbl:label key='lbl_pdp_product_find_store' language="${pageContext.request.locale.language}" />
																					</dsp:a>
                                                                                </div>
                                                                                
                                                                       </c:if>

                                                                            <dsp:input id="catalogRefId" bean="GiftRegistryFormHandler.skuId" value="${skuID}" type="hidden" />
                                                                            <dsp:input id="productId" bean="GiftRegistryFormHandler.productId" value="${prodID}" type="hidden" />
                                                                            <dsp:input id="quantity" bean="GiftRegistryFormHandler.quantity" type="hidden" value="${commItem.BBBCommerceItem.quantity}" />


                                                                            <dsp:getvalueof var="appid" bean="Site.id" />
                                                                            <dsp:droplet name="AddItemToGiftRegistryDroplet">
                                                                                <dsp:param name="siteId" value="${appid}"/>
                                                                             <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_move_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                                                                                <dsp:oparam name="output">
                                                                                    <c:set var="sizeValue">
                                                                                        <dsp:valueof param="size" />
                                                                                    </c:set>
                                                                                </dsp:oparam>
                                                                            </dsp:droplet>

                                                                            <c:if test="${not flagOff}">
                                                                            <dsp:getvalueof var="transient" bean="Profile.transient" />
                                                                            <input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
                                                                            <input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
                                                                            <input type="hidden" name="regQuantity" class="frmAjaxSubmitData" value="${quantity}" />
                                                                            <input type="hidden" name="reqWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
                                                                            <input type="hidden" name="currentCount" class="frmAjaxSubmitData" value="${currentCount}" />
                                                                            <c:choose>
                                                                            
                                                                                <c:when test="${transient == 'false' and not isLtlFlag}">
                                                                                    <c:choose>
                                                                                    <c:when
    																				test="${sizeValue>1 && (isPersonalizationIncomplete|| (not empty referenceNumber && !enableKatoriFlag))}">
                                                                                    <div class="fr btnAddToRegistrySel">
                                                                                    <div class="upperCase addToRegistry addToRegistrySel">
                                                                                    <div class="select button_select">
                                                                                            <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
                                                                                            <dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
                                                                                                <dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                                                                <dsp:droplet name="AddItemToGiftRegistryDroplet">
                                                                                                    <dsp:param name="siteId" value="${appid}"/>
                                                                                                    <dsp:oparam name="output">
                                                                                                    <dsp:option> <bbbl:label key="lbl_move_to_registry"
                                                                                                                language="${pageContext.request.locale.language}" /></dsp:option>
                                                                                                        <dsp:droplet name="ForEach">
                                                                                                            <dsp:param name="array" param="registrySkinnyVOList" />
                                                                                                            <dsp:oparam name="output">
                                                                                                                <dsp:param name="futureRegList" param="element" />
                                                                                                                <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                                                                <dsp:option value="${regId}"  iclass="${event_type}">
                                                                                                                    <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                                                                </dsp:option>
                                                                                                            </dsp:oparam>
                                                                                                        </dsp:droplet>
                                                                                                    </dsp:oparam>
                                                                                                </dsp:droplet>
                                                                                                <dsp:tagAttribute name="disabled" value="disabled" />
                                                                                            </dsp:select>
                                                                                            <input class="btnAjaxSubmitSFL hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}','${isPersonalized}','${eximCustomizationCodesMap[personalizationOptions]}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
                                                                                    </div>
                                                                                    </div>
                                                                                    
                                                                                    
                                                                                    </div>
                                                                                                    </c:when>
                                                                                        <c:when test="${sizeValue>1}">
                                                                                        <div class="fr btnAddToRegistrySel">
                                                                                        <div class="upperCase addToRegistry addToRegistrySel">
                                                                                        <div class="select button_select">
                                                                                                <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
                                                                                                <dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
                                                                                                    <dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                                                                    <dsp:droplet name="AddItemToGiftRegistryDroplet">
                                                                                                        <dsp:param name="siteId" value="${appid}"/>
                                                                                                        <dsp:oparam name="output">
                                                                                                        <dsp:option> <bbbl:label key="lbl_move_to_registry"
                                                                                                                    language="${pageContext.request.locale.language}" /></dsp:option>
                                                                                                            <dsp:droplet name="ForEach">
                                                                                                                <dsp:param name="array" param="registrySkinnyVOList" />
                                                                                                                <dsp:oparam name="output">
                                                                                                                    <dsp:param name="futureRegList" param="element" />
                                                                                                                    <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                    <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                                                                    <dsp:option value="${regId}"  iclass="${event_type}">
                                                                                                                        <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                                                                    </dsp:option>
                                                                                                                </dsp:oparam>
                                                                                                            </dsp:droplet>
                                                                                                        </dsp:oparam>
                                                                                                    </dsp:droplet>
                                                                                                </dsp:select>
                                                                                                <input class="btnAjaxSubmitSFL hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}','${isPersonalized}','${eximCustomizationCodesMap[personalizationOptions]}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
                                                                                        </div>
                                                                                        </div>
                                                                                        
                                                                                        
                                                                                        </div>
                                                                                        </c:when>
                                                                                        <c:when test="${sizeValue==1}">
                                                                                        <dsp:droplet name="AddItemToGiftRegistryDroplet">
                                                                                                <dsp:param name="siteId" value="${appid}"/>
                                                                                                        <dsp:oparam name="output">
                                                                                                            <dsp:droplet name="ForEach">
                                                                                                                <dsp:param name="array" param="registrySkinnyVOList" />
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
                                                                                                        </dsp:oparam>
                                                                                                    </dsp:droplet>
                                                                                        <div class="fr addToRegistry">
                                                                                            <div class="<c:if test='${isItemExcluded || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag) || caDisabled}'>button_disabled</c:if>">
                                                                                            	<%-- <input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData omniRegId" value="${regId}" />
                                                                                            	<input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/>
                                                                                            	<input class="btnAjaxSubmitSFL moveToReg" name="123reqRegistryId" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}','${isPersonalized}','${eximCustomizationCodesMap[personalizationOptions]}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" /> --%>
                                                                                             	<input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData omniRegId" value="${regId}" />
    	                                                                                     	<%-- <input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/> --%>
	                                                                                         	<input class="btnAjaxSubmitSFL moveToReg button tiny secondary" name="123reqRegistryId" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}','${isPersonalized}','${eximCustomizationCodesMap[personalizationOptions]}');" role="button" aria-pressed="false" <c:if test='${isItemExcluded || caDisabled || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}'>disabled="disabled"</c:if>/>
                                                                                            </div>
                                                                                        </div>
                                                                                    </c:when>
                                                                            </c:choose>
                                                                        </c:when>
                                                                    </c:choose>
                                                                    </c:if>
                                                                    
                                                            
                                                        </div>
                                                    </div>

                                                    

                                                    <input type="submit" name="btnPickUpInStore" id="btnPickUpInStoreSFL${skuID}" class="hidden pickUpInStoreSFL" value="PickUpInStore" role="button" aria-pressed="false" aria-labelledby="btnPickUpInStoreSFL${skuID}" />
                                                </div>
                                        </li>
                                        <c:if test="${check}">
                                            <dsp:getvalueof id="storeIdCart" bean="CartModifierFormHandler.storeId" />
                                            <dsp:getvalueof id="wishlistItemIdCart" bean="CartModifierFormHandler.wishlistItemId" />
                                            <c:if test="${not empty storeIdCart and not empty wishlistItemIdCart}">
                                                    <div class="savedItemRow saved-item itemFindInStoreUndoLink"></div>
                                            </c:if>
                                            <dsp:include page="saveUndoLink.jsp">
                                                <dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
                                                <dsp:param name="fromWishlist" value="${fromWishlist}"/>
                                                <dsp:param name="quantity" value="${quantityCart}"/>
                                                <dsp:param name="currentCount" value="${countNo}"/>
                                            </dsp:include>
                                        </c:if>
                                    </dsp:oparam>
                                    <dsp:oparam name="outputEnd">
                                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                            <dsp:param name="array" bean="GiftRegistryFormHandler.movedItemMap" />
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="movedItemCount" param="key" />
                                                <dsp:getvalueof var="productID" param="element" />
                                                <dsp:getvalueof bean="GiftRegistryFormHandler.skuId" var="skuId" />
                                                <c:if test="${movedItemCount eq currentCount+1}">
                                                <div id="savedItemID_${skuID}" class="savedItemRow movedItem movedToReg itemMovedToRegUndoLink">
                                                        <div class="prodItemWrapper">
                                                            <p class="fl prodInfo breakWord textLeft">
                                                                <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                                    <dsp:param name="id" value="${productID}" />
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
                                                                <dsp:droplet name="SKUWishlistDetailDroplet">
                                                                    <dsp:param name="siteId" value="${appid}"/>
                                                                    <dsp:param name="skuId" value="${skuId}"/>
                                                                    <dsp:oparam name="output">
                                                                        <dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName" />
                                                                    </dsp:oparam>
                                                                </dsp:droplet>
                                                                <dsp:a iclass="prodName" title="${prodName}" page="${finalUrl}?skuId=${skuId}">${prodName}</dsp:a> has been moved to registry #<dsp:valueof bean="GiftRegistryFormHandler.movedItemRegistryId" />.
                                                            </p>
                                                            <p class="width_2 noMar padRight_10 padTop_10 padBottom_10 fr textRight">

                                                                        <dsp:droplet name="GetRegistryVODroplet">
                                                                            <dsp:param name="siteId" value="${applicationId}"/>
                                                                            <dsp:param bean="GiftRegistryFormHandler.movedItemRegistryId" name="registryId" />
                                                                            <dsp:oparam name="output">
                                                                                  <dsp:getvalueof var="registryVO" param="registryVO"/>

                                                                                    <dsp:droplet name="GetRegistryTypeNameDroplet">
                                                                                        <dsp:param name="siteId" value="${applicationId}"/>
                                                                                        <dsp:param name="registryTypeCode" value="${registryVO.registryType.registryTypeName}"/>
                                                                                        <dsp:oparam name="output">
                                                                                            <dsp:a iclass="lnkAction viewRegistry" href="/tbs/giftregistry/view_registry_owner.jsp" title="View your registry">View your registry
                                                                                                <dsp:param name="registryId" bean="GiftRegistryFormHandler.movedItemRegistryId"/>
                                                                                                <dsp:param name="eventType" param="registryTypeName"/>
                                                                                            </dsp:a>
                                                                                        </dsp:oparam>
                                                                                    </dsp:droplet>

                                                                            </dsp:oparam>
                                                                        </dsp:droplet>
                                                            </p>
                                                            
                                                        </div>
                                                </div>
                                                </c:if>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </div>
                            
                            </dsp:form>
                        </div>
                        
                        </div>
                        </div>
                    </dsp:oparam>
                </dsp:droplet>
                <script type="text/javascript">
		$(document).ready(function(){
			$(".nearby-stores").attr("data-reveal-id","nearbyStore");
			$(".nearby-stores").attr("data-reveal-ajax","true");
			$(document).foundation('reflow');
		});
	</script>
</dsp:page>
