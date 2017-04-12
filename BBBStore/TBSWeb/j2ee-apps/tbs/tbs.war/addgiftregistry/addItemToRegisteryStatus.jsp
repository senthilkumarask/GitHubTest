<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:getvalueof var="jasonObj" param="addItemResults" />
		<dsp:getvalueof var="regId" param="registryId" />
		<dsp:getvalueof var="regName" param="registryName" />
		<dsp:getvalueof var="totQty" param="totQuantity" />
		<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
	<dsp:getvalueof var="addAll" value="${param.addAll}" /> 
		 <c:if test="${not empty addAll}">
             <dsp:setvalue  bean="GiftRegistryFormHandler.kickStarterAddAllAction" value="AddAllItems"/>
             <dsp:getvalueof var="consultantName" value="${param.heading1}" />
         </c:if>
		<c:if test="${not empty jasonObj}">
			<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.addItemToGiftRegistry" />
		<dsp:getvalueof
			bean="GiftRegistryFormHandler.errorFlagAddItemToRegistry" var="flag" />
			<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />
		</c:if>

		<dsp:getvalueof id="omnitureRegistryProd"  param="giftRegistryViewBean.omniProductList"/>
		<dsp:getvalueof id="omnitureRegistryId"  param="giftRegistryViewBean.registryId"/>
		<dsp:getvalueof id="omnitureRegistryName"  param="giftRegistryViewBean.registryName"/>
		<dsp:getvalueof id="registryName"  param="giftRegistryViewBean.registryName"/>
		<dsp:getvalueof id="totQuantity"  param="giftRegistryViewBean.totQuantity"/>
			<c:if test="${empty addAll}">
			<dsp:getvalueof id="consultantName"  param="giftRegistryViewBean.consultantName"/>
			</c:if>
	<c:choose>
		<c:when test="${not empty param.additemflagerror}">
			<c:choose>
			    <c:when test="${param.additemflagerror == 'true'}">
			        <div title='<bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>'>
                         <label class="error">
                            <bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>
                         </label>
						<div class="fl button_active">				
								<input type="submit" value="CLOSE" name="CLOSE" class="small button service expand close-modal" role="button" aria-pressed="false">
						</div>
			        </div>
			    </c:when>
			    <c:otherwise>
					<c:if test="${omnitureRegistryName eq 'Other'}">
						<c:set var="omnitureRegistryName" value="etc"/>
					</c:if>			    
			        <div title='${totQty} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${registryName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
			        <label>${totQty} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${registryName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/></label><br><br> 
						<div class="fl button_active button_active_orange">				
								<input type="submit" value="Ok" name="Ok" class="small button service expand close-modal" role="button" aria-pressed="false">
						</div>
						<div class="fl bold marTop_5 marLeft_10">		
								<dsp:a href="/tbs/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
									<dsp:param name="registryId" value="${regId}"/>
									<dsp:param name="eventType" value="${registryName}"/>
								</dsp:a>
							<div class="clear"></div>
						</div>
					</div>
					<script type="text/javascript">
					pageName = document.location.pathname.split('/')[2];
					var eVar1="";
					if (pageName === ""){
						omniPageName = "Home";
					} else {
						var consultantName ="${consultantName}";
						if(pageName == 'product'){
							omniPageName = pageName + 'detail Page';
						}else{
							if(pageName == 's'){
								omniPageName = 'browse Page';
							}
							else {
								if(!consultantName.length){
									omniPageName = pageName+'Page';
									eVar1="";
									}else{
									omniPageName =pageName+'-'+consultantName;
									eVar1=omniPageName;
								}
							}
						}
						
					}
						  var BBB = BBB || {};	
							BBB.registryInfo = {
									registryPagename: "registryAdd",
								    product:"${omnitureRegistryProd}",
								    var1  : eVar1,
									var23 : '${omnitureRegistryName}',
									var24 : '${omnitureRegistryId}',
									var46 : omniPageName
								};
							//Commenting this since this is getting called from the js
							//addItemRegistryOmniture(BBB.registryInfo,omniPageName);
		            </script>
			    </c:otherwise>
			</c:choose>
		</c:when>
	<c:otherwise>
			<c:if test="${omnitureRegistryName eq 'Other'}">
				<c:set var="omnitureRegistryName" value="etc"/>
			</c:if>
			<c:choose>
				<c:when test="${flag==true }">
					<div title='<bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>'>
                    <label class="error">
                       <bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>
                    </label>
						<dsp:droplet name="ErrorMessageForEach">
							<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions" />
								<dsp:oparam name="output">
									<p class="error"><dsp:valueof param="message"/></p>
								</dsp:oparam>
						</dsp:droplet>
						<div class="fl button_active button_active_orange">				
								<input type="submit" value="Ok" name="Ok" class="small button service expand close-modal" role="button" aria-pressed="false">
						</div>
					</div>
				</c:when>
				<c:otherwise>		
				<div title='${totQuantity} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${registryName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
				<label>${totQuantity} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${registryName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/></label><br><br>
					<div class="fl button_active">				
					<input type="submit" value="CLOSE" name="CLOSE" class="small button service expand close-modal" role="button" aria-pressed="false">
					</div>
    				<dsp:a href="/tbs/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
    					<dsp:param name="registryId" value="${omnitureRegistryId}"/>
    					<dsp:param name="eventType" value="${registryName}"/>
    				</dsp:a>
			    </div>
					<script type="text/javascript">
					pageName = document.location.pathname.split('/')[2];
					var eVar1="";
					if (pageName === ""){
						omniPageName = "Home";
					} else {
						var consultantName ="${consultantName}";
						if(pageName == 'product'){
							omniPageName = pageName + ' detail Page';
						}else{
							if(pageName == 's'){
								omniPageName = 'browse Page';
							}
							else {
								if(!consultantName.length){
									omniPageName = pageName+'page';
									eVar1="";
									}else{
									omniPageName =pageName+'-'+consultantName;
									eVar1=omniPageName;
								}
							}
						}
						
					}
					  var BBB = BBB || {};	
						BBB.registryInfo = {
								registryPagename: "registryAdd",
							    product:"${omnitureRegistryProd}",
							    var1  : eVar1,
								var23 : '${omnitureRegistryName}',
								var24 : '${omnitureRegistryId}',
								var46 : omniPageName
							};
						//addItemRegistryOmniture(BBB.registryInfo,omniPageName); 
		            </script>
		           
				</c:otherwise>
			</c:choose>	
		</c:otherwise>		
		</c:choose>
	<!-- RM# 33682 -->
	<%-- <dsp:setvalue bean="GiftRegistryFormHandler.clearSessionBeanData" /> --%>
</dsp:page>
