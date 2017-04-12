<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:getvalueof var="referer" bean="/OriginatingRequest.referer"/>

<c:choose>
	<c:when test="${fn:contains(referer, '/browse/product_details.jsp')}">
		<c:set var="redirectUrl">${fn:substringBefore((fn:replace(referer, 'showpopup=true', 'showpopup=false')),'&_requestid')}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="redirectUrl">${referer}</c:set>
	</c:otherwise>
</c:choose>

	<dsp:form id="recommendPopup"  iclass="productDetails" method="post" action="">
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.recommededFlag" value="true"/>
		<dsp:getvalueof var="prodId" param="productId"/>
		<dsp:getvalueof var="regId" param="regId" />
		<dsp:getvalueof var="skuId" param="skuId"/>

		<h3 class="modalTitle reg"><bbbl:label key="lbl_recommend_for_your_friend" language="${pageContext.request.locale.language}"/> </h3>
			<div class="recommendationQuantity clearfix padtop-10">

				<div class="quantityPDP">
					<div class="spinner">					
				 		<!-- <label id="lblprodDetailDescQty" class="txtOffScreen" for="prodDetailDescQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label> -->
						<a href="#" class="scrollDown down reg" id="prodDetailDescQty" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
						<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
						<input id="quantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl addItemToRegis _qty itemQuantity addItemToList" type="text" name="quantity" role="textbox" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.recommendedQuantity" name="quantity" id="quantity"/>
				 		<!-- <label id="lblprodDetailIncQty" class="txtOffScreen" for="prodDetailIncQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label> -->
				 		<a href="#" class="scrollUp up reg" id="prodDetailIncQty" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>

						<dsp:input type="hidden" bean="GiftRegistryFormHandler.regId" name="regId"  value="${regId}"  />
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.productId" name="prodId" value="${prodId}"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.skuId" name="skuId" value="${skuId}" />
						<dsp:input bean="GiftRegistryFormHandler.recomPopUpSuccessURL" type="hidden" value="${redirectUrl}"/>
	                    <dsp:input bean="GiftRegistryFormHandler.recomPopUpErrorURL" type="hidden" value="${redirectUrl}"/>

					</div>
			  </div>
			</div>
		<p><bbbl:label key="lbl_add_comments_optional" language="${pageContext.request.locale.language}"></bbbl:label> </p>
		<div>
		<dsp:textarea bean="GiftRegistryFormHandler.comment" maxlength="1000" name="comments" id="userComments"/>

		</div>
		<div class="marTop_20 buttonpane clearfix">
			<div>

				<dsp:input bean="GiftRegistryFormHandler.addItemToGiftRegistry" iclass="button-Med btnRegistryPrimary" name="recommend" value="RECOMMEND" type="submit"/>

			</div>
			<a class="buttonTextLink close-any-dialog capitalize" href="#" role="link"><bbbl:label key="lbl_notifyme_cancel" language="${pageContext.request.locale.language}" /></a>
		</div>
	</dsp:form>


</dsp:page>

