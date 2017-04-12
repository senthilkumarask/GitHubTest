<dsp:page>

 <bbb:pageContainer>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

<dsp:droplet name="/atg/commerce/catalog/ProductLookup">
   <dsp:param name="elementName" value="product"/>
   <%-- id would also be a param here but it was passed in --%>
   <dsp:oparam name="output">


<dsp:form action="add_to_cart_test.jsp" method="post">
<input name="id" type="hidden" value='<dsp:valueof param="product.repositoryId"/>'>
<dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="../cart/cart.jsp"/>
<dsp:input bean="CartModifierFormHandler.SessionExpirationURL" type="hidden" value="session_expired.jsp"/>
<table border=1>
<tr>
<td>SKU</td>
<td>Quantity</td>
</tr>
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param name="array" param="product.childSKUs"/>
  <dsp:param name="elementName" value="sku"/>
  <dsp:param name="indexName" value="skuIndex"/>
  <dsp:oparam name="outputStart">
    <dsp:input bean="CartModifierFormHandler.addItemCount" paramvalue="size" type="hidden"/>
  </dsp:oparam>
  <dsp:oparam name="output">
    <tr>
    <td><dsp:valueof param="sku.displayName"/></td>
    <td>
      <dsp:input bean="CartModifierFormHandler.items[param:skuIndex].quantity" size="4" type="text" value="0"/>
      <dsp:input bean="CartModifierFormHandler.items[param:skuIndex].catalogRefId" paramvalue="sku.repositoryId" type="hidden"/>
      <dsp:input bean="CartModifierFormHandler.items[param:skuIndex].productId" paramvalue="product.repositoryId" type="hidden"/>
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" param="sku.type"/>
        <dsp:oparam name="configurableSku">
      <dsp:input bean="CartModifierFormHandler.items[param:skuIndex].commerceItemType" beanvalue="CartModifierFormHandler.configurableItemTypeName" type="hidden"/>
        </dsp:oparam>
      </dsp:droplet>
    </td>
    </tr>
  </dsp:oparam>
</dsp:droplet>
</table>
<BR>
<dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" value="Add To Cart"/>
</dsp:form>

</dsp:oparam>
</dsp:droplet>


</bbb:pageContainer>
</dsp:page>