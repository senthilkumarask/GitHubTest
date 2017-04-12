<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="TBS_BedBathUSSite" scope="request">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite" scope="request">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite" scope="request">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>


<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}"/>
<c:set target="${placeHolderMap}" property="imagePath" value="${imagePath}"/>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/> 
<dsp:droplet name="GiftRegistryFlyoutDroplet">
            <dsp:param name="profile" bean="Profile"/>
            <dsp:oparam name="output">
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
               <dsp:param name="value" param="userStatus"/>
                <dsp:oparam name="1"> 
                 <div id="headerWrap" class="row">
			        <c:choose>
				        <c:when test="${appid eq TBS_BedBathUSSite}">
						  <%-- Start: Added for Scope # 81 H1 tags --%>
				          <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
				          <%-- End: Added for Scope # 81 H1 tags --%>
						  <bbbt:textArea key="txt_bridal_sub_header" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/> 
				         </c:when>
				        <c:otherwise>
						  <%-- Start: Added for Scope # 81 H1 tags --%>
				          <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
				          <%-- End: Added for Scope # 81 H1 tags --%>
				         <bbbt:textArea key="txt_bridal_sub_header_ca" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
					    </c:otherwise>
			        </c:choose>
		        </div>
			 </dsp:oparam>
			 
			 <dsp:oparam name="2"> 
	            <div id="headerWrap" class="row">
		        <c:choose>
			        <c:when test="${appid eq TBS_BedBathUSSite}">
					  <%-- Start: Added for Scope # 81 H1 tags --%>
			          <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
			          <%-- End: Added for Scope # 81 H1 tags --%>
					  <bbbt:textArea key="txt_bridal_sub_header" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/> 
			         </c:when>
			        <c:otherwise>
					 <%-- Start: Added for Scope # 81 H1 tags --%>
			         <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
			         <%-- End: Added for Scope # 81 H1 tags --%>
			         <bbbt:textArea key="txt_bridal_sub_header_ca" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
				    </c:otherwise>
			     </c:choose>
	        	</div>
			 </dsp:oparam>
								 
		   <dsp:oparam name="3"> 
	        <div id="headerWrap" class="row">
		        <c:choose>
		        <c:when test="${appid eq TBS_BedBathUSSite}">
				   <%-- Start: Added for Scope # 81 H1 tags --%>
		           <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_us_mulreg_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
		           <%-- End: Added for Scope # 81 H1 tags --%>
				   <bbbt:textArea key="txt_bridal_sub_header_us_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
		        </c:when>
		        <c:otherwise>
					 <%-- Start: Added for Scope # 81 H1 tags --%>
			         <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
			         <%-- End: Added for Scope # 81 H1 tags --%>
			         <bbbt:textArea key="txt_bridal_sub_header_ca_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
			    </c:otherwise>
		        </c:choose>
	        </div>
		 </dsp:oparam>
		<dsp:oparam name="4"> 
		    <div id="headerWrap" class="row">
		        <c:choose>
		        <c:when test="${appid eq TBS_BedBathUSSite}">
				    <%-- Start: Added for Scope # 81 H1 tags --%>
		        	<h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_us_mulreg_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
		        	<%-- End: Added for Scope # 81 H1 tags --%>
				    <bbbt:textArea key="txt_bridal_sub_header_us_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
		         </c:when>
		        <c:otherwise>
					 <%-- Start: Added for Scope # 81 H1 tags --%>
			         <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
			         <%-- End: Added for Scope # 81 H1 tags --%>
			         <bbbt:textArea key="txt_bridal_sub_header_ca_mulreg" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
			    </c:otherwise>
		        </c:choose>
	        </div>
		</dsp:oparam>
	  </dsp:droplet>
 	   </dsp:oparam>
      </dsp:droplet>