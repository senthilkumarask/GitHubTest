<dsp:page>
   	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ProfileExistCheckDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryAddressOrderDroplet"/>
    <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" /> 
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/SimplifyRegFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    
    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event">
        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
       

        

        <c:set var="BedBathCanadaSite">
            <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
        </c:set>
    </dsp:getvalueof>
    
    <div class="steps step2 clearfix grid_12 alpha omega">
        <%--Preview--%>
       
                <div class="grid_4 alpha clearfix">
                    <div class="grid_4 alpha omega clearfix">
                        <div class="entry pushDown">
                        
                      FullName:  <dsp:input type="text" bean="SimplifyRegFormHandler.userFullName"/>
					  
					  Email:   <dsp:input type="text" bean="SimplifyRegFormHandler.email"/>
						 
					 						 
					 	 Password: <dsp:input tabindex="4" id="password" bean="SimplifyRegFormHandler.password" name="loginPasswd" value="" type="password" autocomplete="off" iclass="showpassLoginFrag"/>
						 
                        </div>
						
						<div>
						
						 <div class="button">
                         <dsp:input type="submit" value="checkUser" bean="SimplifyRegFormHandler.verifyUser"/>
						 
						</div>
						</div>
                        </div>
						 <div class="button">
						<dsp:input type="submit" value="CreateRegistry" bean="SimplifyRegFormHandler.createRegistry"/>
						   			
						</div>
                  </div>
          
        
        <%--Preview End--%>

        </div>
</dsp:page>
