<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <bbb:pageContainer index="false" follow="false">

		<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param bean="GiftlistFormHandler.formError" name="value" />
			
			<dsp:oparam name="true">
				<font color=cc0000><STRONG><UL>
							<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
								<dsp:param bean="GiftlistFormHandler.formExceptions"
											name="exceptions" /> 
								
								<dsp:oparam name="output">
									<LI><dsp:valueof param="message" />
								
										</dsp:oparam>
							</dsp:droplet>
						</UL></STRONG></font>
			</dsp:oparam>
		</dsp:droplet>

  
<dsp:form action="wish_list.jsp" method="post">
<%-- <dsp:valueof bean="Profile.catalog.id"/> --%>

<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist"/>
<dsp:setvalue paramvalue="wishlist.giftlistItems" param="items"/>
<dsp:setvalue paramvalue="wishlist.id" param="giftlistId"/>

<dsp:input bean="GiftlistFormHandler.giftlistId" paramvalue="giftlistId"
           type="hidden"/>



<table cellpadding=0 cellspacing=0 border=0>
  <tr>
      <td class=box-top-store><b>Add to Wish List</b></td>
  </tr>
  <tr>
      <td class=box>

		 <%-- <input name="id" type="hidden" value='<dsp:valueof param="id"/>'>
		 <input type="hidden" name="itemType" value="product">
		 <input name="itemId" type="hidden" value='<dsp:valueof param="Product.repositoryId"/>'> --%>
		 <%-- Client DOM XSRF
		 <dsp:input bean="GiftlistFormHandler.addItemToGiftlistSuccessURL" type="hidden" value="wish_list.jsp"/>
		 <dsp:input bean="GiftlistFormHandler.addItemToGiftlistErrorURL" type="hidden" value="add_item_to_wishlist.jsp"/>
		  <dsp:input bean="GiftlistFormHandler.addItemToGiftlistLoginURL" type="hidden" value="../account/login.jsp"/>
		 --%>
		<br>
		 Add <dsp:input bean="GiftlistFormHandler.quantity" size="4" type="text" value="1"/><br>
		 Sku Id : <dsp:input bean="GiftlistFormHandler.catalogRefIds" value="xsku1255"/><br>
		 Product Id : <dsp:input bean="GiftlistFormHandler.productId" value="xprod1048"/><br><br>
		
		 <dsp:input bean="GiftlistFormHandler.addItemToGiftlist" type="submit" value="Add Item To Wishlist"/>
		 <dsp:input bean="GiftlistFormHandler.addItemListToGiftlist" type="submit" value="Add Item List To Wishlist"/>
		 
	
    </td>
  </tr>
  
 </table>
 </dsp:form>
</bbb:pageContainer>
</dsp:page>
