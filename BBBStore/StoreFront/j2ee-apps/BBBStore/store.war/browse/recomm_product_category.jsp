<dsp:page>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="categoryId" param="categoryId" />
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/GetChildAndSiblingsProductsDroplet" />

	<dsp:droplet name="GetChildAndSiblingsProductsDroplet">
		<dsp:param name="productId" param="productId" />
		<dsp:oparam name="output">

			<dsp:getvalueof var="siblingProductDetails"	param="siblingProductDetails" />
			<dsp:getvalueof var="isCollection" param="isCollection" />
		</dsp:oparam>
	</dsp:droplet>
	
	<div id="pdp_cav">
	<div id="botCrossSell-tabs1" class="categoryProductTabsData noBorder">
	<dsp:include page="recommended_products.jsp">
		<dsp:param name="productsVOsList" value="${siblingProductDetails}" />
		<dsp:param name="crossSellFlag" value="true" />
		<dsp:param name="desc" value="You may also like (desc)" />
	</dsp:include>
	</div>
	</div>


</dsp:page>