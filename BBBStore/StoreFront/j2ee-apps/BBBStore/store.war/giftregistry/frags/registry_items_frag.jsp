<dsp:page>

<dsp:getvalueof var="bucketName" param="categoryVO.displayName"
	idtype="java.lang.String" />
<dsp:getvalueof var="bucketCount" param="categoryVO.registryItemsCount" />
<dsp:getvalueof var="catName" param="categoryVO.displayName" />
<dsp:getvalueof var="catId" param="categoryVO.categoryId" />
<dsp:getvalueof var="eventType" param="eventType" />
<dsp:getvalueof var="view" param="view" />
<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="showC2Flag"><bbbc:config key="${eventType}_C2Enable" configName="ContentCatalogKeys" /></c:set>
<c:choose>
	<c:when test="${catId eq 'other'}">
		<c:set var="addItemFlag">false</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="addItemFlag">true</c:set>
	</c:otherwise>
</c:choose>

	<dsp:getvalueof var="count" value="${bucketCount}" />

	<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
	
		
		<dsp:getvalueof var="categoryId" param="categoryVO.categoryId" />
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
	
		<c:if test="${categoryId eq 'other'}">
			<ul class="productDetailList giftViewProduct">
				<dsp:include page="registry_items_list.jsp">
					<dsp:param name="index" value="1" />
					<dsp:param name="registryItemsList"
						param="categoryVO.registryItems" />
				</dsp:include>
			</ul>
		</c:if>
		<dsp:getvalueof var="c1ChildCatMap"
			param="categoryVO.childCategoryVO" />
		<c:if test="${showC2Flag && not empty c1ChildCatMap}">
		<div class="c2-wrapper">
			<a href="javascript:void(0);" role='button'
				class=" hidden c2-scroll-btn c2-scroll-left disabled"
				aria-label="scroll left"></a>
			<div class="c2-scroll-wrapper">
				<div class="c2-cat-header all active" data-menu-content="All"
					data-ref="" tabindex="-1" aria-hidden='false'>
					<bbbl:label key='lbl_all_c2_items'
						language="${pageContext.request.locale.language}" />
				</div>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="categoryVO.childCategoryVO" />
					<dsp:param name="elementName" value="c2CategoryVO" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="index" param="index" />
						<dsp:getvalueof var="c2Category" param="c2CategoryVO" />
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
							<div class="c2-cat-header <c:if test="${c2Category.categoryId eq 'OTHER_C2'}">hide</c:if>"
								data-menu-content="${c2Category.categoryId}" data-ref=""
								tabindex="${index}" aria-hidden='false'>${c2Category.displayName}</div>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</div>
			<a href="javascript:void(0);" role='button'
				class="hidden c2-scroll-btn c2-scroll-right"
				aria-label="scroll right"></a>
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
			<dsp:param name="array" param="categoryVO.childCategoryVO" />
			<dsp:param name="elementName" value="c2CategoryVO" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="index" param="index" />
				<dsp:getvalueof var="c2Category" param="c2CategoryVO" />
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
				<ul
					class="productDetailList giftViewProduct ${c2Category.categoryId} ${c2Empty} ${hidden}">
					<!-- c2CategoryVO.registryItems -->
					<c:choose>
						<c:when test="${c2Category.registryItemsCount > 0}">
							<dsp:include page="registry_items_list.jsp">
								<dsp:param name="index" value="${index}" />
								<dsp:param name="registryItemsList"
									value="${c2Category.registryItems}" />
							</dsp:include>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="c2CategoryVO.childCategoryVO" />
								<dsp:param name="elementName" value="c3CategoryVO" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="c3Category" param="c3CategoryVO" />
									<dsp:include page="registry_items_list.jsp">
										<dsp:param name="index" value="${index}" />
										<dsp:param name="registryItemsList"
											value="${c3Category.registryItems}" />
									</dsp:include>
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${c2Category.categoryId ne 'All'}">
									<c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" param="c2CategoryVO.uscategoryURL" />
													<dsp:getvalueof var="isOverriddenURL" param="c2CategoryVO.usOverriddenURL"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" param="c2CategoryVO.babycategoryURL" />
													<dsp:getvalueof var="isOverriddenURL" param="c2CategoryVO.babyOverriddenURL"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" param="c2CategoryVO.cacategoryURL" />
													<dsp:getvalueof var="isOverriddenURL" param="c2CategoryVO.caOverriddenURL"/>
											</c:otherwise>
										</c:choose>
									
								</c:when>
								<c:otherwise>
								<c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" param="categoryVO.uscategoryURL" />
													<dsp:getvalueof var="isOverriddenURL" param="categoryVO.usOverriddenURL"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" param="categoryVO.babycategoryURL" />
													<dsp:getvalueof var="isOverriddenURL" param="categoryVO.babyOverriddenURL"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" param="categoryVO.cacategoryURL" />
													<dsp:getvalueof var="isOverriddenURL" param="categoryVO.caOverriddenURL"/>
											</c:otherwise>
										</c:choose>
									
								</c:otherwise>
							</c:choose>
							<li>
								<div class="add-more-c3">
									<bbbl:label key='lbl_add_more_items_text'
										language="${pageContext.request.locale.language}" />
								</div> <a href="<c:if test="${!isOverriddenURL}">${contextPath}</c:if>${c2Url}" class="add-New-Item button-Med btnSecondary"> <bbbl:label
										key='lbl_mng_regitem_addmoreitem'
										language="${pageContext.request.locale.language}" />
							</a>
							</li>
						</c:otherwise>
					</c:choose>
				</ul>
			</dsp:oparam>
		</dsp:droplet>

</dsp:page>
