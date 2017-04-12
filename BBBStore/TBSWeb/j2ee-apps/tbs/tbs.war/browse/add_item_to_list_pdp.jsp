<dsp:page>
	<dsp:importbean
		bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:getvalueof var="jasonObj" param="addItemResults" />
	<dsp:setvalue bean="GiftlistFormHandler.jsonResultString" value="${jasonObj}" />
	<dsp:setvalue bean="GiftlistFormHandler.fromProductPage" value="${true}"/>
	<dsp:setvalue bean="GiftlistFormHandler.addItemToGiftlistLoginURL" value="/tbs/account/login.jsp"/>
	<dsp:setvalue bean="GiftlistFormHandler.addItemListToGiftlist" />
	<dsp:getvalueof
		bean="GiftlistFormHandler.addWishlistSuccessFlag" var="flag" />
	<dsp:getvalueof
		bean="GiftlistFormHandler.totalQuantityAdded" var="totalQuantityAdded" />

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
 <div class="row">
	<dsp:getvalueof	bean="GiftlistFormHandler.omniProdList" var="omniProdList" />
	<dsp:getvalueof	bean="GiftlistFormHandler.referenceNumber" var="refNum" />

	<c:choose>
	<c:when test="${not empty refNum}">
	 <c:set var="isPersonalized" value="true"/>
	</c:when>
	<c:otherwise>
	 <c:set var="isPersonalized" value="false"/>
	</c:otherwise>
	</c:choose>

	<c:set var="itlbl">
		<bbbl:label key="lbl_item_added_to_list" language ="${pageContext.request.locale.language}"/>
	</c:set>
	<c:choose>
		<c:when test="${flag==true}">
			<div title="${totalQuantityAdded} ${itlbl}">
                 <label>
                    ${totalQuantityAdded} ${itlbl}
                </label><br/><br/>
			</div>
			<script type="text/javascript">
				var isPersonalized = ${isPersonalized};
                if (typeof resx === 'object') {
                    var prodList = "${prodList}";
                    resx.event = "wishlist";
                    resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
                }
                if (typeof certonaResx === 'object') { certonaResx.run();  }
                if (typeof additemtolist === 'function') {
                	if('${finalOut}'!== null && '${finalOut}' !== ''){
                	     additemtolist('${finalOut}',isPersonalized);
                	}else{
                		var omniVar = $.parseJSON('${jasonObj}');
                		var finalOut = ';'+omniVar.parentProdId+';;;event38='+omniVar.addItemResults[0].qty+'|event39='+(parseInt(omniVar.addItemResults[0].qty,10) * parseFloat((""+(omniVar.addItemResults[0].price||0)).replace("$",'')))+';eVar30='+omniVar.addItemResults[0].skuId;
                		additemtolist(finalOut,isPersonalized);
                	}
                }
            </script>

		</c:when>
		<c:otherwise>
		<div title="Error occured while adding item(s) to Saved Items">
            <label class="error">
               Error occured while adding item(s) to Saved Items
            </label>
		</div>
		</c:otherwise>
	</c:choose>
       <div class="fl small-6 columns button_active">
			<input class="small button service expand close-modal" type="submit" name="CLOSE" value="CLOSE" role="button" aria-pressed="false" >
	   </div>
 	   <div class="fl small-6 columns bold">
 	      <a href="${contextPath}/wishlist/wish_list.jsp" class="small button secondary column"><bbbl:label key="lbl_overview_saved_items_manage" language ="${pageContext.request.locale.language}"/></a>
 	   </div>
	</div>
</dsp:page>
