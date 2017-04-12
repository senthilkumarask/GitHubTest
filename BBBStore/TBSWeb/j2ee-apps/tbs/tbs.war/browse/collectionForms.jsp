<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/selfservice/ChatAskAndAnswerDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

<c:set var="AttributePDPCollection">
	<bbbc:config key="PDPDefaultview" configName="ContentCatalogKeys" />
</c:set>	
<c:set var="defaultView">
<bbbc:config key="PDPDefaultview" configName="ContentCatalogKeys" />
</c:set>

<dsp:getvalueof var="view" param="view"/>
<dsp:getvalueof param="isEverLivingProd" var="isEverLivingProd"/>
    <dsp:getvalueof param="parentProductId" var="parentProductId"/>	
  	<dsp:getvalueof var="appid" bean="Site.id" />
  	<dsp:getvalueof var="poc" param="parentProductId"/>
  	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<dsp:getvalueof var="color" param="color"/>		
	<dsp:droplet name="ChatAskAndAnswerDroplet">
							<dsp:param name="productId" param="parentProductId" />
							<dsp:param name="categoryId" param="categoryId" />
						    <dsp:param name ="poc" param="poc"/>
							<dsp:param name="siteId" value="${appid}" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="PDPAttributesVo" param="PDPAttributesVo"/>
								<c:set var="defaultDisplayType" value="${PDPAttributesVo.defaultDisplayType}" scope="request"/>
							</dsp:oparam>
	</dsp:droplet>  
	<c:if test="${not empty isEverLivingProd && isEverLivingProd==true }">
	<c:set var="EverLiving_defaultDisplayType" scope="request"><tpsw:switch tagName="EverLiving_defaultDisplayType"/></c:set>
	<c:choose>
	<c:when test="${not empty EverLiving_defaultDisplayType }">
	<c:set var="defaultDisplayType" value="${EverLiving_defaultDisplayType}" scope="request"/>
	</c:when>
	<c:otherwise>
	<c:set var="defaultDisplayType" value="Grid" scope="request"/>
	</c:otherwise>
	</c:choose>
	</c:if>
                <dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
				<c:if test="${childProducts ne null && not empty childProducts}">
		        <c:choose>
				<c:when test="${defaultDisplayType == 'Grid'}">
					<c:choose>
						<c:when test="${view == 'list'}">
							<div id="shopCollection" class="collectionItemsBox grid_12 clearfix collectionListView">
						</c:when>
						<c:otherwise>
							<div id="shopCollection" class="collectionItemsBox grid_12 clearfix collectionGridView">
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${view == 'grid'}">
							<div id="shopCollection" class="collectionItemsBox grid_12 clearfix collectionGridView">
						</c:when>
						<c:otherwise>
							<div id="shopCollection" class="collectionItemsBox grid_12 clearfix collectionListView">
						</c:otherwise>
					</c:choose>
				</c:otherwise>
				</c:choose>
		
				<c:choose>
					<c:when test="${empty view && empty defaultDisplayType}">
						<c:set var="view" value="grid"/>
					</c:when>
					<c:when test="${empty view}">
						<c:set var="view" value="${fn:toLowerCase(defaultDisplayType)}"/>
					</c:when>
				</c:choose>
				<dsp:form name="collectionForm" id="collectionForm" method="post">
				<div class="row addToCart">
					<div class="small-6 collection-small large-8 columns">
						<h2><bbbl:label key='lbl_pdp_shop_items_collection' language="${pageContext.request.locale.language}" /></h2>
					</div>
					<div class="small-4 cart-small large-3 columns">
						<c:if test="${not isEverLivingProd }">
							<dsp:input type="submit" bean="CartModifierFormHandler.addMultipleItemsToOrder" value="Add All to Cart" iclass="enableOnDOMReady tiny button expand transactional" name="btnAllAddToCart" />
							<dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL" paramvalue="pageURL" type="hidden"/>
							<dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL" paramvalue="pageURL" type="hidden"/>
						</c:if>
					</div>
					<div class="small-2 view-small large-1 columns">
						<div class="sortIcons fr">
						<c:choose>
							<c:when test="${view == 'list'}">
							<div class="small-4 large-6 columns no-padding-right">
								<a class="active layout-icon-list" title="List View" id="listView"><span> </span></a>
							</div>
							<div class="small-8 large-6 columns no-padding">
								<a class="layout-icon-grid" title="Grid View" id="gridView"><span> </span></a>
							</div>
							</c:when>
							<c:otherwise>
							<div class="small-4 large-6 columns no-padding-right">
								<a class="layout-icon-list" title="List View" id="listView"><span> </span></a>
							</div>
							<div class="small-8 large-6 columns no-padding">
								<a class="active layout-icon-grid" title="Grid View" id="gridView"><span> </span></a>
							</div>
							</c:otherwise>
						</c:choose>		
							<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId} " />
						</div>
					</div>
				</div>
				<hr/>
					<a name="collectionItems"></a>
				<c:choose>
				<c:when test="${not empty isEverLivingProd && isEverLivingProd==true }">
				<c:choose>
				<c:when test="${defaultDisplayType == 'Grid'}">
							<c:choose>
								<c:when test="${view == 'list'}">
									<a id="listView" class="hidden"></a>
									<dsp:include page="everLiving_collectionListView.jsp">	
									</dsp:include>
								</c:when>
								<c:otherwise>
									<a id="gridView" class="hidden"></a>
									<dsp:include page="everLiving_collectionGridView.jsp">
									</dsp:include>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${view == 'grid'}">
									<a id="gridView" class="hidden"></a>
									<dsp:include page="everLiving_collectionGridView.jsp">	
									</dsp:include>
								</c:when>
								<c:otherwise>
									<a id="listView" class="hidden"></a>
									<dsp:include page="everLiving_collectionListView.jsp">	
									</dsp:include>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
				</c:when>
				<c:otherwise>
					<%-- <c:choose>
					<c:when test="${defaultDisplayType == 'Grid'}">
						<c:choose>
							<c:when test="${view == 'list'}">
								<a id="listView" class="hidden"></a>
								<dsp:include page="collectionListView.jsp">	
								</dsp:include>
							</c:when>
							<c:otherwise>
								<a id="gridView" class="hidden"></a>
								<dsp:include page="collectionGridView.jsp">
								</dsp:include>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${view == 'grid'}">
								<a id="gridView" class="hidden"></a>
								<dsp:include page="collectionGridView.jsp">	
								</dsp:include>
							</c:when>
							<c:otherwise>
								<a id="listView" class="hidden"></a>
								<dsp:include page="collectionListView.jsp">	
								</dsp:include>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
					</c:choose> --%>
					<dsp:include page="collectionListView.jsp">
						<dsp:param name="color" value="${color}" />
					</dsp:include>
				</c:otherwise>
				</c:choose>
				</dsp:form>
			</div>
		</c:if>
		
</dsp:page>
