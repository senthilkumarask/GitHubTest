<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:getvalueof var="maxCount" param="BBBProductListVO.bbbProductCount" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="categoryVO" param="categoryVO"/>
	
	<c:set var="rowIndex" value="1" />
	<dsp:getvalueof var="promoSC" param="promoSC"/>
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<c:set var="lbl_product_quick_view"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_compare_product"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_reviews_count"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_review_count"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_grid_write_review_link"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_was_text"><bbbl:label key="lbl_was_text" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:getvalueof var="siteId" bean="/atg/multisite/SiteContext.site.id" />

	<%-- Imran Chnage Start --%>	
	<c:set var="WAS"><bbbc:config key="dynamicPriceLabelWas" configName="ContentCatalogKeys" /></c:set>	
	<c:set var="ORIG"><bbbc:config key="dynamicPriceLabelOrig" configName="ContentCatalogKeys" /></c:set>
	
	<c:set var="isDynamicPriceEnabled"><bbbc:config key="FlagDrivenFunctions" configName="DynamicPricing" /></c:set>	
	
	<c:set var="lblDiscountedIncart"><bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblWasText"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOrigText"><bbbl:label key='lbl_orig_text' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblPVOrigText"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblInCartOrigText"><bbbl:label key="lbl_discount_incart_orig_text" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="compareCustomizationText">
		<bbbc:config key="CompareCustomizationText" configName="EXIMKeys"/>
	</c:set>
	

	
	<%-- R2.2 Stroy 116. Setting variables based on grid size of PLP --%>
	<c:choose>
	   <%-- Adding condition for new 4x4 view |List View Redesign Story |Sprint2 |START --%>
	    <c:when test="${plpGridSize == '4'}">
			<c:choose>
				<c:when test="${not empty categoryVO.bccManagedPromoCategoryVO}">
					<c:set var="gridClass" value=""/>
					<c:set var="liGridClass" value="grid_3"/>
					<c:set var="is_gridview_3x3" value="false"/>
					<c:set var="imageSize" value="170"/>
					<c:set var="gridSize" value="4"/>
					<c:set var="parentGridClass" value="grid_12"/>
				</c:when>
				<c:otherwise>
					<c:set var="gridClass" value=""/>
					<c:set var="liGridClass" value="grid_2_3"/>
					<c:set var="is_gridview_3x3" value="false"/>
					<c:set var="imageSize" value="170"/>
					<c:set var="gridSize" value="4"/>
					<c:set var="parentGridClass" value="grid_9"/>
				</c:otherwise>	
			</c:choose>			
		</c:when>
		<%-- Adding condition for new 4x4 view  |List View Redesign Story |Sprint2 |End --%> 
		<c:when test="${plpGridSize == '3'}">		
			<c:choose>			
				<c:when test="${not empty categoryVO.bccManagedPromoCategoryVO}">
					<c:set var="gridClass" value=""/>
					<c:set var="liGridClass" value="grid_3_4"/>
					<c:set var="is_gridview_3x3" value="true"/>
					<c:set var="imageSize" value="229"/>
					<c:set var="gridSize" value="3"/>
					<c:set var="parentGridClass" value="grid_12"/>
				</c:when>
				<c:otherwise>
					<c:set var="gridClass" value="grid_3"/>
					<c:set var="liGridClass" value="grid_3"/>
					<c:set var="is_gridview_3x3" value="true"/>
					<c:set var="imageSize" value="229"/>
					<c:set var="gridSize" value="3"/>
					<c:set var="parentGridClass" value="grid_9"/>
				</c:otherwise>	
			</c:choose>			
		</c:when>
		<c:otherwise>
			<c:set var="gridClass" value=""/>
			<c:set var="liGridClass" value="grid_2"/>
			<c:set var="is_gridview_3x3" value="false"/>
			<c:set var="imageSize" value="146"/>
			<c:set var="gridSize" value="5"/>
			<c:set var="parentGridClass" value="grid_10"/>
		</c:otherwise>
	</c:choose>	
	<c:choose>
		<c:when test="${promoSC and not is_gridview_3x3}">
			<c:set var="promoGridClass" value="grid_8"/>
		    <c:if test="${plpGridSize == '4'}">
				<c:set var="promoGridClass" value="grid_10"/>
			</c:if>
			<div class="<c:out value="${promoGridClass}"/> alpha <c:out value="${gridClass}"/>">
		</c:when>
		<c:otherwise>
			<div class="<c:out value="${parentGridClass}"/> noMar <c:out value="${gridClass}"/>">
		</c:otherwise>
	</c:choose>
	<c:set var="globalShipMsgDisplayOn" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<%-- R2.2 Story - 178-a Product Comparison tool Changes | Check if compare functionality is enabled --%>
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set> 
	<c:set var="featuredProductMinCount" scope="request"><bbbc:config key="featured_product_display_count" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="promoKeyCenter" scope="request"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
    <c:set var="prodCount"><bbbc:config key="PageLoadProductCountGridView" configName="ContentCatalogKeys" /></c:set>
    <%-- render normal classes/src attribute if disableLazyLoadS7Images is set to "true" --%>
    <c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
        <c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
    </c:if>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="output">
		    <dsp:getvalueof var="productId" param="element.productID" />
			<dsp:getvalueof var="productName" param="element.productName"/>
			<dsp:getvalueof var="prdShipMsgFlag" param="element.shipMsgFlag"/>
			<dsp:getvalueof var="prdDisplayShipMsg" param="element.displayShipMsg"/>
			<dsp:getvalueof var="scene7DomainPath" param="element.sceneSevenURL" />
			<%-- Check for Swatch Flag attribute returned from Search Engine--%>
			<dsp:getvalueof var="swatchFlag" param="element.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="element.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="element.collectionFlag"/>
			<dsp:getvalueof var="promoSC" param="promoSC"/>
			<dsp:getvalueof var="portrait" param="portrait"/>
			<dsp:getvalueof id="count" param="count" />
			<dsp:getvalueof var="vdcFlag" param="element.vdcFlag"/>
			<dsp:getvalueof var="mswpFlag" param="element.mswpFlag"/>
			<c:choose>
			  <c:when test="${count > prodCount}">
			     <%-- render lazy-load classes/src attribute by default (used when disableLazyLoadS7Images flag is "false" or not-defined) --%> 
			     <c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
    			 <c:set var="swatchImageAttrib" scope="request">class="swatchImage lazySwatchLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
			  </c:when>
			  <c:otherwise>
			     <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
    			 <c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
			  </c:otherwise>  
			</c:choose>
			
			<dsp:getvalueof id="productCountDisplayed" param="size" scope="request"/>
			<c:set var="productIdForFeaturedProducts" value="${productId}" scope="request"/>
		 	<c:set var="custClass" value="" />
			<c:choose>
				<c:when test="${promoSC and not is_gridview_3x3}">
					<c:if test="${count % 4 == 1}">
						<div id="row<c:out value="${rowIndex}"/>" class="clearfix prodGridRow">
							<c:set var="custClass" value="alpha" />
					</c:if>
					<c:if test="${count % 4 == 0}">
						<c:set var="custClass" value="omega" />
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${count % gridSize == 1}">
						<div id="row<c:out value="${rowIndex}"/>" class="clearfix prodGridRow">
							<c:set var="custClass" value="alpha noMarLeft" />
					</c:if>
					<c:if test="${count % gridSize == 0}">
						<c:set var="custClass" value="omega noMarRight" />
					</c:if>
				</c:otherwise>
			</c:choose>
			<div class="<c:out value="${liGridClass}"/> product <c:out value="${custClass}"/> registryDataItemsWrap listDataItemsWrap">
				<div class="productShadow"></div>
				<div class="productContent ec_listing">
				<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
					<c:choose>
						<c:when test="${empty seoUrl}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.productID" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:set var="finalUrl" value="${seoUrl}"></c:set>
						</c:otherwise>
					</c:choose>
					<dsp:getvalueof var="pdpUrl" value="${finalUrl}" />
					<dsp:getvalueof var="portraitClass" value=""/>
					
					<%-- R2.2 Stroy 116. Changing image sizes to 229 px if grid size is 3x3 --%>
					<c:if test="${is_gridview_3x3}">
						<dsp:getvalueof var="imageURL" param="element.productImageUrlForGrid3x3"/>
					</c:if>
					<c:if test="${plpGridSize == '4'}">
						<dsp:getvalueof var="imageURL" param="element.productImageUrlForGrid4"/>
					</c:if>
					
					<c:if test="${portrait eq 'true'}">
						<dsp:getvalueof var="portraitClass" value="prodImgPortrait"/>
						<dsp:getvalueof var="imgHeight" value="200"/>
						<dsp:getvalueof var="imageURL" param="element.verticalImageURL"/>
					</c:if>
		        <%--defect fixed by @psin52 --%>
					<c:choose>
					 <c:when test="${frmBrandPage eq true}">
						 <dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}?brandId=${searchTerm}" title="${productName}">
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage lazyLoad loadingGIF" ${prodImageAttrib}="${scene7DomainPath}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="image of ${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="image of ${productName}" width="${imageSize}" height="${imageSize}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					 </c:when>
					<c:when test="${CategoryId eq null}">
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage lazyLoad loadingGIF" ${prodImageAttrib}="${scene7DomainPath}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="image of ${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="image of ${productName}" width="${imageSize}" height="${imageSize}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					</c:when>
					<c:otherwise>
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}?categoryId=${CategoryId}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage lazyLoad loadingGIF" ${prodImageAttrib}="${scene7DomainPath}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="image of ${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="image of ${productName}" width="${imageSize}" height="${imageSize}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					</c:otherwise>
					</c:choose>
					<script>BBB.addPerfMark('ux-primary-action-available');</script>
					<c:choose>
		     	    	<c:when test="${collectionFlag eq 1}">   
		        			<c:set var="quickViewClass" value="showOptionsCollection"/>
		   				</c:when>
						<c:otherwise>
		        			<c:set var="quickViewClass" value="showOptionMultiSku"/>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
							<c:set var="isMultiRollUpFlag" value="true" />	
						</c:when>
						<c:otherwise>
							<c:set var="isMultiRollUpFlag" value="false" />
						</c:otherwise>
					</c:choose>
					<%-- Start :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
					<%@ include file="/search/featured_product.jsp" %>
					<%-- End :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
					
					<%--  Start | R2.2.1 Story 131. Quick View on PLP Search Pages --%>
					<dsp:getvalueof var="prodName" param="element.productName"/>
					<c:choose>
						<c:when test="${plpGridSize == 3}">
							<div class="${liGridClass} alpha padBottom_10 fl quickViewAndCompare">
	                  <span role="button" tabindex="0" aria-label="${lbl_product_quick_view} ${lblOfThe} ${prodName}" class="quickView ${quickViewClass} fl marRight_30 padRight_5"> ${lbl_product_quick_view}</span>
		                    <div class="checkboxItem input clearfix noPad noMar fl noBorder">
		                     <c:if test="${compareProducts eq true}">
		                        <dsp:getvalueof var="productId" param="element.productID"/>
								<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
									<c:choose>
										<c:when test="${inCompareDrawer eq true}">
		                        			<div class="checkbox noMar">
		                            			<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}"  checked = "true" aria-label="${lbl_compare_product} ${prodName}"/>
		                        			</div>
					    <script>
                                             $(document).ready(function(){
                                              $('a[href="#hero"]').attr('tabindex','-1');
                                              $('a[href="#department-navigation"]').attr('tabindex','-1');
                                               $('#compareChkTxt_${productId}').focus();
                                               });
                                            </script>
										
		                        			<div class="label">
		                            			<label for="compareChkTxt_${productId}" class="compareChkTxt">${lbl_compare_product}</label>
		                        			</div>
		                        		</c:when>
		                        		<c:otherwise>
		                        			<div class="checkbox noMar">
		                            			<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}" aria-label="${lbl_compare_product} ${prodName}"/>
		                        			</div>
		                        			<div class="label">
		                            			<label for="compareChkTxt_${productId}" class="compareChkTxt">${lbl_compare_product}</label>
		                        			</div>
		                        		</c:otherwise>
		                        	</c:choose>
		                        </c:if>
		                    </div>
	                             <script>
		                      $('#compareChkTxt_${productId}').change(function(){
                                      var ariaMsg = this.checked ? '<c:out value='${productName}'/> has been added to your compare list' : 'compare <c:out value='${productName}'/>';
                                      $(this).attr('aria-label',ariaMsg);
                                      });
				      </script>
		                    <%-- Data to submit for Add to Cart / Find In Store --%>
							<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
							<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
							<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
							<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
							<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
							<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
							<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
							<input type="hidden" value="${CategoryId}" class="categoryId"/>
							<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
							<input type="hidden" value="" class="selectedRollUpValue"/>
	               		</div>
	               	</c:when>
	               	<c:otherwise>
	               	
	               	    <div class="${liGridClass} alpha padBottom_10 textCenter quickViewAndCompare">
	                    <div class="padTop_10"><span tabindex="0" role="link" aria-label="${lbl_product_quick_view} ${lblOfThe} ${prodName}" class="quickView ${quickViewClass} ">${lbl_product_quick_view}</span></div>
	                    
	                     <c:if test="${compareProducts eq true}">
	                        <dsp:getvalueof var="productId" param="element.productID"/>
							<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
								<c:choose>
									<c:when test="${inCompareDrawer eq true}">	                        			
										<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}"  checked = "true" aria-label="${lbl_compare_product} ${prodName}"/>
										<label for="compareChkTxt_${productId}" class="compareChkTxt">${lbl_compare_product}</label>
									</c:when>
	                        		<c:otherwise>
										<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}" aria-label="${lbl_compare_product} ${prodName}"/>
										<label for="compareChkTxt_${productId}" class="compareChkTxt">${lbl_compare_product}</label>
                                                   <script>
		                                     $('#compareChkTxt_${productId}').change(function(){
                                                     var ariaMsg = this.checked ? '<c:out value='${productName}'/> has been added to your compare list' : 'compare <c:out value='${productName}'/>';
                                                     $(this).attr('aria-label',ariaMsg);
                                                     });
						    </script>
									</c:otherwise>
	                        	</c:choose>
	                        </c:if>
	                    
	                    
	                    <%-- Data to submit for Add to Cart / Find In Store --%>
						<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
						<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
						<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
						<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
						<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
						<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
						<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
						<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
						<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
						<input type="hidden" value="${CategoryId}" class="categoryId"/>
						<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
						<input type="hidden" value="" class="selectedRollUpValue"/>
	               </div>
	               	   
	               	</c:otherwise>
	              </c:choose>
	               
	               <%--  End | R2.2.1 Story 131. Quick View on PLP Search Pages --%>
	               
					<c:if test="${swatchFlag == '1'}">
					<fieldset>
					<legend class="hidden"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></legend>
						<div class="prodSwatchesContainer clearfix">
						<label class="hidden"> <bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
							<span id="radioSel_Color" class="txtOffScreen radioTxtSelectLabel" aria-live="assertive"> </span>
							<ul class="prodSwatches hideSwatchRows" role="radiogroup" aria-label="Colors"> 
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="element.colorSet" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="id" param="key"/>
										<dsp:getvalueof var="size" param="size" />
										<dsp:getvalueof var="countSwatch" param="count" />
										<%-- Added for R2-141 --%>
										<c:choose>
										 <c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
										 <dsp:getvalueof var="colorValue" param="element.color"/>
										   <dsp:getvalueof var="colorParam" value="color"/> 
                                           
                                            <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="color" value="${colorValue}"/>
                                            </c:url>
										  </c:when>
										 <c:otherwise>
										   <dsp:getvalueof var="colorValue" param="element.skuID"/>
										   <dsp:getvalueof var="colorParam" value="skuId"/>
                                           
                                           <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="skuId" value="${colorValue}"/>
                                            </c:url>
										 </c:otherwise>										
										</c:choose>		
										
										
										<dsp:getvalueof var="productUrl" param="element.skuMedImageURL"/>
										<c:if test="${portrait eq 'true'}">
											<dsp:getvalueof var="productUrl" param="element.skuVerticalImageURL"/>
										</c:if>
										<dsp:getvalueof var="swatchUrl" param="element.skuSwatchImageURL"/>
										<dsp:getvalueof var="colorName" param="element.color"/>
										
										<%-- R2.2 Stroy 116. Changing image sizes to 229 px if grid size is 3x3 --%>
										<c:if test="${is_gridview_3x3}">
											<dsp:getvalueof var="productUrl" param="element.skuMedImageUrlForGrid3x3"/>
										</c:if>
										<c:if test="${plpGridSize == '4'}">
											<dsp:getvalueof var="productUrl" param="element.skuMedImageUrlForGrid4"/>
										</c:if>
										<c:choose>
											<c:when test="${not empty productUrl}">
												<li class="fl colorSwatchLi" role="radio" tabindex="${countSwatch == 1 ? 0 : -1}" aria-checked="false"  data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7DomainPath}/${productUrl}" title="${colorName}" data-attr="${colorName}">
													<div class="colorswatch-overlay"> </div> 
													<img ${swatchImageAttrib}="${scene7DomainPath}/${swatchUrl}" height="10" width="10" alt="${colorName}"/>
												</li>
											</c:when>
											<c:otherwise>
												<li class="fl colorSwatchLi" role="radio" tabindex="${countSwatch == 1 ? 0 : -1}" aria-checked="false" data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${imagePath}/_assets/global/images/no_image_available.jpg" title="${colorName}" data-attr="${colorName}">
													<div class="colorswatch-overlay"> </div> 
													<img ${swatchImageAttrib}="${scene7DomainPath}/${swatchUrl}" height="10" width="10" alt="${colorName}"/>
												</li>
											</c:otherwise>
										</c:choose> 	 
									</dsp:oparam>
								</dsp:droplet>
								<div class="clear"></div>
							</ul>
							<c:if test="${(plpGridSize==4 && size > 5) ||  (plpGridSize==3 && size > 7)}">	
								<span class="toggleColorSwatches collapsed"></span>
							</c:if>
						</div>
						</fieldset>
					</c:if>
					
					
				<div class="prodInfo">
					<div class="prodName">
						<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
						<c:choose>
						<c:when test="${empty seoUrl}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.productID" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:set var="finalUrl" value="${seoUrl}"></c:set>
						</c:otherwise>
						</c:choose>
					<c:set var="prodName">
						<dsp:valueof param="element.productName" valueishtml="true"/>
					</c:set>
				 <%--Defect fixed by @psin52 --%>
					<c:choose>
					 <c:when test="${frmBrandPage eq true}">
						  <dsp:a  page="${finalUrl}?brandId=${searchTerm}"  title="${prodName}">
							 <dsp:valueof param="element.productName" valueishtml="true" />
							 </dsp:a>
					 </c:when>
					 <c:when test="${CategoryId eq null}">
					 <dsp:a  page="${finalUrl}"  title="${prodName}">
					 <dsp:valueof param="element.productName" valueishtml="true" />
					 </dsp:a>
					 </c:when>
					 <c:otherwise>
					 <dsp:a  page="${finalUrl}?categoryId=${CategoryId}"  title="${prodName}">
					 <dsp:valueof param="element.productName" valueishtml="true" />
					 </dsp:a>
					 </c:otherwise>
					</c:choose>
					<script>BBB.addPerfMark('ux-primary-action-available');</script>
				</div>
				<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd" language="${pageContext.request.locale.language}"/></c:set>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="element.attributeVO" name="array" />
								<dsp:param name="elementName" value="attributeVO"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="attributeVO" param="attributeVO"/>
									<!--PS-56773 : Checking for customization product attribute with compareCustomizationText -->
									<c:if test="${not empty compareCustomizationText && not empty attributeVO.attributeDescrip && fn:contains(attributeVO.attributeDescrip, compareCustomizationText)}">
										<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd_customize" language="${pageContext.request.locale.language}"/></c:set>
									</c:if>
									<c:choose>
										<c:when test="${isInternationalCustomer eq true}">
											<c:if test="${attributeVO.intlProdAttr eq 'true'}">
												<div class="prodAttribute"><dsp:valueof value="${attributeVO.attributeDescrip}" valueishtml="true"/></div>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${attributeVO.intlProdAttr != 'X' && !(attributeVO.hideAttribute)}">
													<div class="prodAttribute"><dsp:valueof value="${attributeVO.attributeDescrip}" valueishtml="true"/></div>
												</c:when>
												<c:when test="${attributeVO.hideAttribute}">
													<div class="prodAttribute ${attributeVO.skuAttributeId} hidden"><dsp:valueof value="${attributeVO.attributeDescrip}" valueishtml="true"/></div>
												</c:when>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
						
				<c:if test="${BazaarVoiceOn}">
					<dsp:getvalueof var="reviews" param="element.reviews"/>
                    <dsp:getvalueof var="ratingsTitle" param="element.ratingsTitle"/>
					<dsp:getvalueof var="ratings" param="element.ratings" vartype="java.lang.Integer"/>
					<dsp:getvalueof var="rating" param="element.ratingForCSS" vartype="java.lang.Integer"/>
					<c:choose>
						<c:when test="${ratings ne null && ratings ne '0'}">
							<div class="clearfix prodReviews metaFeedback">
								<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"> <span class="ariaLabel">${ratingsTitle}</span>
								</span>
								<span class="prodReviewSpanFont reviewTxt">
								<c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
								   <a href="${contextPath}${pdpUrl}?showRatings=true" title="${productName}" role="link" aria-label="${reviews} ${lbl_reviews_count} ${lblForThe} ${prodName}">
								   	<dsp:valueof param="element.reviews"/>${lbl_reviews_count}
								   </a>
								</c:if>
								<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
								   <a href="${contextPath}${pdpUrl}?showRatings=true" title="${productName}" role="link" aria-label="${reviews} ${lbl_review_count} ${lblForThe} ${prodName}">
									<dsp:valueof param="element.reviews"/>${lbl_review_count}
								   </a>
								</c:if>
								</span>
							</div>
						</c:when>
						<c:otherwise>
							<div class="prodReviews metaFeedback">									
							<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}</span></span><span class="prodReviewSpanFont reviewTxt writeReview">
								<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
								<c:choose>
									<c:when test="${empty seoUrl}">
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="element.productID" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
													param="url" />
											</dsp:oparam>
										</dsp:droplet>
									</c:when>
									<c:otherwise>
										<c:set var="finalUrl" value="${seoUrl}"></c:set>
									</c:otherwise>
								</c:choose>
								<c:set var="writeReviewLink">${lbl_grid_write_review_link}</c:set>
								<a href="${pageContext.request.contextPath}${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}" role="link"  aria-label="${writeReviewLink} ${lblAboutThe} ${prodName}">${writeReviewLink}</a> 
							</span>		
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
				<div class="prodPrice">
					<%-- <dsp:getvalueof var="wasPriceRange" param="element.wasPriceRange"/> --%>
					
						<%--  BBB Dynamic price string logic starts--%>

                                                                  <%@ include file="product_list_dynamic_product_price_frag.jsp" %>

								<%--<dsp:include page="product_list_dynamic_product_price_frag.jsp">
								<dsp:param name="defaultUserCountryCode" value="${defaultUserCountryCode}" />
								
								<dsp:param name="WAS" value="${WAS}" />
								<dsp:param name="ORIG" value="${ORIG}" />
								<dsp:param name="lblDiscountedIncart" value="${lblDiscountedIncart}" />
								<dsp:param name="lblWasText" value="${lblWasText}" />
								<dsp:param name="lblOrigText" value="${lblOrigText}" />
								<dsp:param name="lblPVOrigText" value="${lblPVOrigText}" />
								<dsp:param name="lblInCartOrigText" value="${lblInCartOrigText}" />
								<dsp:param name="lblPriceTBD" value="${lblPriceTBD}" />
								<dsp:param name="isDynamicPriceEnabled" value="${isDynamicPriceEnabled}" />
								
								
								</dsp:include>--%>

							<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand starts --%>
				</div>
				<dsp:getvalueof var="intlRestrictionMssgFlag" param="element.intlRestricted"/>
							
							<c:if test="${isInternationalCustomer and intlRestrictionMssgFlag}">
						
								<div class="notAvailableIntShipMsg plpIntShipMsg cb clearfix"><bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}"/></div>
							</c:if>
					

				<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand ends--%>	
						
						
						<%-- R2.2 Story - 178-a Product Comparison tool Changes : Start --%>
						<c:if test="${globalShipMsgDisplayOn && isInternationalCustomer ne true && prdShipMsgFlag}">
						<div class="freeShipBadge">
						${prdDisplayShipMsg}
						</div>  
						</c:if>
						<%-- <c:if test="${compareProducts eq true}">
							<dsp:getvalueof var="productId" param="element.productID"/>
							<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
							<c:choose>
								<c:when test="${inCompareDrawer eq true}">
									<li><input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}"  checked = "true" />
									<label class="compareChkTxt" for="compareChkTxt_${productId}">${lbl_compare_product}</label>
									</li>
								</c:when>
								<c:otherwise>
									<li><input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}" />
										<label class="compareChkTxt" for="compareChkTxt_${productId}">${lbl_compare_product}</label>
									</li>
								</c:otherwise>
							</c:choose>
						</c:if> --%>
						<%-- R2.2 Story - 178-a Product Comparison tool Changes : End --%>

					<c:choose>
						<c:when test="${inStore eq true && (mswpFlag eq true || collectionFlag eq 1)}">
							<div class="inStoreMayNotBeAvailable">
								<span class="prod-attrib prod-attrib-free-ship"><bbbl:label key="lbl_plp_some_items_may_not_be_available_in_store" language="${pageContext.request.locale.language}"/></span>
							</div>
						</c:when>
						<c:when test="${inStore eq true && (vdcFlag eq true)}">
							<div class="inStoreMayNotBeAvailable">
								<span class="prod-attrib prod-attrib-free-ship"><bbbl:label key="lbl_plp_please_call_for_store_availability" language="${pageContext.request.locale.language}"/></span>
							</div>
						</c:when>
						<c:otherwise>

						</c:otherwise>
					</c:choose>
			</div>
		</div>
	</div>
	<c:choose>
		<c:when test="${promoSC and not is_gridview_3x3}">
			<c:if test="${count % 4 == 0}">
				<dsp:getvalueof var="rowIndex" value="${rowIndex+1}" />
				</div>
			</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${count % gridSize == 0}">
					<dsp:getvalueof var="rowIndex" value="${rowIndex+1}" />
					</div>
				</c:if>
				<c:if test="${count eq maxCount && count % gridSize !=0}">
					</div>
				</c:if>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	</div>
	<script>BBB.addPerfMark('ux-primary-content-displayed');</script>

</dsp:page>
