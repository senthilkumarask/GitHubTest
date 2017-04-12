<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof param="addressID" var="addressID"/>


<form id="addressToShipModalForm" method="post" action="#" class="flatform">


 <div class="alert alert-error hidden">
     <p><bbbl:label key="lbl_spc_select_an_address" language="${pageContext.request.locale.language}" /></p>
 </div>

<dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplayShippingAddress">
   <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
   <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>            
   <dsp:param name="addressContainer" bean="/com/bbb/commerce/checkout/ShippingAddContainer"/>
   <dsp:param name="isPackHold" value="${isPackHold}"/> 
   <dsp:oparam name="output">
       
       
      <dsp:droplet name="ForEach">    
        <dsp:param name="array" param="profileAddresses" />
        <dsp:param name="elementName" value="address" />
        <dsp:param name="sortProperties" value="id"/>
        <dsp:oparam name="output">
            <c:set var="containsaddressForUser" scope="request" value="true"/>
            <dsp:getvalueof param="address.identifier" var="currentAddress"/>
            <dsp:getvalueof param="address.id" var="currentAddressID"/>
            <dsp:getvalueof param="index" var="index"/>
    <dsp:droplet name="MapToArrayDefaultFirst">
            			<dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId" />
            			<dsp:param name="defaultId2" bean="Profile.billingAddress.repositoryId" />
            			<dsp:param name="map" bean="Profile.secondaryAddresses" />
            			<dsp:param name="sortByKeys" value="true" />
            			<dsp:param name="currentAddressID" value="${currentAddressID}"/>
            			<dsp:oparam name="output">
            			  <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray" />
            			  <dsp:getvalueof var="nickname" vartype="java.lang.Object" param="nickname" />
	</dsp:oparam>
	</dsp:droplet>
            <div class="radioItem input clearfix noBorder">

              
                
            	<input 
            		type="radio" 
            		name="addressToShipModal" 
            		id="addressToShipModal${index}" 
            		value="${currentAddress}"                  
                class="required"
                data-firstName="<dsp:valueof param="address.firstName" />" 
                data-middleName="<dsp:valueof param="address.middleName" />" 
                data-lastName="<dsp:valueof param="address.lastName" />" 
                data-companyName="<dsp:valueof param="address.companyName" />" 
                data-address1="<dsp:valueof param="address.address1" />" 
                data-address2="<dsp:valueof param="address.address2" />" 
                data-city="<dsp:valueof param="address.city" />" 
                data-state="<dsp:valueof param="address.state" />" 
                data-zipCode="<dsp:valueof param="address.postalCode" />"
                data-country="<dsp:valueof param="address.country" />"
                data-nickname="${nickname}"
                data-currentAddressID="${currentAddressID}"                
                <c:if test="${currentAddressID eq addressID}"> checked="checked" </c:if>
               />
                      
               <div class="label">
                    <label id="lbladdressToShip2p${index}" for="addressToShip2p${index}">
                        <span><dsp:valueof param="address.firstName" /> <dsp:valueof param="address.lastName" /></span>
                        <dsp:getvalueof var="tempCompanyName" param="address.companyName" />
								<c:if test="${tempCompanyName != ''}">
										 <span><dsp:valueof param="address.companyName" /></span>
								</c:if>
                        <span>
	                     <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
								<dsp:param name="value" param="address.address2"/>
									<dsp:oparam name="true">
										<dsp:valueof param="address.address1" />
									</dsp:oparam>
									<dsp:oparam name="false">
										<dsp:valueof param="address.address1" />, <dsp:valueof param="address.address2" />
									</dsp:oparam>
								</dsp:droplet>
                        </span>
                        <span><dsp:valueof param="address.city" />, <dsp:valueof param="address.state"/> <dsp:valueof param="address.postalCode"/></span>
                    </label>
                    <a href="#" class="editAddressLink" ><bbbl:label key="lbl_spc_edit_address_link" language="${pageContext.request.locale.language}" /></a>
               </div>
            </div>                                                                              
        </dsp:oparam>
    </dsp:droplet>
       
   </dsp:oparam>
</dsp:droplet>

	<div class="clear"></div>

	<a href="#" class="addNewAddressLink" ><bbbl:label key="lbl_spc_add_new_address_link" language="${pageContext.request.locale.language}" /></a>
</form>


</dsp:page>