<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
	
<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="scene7Path">
    <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<dsp:getvalueof var="recommSkuVO" param="recommSkuVO" />
<dsp:getvalueof var="commItem" param="commItem" />
<c:if test="${commItem.BBBCommerceItem.catalogRefId eq recommSkuVO.cartSkuId}">
	<dsp:getvalueof param="recommSkuVO.recommSKUVO" var="skuVO" />
	<dsp:getvalueof param="recommSkuVO.recommSKUVO.skuId" var="skuId" />
	<dsp:getvalueof param="recommSkuVO.recommSKUVO.displayName" var="displayName" />	
	<dsp:getvalueof param="recommSkuVO.recommSKUVO.parentProdId" var="parentProdId" />
	<dsp:getvalueof param="recommSkuVO.listPrice" var="listPrice"/>
	<dsp:getvalueof param="recommSkuVO.salePrice" var="salePrice"/>
	<dsp:getvalueof param="recommSkuVO.recommSKUVO.skuImages.mediumImage" var="skuImage"/>
    <h3 class="recomProdDescription">           
        <dsp:valueof param="recommSkuVO.comment"/>
    </h3>
	<div class="alert recommendedProduct savedItemRow small-6 columns no-padding">
	
		<%-- get url for item --%>
		<dsp:droplet name="CanonicalItemLink">
           <dsp:param name="id" value="${parentProdId }"  />
           <dsp:param name="itemDescriptorName" value="product" />
           <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
           <dsp:oparam name="output">
           
           		<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />			
				<c:if test="${itemFlagoff or disableLink}">
					<c:set var="finalUrl" value="#"/>
				</c:if>
           
	           	<dsp:droplet name="BBBPriceDisplayDroplet">
	               <dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
	               <dsp:param name="profile" bean="Profile"/>
	               <dsp:oparam name="output">
	                   <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
	               </dsp:oparam>
	           	</dsp:droplet>
	        </dsp:oparam>
	    </dsp:droplet>
		<div class="recomProdImage">
			<dsp:a iclass="prodImg" page="${finalUrl}?skuId=${skuId}" title="${displayName}&nbsp;$${unitListPrice}">
				<c:choose>
					<c:when test="${empty skuImage}">
						<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${displayName}&nbsp;$${unitListPrice}" title="${displayName}&nbsp;$${unitListPrice}" />
					</c:when>
					<c:otherwise>
						<img class="productImage noImageFound" src="${scene7Path}/${skuImage}" alt="${displayName}&nbsp;$${unitListPrice}" title="${displayName}&nbsp;$${unitListPrice}" />
					</c:otherwise>
				</c:choose>
			</dsp:a>
		</div>
		<div class="recomProdName small-12 columns no-padding">
			<dsp:a iclass="" page="${finalUrl}?skuId=${skuId}" title="${displayName}&nbsp;$${unitListPrice}">
				<span class="prodName productName "><c:out value="${displayName}" escapeXml="false" /></span>
			</dsp:a>		
		</div><br/><br/>
		<div class="recomProdPrice  small-6 columns no-padding">
			<span>$${listPrice }</span>
			<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${parentProdId }">
			<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuId }">
			<input type="hidden" name="iCount" class="frmAjaxSubmitData" value="1">
			<input type="hidden" name="isRecommItem" class="frmAjaxSubmitData" value="true">
		</div>
        <div class="qty-spinner quantityBox quantity small-6 columns no-padding">
                <a title="Decrease quantity" class="scrollDown button minus secondary"><span></span></a> 
                <input data-max-value="99" name="cartQuantity" id="pqty${skuId}" title="Enter Quantity" class="quantity-input itemQuantity pqty qty addItemToRegis _qty frmAjaxSubmitData addItemToList escapeHTMLTag resetInitialQTY" type="text" value="1" data-initialqty="1" data-change-store-submit="qty" data-change-store-errors="required digits nonZero"  role="textbox" aria-required="true" aria-describedby="pqty${skuId}" maxlength="2" /> 
                <a title="Increase quantity" class="scrollUp button plus secondary"><span></span></a>
            </div> <br/><br/><br/>
		<div class="recomProdQty small-12 medium-10 columns no-padding">
			<input name="addItemToOrder" type="button" data-ajax-frmid="frmAddRecommendedItem" value="Add to Cart" 
			    class="btnAjaxSubmitSFL moveRecommendedToCart small button transactional expand" aria-pressed="false" role="button" /> 
		</div>
	</div>
</c:if>
</dsp:page>