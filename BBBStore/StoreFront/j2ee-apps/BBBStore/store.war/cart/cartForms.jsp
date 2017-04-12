<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler" />
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:form formid="frmCartItemRemove" id="frmCartItemRemove" iclass="frmAjaxSubmit clearfix frmCartItemRemove" method="post" action="ajax_handler_cart.jsp">
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.commerceItemId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeCommerceItemId" />
		</dsp:input>
		<dsp:input id="btnRemove" type="submit" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.removeItemFromOrderForms">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeSubmitButton" />
		</dsp:input>
	</dsp:form>
	
	<dsp:form formid="frmCartServiceRemove" id="frmCartServiceRemove" iclass="frmAjaxSubmit clearfix frmCartServiceRemove" method="post" action="ajax_handler_cart.jsp">
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.commerceItemId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeCommerceItemId" />
		</dsp:input>
		<dsp:input id="btnRemove" type="submit" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.removeServiceFromCommerceItem">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeSubmitButton" />
		</dsp:input>
	</dsp:form>

	<dsp:form formid="frmCartAddService" id="frmCartAddService" iclass="frmAjaxSubmit clearfix frmCartAddService" method="post" action="ajax_handler_cart.jsp">
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.commerceItemId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeCommerceItemId" />
		</dsp:input>
		<%--<input type="hidden" class="frmAjaxSubmitData" name="porchPayLoadJson" />--%>
			
		<%----%>
		<dsp:input value="" type="hidden"name="porchPayLoadJson" bean="CartModifierFormHandler.cartPayLoadResultString">			
		</dsp:input>
		
		<dsp:input id="btnRemove" type="submit" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.addServiceToCommerceItem">
			<dsp:tagAttribute name="data-ajax-fieldName" value="removeSubmitButton" />
		</dsp:input>
	</dsp:form> 
	
	<dsp:form formid="frmCartAssociateRegistryContext" id="frmCartAssociateRegistryContext" iclass="frmAjaxSubmit clearfix frmCartAssociateRegistryContext" method="post" action="ajax_handler_cart.jsp">
		 <dsp:input bean="CartModifierFormHandler.fromPage" type="hidden" value="associateRegistry" />
		<%-- Client DOM XSRF
		<dsp:input type="hidden" bean="CartModifierFormHandler.associateRegistryContextSuccessURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.associateRegistryContextErrorURL" value="/store/cart/ajax_handler_cart.jsp" />
		--%>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.buyOffAssociationSkuId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="buyOffAssociationSkuId" />
		</dsp:input>
		<dsp:input id="btnAssociateRegistryContext" type="submit" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.associateRegistryContextWithCart">
			<dsp:tagAttribute name="data-ajax-fieldName" value="associateRegistryContextButton" />
		</dsp:input>
	</dsp:form>
	
	<dsp:form formid="frmCartShipOnline" id="frmCartShipOnline" iclass="frmAjaxSubmit clearfix frmCartShipOnline" method="post" action="ajax_handler_cart.jsp">
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.changeToShipOnlineSuccessURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.changeToShipOnlineErrorURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="BBBShippingGroupFormhandler.commerceItemId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="shipCommerceItemId" />
		</dsp:input>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="BBBShippingGroupFormhandler.oldShippingId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="shipOldShippingId" />
		</dsp:input>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="BBBShippingGroupFormhandler.newQuantity">
			<dsp:tagAttribute name="data-ajax-fieldName" value="shipNewQuantity" />
		</dsp:input>
		<dsp:input id="btnShipThisItem" type="submit" iclass="hidden" bean="BBBShippingGroupFormhandler.changeToShipOnline" />
	</dsp:form>
	<dsp:form formid="frmCartSaveForLater" id="frmCartSaveForLater" iclass="frmAjaxSubmit clearfix frmCartSaveForLater" method="post" action="ajax_handler_cart.jsp">
	   <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="updateGiftlist" />
		<%-- Client DOM XSRF
		<dsp:input type="hidden" bean="GiftlistFormHandler.moveItemsFromCartLoginURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" bean="GiftlistFormHandler.moveItemsFromCartErrorURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" bean="GiftlistFormHandler.moveItemsFromCartSuccessURL" value="/store/cart/ajax_handler_cart.jsp" />  --%>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.currentItemId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveCurrentItemId" />
		</dsp:input>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.quantity">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveQuantity" />
		</dsp:input>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.countNo">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveCount" />
		</dsp:input>
		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.storeId">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveStoreId" />
		</dsp:input>
		<dsp:input id="bts" iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.bts" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveBts" />
		</dsp:input>
		<dsp:input type="submit" name="btnSaveForLater" id="btnSaveForLater" iclass="hidden" bean="GiftlistFormHandler.moveItemsFromCart" value="true" />
	</dsp:form>
	<dsp:form formid="frmCartUndoSave" id="frmCartUndoSave" iclass="frmAjaxSubmit clearfix frmCartUndoSave" method="post" action="ajax_handler_cart.jsp">
		<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
		<dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" value="1" />
		<dsp:input bean="CartModifierFormHandler.undoCheck" type="hidden" value="true" />
		<dsp:input bean="CartModifierFormHandler.fromWishlist" type="hidden" value="true" />
		<dsp:input id="undoCheck" bean="CartModifierFormHandler.undoCheck" type="hidden" value="true" />
		<dsp:input bean="CartModifierFormHandler.fromCart" value="true" iclass="undoSaveData" type="hidden" />

		<dsp:input type="hidden" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.countNo">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoCount" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].productId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoProductId" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].catalogRefId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoCatalogRefId" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].quantity" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoQuantity" />
		</dsp:input>
		<dsp:input id="storeId" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].value.storeId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoStoreId" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.ltlShipMethod" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoLtlShipMethod" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.prevLtlShipMethod" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoPrevLtlShipMethod" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].value.shipMethodUnsupported" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoShipMethodUnsupported" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].value.whiteGloveAssembly" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoWhiteGloveAssembly" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].value.referenceNumber" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoRefNum" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.wishListId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoWishListId" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.wishlistItemId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoWishlistItemId" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.value.registryId" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoRegistryId" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.items[0].value.bts" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoBts" />
		</dsp:input>
		<dsp:input iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.bts" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="saveUndoBts" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.moveWishListItemToOrderForms" type="submit" name="btnUndoSave" id="btnUndoSave" iclass="hidden" value="MOVE TO CART" />
	</dsp:form>
	<dsp:form formid="frmAddRecommendedItem" id="frmAddRecommendedItem" iclass="frmAjaxSubmit clearfix" method="post" action="ajax_handler_cart.jsp">
		<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
		<dsp:input bean="CartModifierFormHandler.addItemCount" iclass="frmAjaxSubmitData hidden" type="hidden" value="1" />
		<dsp:input bean="CartModifierFormHandler.recommItemSelected" iclass="frmAjaxSubmitData hidden" type="hidden" value="true" >
		</dsp:input>
		
		<dsp:input bean="CartModifierFormHandler.items[0].productId" iclass="frmAjaxSubmitData hidden" value="${prodID}" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="prodID" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" iclass="frmAjaxSubmitData hidden" value="${skuID}" type="hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="skuID" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.items[0].quantity" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="cartQuantity" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.fromWishlist" type="hidden" iclass="frmAjaxSubmitData hidden" value="false" />
		<dsp:input bean="CartModifierFormHandler.countNo" type="hidden" iclass="frmAjaxSubmitData hidden">
			<dsp:tagAttribute name="data-ajax-fieldName" value="iCount" />
		</dsp:input>
		<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden" value="associateRegistry" />
		<%-- Client DOM XSRF
		<dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" iclass="frmAjaxSubmitData hidden" type="hidden" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" iclass="frmAjaxSubmitData hidden" type="hidden" value="/store/cart/ajax_handler_cart.jsp" />
		--%>
		<dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" iclass="frmAjaxSubmitData hidden" value="ADD TO CART" />
	</dsp:form>
	
	<dsp:form formid="frmCartItemEdit" id="frmCartItemEdit" iclass="frmAjaxSubmit clearfix frmCartItemEdit" method="post" action="">
        <dsp:input type="hidden" name="commerceItemId" iclass="frmAjaxSubmitData" bean="CartModifierFormHandler.commerceItemId" />
        <dsp:input type="hidden" iclass="" id="personalizationRefNum" bean="CartModifierFormHandler.referenceNumber" />
        <dsp:input id="btnEdit" type="submit" iclass="hidden" bean="CartModifierFormHandler.editPersonalisedItem" />
    </dsp:form>
    
    <dsp:form formid="frmCartRemoveRestrict" id="frmCartRemoveRestrict" iclass="frmAjaxSubmit clearfix frmCartRemoveRestrict" method="post" action="ajax_handler_cart.jsp">
    	 <dsp:input type="hidden" bean="CartModifierFormHandler.setOrderSuccessURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderErrorURL" value="/store/cart/ajax_handler_cart.jsp" />
		
    	 <dsp:input type="hidden" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.updateCartInfoSemiColonSeparated">
			<dsp:tagAttribute name="data-ajax-fieldName" value="updateCartInfoSemiColonSeparated" />
		</dsp:input>
    	 <dsp:input id="btnRemoveRestrict" type="submit" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.updateCart">
			<dsp:tagAttribute name="data-ajax-fieldName" value="updateCartInfoSemiColonSeparated" />
		</dsp:input>
	</dsp:form>
	<dsp:form formid="frmCartRemoveOOS" id="frmCartRemoveOOS" iclass="frmAjaxSubmit clearfix frmCartRemoveOOS" method="post" action="ajax_handler_cart.jsp">
	 <dsp:input type="hidden" bean="CartModifierFormHandler.setOrderSuccessURL" value="/store/cart/ajax_handler_cart.jsp" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderErrorURL" value="/store/cart/ajax_handler_cart.jsp" />
    	 <dsp:input type="hidden" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.updateCartInfoSemiColonSeparated">
			<dsp:tagAttribute name="data-ajax-fieldName" value="updateCartInfoSemiColonSeparatedOOS" />
		</dsp:input>
    	<dsp:input id="btnRemoveRestrictOOS" type="submit" iclass="frmAjaxSubmitData hidden" bean="CartModifierFormHandler.updateCart">
			<dsp:tagAttribute name="data-ajax-fieldName" value="updateCartInfoSemiColonSeparatedOOS" />
		</dsp:input>
	</dsp:form>
	
</dsp:page>