<%@ page import="com.bbb.constants.BBBCoreConstants"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/CollegeCategoryDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	
	<c:url var="url" value="/browse/productListing.jsp">
		<c:param name="categoryId" value="cat50001" />
	</c:url>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="currentSiteId" param="currentSiteId" />
	<div class="small-3 column">
		<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BuyBuyBabySite">
			<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BedBathCanadaSite">
			<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BedBathUSStoreFlag">
			<bbbc:config key="BedBathUS" configName="CollegeStoresFlag" />
		</c:set>
		<c:set var="BedBathCanadaStoreFlag">
			<bbbc:config key="BedBathCanada" configName="CollegeStoresFlag" />
		</c:set>
		<c:set var="findACollegeLinks_us">
		<bbbc:config key="findACollegeLinks_BedBathUS" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="findACollegeLinks_ca">
		<bbbc:config key="findACollegeLinks_BedBathCanada" configName="ContentCatalogKeys" />
		</c:set>
		<c:if test="${currentSiteId == TBS_BedBathUSSite && BedBathUSStoreFlag == 1  || currentSiteId == TBS_BedBathCanadaSite && BedBathCanadaStoreFlag == 1}"  >
			<bbbt:textArea key="txt_collegeflyout_find_my_college" language="${pageContext.request.locale.language}"></bbbt:textArea>
		</c:if>

		<%-- <dsp:form method="post" action="https://${pageContext.request.serverName}${contextPath}/giftregistry/registry_creation_form.jsp">
			<dsp:input bean="GiftRegistryFormHandler.registryEventType" value="COL" type="hidden"/>
			<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="/tbs/giftregistry/registry_creation_form.jsp" />
		    <dsp:input type="submit" id="registrySubmit" iclass="hidden" bean="GiftRegistryFormHandler.registryTypes" value=""  >
                <dsp:tagAttribute name="aria-pressed" value="false"/>
                <dsp:tagAttribute name="aria-labelledby" value="registrySubmit"/>
                <dsp:tagAttribute name="role" value="button"/>
            </dsp:input>
		</dsp:form>
		<bbbt:textArea key="txt_collegeflyout_college_registry" language="${pageContext.request.locale.language}"></bbbt:textArea> --%>
	</div>
	<div id="collegeRegistryFlyoutCategories" class="small-5 column">
        <bbbt:textArea key="txt_collegeflyout_shop_our_products" language="${pageContext.request.locale.language}"></bbbt:textArea>
			<dsp:droplet name="CollegeCategoryDroplet">
				<dsp:oparam name="output">
					<dsp:param param="collegeCategories" name="array" />
					<dsp:getvalueof var="subcategoriesList" param="subcategoriesList" />
				<%-- <dsp:valueof param="collegeCategories" /> --%>
					<dsp:droplet name="ForEach">
						<dsp:param param="collegeCategories.subCategories" name="array" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="count" param="count" />
							<dsp:getvalueof var="size" param="size" />
							<ul class="small-6 columns">
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="count" param="count" />
							<dsp:getvalueof var="index" param="index" />

							<dsp:getvalueof var="categoryImage"	param="element.categoryImage" />
							<c:if test="${(index gt 1) and ((index mod 3) eq 0)}">
								</ul>
								<ul class="small-6 columns">
							</c:if>
							<li>
							<dsp:droplet name="CanonicalItemLink">
								<dsp:param name="id" param="element.categoryId" />
								<dsp:param name="itemDescriptorName" value="category" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
									<dsp:getvalueof param="element.categoryName" var="catName" />
									<dsp:getvalueof var="categoryId" param="element.categoryId">
									
									<c:set var="collegiateCat"><bbbc:config key="collegiateMerchCategoryKey_${currentSiteId}" configName="ContentCatalogKeys" /></c:set>
									<c:if test="${categoryId eq collegiateCat}">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="/bbcollege/collegiate_merchandise.jsp" />
									</c:if>
									</dsp:getvalueof>
									<dsp:a iclass="icon" page="${finalUrl}?fromCollege=true">
									<span class="img">
									   <c:choose>
											<c:when test="${empty categoryImage}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="70" width="54" alt="${catName}" title="${catName}" />
											</c:when>
											<c:otherwise>
											<img src="${categoryImage}" class="noImageFound" alt="${catName}" title="${catName}" />
										     </c:otherwise>
								        </c:choose>
									</span>
									<span class="catName">${catName}</span>
									</dsp:a>
								</dsp:oparam>
							</dsp:droplet>
							</li>
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							</ul>
						</dsp:oparam>
					</dsp:droplet>

				</dsp:oparam>
			</dsp:droplet>
	</div>
	<div class="small-4 column otherLinks">
		<bbbt:textArea key="txt_collegeflyout_checklist" language="${pageContext.request.locale.language}"></bbbt:textArea>
		<h3>
			<bbbl:label key="lbl_collegeflyout_productsfor"
				language="${pageContext.request.locale.language}" />
		</h3>
		<p>
			<bbbl:label key="lbl_collegeflyout_comfortable_sleep" language="${pageContext.request.locale.language}" /> 
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200061&fromCollege=true">&raquo; A comfortable sleep</a>--%>
		</p>
		<p>
			<bbbl:label key="lbl_collegeflyout_workarea" language="${pageContext.request.locale.language}" />
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200062&fromCollege=true">&raquo; An inspiring work area</a>--%>
		</p>
		<p>
			<bbbl:label key="lbl_collegeflyoutmore_space" language="${pageContext.request.locale.language}" /> 
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200063&fromCollege=true">&raquo; More space in your dorm</a>--%>
		</p>
		<p>
			 <bbbl:label key="lbl_collegeflyout_keep_sapce_clean" language="${pageContext.request.locale.language}" /> 
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200064&fromCollege=true">&raquo; Keeping my space clean</a>--%>
		</p>
		<p>
			 <bbbl:label key="lbl_collegeflyout_doing_laundry" language="${pageContext.request.locale.language}" /> 
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200065&fromCollege=true">&raquo; Doing laundry</a>--%>
		</p>
		<p>
			 <bbbl:label key="lbl_collegeflyout_brighten_room" language="${pageContext.request.locale.language}" /> 
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200066&fromCollege=true">&raquo; Brightening up a room</a>--%>
		</p>
		<p>
			 <bbbl:label key="lbl_collegeflyout_cold_room" language="${pageContext.request.locale.language}" /> 
				<%-- <a href="${contextPath}/cms/guide_detail.jsp?guideId=200067&fromCollege=true">&raquo; A cold room</a>--%>
		</p>
	</div>
	
	<c:if test="${findACollegeLinks_ca eq true && currentSiteId eq TBS_BedBathCanadaSite}">
	<bbbt:textArea key="txt_shopforcollege_findACollegeLinks" language="${pageContext.request.locale.language}"></bbbt:textArea>
	</c:if>
	<c:if test="${findACollegeLinks_us eq true  && currentSiteId eq TBS_BedBathUSSite}">
	<bbbt:textArea key="txt_shopforcollege_findACollegeLinks" language="${pageContext.request.locale.language}"></bbbt:textArea>
	</c:if>
</dsp:page>