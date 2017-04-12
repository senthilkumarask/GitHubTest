<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="registryId" param="registryId"/>
	<dsp:form id="frmRowItemRemove" method="post" action="${contextPath}/giftregistry/frags/registry_modified_item_json.jsp?">
	<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryModified" />
		<dsp:input type="hidden" name="registryId" bean="GiftRegistryFormHandler.value.registryId"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="registryId"/>
		</dsp:input>
		<dsp:input type="hidden" name="skuId" bean="GiftRegistryFormHandler.value.SKU" iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="skuId"/>
		</dsp:input>
		<dsp:input type="hidden" name="prodId" bean="GiftRegistryFormHandler.value.PRODUCT_ID" iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="prodId"/>
		</dsp:input>
		<dsp:input type="hidden" name="rowId" bean="GiftRegistryFormHandler.value.rowId"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="rowId"/>
		</dsp:input>
		<dsp:input type="hidden" name="regItemOldQty" bean="GiftRegistryFormHandler.value.regItemOldQty"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="regItemOldQty"/>
		</dsp:input>
		<dsp:input type="hidden" name="ltlDeliveryPrices" bean="GiftRegistryFormHandler.ltlDeliveryPrices"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlDeliveryPrices"/>
		</dsp:input>
		<dsp:input type="hidden" name="ltlDeliveryServices" bean="GiftRegistryFormHandler.ltlDeliveryServices"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlDeliveryServices"/>
		</dsp:input>
		<dsp:input type="hidden" name="itemTypes" bean="GiftRegistryFormHandler.value.itemTypes"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="itemTypes"/>
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.quantity" type="hidden" name="qty"  iclass="frmAjaxSubmitData" >
            <dsp:tagAttribute name="data-ajax-fieldName" value="quantity"/>
		</dsp:input>
		<dsp:input type="hidden" name="refNum" bean="GiftRegistryFormHandler.value.refNum" iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="refNum"/>
		</dsp:input>
		<%-- <dsp:getvalueof var="successUrlVar" value="${contextPath}/giftregistry/frags/registry_modified_item_json.jsp?registryId=${registryId}" />
			
		<dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="${successUrlVar}"/>        
		<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${successUrlVar}" /> --%>
<dsp:input bean="GiftRegistryFormHandler.queryParam" type="hidden" 
					value="registryId=${registryId}"/>
		<dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry" 
				id="remove" 	type="submit" style="display:none"  value="Remove" iclass="hidden"/>
	</dsp:form>

	<dsp:form id="frmRowItemUpdate" method="post" action="${contextPath}/giftregistry/frags/registry_modified_item_json.jsp?">
		<dsp:input type="hidden" name="skuId" bean="GiftRegistryFormHandler.value.SKU" iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="skuId"/>
		</dsp:input>
		<dsp:input type="hidden" name="prodId" bean="GiftRegistryFormHandler.value.PRODUCT_ID" iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="prodId"/>
		</dsp:input>
		<dsp:input type="hidden" name="rowId" bean="GiftRegistryFormHandler.value.rowId"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="rowId"/>
		</dsp:input>
		<dsp:input type="hidden" name="ltlDeliveryServices" bean="GiftRegistryFormHandler.ltlDeliveryServices"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlDeliveryServices"/>
		</dsp:input>
		<dsp:input type="hidden" name="ltlDeliveryPrices" bean="GiftRegistryFormHandler.ltlDeliveryPrices"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlDeliveryPrices"/>
		</dsp:input>
		<dsp:input type="hidden" name="alternateNum" bean="GiftRegistryFormHandler.alternateNum"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="alternateNum"/>
		</dsp:input>
		<dsp:input type="hidden" name="registryId" bean="GiftRegistryFormHandler.value.registryId"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="registryId"/>
		</dsp:input>
		<dsp:input type="hidden" name="regItemOldQty" bean="GiftRegistryFormHandler.value.regItemOldQty"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="regItemOldQty"/>
		</dsp:input>  		
		<dsp:input type="hidden" name="itemTypes" bean="GiftRegistryFormHandler.value.itemTypes"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="itemTypes"/>
		</dsp:input>
		<dsp:input type="hidden" name="refNum" bean="GiftRegistryFormHandler.value.refNum" iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="refNum"/>
		</dsp:input>
		<dsp:input type="hidden" name="updateDslFromModal" bean="GiftRegistryFormHandler.updateDslFromModal"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="updateDslFromModal"/>
		</dsp:input>
		<dsp:input type="hidden" name="itemPrice" bean="GiftRegistryFormHandler.itemPrice"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="itemPrice"/>
		</dsp:input>
        <dsp:input type="hidden" name="modifiedItemIndex" bean="GiftRegistryFormHandler.modifiedItemIndex"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="modifiedItemIndex"/>
		</dsp:input>
		<dsp:input type="hidden" name="modifiedItemQuantity" bean="GiftRegistryFormHandler.modifiedItemQuantity"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="modifiedItemQuantity"/>
		</dsp:input>
		<%-- Client DOM XSRF | Part -1
		<dsp:getvalueof var="successUrlVar" value="${contextPath}/giftregistry/frags/registry_modified_item_json.jsp?registryId=${registryId}" />
		<dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${successUrlVar}" />
		<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${successUrlVar}" /> --%>
<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryModified" />
<dsp:input bean="GiftRegistryFormHandler.queryParam" type="hidden" 
					value="registryId=${registryId}"/>
		<dsp:input bean="GiftRegistryFormHandler.updateRegistryItem"
				id="update"	type="submit" style="display:none"  value="Update" iclass="hidden"/>
	</dsp:form>

	<dsp:form method="post" id="frmRowAddToRegistry" action="${contextPath}/giftregistry/frags/registry_modified_item_json.jsp?">

		<dsp:input bean="GiftRegistryFormHandler.productId" type="hidden" name="prodId"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="prodId"/>
		</dsp:input>
           <dsp:input bean="GiftRegistryFormHandler.skuIds"  type="hidden" name="skuId"  iclass="frmAjaxSubmitData" >
            <dsp:tagAttribute name="data-ajax-fieldName" value="skuId"/>
		</dsp:input>
           <dsp:input bean="GiftRegistryFormHandler.quantity" type="hidden" name="qty"  iclass="frmAjaxSubmitData" >
            <dsp:tagAttribute name="data-ajax-fieldName" value="quantity"/>
		</dsp:input>
           <dsp:input bean="GiftRegistryFormHandler.registryId" type="hidden" name="registryId" value="${registryId}" iclass="" >
            <dsp:tagAttribute name="data-ajax-fieldName" value="registryId"/>
		</dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.refNum" type="hidden" name="refNum"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="refNum"/>
		</dsp:input>
		<dsp:input type="hidden" name="ltlDeliveryServices" bean="GiftRegistryFormHandler.ltlDeliveryServices"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="ltlDeliveryServices"/>
		</dsp:input>
        <dsp:input bean="GiftRegistryFormHandler.personalizationCode" type="hidden" name="personalizationCode"  iclass="frmAjaxSubmitData" >
			<dsp:tagAttribute name="data-ajax-fieldName" value="personalizationCode"/>
		</dsp:input>
     <%--    <dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="${successUrlVar}" iclass="frmAjaxSubmitData" />
        <dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" value="${successUrlVar}" iclass="frmAjaxSubmitData" /> --%>
        <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryModified" />
        <dsp:input bean="GiftRegistryFormHandler.queryParam" type="hidden" 
					value="registryId=${registryId}"/>
		<dsp:input bean="GiftRegistryFormHandler.addItemToGiftRegistryFromCetona" 
				id="update"	type="submit" style="display:none"  value="Add" iclass="hidden"/>	 
				
	</dsp:form>	
	
	<li id="removeUndoRow" class="registeryItemUndoRow" style="display: none;">
		<div class="prodItemWrapper clearfix">
			<p class="small-10 columns prodInfo breakWord textLeft">
				<span class="blueName prodTitle"></span>
				&nbsp;has been removed.&nbsp;&nbsp;
				<a class="undoRemoveRegistryItem" data-ajax-frmid="frmCartUndoSave" title="Undo" href="#">Undo</a> 
				<input type="hidden" name="prodId" class="frmAjaxSubmitData" data-ajax-fieldName="prodId" value="">				
				<input type="hidden" name="quantity" class="frmAjaxSubmitData" data-ajax-fieldName="qty" value="">
				<input type="hidden" name="skuId" class="frmAjaxSubmitData" data-ajax-fieldName="skuId" value="">
				<input type="hidden" name="refNum" class="frmAjaxSubmitData" data-ajax-fieldName="refNum" value="">
				<input type="hidden" name="personalizationCode" class="frmAjaxSubmitData" data-ajax-fieldName="personalizationCode" value="">
				<input type="hidden" name="ltlDeliveryServices" data-ajax-fieldName="ltlDeliveryServices" class="frmAjaxSubmitData" value="" />			
			</p>
		</div>
	</li>
	
</dsp:page>