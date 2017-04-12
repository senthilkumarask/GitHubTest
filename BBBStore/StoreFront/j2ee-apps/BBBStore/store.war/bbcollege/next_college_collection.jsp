<%-- this page is included in the product_details.jsp when the product_details.jsp is requested from dorm_room_collection page. --%>
<dsp:page>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean
		bean="/com/bbb/cms/droplet/NextCollegeCollectionDroplet" />
	<dsp:droplet name="NextCollegeCollectionDroplet">
		<dsp:param name="productId" param="productId" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="productId" param="nextCollectionVo.productId" />
			<dsp:getvalueof var="productName" param="nextCollectionVo.name" />
			<dsp:getvalueof var="prodThumbNail" param="nextCollectionVo.productImages.smallImage" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
		<dsp:param name="id" value="${productId}" />
		<dsp:param name="itemDescriptorName" value="product" />
		<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
			<dsp:a iclass="prodNextCollection" page="${finalUrl}" title="Next collection">
				<span class="img">
				<c:choose>
				<c:when test="${empty prodThumbNail}">
					<img height="63" width="63" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productId}" />
				</c:when>
				<c:otherwise>
					<img height="63" width="63" src="${scene7Path}/${prodThumbNail}" alt="${productId}" class="noImageFound"/>
				</c:otherwise>
				</c:choose>
				</span>
				<span class="txt"><bbbl:label key="lbl_next_collection" language="${pageContext.request.locale.language}" /> ${productName}</span>
				<dsp:param name="showFlag" value="1" />
			</dsp:a>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>