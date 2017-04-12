<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
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
	<li class="alert alert-info clearfix recommendedProduct savedItemRow">
	
		<%-- get url for item --%>
		<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
           <dsp:param name="id" value="${parentProdId }"  />
           <dsp:param name="itemDescriptorName" value="product" />
           <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
           <dsp:oparam name="output">
           
           		<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />			
				<c:if test="${itemFlagoff or disableLink}">
					<c:set var="finalUrl" value="#"/>
				</c:if>
           
	           	<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
	               <dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
	               <dsp:param name="profile" bean="Profile"/>
	               <dsp:oparam name="output">
	                   <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
	               </dsp:oparam>
	           	</dsp:droplet>
	           	
               	
				
	        </dsp:oparam>
	    </dsp:droplet>
	
	
		<div class="grid_3 recomProdDescription">			
			<dsp:valueof param="recommSkuVO.comment"/>
		</div>
		<div class="grid_1 recomProdImage">
			<dsp:a iclass="prodImg" page="${finalUrl}?skuId=${skuId}" title="${displayName}" onclick="cartAnalyzerCrossSell()" >
				<c:choose>
					<c:when test="${empty skuImage}">
						<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="image of ${displayName}" height="63" width="63" />
					</c:when>
					<c:otherwise>
						<img class="productImage noImageFound" src="${scene7Path}/${skuImage}" alt="image of ${displayName}" height="63" width="63" />
					</c:otherwise>
				</c:choose>
			</dsp:a>
		</div>
		<div class="grid_3 recomProdName">
			<dsp:a iclass="" page="${finalUrl}?skuId=${skuId}" title="${displayName}" onclick="cartAnalyzerCrossSell()">
				<span class="prodName productName "><c:out value="${displayName}" escapeXml="false" /></span>
			</dsp:a>		
		</div>
		<script type="text/javascript">
				function cartAnalyzerCrossSell(){
					if(typeof s != "undefined"){
						s.products = "";
						s_crossSell('Cart Analyzer');
					}	
				}	
		</script>	
		<div class="alpha grid_2 recomProdPrice omega">
		<%--BBBSL-7768 | Displaying Sale price in case of clearance products --%>
		<dsp:getvalueof var="isdynamicPrice" value="${skuVO.dynamicPriceSKU}" />
		<dsp:getvalueof var="priceLabelCode" value="${skuVO.pricingLabelCode}" />
		<dsp:getvalueof var="inCartFlag" value="${skuVO.inCartFlag}" />
		<dsp:include page="/browse/browse_price_frag.jsp">
			    <dsp:param name="priceLabelCode" value="${priceLabelCode}" />
				<dsp:param name="inCartFlag" value="${inCartFlag}" />
				<c:if test="${salePrice ne 0.0}">
				<dsp:param name="salePrice" value="${salePrice}" />
				</c:if>
				<dsp:param name="listPrice" value="${listPrice}" />
				<dsp:param name="isdynamicPriceEligible" value="${isdynamicPrice}" />
		</dsp:include> 						
		
						
			<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${parentProdId }">
			<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuId }">
			<input type="hidden" name="iCount" class="frmAjaxSubmitData" value="1">
		</div>
		<div class="grid_3 fr recomProdQty alpha omega">
		
			<div class="spinner">
            
                <input name="cartQuantity" id="pqty${skuId}" title="Enter Quantity" class="fl pqty qty addItemToRegis _qty frmAjaxSubmitData  addItemToList escapeHTMLTag resetInitialQTY" type="text" value="1" data-initialqty="1" data-change-store-submit="qty" data-change-store-errors="required digits nonZero"  role="textbox" aria-required="true" aria-describedby="pqty${skuId}" maxlength="2" /> 
              
            </div>
		
			<div class="button <c:if test="${appid eq 'BuyBuyBaby'}">button_active button_active_orange</c:if> alpha omega"> 
				 <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
               <dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
               <dsp:oparam name="false">                   
                   	<input name="addItemToOrder"
						type="button" 
						data-ajax-frmid="frmAddRecommendedItem"
						data-skuid="${parentProdId}"
						data-evars="eVar30=${skuId}|eVar15=Cart Analyzer"
						data-events="scAdd"
						value="Add to Cart" 
						class="btnAjaxSubmitSFL moveRecommendedToCart" 
						aria-pressed="false" 
						role="button" /> 
               </dsp:oparam>
               <dsp:oparam name="true">
                   	<input name="addItemToOrder"
						type="button" 
						data-ajax-frmid="frmAddRecommendedItem"
						data-skuid="${parentProdId}"
						data-evars="eVar30=${skuId}|eVar15=Cart Analyzer"
						data-events="scAdd,scOpen"
						value="Add to Cart" 
						class="btnAjaxSubmitSFL moveRecommendedToCart" 
						aria-pressed="false" 
						role="button" /> 
               </dsp:oparam>
            </dsp:droplet> 
						<%-- need this??
							onclick="additemtoorder(';3248277;;;;eVar30=42271901|eVar15=Wish List','scAdd')"   --%>
			</div>
		
		</div>
		

		
	
	</li>
</c:if>