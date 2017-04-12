<dsp:page>

<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sortSeq"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="regEventDate" param="regEventDate" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/RegistryItemsDisplayDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	
		<style type="text/css">
		.hide {
			display: none;
		}
	</style>
	<c:choose>
   <c:when test="${currentSiteId eq 'BuyBuyBaby'}">
      <svg class='hide'>
         <symbol id='icon-star-icon' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#f75ea2' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
         <symbol id='icon-star-icon_fill' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#3977c5' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
      </svg>
   </c:when>
   <c:otherwise>
      <svg class='hide'>
         <symbol id='icon-star-icon' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#01aef0' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
         <symbol id='icon-star-icon_fill' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#273691' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
      </svg>
   </c:otherwise>
</c:choose>

	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
        <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
        <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="othersCat"><bbbc:config key="DefaultCategoryForRegistry" configName="ContentCatalogKeys" /></c:set>
	<c:set var="enableLTLReg"><bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="enableKatoriFlag"><bbbc:config key="enableKatori" configName="EXIMKeys" /></c:set>
	<c:set var="showC2Flag"><bbbc:config key="${eventType}_C2Enable" configName="ContentCatalogKeys" /></c:set>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="regAddress" value="${fn:escapeXml(param.regAddress)}" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="isInternationalCustomer" param="isInternationalCustomer" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dsp:getvalueof var="regPublic" param="regPublic" />
	<dsp:getvalueof var="expandedCategory" param="expandedCategory"/>
	 <dsp:getvalueof var="registryItemsAll" param="registryItemsAll" />
	<input type="hidden" id="expandedCategory" value="${expandedCategory}"/>
	<dsp:include page="registry_modified_item_form.jsp">
	</dsp:include>
	<c:set var="beforeExpandedCat" value="true"/>
	<dsp:form id="formID1" method="post">
	<dsp:input type="hidden" bean="GiftRegistryFormHandler.size" paramvalue="totEntries" />
	<c:set var="valueIndex">0</c:set>
	<div class="accordionReg1 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg1">
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="ephCategoryBuckets" />
		<dsp:param name="elementName" value="categoryVO"/>
		<dsp:oparam name="output">
		<dsp:getvalueof var="sizeIE" param="size"/>
		<dsp:getvalueof var="countIE" param="count"/>
			<dsp:getvalueof var="bucketName" param="categoryVO.displayName" idtype="java.lang.String" />
			<dsp:getvalueof var="bucketCount" param="categoryVO.registryItemsCount"/>
							<dsp:getvalueof var="catName" param="categoryVO.displayName"/>
							<dsp:getvalueof var="catId" param="categoryVO.categoryId"/>
							<c:choose>
								<c:when test="${catId eq 'other'}">
									<c:set var="addItemFlag">false</c:set>								
								</c:when>
								<c:otherwise>
									<c:set var="addItemFlag">true</c:set>
								</c:otherwise>
							</c:choose>
							<c:set var="showC1" value="false"/>
							<c:choose>
								<c:when test="${view eq '1'}">
									<c:set var="showC1" value="true"/>
								</c:when>
								<c:otherwise>
									<c:if test="${bucketCount > 0}">
										<c:set var="showC1" value="true"/>
									</c:if>
								</c:otherwise>
							</c:choose>
							<c:if test="${showC1}">
							<dsp:getvalueof var="count" value="${bucketCount}"/>
							<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all ${catId}_item"><span class="ui-icon ui-icon-triangle-1-e"></span><span class="accordionTitle block clearfix"><a href="#" class="fl accordionLink">${fn:toLowerCase(catName)}&nbsp;(${count})</a>
							<c:if test="${!empty catId && catId ne 'other'}">
							<c:choose>
            					<c:when test="${currentSiteId == 'BedBathUS'}">
									<dsp:getvalueof var="finalUrl" param="categoryVO.uscategoryURL" />
									<dsp:getvalueof var="isOverriddenURL" param="categoryVO.usOverriddenURL"/>
								</c:when>
								<c:when test="${currentSiteId == 'BuyBuyBaby'}">
									<dsp:getvalueof var="finalUrl" param="categoryVO.babycategoryURL" />
									<dsp:getvalueof var="isOverriddenURL" param="categoryVO.babyOverriddenURL"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="finalUrl" param="categoryVO.cacategoryURL" />
									<dsp:getvalueof var="isOverriddenURL" param="categoryVO.caOverriddenURL"/>
								</c:otherwise>
							</c:choose>
								<a href="<c:if test="${!isOverriddenURL}">${contextPath}</c:if>${finalUrl}" title="${catName}" class="fr addNewItem">
                             		<c:if test="${addItemFlag eq 'true'}">
										<bbbl:label key='lbl_mng_regitem_addmoreitem' language="${pageContext.request.locale.language}" />
									</c:if>
                           		</a>
							</c:if>
							</span></h2>

							
							<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
                            <div id="${catId}_item" class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
                            	<c:choose>
                            		<c:when test="${beforeExpandedCat eq 'false' && expandedCategory ne catId}">
                            			<c:if test="${showLoader}">
	                            			<span class="my-item-loader"></span>
	                           			</c:if>
                            		</c:when>
                            		<c:otherwise>
                            			<c:set var="showLoader">true</c:set>
                            			<c:if test="${expandedCategory eq catId}">
	                            			<c:set var="beforeExpandedCat">false</c:set>
                            			</c:if>
                            			<!-- For Grouping items not belonging to any category in others_category -->
		                            	<dsp:getvalueof var="categoryId" param="categoryVO.categoryId"/>
		                            	<c:if test="${categoryId eq 'other'}">
		                            		<ul class="productDetailList giftViewProduct">
												<dsp:include page="registry_items_list.jsp">
													<dsp:param name="index" value="1"/>
													<dsp:param name="registryItemsList" param="categoryVO.registryItems"/>
												</dsp:include>
		                            		</ul>
		                            	</c:if>
    									<dsp:getvalueof var="c1ChildCatList" param="categoryVO.childCategoryVO"/>
		                            	<c:if test="${showC2Flag && not empty c1ChildCatList}">
			                            <div class="c2-wrapper">
			                            	<a href="javascript:void(0);" role='button' class=" hidden c2-scroll-btn c2-scroll-left disabled" aria-label="scroll left"></a>
		    								<div class="c2-scroll-wrapper">
		    									<div class="c2-cat-header all active" data-menu-content="All" data-ref="" tabindex="-1" aria-hidden='false'>
		    									<bbbl:label key='lbl_all_c2_items' language="${pageContext.request.locale.language}" /></div>
			                            	<dsp:droplet name="ForEach">
												<dsp:param name="array" param="categoryVO.childCategoryVO"/>
												<dsp:param name="elementName" value="c2CategoryVO"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="index" param="index"/>
													<dsp:getvalueof var="c2Category" param="c2CategoryVO"/>
													<c:set var="showC2" value="false"/>
													<c:if test="${c2Category.categoryId ne 'All'}">
														<c:choose>
															<c:when test="${view eq 2 || view eq 3}">
																<c:if test="${c2Category.registryItemsCount > 0}">
																	<c:set var="showC2" value="true"/>	
																</c:if>
															</c:when>
															<c:otherwise>
																<c:set var="showC2" value="true"/>	
															</c:otherwise>
														</c:choose>
													</c:if>
													<c:if test="${showC2}">
											        	<div class="c2-cat-header <c:if test="${c2Category.categoryId eq 'OTHER_C2'}">hide</c:if>" data-menu-content="${c2Category.categoryId}" data-ref="" tabindex="${index}" aria-hidden='false'>${c2Category.displayName}</div>
										        	</c:if>
												</dsp:oparam>
											</dsp:droplet>
											</div>
											<a href="javascript:void(0);" role='button' class="hidden c2-scroll-btn c2-scroll-right" aria-label="scroll right"></a>
			                            </div>
			                            </c:if>
										<c:if test="${count == 0 and catName ne 'other'}">
											<ul class="productDetailList giftViewProduct All">
												<li>
												<div class="add-more-c3">
													<bbbl:label key='lbl_add_more_items_text' language="${pageContext.request.locale.language}" />
												</div>
												<jsp:useBean id="placeHolderMap"
													class="java.util.HashMap" scope="request" />
												<c:set target="${placeHolderMap}"
													property="c1Name"
													value="${catName}" />
												<a href="<c:if test="${!isOverriddenURL}">${contextPath}</c:if>${finalUrl}" class="add-New-Item button-Med btnSecondary">
													<bbbl:label key='lbl_rlv_shop_all_c1' placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
												</a>
										   		</li>
											</ul>
										</c:if>
		                            	<dsp:droplet name="ForEach">
												<dsp:param name="array" param="categoryVO.childCategoryVO"/>
												<dsp:param name="elementName" value="c2CategoryVO"/>
												<dsp:oparam name="output">
														<dsp:getvalueof var="index" param="index"/>
														<dsp:getvalueof var="c2Category" param="c2CategoryVO"/>
														<c:choose>
															<c:when test="${c2Category.registryItemsCount > 0}">
																<c:set var="c2Empty" value=""/>
																<c:set var="hidden" value="" />
															</c:when>
															<c:otherwise>
																<c:set var="c2Empty" value="c2Empty"/>
																<c:set var="hidden" value="hidden" />
															</c:otherwise>
														</c:choose>
														<ul class="productDetailList giftViewProduct ${c2Category.categoryId} ${c2Empty} ${hidden}">
															<!-- c2CategoryVO.registryItems -->
															<c:choose>
																<c:when test="${c2Category.registryItemsCount > 0}">
																	<dsp:include page="registry_items_list.jsp">
																		<dsp:param name="index" value="${index}"/>
																		<dsp:param name="registryItemsList" value="${c2Category.registryItems}"/>
																	</dsp:include>
																	<dsp:droplet name="ForEach">
																		<dsp:param name="array" param="c2CategoryVO.childCategoryVO"/>
																		<dsp:param name="elementName" value="c3CategoryVO"/>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="c3Category" param="c3CategoryVO"/>
																			<dsp:include page="registry_items_list.jsp">
																				<dsp:param name="index" value="${index}"/>
																				<dsp:param name="registryItemsList" value="${c3Category.registryItems}"/>
																			</dsp:include>
																		</dsp:oparam>
																	</dsp:droplet>   
																</c:when>
																<c:otherwise>																       															
       																 <c:choose>
            																<c:when test="${currentSiteId == 'BedBathUS'}">
																				<dsp:getvalueof var="c2Url" param="c2CategoryVO.uscategoryURL" />
																				<dsp:getvalueof var="isOverriddenURLC2" param="c2CategoryVO.usOverriddenURL"/>
																			</c:when>
																			<c:when test="${currentSiteId == 'BuyBuyBaby'}">
																				<dsp:getvalueof var="c2Url" param="c2CategoryVO.babycategoryURL" />
																				<dsp:getvalueof var="isOverriddenURLC2" param="c2CategoryVO.babyOverriddenURL"/>
																			</c:when>
																			<c:otherwise>
																				<dsp:getvalueof var="c2Url" param="c2CategoryVO.cacategoryURL" />
																				<dsp:getvalueof var="isOverriddenURLC2" param="c2CategoryVO.caOverriddenURL"/>
																			</c:otherwise>
																		</c:choose>
																	<li>
																	<div class="add-more-c3">
																		<bbbl:label key='lbl_add_more_items_text' language="${pageContext.request.locale.language}" />
																	</div>
																	<a href="<c:if test="${!isOverriddenURLC2}">${contextPath}</c:if>${c2Url}" class="add-New-Item button-Med btnSecondary">
																		<bbbl:label key='lbl_mng_regitem_addmoreitem' language="${pageContext.request.locale.language}" />
																	</a>
															   		</li>
																</c:otherwise>
															</c:choose>
														</ul>
												</dsp:oparam>
										</dsp:droplet>
                            		</c:otherwise>
                            	</c:choose>
                            </div>
                            </c:if>
							<%-- <c:if test="${count ne 0 }"><li class="noBorder"><a href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a></li></c:if> --%>
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${not empty registryItemsAll}">
		<ul class="productDetailList giftViewProduct">
												<dsp:include page="registry_items_list.jsp">
													<dsp:param name="index" value="1"/>
													<dsp:param name="registryItemsList" value="${registryItemsAll}"/>
												</dsp:include>
		                            		</ul>
											</c:if>
	<dsp:getvalueof var="totEntries" param="totEntries" />
	<dsp:getvalueof var="ephCategoryBuckets" param="ephCategoryBuckets" />
	<dsp:getvalueof var="c1id" param="c1id" />
		<dsp:getvalueof var="addedCount" param="addedCount" /> 
	<c:if test="${empty c1id}">
	<c:choose>
		<c:when test="${empty ephCategoryBuckets}">
			<ul class="emptyExplArea">
				<c:choose>
					<c:when test="${totEntries == 0 && giftsRegistered gt 0 && view ==3}">
						<li class="emptyExplMsg"><bbbl:label key='lbl_ic_mkt_msg'
								language="${pageContext.request.locale.language}" /></li>
						<li class="emptyExplBtnShare">
						<a href="#" class="button-Med btnSecondary checkListShare">
						<bbbl:label
								key='lbl_share_reg_friends'
								language="${pageContext.request.locale.language}" />
						</a>
						</li>
					</c:when>
					<c:when test="${totEntries == 0 && giftsRegistered gt 0 && view ==2}">
						<li class="emptyExplMsg explMsgBrowseAddGifts"><bbbl:label key='lbl_ic_mkt_msg'
							language="${pageContext.request.locale.language}" /></li>
						<li class="emptyExplBtnShare">
						<a href="#" class="button-Med btnSecondary" id="checklistbrowseAddGifts">
							<bbbl:label key="lbl_registry_browse_add_gifts_tab" language="${pageContext.request.locale.language}" />
						</a>
						</li>
						<!-- <li class="emptyExplBtn">
						<a href="../view_registry_owner.jsp" class="seeAllItemLink" id="seeAllItems">
							<bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" />
						</a>
						</li> -->
						<li class="emptyExplBtn">
						<dsp:a href="../view_registry_owner.jsp" id="seeAllItems" bean="GiftRegistryFormHandler.viewEditRegistry" value=""  iclass="seeAllItemLink" requiresSessionConfirmation="false">
							<dsp:param name="registryId" value="${registryId}" />
							<dsp:param name="eventType" value="${eventType}" />
							<bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" />
						</dsp:a>
						</li> 
					</c:when>
					<c:otherwise>
						<li class="emptyExplMsg"><bbbl:label key='lbl_ic_mkt_msg'
								language="${pageContext.request.locale.language}" /></li>
						<li class="emptyExplBtn">
						<a href="#" class="button-Med btnSecondary"><bbbl:label
							key='lbl_explore_checklist'
							language="${pageContext.request.locale.language}" />
						</a>
						</li>
					</c:otherwise>
				</c:choose>
			</ul>
		</c:when>
		<c:otherwise>
		<c:choose>
			<c:when test="${empty totEntries || totEntries == 0 && view ==3}">
				<ul class="emptyExplArea">
					<li class="emptyExplMsg"><bbbl:label key='lbl_ic_mkt_msg'
							language="${pageContext.request.locale.language}" /></li>
					<li class="emptyExplBtnShare"><a href="#"
						class="button-Med btnSecondary checkListShare"><bbbl:label
								key='lbl_share_reg_friends'
								language="${pageContext.request.locale.language}" /></a></li>
				</ul>
			</c:when>

		</c:choose>
		</c:otherwise>
	</c:choose>
	
	</c:if>
	<c:if test="${not empty c1id && (empty addedCount || addedCount == 0)}">
	<ul class="emptyExplArea">
				<li class="emptyExplMsg"><bbbl:label key='lbl_ic_mkt_msg'
						language="${pageContext.request.locale.language}" /></li>
				<li class="emptyExplBtn">
		<a href="#" class="button-Med btnSecondary"><bbbl:label
					key='lbl_explore_reg_checklist'
					language="${pageContext.request.locale.language}" /></a>
					</li>
					</ul>
	</c:if>
		</div>
	</dsp:form>
	<c:set var ="operation"><dsp:valueof bean="GiftRegistryFormHandler.registryItemOperation"/></c:set>
	<c:set var ="removedProdID"><dsp:valueof bean="GiftRegistryFormHandler.removedProductId"/></c:set>
	<c:set var ="addedProdID"><dsp:valueof bean="GiftRegistryFormHandler.productStringAddItemCertona"/></c:set>
	<script type="text/javascript">
   		var operation='${operation}';
			 if ( operation == 'addedCertonaItem') {
	    	var BBB = BBB || {};

	    	BBB.registryInfo = {
	    			registryPagename: "itemAddedToRegistryFromCertona",
	    			products : '${addedProdID}',
	    			var23 : '${fn:escapeXml(eventType)}',
	    			var24 : '${registryId}'
	    		};
	    	}
    </script>

    <script type="text/javascript">
    	var sa_page,
    		sa_instagram_registry_id,
    		sa_instagram_user_email,
			sa_instagram_registry_id,
			sa_instagram_registry_type,
			sa_instagram_registry_is_owner_view,
			sa_array = [],
			sa_s22_instagram_allow_vc_upload,
			sa_photo_upload_products = [];

		function sa_async_load() {
			var sa = document.createElement('script');sa.type = 'text/javascript';
			sa.async = true;sa.src = '${saSrc}';
			var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);
		}

		function triggerImplementationCode(){

			sa_page="1",
			sa_instagram_registry_id= '${registryId}',			
			sa_instagram_user_email= $("#userEmail").val(),
			sa_instagram_registry_id= '${registryId}',
			sa_instagram_registry_type= '${eventType}',
			sa_instagram_registry_is_owner_view= '1',
			sa_array = [],
			sa_s22_instagram_allow_vc_upload = '1';

			$('#formID1').find('.accordionReg').each(function(){
				$(this).find('.accordionDiv').each(function(){
					var _this =  $(this).find('li.grid_12');
				 	_this.each(function() {
				 		if($(this).find('#sa_vc').val() > 0) {
				 			var $this = $(this).find('div.ugc_sa');
							var sa_id = $this.attr('id');
							var prod_id = $this.attr('data-prodId');
							sa_array[sa_id] = prod_id;
						}
				 	});
				});
			});


			sa_photo_upload_products = sa_array;
			console.log(sa_photo_upload_products);

			(function() {
				sa_async_load();
			}());



		}

		
		triggerImplementationCode();

		$(".triggerBVsubmitReview")
		  .on("mouseenter", function() {
		    $(this).find("use").attr("xlink:href", "#icon-star-icon");
		  })
		  .on("mouseleave", function() {
		   $(this).find("use").attr("xlink:href", "#icon-star-icon_fill");
		 });
	</script>
</dsp:page>