<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:form formid="frmSaveItemRemove" id="frmSaveItemRemove" iclass="frmAjaxSubmit clearfix frmSaveItemRemove" method="post" action="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp">
		<dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="itemJustMovedToSave" />
		<%-- Client DOM XSRF
		<dsp:input bean="GiftlistFormHandler.removeItemsFromGiftlistSuccessURL" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
		<dsp:input bean="GiftlistFormHandler.removeItemsFromGiftlistErrorURL" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" /> --%>
		<dsp:input bean="GiftlistFormHandler.removeGiftitemIds" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeGiftitemId" />
		</dsp:input>
		<dsp:input bean="GiftlistFormHandler.giftlistId" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="giftlistId" />
		</dsp:input>
		<dsp:input bean="GiftlistFormHandler.removeItemsFromGiftlist" name="btnRemove" id="btnRemoveSFL0${currentCount}" type="submit" value="Remove" iclass="frmAjaxSubmitData hidden" />
	</dsp:form>
	<dsp:form formid="frmSaveUndoCart" id="frmSaveUndoCart" iclass="frmAjaxSubmit clearfix frmSaveUndoCart" method="post" action="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp">
		<dsp:input type="hidden" bean="GiftlistFormHandler.fromCartPage" iclass="undoMoveToCartData" value="true" />
		<dsp:input type="hidden" bean="GiftlistFormHandler.undoComItemId" iclass="frmAjaxSubmitData">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoCommerceId" />
		</dsp:input>
		<dsp:input bean="GiftlistFormHandler.countNo" type="hidden" iclass="frmAjaxSubmitData">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoCount" />
		</dsp:input>
		<dsp:input bean="GiftlistFormHandler.undoOpt" type="hidden" value="true" iclass="frmAjaxSubmitData" />
		<dsp:input bean="GiftlistFormHandler.quantity" iclass="frmAjaxSubmitData" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoQuantity" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.bts" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoBts" />
		</dsp:input>
		 <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="itemJustMovedToSave" />
		<%-- Client DOM XSRF
		<dsp:input bean="GiftlistFormHandler.moveItemsFromCartLoginURL" iclass="frmAjaxSubmitData" type="hidden" value="/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
		<dsp:input bean="GiftlistFormHandler.moveItemsFromCartSuccessURL" iclass="frmAjaxSubmitData" type="hidden" value="/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
		<dsp:input bean="GiftlistFormHandler.moveItemsFromCartErrorURL" iclass="frmAjaxSubmitData" type="hidden" value="/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" /> --%>
		<dsp:input bean="GiftlistFormHandler.moveItemsFromCart" type="submit" value="MOVE TO CART" id="btnUndoMoveToCart2" iclass="hidden" name="btnUndoMoveToCart" />
	</dsp:form>
	<dsp:form formid="frmSaveForLater" id="frmSaveForLater" iclass="frmAjaxSubmit clearfix frmSaveForLater" method="post" action="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp">
		<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
		<dsp:input bean="CartModifierFormHandler.addItemCount" iclass="frmAjaxSubmitData hidden" type="hidden" value="1" />

		<dsp:input bean="CartModifierFormHandler.items[0].productId" iclass="frmAjaxSubmitData hidden" value="${prodID}" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="prodID" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" iclass="frmAjaxSubmitData hidden" value="${skuID}" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="skuID" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].quantity" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartQuantity" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.fromWishlist" type="hidden" iclass="frmAjaxSubmitData hidden" value="true" />
		<dsp:input bean="CartModifierFormHandler.wishListId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartWishListItemId" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.wishlistItemId" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartWishListItemId" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.registryId" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="registryId" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.referenceNumber" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="refNum" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.ltlShipMethod" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlShipMethod" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.prevLtlShipMethod" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="prevLtlShipMethod" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.shipMethodUnsupported" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="shipMethodUnsupported" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.whiteGloveAssembly" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="whiteGloveAssembly" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.countNo" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="iCount" />
		</dsp:input>
			<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].value.bts" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="btsValue" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.bts" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="btsValue" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden" value="saveForLaterForms" />
        <%-- Client DOM XSRF
		<dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" iclass="frmAjaxSubmitData hidden" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
		<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" iclass="frmAjaxSubmitData hidden" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
		--%>
		<dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" iclass="frmAjaxSubmitData hidden" value="MOVE TO CART" />
	</dsp:form>
	<dsp:form formid="frmRegSaveForLater" id="frmRegSaveForLater" iclass="frmAjaxSubmit clearfix frmRegSaveForLater" method="post" action="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp">
		<dsp:input bean="GiftRegistryFormHandler.skuId" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="skuID" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.ltlDeliveryServices" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlDeliveryServices" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.alternateNumber" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="alternateNum" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.refNum" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="refNum" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.personalizationType" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="personalizationType" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.productId" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="prodID" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.quantity" iclass="frmAjaxSubmitData hidden" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="regQuantity" />
		</dsp:input>
		<dsp:input id="moveItemFromSaveForLater" bean="GiftRegistryFormHandler.moveItemFromSaveForLater" iclass="frmAjaxSubmitData hidden" type="hidden" value="true" />
		<dsp:input bean="GiftRegistryFormHandler.wishListItemId" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="reqWishListItemId" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.movedItemIndex" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="currentCount" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.registryId" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="reqRegistryId" />
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.fromWishListPage" value="${true}" type="hidden" iclass="frmAjaxSubmitData hidden" />
		<dsp:input bean="GiftRegistryFormHandler.moveToRegistryResponseURL" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" iclass="frmAjaxSubmitData hidden" type="hidden" />
		<dsp:input id="btnMoveToRegSel" iclass="frmAjaxSubmitData hidden" bean="GiftRegistryFormHandler.addItemToGiftRegistry" type="submit" value="Move to Registry" />
	</dsp:form>
	
	<dsp:form formid="frmSaveForLaterItemEdit" id="frmSaveForLaterItemEdit" iclass="clearfix frmSaveForLaterItemEdit" method="post" action="/store/wishlist/wish_list.jsp">
        <dsp:input type="hidden" iclass="" id="personalizationRefNum" bean="GiftlistFormHandler.referenceNumber"/>
        <dsp:input type="hidden" iclass="" id="personalizationFullImg" bean="GiftlistFormHandler.fullImagePath" />
        <dsp:input type="hidden" iclass="" id="personalizationThumbImg" bean="GiftlistFormHandler.thumbnailImagePath" />
        <dsp:input type="hidden" iclass="" id="personalizationMFullImg" bean="GiftlistFormHandler.mobileFullImagePath" />
        <dsp:input type="hidden" iclass="" id="personalizationMThumbImg" bean="GiftlistFormHandler.mobileThumbnailImagePath" />
        <dsp:input type="hidden" iclass="" id="personalizationPrice" bean="GiftlistFormHandler.personalizePrice"/>
        <dsp:input type="hidden" iclass="" id="personalizationOptions" bean="GiftlistFormHandler.personalizationOptions" />
        <dsp:input type="hidden" iclass="" id="personalizationDetails" bean="GiftlistFormHandler.personalizationDetails" />
        <dsp:input type="hidden" iclass="" id="personalizationStatus" bean="GiftlistFormHandler.personalizationStatus"/>
        <dsp:input type="hidden" iclass="" id="currentItemId" bean="GiftlistFormHandler.currentItemId" />
        <dsp:input type="hidden" iclass="" id="giftlistId" bean="GiftlistFormHandler.giftlistId" />
        <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="editPersonalisedWishList" />
        <%-- Client DOM XSRF
        <dsp:input bean="GiftlistFormHandler.personalizedEditItemFormsErrorURL" iclass="" type="hidden" value="/store/wishlist/wish_list.jsp" />
        <dsp:input bean="GiftlistFormHandler.personalizedEditItemFormsSuccessURL" iclass="" type="hidden" value="/store/wishlist/wish_list.jsp" /> --%>
        <dsp:input id="btnSFLEdit" type="submit" iclass="hidden" bean="GiftlistFormHandler.editPersonalisedGiftItem" />

    </dsp:form>
</dsp:page>