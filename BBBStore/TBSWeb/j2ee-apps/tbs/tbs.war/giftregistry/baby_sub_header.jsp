<dsp:page>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/> 
	
	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}"/>
	<c:set target="${placeHolderMap}" property="imagePath" value="${imagePath}"/>
	
	<dsp:getvalueof var="userStatus" param="userStatus"/>
	
	        <dsp:droplet name="Switch">
	            <dsp:param name="value" value="${userStatus}"/>
	                <dsp:oparam name="1">
<%-- 	             Added for Scope # 81 H1 tags  --%>
	                 <%-- <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1> --%>
					 <bbbt:textArea key="txt_bridal_sub_header_baby" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
  
					</dsp:oparam>
					
					<dsp:oparam name="2">
<%-- 					Added for Scope # 81 H1 tags  --%>
						<%-- <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1> --%>
				       <bbbt:textArea key="txt_bridal_sub_header_baby" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
  					</dsp:oparam>
					
					<dsp:oparam name="4">
<%-- 					Added for Scope # 81 H1 tags  --%>
						<%-- <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1> --%>
						 <bbbt:textArea key="txt_baby_sub_header_us_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
					</dsp:oparam>
					
					<dsp:oparam name="3">
<%-- 					Added for Scope # 81 H1 tags  --%>
						<%-- <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1> --%>
					    <bbbt:textArea key="txt_baby_sub_header_us_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
					</dsp:oparam>
			</dsp:droplet>
</dsp:page>