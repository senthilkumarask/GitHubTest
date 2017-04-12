<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

	<!-- compare container -->
	<input type="hidden" id="numCompareSelected" value="0" />
	<%-- <section class="compare-container">
        <div id="compareDrawer" class="compare-box-wrapper">
            <div class="compare-box row">
                <div class="compare-text-wrapper small-12 large-3 columns no-padding-large">
                    <h3 class="marBottom_5"><bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" /></h3>
                    <span><bbbl:textArea key="txt_compare_product" language="${pageContext.request.locale.language}"/></span>
                </div>
                <c:set var="imageSize" value="63"/>
                <div class="compare-items small-12 large-6 columns">
                    <ul class="small-block-grid-4 compare-grid">
                          <dsp:getvalueof var="items" bean="ProductComparisonList.items"/>
                          <c:set var="size" value="${fn:length(items)}"/>
                              <dsp:droplet name="ForEach">
                                  <dsp:param name="array" value="${items}" />
                                  <dsp:oparam name="output">
                                  <dsp:getvalueof var="productId" param="element.productId"/>
                                  <dsp:getvalueof var="skuId" param="element.skuId"/>
                                  <dsp:getvalueof var="image" param="element.imagePath"/>
                                  <dsp:getvalueof var="mediumImage" param="element.thumbnailImagePath"/>
                                  <dsp:getvalueof var="imgPath" value="${image}"/>
                                  <li class="compare-item" data-productid="${productId}">
                                      <span class="close-button"></span>
                                      <c:choose>
                                      <c:when test="${not empty skuId}">
                                      <img class="compare-image" src="${imgPath}" height="${imageSize}" width="${imageSize}"/>
                                      </c:when>
                                      <c:otherwise>
                                      <img class="compare-image" src="${imgPath}" height="${imageSize}" width="${imageSize}"/>
                                      </c:otherwise>
                                      </c:choose>
                                  </li>
                                  </dsp:oparam>
                              </dsp:droplet>
                          <c:set var="emptySize" value="${4-size}"/>
                          <c:forEach var="i" begin="1" end="${emptySize}">
                          <li class="compare-item empty-item">
                              <span class="close-button"></span>
                              <img class="compare-image" src="${imagePath}/_assets/global/images/compare_add_item.png" alt="Compare Product" />
                          </li>
                          </c:forEach>
                    </ul>
                </div>
                <div class="compare-controls small-12 large-3 columns">
                    <div class="row">
                        <div class="small-6 hide-for-large-up columns">
                            <a href="#" class="remove-product button tiny download expand clear-all">Clear All Selections</a>
                        </div>
                        <div class="small-6 large-12 columns">
                            <a href="${contextPath}/compare/product_comparison.jsp" class="button tiny service expand compare-submit compare-button">
                                <bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" />
                            </a>
                        </div>
                        <div class="show-for-large-up large-12 columns">
                            <a href="#" class="remove-product clear-all"><span></span>Clear All Selections</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
	</section> --%>
	<div id="compareModal" class="reveal-modal small" data-reveal>
		<div class="row">
			<div class="small-12 columns no-padding">
				<h1>Sorry!</h1>
				<h3>Only up to 4 items can be compared at a time.</h3>
				<p class="p-secondary">
					Lorem ipsum dolor sit amet, consectetur adipiscing elit.<br>
					Curabitur posuere mauris vitae nibh pharetra, geugiat sodales ligula.
				</p>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-6 columns">
				<a href="#" class="button small secondary close-modal">go back</a>
			</div>
			<div class="small-12 large-6 columns">
				<a  href="${contextPath}/compare/product_comparison.jsp" class="button small service column compare-submit compare-button">compare products</a>
			</div>
		</div>
		<a class="close-reveal-modal">&#215;</a>
	</div>

	<%-- KP COMMENT START: just commenting in case we want to use labels later --%>
	<section class="compare-container">
	<div id="compareDrawer" class="compare-box-wrapper">
		<div class="compare-box row">
			<div class="compare-text-wrapper small-12 large-3 columns no-padding-large">
                <span class="compare-toggle open compare-open"></span>
				<h3><bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" /></h3>
				<span><bbbl:textArea key="txt_compare_product" language="${pageContext.request.locale.language}"/></span>
			</div>
			<c:set var="imageSize" value="63"/>
			<div class="compare-items small-12 large-6 columns">
            <ul class="small-block-grid-4 compare-grid">
				<dsp:getvalueof var="items" bean="ProductComparisonList.items"/>
				<c:set var="size" value="${fn:length(items)}"/>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${items}" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="productId" param="element.productId"/>
						<dsp:getvalueof var="skuId" param="element.skuId"/>
						<dsp:getvalueof var="image" param="element.imagePath"/>
						<dsp:getvalueof var="mediumImage" param="element.thumbnailImagePath"/>
						<dsp:getvalueof var="imgPath" value="${image}"/>
						<li class="compare-item" data-productid="${productId}">
							<a href="#"><span class='close-button'></span></a>
							<c:choose>
							<c:when test="${not empty skuId}">
							<img class="compare-image" src="${imgPath}" height="${imageSize}" width="${imageSize}"/>
							</c:when>
							<c:otherwise>
							<img class="compare-image" src="${imgPath}" height="${imageSize}" width="${imageSize}"/>
							</c:otherwise>
							</c:choose>
						</li>
						</dsp:oparam>
					</dsp:droplet>
				<c:set var="emptySize" value="${4-size}"/>
				<c:forEach var="i" begin="1" end="${emptySize}">
				<li class="compare-item empty-item">
					<img class="compare-image" src="/_assets/global/images/compare_add_item.png" alt="Compare Product" />
				</li>
				</c:forEach>
            </ul>
			</div>
			<div class="compare-controls small-12 large-3 columns">
				<div class="small-6 hide-for-large-up columns">
                    <a href="#" class="remove-product button tiny download expand clear-all">Clear All Selections</a>
                </div>
                
                <div class="button_active small-6 large-12 columns">
				<a href="${contextPath}/compare/product_comparison.jsp" class="button tiny service column compare-submit compare-button">
					<bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" />
				</a>
				</div>
                
                <div class="show-for-large-up large-12 columns">
                    <a href="#" class="remove-product clear-all"><span></span>Clear All Selections</a>
                </div>
				<%-- <div class="remove-btn fl cb">
					<a href="#" class="remove-product"><span class="remove-icon"></span><span><bbbl:label key="lbl_remove_products" language="${pageContext.request.locale.language}" /></span></a>
				</div> --%>
			</div>
		</div>
	</div>
	</section>
	<%-- KP COMMENT END --%>

</dsp:page>
