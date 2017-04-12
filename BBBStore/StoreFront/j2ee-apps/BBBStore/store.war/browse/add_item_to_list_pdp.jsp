<dsp:page>
	<dsp:importbean
		bean="/atg/commerce/gifts/GiftlistFormHandler" /> 
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="jasonObj" param="addItemResults" />
	<dsp:getvalueof var="wishlistToggle" param="wishlistToggle" />
	<dsp:getvalueof var="currentItemId" param="currentItemId" />
	<dsp:getvalueof var="ltlDsl" param="ltlDsl" />
	<dsp:getvalueof var="notloggedIn" bean="Profile.transient"/>
	<dsp:setvalue bean="GiftlistFormHandler.jsonResultString" value="${jasonObj}" />
		<dsp:setvalue bean="GiftlistFormHandler.wishlistToggle" value="${wishlistToggle}" />
		<dsp:setvalue bean="GiftlistFormHandler.currentItemId" value="${currentItemId}" />
		<dsp:setvalue bean="GiftlistFormHandler.ltlDsl" value="${ltlDsl}" />
	<dsp:setvalue bean="GiftlistFormHandler.fromProductPage" value="${true}"/>
	<dsp:setvalue bean="GiftlistFormHandler.addItemToGiftlistLoginURL" value="../account/login.jsp"/>
	<dsp:getvalueof var="porchWarning" param="porchWarning" />
	<c:choose>
	<c:when test="${wishlistToggle == null}">
		<dsp:setvalue bean="GiftlistFormHandler.addItemListToGiftlist" />
	</c:when>
	<c:otherwise>
	<dsp:setvalue bean="GiftlistFormHandler.toggleWishListSwitch" />
	</c:otherwise>
	</c:choose>
	<dsp:getvalueof
		bean="GiftlistFormHandler.addWishlistSuccessFlag" var="flag" />
	<dsp:getvalueof
		bean="GiftlistFormHandler.totalQuantityAdded" var="totalQuantityAdded" />
	<c:if test="${wishlistToggle eq false}">
		<dsp:getvalueof
		bean="GiftlistFormHandler.lastItemRemoval" var="lastItemRemoval" />
	</c:if>
	<dsp:getvalueof
		bean="GiftlistFormHandler.prodList" var="prodList" />
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="omniVar" value=""/>	
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" bean="GiftlistFormHandler.omniProdList"/>
		<dsp:param name="elementName" value="prodInfo"/>
		<dsp:oparam name="output">
			<dsp:getvalueof id="prodInfo" param="prodInfo"/>
			<c:set var="finalOut" value="${finalOut}${omniVar}${prodInfo}"/>
			<c:set var="omniVar" value=","/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof	bean="GiftlistFormHandler.omniProdList" var="omniProdList" />
	<c:set var="itlbl">
		<bbbl:label key="lbl_item_added_to_list" language ="${pageContext.request.locale.language}"/>
	</c:set>
	
	<c:set var="removelbl">
		<bbbl:label key="lbl_item_removed_from_list" language ="${pageContext.request.locale.language}"/>
	</c:set>
	<c:if test="${wishlistToggle==false}">
		<div title="${totalQuantityAdded} ${removelbl}">
			</div>
	</c:if>
	<c:choose>
		<c:when test="${flag==true}">
			<div title="${totalQuantityAdded} ${itlbl}">
			</div>
			<script type="text/javascript">
                if (typeof resx === 'object') {
                    var prodList = "${prodList}";
                    resx.event = "wishlist";
                    resx.links='';
                    resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
                }
                if (typeof certonaResx === 'object') { certonaResx.run();  }
                if (typeof additemtolist === 'function') { additemtolist('${finalOut}'); } 
            </script>

            <%-- Check if porch service was added to this product, show msg saying service does not get attached--%>
			<%-- TODO - make label --%>
			<c:if test="${porchWarning eq true}">
            	<h5 class="red padBottom_10">
            		<bbbl:label key="lbl_bbby_porch_pdp_save_for_later" language ="${pageContext.request.locale.language}"/>
            	</h5>
            </c:if>
	
		</c:when>
		<c:otherwise>
		<c:if test="${wishlistToggle==true || wishlistToggle == null}">
			<div class="marBottom_10 errorSFLMessage" title="Error occured while adding item(s) to Saved Items">
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param name="exceptions" bean="GiftlistFormHandler.formExceptions"/>
					<dsp:oparam name="output">
						<dsp:valueof param="message"/>
					</dsp:oparam>
				</dsp:droplet>
			</div>
		</c:if>
		</c:otherwise>
	</c:choose>
	<div class="fl noMarBot clearfix">
       <div class="fl button button_active">
			<input class="close-any-dialog" type="submit" name="CLOSE" value="CLOSE" role="button" aria-pressed="false" >
	   </div>
	   <c:choose>
	   	<c:when test="${notloggedIn}">
	   	      <c:set var="targetUrl" value="${contextPath}/cart/cart.jsp"/>
	   	</c:when>
	   	<c:otherwise>
	   		<c:set var="targetUrl" value="${contextPath}/wishlist/wish_list.jsp"/>
	   	</c:otherwise>
	   	</c:choose>
	   <c:if test="${empty lastItemRemoval || lastItemRemoval eq false}">
			<div class="fl bold marTop_5 marLeft_10">
				<a href="${targetUrl}" class="ctaLink"><bbbl:label key="lbl_overview_saved_items_manage" language ="${pageContext.request.locale.language}"/></a>
			<div class="clear"></div>
			</div>
	   </c:if>
	</div>
	
</dsp:page>


