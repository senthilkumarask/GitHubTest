<dsp:page>
  <dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
  
  <dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
  <dsp:getvalueof var="catId" param="categoryId"/>
      
    <c:if test="${empty catId && frmBrandPage ne true}">
	    <c:set var="vendorKey"><bbbc:config key="VendorParam" configName="VendorKeys"/></c:set>
	    <c:if test="${!fn:containsIgnoreCase(vendorKey, 'VALUE NOT FOUND FOR KEY') && not empty vendorKey}">
			<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
			<c:if test="${not empty vendorParam}">
			  <c:set var="vendorParam" value="&${vendorKey}=${vendorParam}" />
		    </c:if>      
	    </c:if>
    </c:if>   
</dsp:page>