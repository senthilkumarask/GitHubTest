<dsp:page>

<!-- BPS-1381 Integrate Declined Tab -->

<script type="text/javascript">
	$(".recommendationCount").css("display", "none");
</script>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RecommendationInfoDisplayDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof var="registryId" value="${fn:escapeXml(param.registryId)}"/>
<dsp:getvalueof var="eventType" value="${fn:escapeXml(param.eventType)}"/>

<dsp:getvalueof var="registryType" param="registryType"/>
<dsp:getvalueof var="regFirstName" param="regFirstName"/>
<dsp:getvalueof var="regEventDate" param="regEventDate"/>
<dsp:getvalueof var="pageNum" param="pageNum" />
<dsp:getvalueof var="eventTypeCode" param="eventTypeCode"/>
<c:set var="pageSize">
	<bbbl:label key="lbl_recommendation_page_size" language="${pageContext.request.locale.language}"></bbbl:label>
</c:set>
<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>
<c:set var="requestURL" value="${pageContext.request.requestURI}"/>
<dsp:getvalueof var="sortOption" value="${fn:escapeXml(param.sortOption)}" />
<c:set var="fullURL" value="${requestURL}?registryId=${registryId}&eventType=${eventType}&recommenderTab=true&pageNum=0"/>
<c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
			</c:set>
<c:set var= "header_class" value= "ui-state-active ui-corner-top" />
<c:set var= "body_class" value= "visible invisible ui-accordion-content-active" />
<div id="pending-registry">
			
				<div class="kickstarterSectionHeader grid_12 pending_recommendation_header noMar">
					<div class="grid_5 alpha">
						<bbbt:textArea key="txt_registry_recommendations"
							language="${pageContext.request.locale.language}"></bbbt:textArea>

					</div>
					<div class="grid_7 omega fr">
						<bbbt:textArea key="txt_registry_recomm_area"
							language="${pageContext.request.locale.language}"></bbbt:textArea>

					</div>
				</div>
				<div class="clear"></div>
				<div class="sort_container">
					<ul class="inner_tab fl">
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="pendingRecommendation"><bbbl:label
									key="lbl_registry_owner_pending"
									language="${pageContext.request.locale.language}" /></a></li>
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="acceptedRecommendation"><bbbl:label
									key="lbl_registry_owner_accepted"
									language="${pageContext.request.locale.language}" /></a></li>
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="declinedRecommendation" class="active"><bbbl:label
									key="lbl_registry_owner_declined"
									language="${pageContext.request.locale.language}" /></a></li>
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="recommendersRecommendation"><bbbl:label
									key="lbl_registry_owner_recommenders"
									language="${pageContext.request.locale.language}" /></a></li>

					</ul>
					<ul class="fr">
						<li class="padTop_5"><span class="padRight_5">
							<bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></span></li>

						<li>
							<select class="uniform" id="recommendationSort" aria-hidden="false">
							
							<option value="${fullURL}&sortOption=date#t=recommendations"
									<c:if test="${sortOption eq 'date'}">selected='selected'</c:if>>
									<bbbl:label key="lbl_sort_by_recommenddate"
										language="${pageContext.request.locale.language}" />
								</option>
								<option value="${fullURL}&sortOption=recommender#t=recommendations"
									<c:if test="${sortOption eq 'recommender' || empty sortOption}">selected="selected"</c:if>>
									<bbbl:label key="lbl_sort_by_recommender"
										language="${pageContext.request.locale.language}" />
								</option>
								<option value="${fullURL}&sortOption=listPrice#t=recommendations"
									<c:if test="${sortOption eq 'listPrice'}">selected="selected"</c:if>>
									<bbbl:label key="lbl_sort_by_recommend_price"
										language="${pageContext.request.locale.language}" />
								</option>
								<option value="${fullURL}&sortOption=category#t=recommendations"
									<c:if test="${sortOption eq 'category'}">selected="selected"</c:if>>
									<bbbl:label key="lbl_sort_by_category"
										language="${pageContext.request.locale.language}" />
								</option>
							</select>
						</li>
					</ul>
				</div>
				<div class="ajaxLoadWrapper" id="registryLoader">
						<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
				</div>
				
	<dsp:droplet name="RecommendationInfoDisplayDroplet">
		<dsp:param name="registryId" value="${registryId}" />
		<dsp:param name="tabId" value="2" />
		<dsp:param name="sortOption" value="${fn:escapeXml(param.sortOption)}" />
		<dsp:param name="pageNumber" value="${pageNum}" />
		<dsp:param name="pageSize" value="${pageSize}" />
		<dsp:param name="eventTypeCode" value="${eventTypeCode}" />
		<c:if test="${(sortOption eq 'recommender') || (empty sortOption)}">
			<dsp:getvalueof var="prevRecommender" param="prevRecommender" />
		</c:if>
		<dsp:oparam name="output">
		 <dsp:getvalueof var="groupByFlag" param="groupByFlag" />
		
				<dsp:getvalueof var="recommendationProductList"
					param="recommendationProduct" />
					
					<c:if test="${groupByFlag eq 'date' }">
						
						<div class="accordionReg1 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons">
                         <h2 class="pending ui-accordion-header ui-helper-reset ${header_class}">
                         <span class="ui-icon ui-icon-triangle-1-e"></span>
                         <span class="accordionTitle block clearfix giftTitle">
                         <a href="#" class="fl accordionLink "><bbbl:label key="lbl_recom_date_sort" language ="${pageContext.request.locale.language}"/></a>
                         </span></h2>
                          <div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ${body_class}">
						</c:if>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${recommendationProductList}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="skuId" param="element.skuId" />
						<dsp:getvalueof var="skuDisplayName"
							param="element.skuDisplayName" />
						<dsp:getvalueof var="skuColor" param="element.skuColor" />
						<dsp:getvalueof var="skuSize" param="element.skuSize" />
						<dsp:param name="displayShipMsg" value="${displayShipMsg}" />
						<dsp:getvalueof var="upc" param="element.upc" />
						<dsp:getvalueof var="comment" param="element.comment" />
						<dsp:getvalueof var="firstName" param="element.firstName" />
						<dsp:getvalueof var="lastName" param="element.lastName" />
						<dsp:getvalueof var="skuListPriceCurrencey" param="element.skuListPrice" />
						<dsp:getvalueof var="recommendedQuantity" param="element.recommendedQuantity" />
						<dsp:getvalueof var="bazaarVoiceVo" param="element.bvProductVO" />
						<dsp:getvalueof var="skuImage" param="element.imageVO.smallImage" />
						<dsp:getvalueof var="productId" param="element.productId" />
						<dsp:getvalueof var="fname_profileId" param="element.fname_profileId" />
						<dsp:getvalueof var="repositoryId" param="element.repositoryId" />
						<dsp:getvalueof var="ltlItem" param="element.ltl" />
						<dsp:getvalueof var="skuSalePrice" param="element.skuSalePrice" />
						<dsp:getvalueof var="regItem" param="element" />

                        <c:set var="skuListPrice"><dsp:valueof converter="currency" value="${skuListPriceCurrencey}" /></c:set>
						
						
						
										<c:choose>

	<c:when test="${groupByFlag eq 'date' }">


		<dsp:include page="declined_recommendation_tab_grpby_recommender_date.jsp"
			flush="true">
			<dsp:param name="skuId" value="${skuId}" />
			<dsp:param name="skuDisplayName" value="${skuDisplayName}" />
			<dsp:param name="skuColor" value="${skuColor}" />
			<dsp:param name="skuSize" value="${skuSize}" />
			<dsp:param name="displayShipMsg" value="${displayShipMsg}" />
			<dsp:getvalueof var="displayShipMsg" param="regItem.displayShipMsg" />
			<dsp:param name="upc" value="${upc}" />
			<dsp:param name="comment" value="${comment}" />
			<dsp:param name="firstName" value="${firstName}" />
			<dsp:param name="lastName" value="${lastName}" />
			<dsp:param name="skuListPrice" value="${skuListPrice}" />
			<dsp:param name="skuSalePrice" value="${skuSalePrice}" />
			<dsp:param name="recommendedQuantity" value="${recommendedQuantity}" />
			<dsp:param name="bazaarVoiceVo" value="${bazaarVoiceVo}" />
			<dsp:param name="skuImage" value="${skuImage}" />
			<dsp:param name="productId" value="${productId}" />
			<dsp:param name="fname_profileId" value="${fname_profileId}" />
			<dsp:param name="repositoryId" value="${repositoryId}" />
			<dsp:param name="ltlItem" value="${ltlItem}" />
			<dsp:param name="scene7Path" value="${scene7Path}" />
			<dsp:param name="regItem" value="${regItem}" />
		</dsp:include>
	</c:when>
	</c:choose>
					</dsp:oparam>
				</dsp:droplet>
					<c:if test="${groupByFlag eq 'category' or groupByFlag eq 'price' or groupByFlag eq 'recommender' }">
									<dsp:include page="declined_recommendation_tab_grpby_category_price.jsp" flush="true" >
								
										<dsp:param name="scene7Path" value="${scene7Path}"/>
									</dsp:include>
								</c:if>
				<input type="hidden" name="prevRecommender_${pageNum}" value="${prevRecommender}"/>
				<input type="hidden" name="sortOption" value="${sortOption}"/>

			<!-- </div> -->
		
			<c:if test="${groupByFlag eq 'date' }">
		</div>
		</div>
	</c:if>
		</dsp:oparam>

		<dsp:oparam name="error">
          <c:if test="${pageNum eq '0'}">
           <div class="emptyRecommenderMsg"><bbbt:textArea key="txt_empty_declined_recommendations" language ="${pageContext.request.locale.language}"/></div>
          </c:if>
        </dsp:oparam>

	</dsp:droplet>
  </div>
</dsp:page>