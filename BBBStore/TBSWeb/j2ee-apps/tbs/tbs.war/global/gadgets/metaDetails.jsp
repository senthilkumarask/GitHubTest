<dsp:page>
  <%--
      Global gadget used for rendering SEO meta tags

      Parameters:
      - catalogItem (optional) - product or category item used to combine product/category attributes
                                  with static SEO content from repository.
      - contentKey (optional) - key used to search SEOTags repository item, if not provided
                                  then page URL used instead.
  --%>
  <dsp:importbean bean="/atg/multisite/Site" />
  <dsp:importbean bean="/atg/dynamo/droplet/RQLQueryRange" />
  
  <dsp:getvalueof var="catalogItem"  param="catalogItem"/>
  <dsp:getvalueof var="titleString" param="titleString"/>
  <dsp:getvalueof var="contentKey" param="contentKey"/>
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:getvalueof var="productId" param="productId" />
  <dsp:getvalueof var="guideId" param="guideId" />
  <dsp:getvalueof var="pageName" param="pageName" />
  <dsp:getvalueof var="brandId" param="brandId" />

  <c:set var="pageUrl" value="${pageContext.request.servletPath}" />
    <c:choose>
	    <c:when test="${not empty categoryId && empty productId && empty contentKey}">
    		<c:set var="contentKey" value="${categoryId}" />
	  	</c:when>
	    <c:when test="${not empty productId}">
    		<c:set var="contentKey" value="${productId}" />
    		<dsp:getvalueof var="isProdId" vartype="boolean" value="true"/>
	    </c:when>
    	<c:when test="${not empty guideId && empty contentKey}">
    		<c:set var="contentKey" value="/guide/${guideId}/" />
	    </c:when>
    	<c:when test="${not empty pageName && empty contentKey}">
	    	<c:set var="contentKey" value="${pageName}" />
	    </c:when>
	    <c:when test="${not empty brandId && empty contentKey}">
	    	<c:set var="contentKey" value="/brand/${brandId}" />
	    </c:when>
    	<%-- for Registry and Static page content key is coming from respective page as both uses pageName variable --%>    
    </c:choose> 
  <c:set var="key" value="${(not empty contentKey) ? contentKey : pageUrl}"/>
  <dsp:getvalueof var="site" bean="Site.id"/>
  <c:set var="queryRQL" value="${'key = :key'}"/>
  <c:if test="${not empty key}">
  	<c:choose>
  		<c:when test="${isProdId}">
				<dsp:droplet name="/com/bbb/commerce/browse/droplet/MinimalProductDetailDroplet">
					<dsp:param name="id" value="${key}" />
					<dsp:param name="isMetaDataRequired" value="true"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="title" param="productVo.name" />
						<dsp:getvalueof var="description" param="productVo.shortDescription" />
						<dsp:getvalueof var="keywords" param="productVo.prdKeywords" />
					</dsp:oparam>
				</dsp:droplet>
		</c:when>
  		<c:otherwise>
		  <dsp:droplet name="RQLQueryRange">
		    <dsp:param name="repository" value="/atg/seo/SEORepository" />
		    <dsp:param name="itemDescriptor" value="SEOTags" />
		    <dsp:param name="howMany" value="1" />
		    <dsp:param name="key" value="${key}" />
		    <dsp:param name="site" bean="Site.id"/>
		    <dsp:param name="queryRQL" value="${queryRQL}" />
		    <dsp:oparam name="output">
		      <dsp:getvalueof var="title" param="element.title" />
		      <dsp:getvalueof var="description" param="element.description"/>
		      <dsp:getvalueof var="keywords" param="element.keywords"/>
		    </dsp:oparam> 
		  </dsp:droplet>
		</c:otherwise>
 	 </c:choose>
  </c:if>

  <%-- Add product/category specific information to title and meta keywords/description if
          catalogItem is passed in --%>
      
  <c:if test="${not empty catalogItem}">
   
    <dsp:getvalueof var="itemName" param="catalogItem.displayName"/>
    <dsp:getvalueof var="itemDescription" param="catalogItem.longDescription"/>
    <dsp:getvalueof var="itemKeywords" param="catalogItem.keywords"/>
    <c:if test="${not empty itemName}">
      <c:set var="title"><dsp:valueof valueishtml="true">${itemName} ${title}</dsp:valueof></c:set>
    </c:if>
    <c:if test="${not empty itemDescription}">
      <c:set var="description" value="${itemDescription} ${description}"/>
    </c:if>
    <c:if test="${not empty itemKeywords}">
      <c:set var="keywords" value="${fn:substring(itemKeywords,fn:indexOf(itemKeywords,'[')+1,fn:indexOf(itemKeywords,']'))},${keywords}"/>
    </c:if>
  </c:if> 
  <%-- Page's title --%>
  <c:choose>
    <c:when test="${not empty title}">    
      <title><c:out value="${title}" escapeXml="false" /> - <bbbl:label key="lbl_metadetails_title" language ="${pageContext.request.locale.language}"/></title>
      <c:set var="pageTitle" value="${title}" scope="request"/>
	   <c:set var="fbPageTitle" value="${title}" scope="request" />
    </c:when>  
    <c:when test="${not empty titleString}">    
      <title><c:out value="${titleString}" escapeXml="true"/> - <bbbl:label key="lbl_metadetails_title" language ="${pageContext.request.locale.language}"/></title>
      <c:set var="title" value="${titleString}"/> 
      <c:set var="pageTitle" value="${titleString}" scope="request"/> 
	   <c:set var="fbPageTitle" value="${title}" scope="request" />
    </c:when>  
    <c:otherwise>
      <title>
        <bbbl:label key="lbl_metadetails_title" language ="${pageContext.request.locale.language}"/>
      </title> 
    </c:otherwise>
  </c:choose>   
  
  
  
  <%-- Page's meta description --%>
  <c:if test="${not empty description}">
    <c:set var="description" value="${fn:replace(description, '\"', '')}" />
	<c:set var="fbDesc" value="${description}" scope="request" />     
    <meta name="description" content="${description}" />
  </c:if>     
       
  <%-- Page's meta keywords --%>
  <c:if test="${not empty keywords}">
    <c:set var="keywords" value="${fn:replace(keywords, '\"', '')}" />
    <meta name="keywords" content="${keywords}"/>
  </c:if>
  
  <%-- Mobile Deep Linking Meta tag. JIRA request PS-5158--%>
  <%-- KP COMMENT: Missing files --%>
  <%-- 
  <c:if test="${not empty categoryId && empty productId}">
	 <dsp:include page="/browse/mobile/mobilecategories_metatag.jsp"/>
	 <c:set var="categoryOrder" value="${categoryPath}"/>
	 <meta name="MOBILE_CATEGORY" content="${categoryOrder}"/>
  </c:if> 
  --%>
  
   <%-- Mobile Deep Linking Meta tag. JIRA request PS-5158--%>
    <%-- KP COMMENT: Missing files --%>
  <%-- 
  <c:if test="${not empty categoryId && empty productId}">
	 <dsp:include page="/browse/mobile/mobilecategories_metatag.jsp"/>
	 <c:set var="categoryOrder" value="${categoryPath}"/>
	 <meta name="MOBILE_CATEGORY" content="${categoryOrder}"/>
  </c:if>
--%>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/global/gadgets/metaDetails.jsp#1 $$Change: 633540 $--%>