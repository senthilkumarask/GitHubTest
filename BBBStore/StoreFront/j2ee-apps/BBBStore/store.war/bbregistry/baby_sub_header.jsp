	<dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>    
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	
	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}"/>
	<c:set target="${placeHolderMap}" property="imagePath" value="${imagePath}"/>
	
	<dsp:droplet name="GiftRegistryFlyoutDroplet">
		<dsp:param name="profile" bean="Profile"/>
		    <dsp:oparam name="output">
	        <dsp:droplet name="/atg/dynamo/droplet/Switch">
	            <dsp:param name="value" param="userStatus"/>
	                <dsp:oparam name="1">
<%-- 	             Added for Scope # 81 H1 tags  --%>
	                 <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
					 <bbbt:textArea key="txt_bridal_sub_header_baby" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
  
					</dsp:oparam>
					
					<dsp:oparam name="2">
<%-- 					Added for Scope # 81 H1 tags  --%>
						<h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
				       <bbbt:textArea key="txt_bridal_sub_header_baby" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
  					</dsp:oparam>
					
					<dsp:oparam name="4">
<%-- 					Added for Scope # 81 H1 tags  --%>
						<h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
						 <bbbt:textArea key="txt_baby_sub_header_us_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
					</dsp:oparam>
					
					<dsp:oparam name="3">
<%-- 					Added for Scope # 81 H1 tags  --%>
						<h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
					    <bbbt:textArea key="txt_baby_sub_header_us_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
					</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	  </dsp:droplet>
	  