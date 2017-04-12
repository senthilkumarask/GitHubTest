<%@ page import="com.bbb.commerce.browse.BBBSearchBrowseConstants" %>

<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<c:set var="totalCount" value="0" />
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="<%= BBBSearchBrowseConstants.PRODUCT_VO_LIST %>" />
		<dsp:oparam name="output"><dsp:getvalueof var="imageURL" param="element.productImages.thumbnailImage"/>
			<dsp:getvalueof id="count" param="count"/>
			<c:set var="custClass" value="" />
			<c:if test="${count % 3 == 1}">
				<ul id="row<c:out value="${count}"/>" class="grid_12 prodGridRow">
				<c:set var="custClass" value="alpha" />
			</c:if>
			<c:if test="${count % 3 == 0}">
				<c:set var="custClass" value="omega" />
			</c:if>
			<li class="grid_4 product <c:out value="${custClass}"/>">		
				<div class="productContent">						
						<dsp:droplet
									name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="element.productID" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
											param="url" />
                                            <c:set var="prodName"><dsp:valueof param="element.name" valueishtml="true" /></c:set>
										<dsp:a page="${finalUrl}">
											<c:choose>
											<c:when test="${empty imageURL}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" height="213" width="312"/>
											</c:when>
											<c:otherwise>
												<img src="${scene7Path}/${imageURL}" alt="${prodName}" class="noImageFound" height="213" width="312"/>
											</c:otherwise>
											</c:choose>
										</dsp:a>
									</dsp:oparam>
								</dsp:droplet>			
					<ul class="prodInfo">
				
						<li class="prodName textCenter"><dsp:droplet
									name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="element.productID" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
											param="url" />
										<dsp:a page="${finalUrl}">
											<dsp:valueof param="element.name" valueishtml="true" />
										</dsp:a>
									</dsp:oparam>
								</dsp:droplet></li>				
					</ul>
				</div>
			</li>
			<c:if test="${count % 3 == 0}">
				</ul>
			</c:if>
			<c:set var="totalCount" value="${count}" />
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${totalCount % 3 != 0}">
		</ul>
	</c:if>
</dsp:page>