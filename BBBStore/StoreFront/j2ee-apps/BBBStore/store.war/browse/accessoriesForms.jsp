<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site" />
	<c:set var="AttributePDPCollection">
		<bbbc:config key="PDPDefaultview" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="defaultView">
		<bbbc:config key="PDPDefaultview" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:importbean bean="/com/bbb/selfservice/ChatAskAndAnswerDroplet" />
	<dsp:getvalueof var="view" value="${fn:escapeXml(param.view)}" />
	<dsp:getvalueof value="${fn:escapeXml(param.color)}" var="plpColor" />
	<c:set var="enableKatoriFlag" scope="request">
		<bbbc:config key="enableKatori" configName="EXIMKeys" />
	</c:set>
	<dsp:getvalueof param="isEverLivingProd" var="isEverLivingProd" />
	<c:set var="colorParam" value="" />
	<c:if test="${not empty plpColor}">
		<c:set var="colorParam" value="&color=${plpColor}" />
	</c:if>
	<c:set var="count" value="1" />
	<c:set var="collectionId_Omniture" scope="request"></c:set>
	<dsp:getvalueof param="fromAjax" var="fromAjax" />
	<dsp:getvalueof param="parentProductId" var="parentProductId" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="poc" param="parentProductId" />
	<dsp:getvalueof var="categoryId" param="categoryId" />
	<dsp:getvalueof var="color" value="${fn:escapeXml(param.color)}" />
	<dsp:getvalueof var="bts" value="${fn:escapeXml(param.bts)}"  />
	<dsp:droplet name="ChatAskAndAnswerDroplet">
		<dsp:param name="productId" param="parentProductId" />
		<dsp:param name="categoryId" param="categoryId" />
		<dsp:param name="poc" param="poc" />
		<dsp:param name="siteId" value="${appid}" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="PDPAttributesVo" param="PDPAttributesVo" />
			<c:set var="defaultDisplayType"
				value="${PDPAttributesVo.defaultDisplayType}" scope="request" />
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${not empty isEverLivingProd && isEverLivingProd==true }">
		<c:set var="EverLiving_defaultDisplayType" scope="request">
			<tpsw:switch tagName="EverLiving_defaultDisplayType" />
		</c:set>
		<c:choose>
			<c:when test="${not empty EverLiving_defaultDisplayType }">
				<c:set var="defaultDisplayType"
					value="${EverLiving_defaultDisplayType}" scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="defaultDisplayType" value="Grid" scope="request" />
			</c:otherwise>
		</c:choose>
	</c:if>

	<dsp:getvalueof var="showAccessories" param="showAccessories" />
	<c:if test="${not empty showAccessories && showAccessories}">
		<c:choose>
			<c:when
				test="${not empty isEverLivingProd && isEverLivingProd==true }">
				<dsp:droplet
					name="/com/bbb/commerce/browse/droplet/EverLivingDetailsDroplet">
					<dsp:param name="id" param="parentProductId" />
					<dsp:param name="siteId" value="${appid}" />
					<dsp:param name="registryId" param="registryId" />
					<dsp:param name="poc" value="${poc}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="collectionVO" param="collectionVO" />
					</dsp:oparam>
				</dsp:droplet>
			</c:when>
			<c:otherwise>
				<dsp:droplet
					name="/com/bbb/commerce/browse/droplet/ProductDetailDroplet">
					<dsp:param name="id" param="parentProductId" />
					<dsp:param name="siteId" value="${appid}" />
					<dsp:param name="registryId" param="registryId" />
					<dsp:param name="poc" value="${poc}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="collectionVO" param="collectionVO" />
					</dsp:oparam>
				</dsp:droplet>
			</c:otherwise>
		</c:choose>
		<dsp:param name="collectionVO" value="${collectionVO}" />
	</c:if>

	<dsp:getvalueof var="childProducts"
		value="${collectionVO.childProducts}" />
	<c:if test="${childProducts ne null && not empty childProducts}">
		<c:choose>
			<c:when
				test="${not empty isEverLivingProd && isEverLivingProd==true }">
				<c:choose>
					<c:when test="${defaultDisplayType == 'Grid'}">
						<c:choose>
							<c:when test="${view == 'list'}">
								<div id="shopCollection"
									class="collectionItemsBox container_12 clearfix collectionListView">
							</c:when>
							<c:otherwise>
								<div id="shopCollection"
									class="collectionItemsBox container_12 clearfix collectionGridView">
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${view == 'grid'}">
								<div id="shopCollection"
									class="collectionItemsBox container_12 clearfix collectionGridView">
							</c:when>
							<c:otherwise>
								<div id="shopCollection"
									class="collectionItemsBox container_12 clearfix collectionListView">
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${defaultDisplayType == 'Grid'}">
						<c:choose>
							<c:when test="${view == 'list'}">
								<div id="shopCollection"
									class="collectionItemsBox grid_12 clearfix collectionListView">
							</c:when>
							<c:otherwise>
								<div id="shopCollection"
									class="collectionItemsBox grid_12 clearfix collectionGridView">
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${view == 'grid'}">
								<div id="shopCollection"
									class="collectionItemsBox grid_12 clearfix collectionGridView">
							</c:when>
							<c:otherwise>
								<div id="shopCollection"
									class="collectionItemsBox grid_12 clearfix collectionListView">
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${empty view && empty defaultDisplayType}">
				<c:set var="view" value="grid" />
			</c:when>
			<c:when test="${empty view}">
				<c:set var="view" value="${fn:toLowerCase(defaultDisplayType)}" />
			</c:when>
		</c:choose>
		<dsp:form name="collectionForm" id="collectionForm" method="post">
			<div class="sortIcons fr">
				<c:choose>
					<c:when test="${view == 'list'}">
						<a title="Grid View" id="gridView" role="link"
							href="${finalUrl}?productId=<dsp:valueof param="parentProductId"/>${colorParam}&view=grid&#gridView"></a>
						<a class="active" title="List View" id="listView" role="link"
							href="${finalUrl}?productId=<dsp:valueof param="parentProductId"/>${colorParam}&view=list&#listView"></a>
					</c:when>
					<c:otherwise>
						<a class="active" title="Grid View" id="gridView" role="link"
							href="${finalUrl}?productId=<dsp:valueof param="parentProductId"/>${colorParam}&view=grid&#gridView"></a>
						<a title="List View" id="listView" role="link"
							href="${finalUrl}?productId=<dsp:valueof param="parentProductId"/>${colorParam}&view=list&#listView"></a>
					</c:otherwise>
				</c:choose>

				<input type="hidden" name="parentProdId"
					class="_prodId addItemToRegis addItemToList"
					value="${parentProductId} " />
			</div>
			<a name="collectionItems"></a>
			<h2>
				<bbbl:label key='lbl_pdp_accessories'
					language="${pageContext.request.locale.language}" />
			</h2>

			<c:choose>
				<c:when
					test="${not empty isEverLivingProd && isEverLivingProd==true }">
					<c:choose>
						<c:when test="${defaultDisplayType == 'Grid'}">
							<c:choose>
								<c:when test="${view == 'list'}">
									<dsp:include page="everLiving_accessoriesListView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:when>
								<c:otherwise>
									<dsp:include page="everLiving_accessoriesGridView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${view == 'grid'}">
									<dsp:include page="everLiving_accessoriesGridView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:when>
								<c:otherwise>
									<dsp:include page="everLiving_accessoriesListView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${defaultDisplayType == 'Grid'}">
							<c:choose>
								<c:when test="${view == 'list'}">
									<dsp:include page="accessoriesListView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:when>
								<c:otherwise>
									<dsp:include page="accessoriesGridView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${view == 'grid'}">
									<dsp:include page="accessoriesGridView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:when>
								<c:otherwise>
									<dsp:include page="accessoriesListView.jsp">
										<dsp:param name="fromAjax" value="${fromAjax}"/>
									</dsp:include>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</dsp:form>
		</div>
	</c:if>
</dsp:page>