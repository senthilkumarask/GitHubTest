<dsp:page>    

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/selfservice/CanadaStoreLocatorDroplet" />
<dsp:importbean var="canadaStoreLocatorVO" bean="/com/bbb/commerce/catalog/vo/CanadaStoreLocatorVO" />
<dsp:importbean var="storeVO" bean="/com/bbb/commerce/catalog/vo/StoreVO" />

<c:set var="section" value="selfService" scope="request" />
<c:set var="pageWrapper" value="findStore canadaStoreLocator useMapQuest useStoreLocator" scope="request" />
  
<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">${section}</jsp:attribute>
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:body>
    
     <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
 
  <div id="pageWrapper" class="${pageWrapper}">
    <div id="content" class="container_12 clearfix" role="main"> 
        <div class="grid_12">
            <h1><bbbl:label key="lbl_canada_store_location" language="${pageContext.request.locale.language}" /></h1>
        </div>
        <div class="grid_9 clearfix"> 
            
               <dsp:droplet name="CanadaStoreLocatorDroplet">
              	 <dsp:oparam name="output">
			            <dl class="brandChars noMarTop">
			                <dd class="clearfix"> 
			              	    <dsp:droplet name="/atg/dynamo/droplet/ForEach">   
							  		<dsp:param name="array" param="canadaStoreLocator"/>  
							  		<dsp:param name="sortProperties" value="+provinceName,address"/>
							    	<dsp:oparam name="output">
						          
						    	     <dsp:getvalueof var="count" param="count"/>
				    	  			 <dsp:getvalueof var="size" param="size"/>
				    	      
				    	     		<c:if test="${count == 1}">
				    	   				<dsp:getvalueof var="currentState" param="element.provinceName"/>
				    	   				<dsp:getvalueof var="currentStateCode" param="element.province"/>
				     	     		    <a class="first smoothScrollTo" data-smoothscroll-topoffset="55" title="${currentState}" href="#brandChar${currentStateCode}">${currentState}</a>
				     	     		</c:if> 
				    	     		<dsp:getvalueof var="newState" param="element.provinceName"/>
				    	     		 <dsp:getvalueof var="newStateCode" param="element.province"/>
				    	     		<c:choose>
				     	     			<c:when test="${currentState ne newState}">
				     	             	 <c:set var="currentState" value="${newState}"/>  
				     	             	   <c:set var="currentStateCode" value="${newStateCode}"/>    
				     	                   <span>|</span><a class="first smoothScrollTo" data-smoothscroll-topoffset="55" title="${currentState}" href="#brandChar${currentStateCode}">${currentState}</a>     	  
				     	     			</c:when>
				     	     		</c:choose>
				     	      		<c:if test="${count == size}">
				     	             	<span class="last">|</span> 
				     	      		</c:if>
				     	           </dsp:oparam>
						        </dsp:droplet>      
			                </dd>
			            </dl>
			            <div id="storeResults" class="clearfix">


              	    <dsp:droplet name="/atg/dynamo/droplet/ForEach">   
				  		<dsp:param name="array" param="canadaStoreLocator"/>  
				  		<dsp:param name="sortProperties" value="+provinceName,address"/>
				  		
				    	<dsp:oparam name="output">

				    	     <dsp:getvalueof var="count" param="count"/>
				    	     <dsp:getvalueof var="size" param="size"/>
				    	      
				    	     <c:if test="${count == 1}">
				    	   			<dsp:getvalueof var="currentState" param="element.provinceName"/>
				    	   			<dsp:getvalueof var="currentStateCode" param="element.province"/>
				     	                <div class="brandLinks clearfix">
				     	            		<a class="brandLinksHeader" name="brandChar${currentStateCode}"><dsp:valueof param="element.provinceName"/></a>
				     	     </c:if> 
				    	     <dsp:getvalueof var="newState" param="element.provinceName"/>
				    	     <dsp:getvalueof var="newStateCode" param="element.province"/>
				    	     <dsp:getvalueof var="counter" param="counter"/>
 				    	     <c:choose>
				     	     	<c:when test="${currentState ne newState}">
				     	              <c:set var="currentState" value="${newState}"/>  
				     	              <c:set var="currentStateCode" value="${newStateCode}"/>    
                                        <div class="clear"></div>
				     					</div>
				     	            	 <div class="brandLinks clearfix">
				     	            		<a class="brandLinksHeader" name="brandChar${currentStateCode}"><dsp:valueof param="element.provinceName"/></a>   
						     	            <div class="storeResult borderBottom grid_9 suffix_3 clearfix">
						     	            	 <dsp:include page="canada_store_info.jsp">
						     	            	 	<dsp:param name="storeDetails" param="element" />
						     	            	 	<dsp:param name="counter" param="count" />
						     	            	 	<dsp:param name="firstIteration" value="true" />
													<dsp:param name="linkViewMap" value="1" />
												</dsp:include>
											</div>	
				     	     	</c:when>
				     	     	<c:otherwise>
				     	     	      <div class="storeResult borderBottom grid_9 suffix_3 clearfix">
				     	     	      <dsp:include page="canada_store_info.jsp">
					     	     	      <dsp:param name="storeDetails" param="element" />
					     	     	      <dsp:param name="counter" param="count" />
					     	     	      <dsp:param name="firstIteration" value="false" />
										  <dsp:param name="linkViewMap" value="1" />
								      </dsp:include>
								      </div>
				     	     	 </c:otherwise>
				     	     </c:choose>
				     	</dsp:oparam>
                        <dsp:oparam name="outputEnd">
                            <div class="clear"></div>
                            </div>
                        </dsp:oparam>
				     	
				    </dsp:droplet>
                        <div class="clear"></div>
				     </div>  
				 </dsp:oparam>
				   <dsp:oparam name="error">
		    			<dsp:getvalueof var="varSystemSrror" param="systemError"></dsp:getvalueof>
		    			 <c:choose>
			            	<c:when test="${varSystemSrror=='err_fetching_canada_stores' }">
						   		 <label class="error">
						    		<bbbe:error key="err_fetching_canada_stores" language="${pageContext.request.locale.language}"/>
						 	   </label>
						    </c:when>
				 	   </c:choose>
		    		</dsp:oparam>   
				</dsp:droplet>
           
        </div>
        <div class="grid_3 clearfix">
            <div class="teaser_229 benefitsAccountTeaser clearfix">
                <div >
                    <p class="marTop_10 marBottom_5"><bbbt:textArea key="txtarea_canda_store_locator_desc" language ="${pageContext.request.locale.language}"/></p>
                </div>
                
                 <dsp:droplet name="CanadaStoreLocatorDroplet">
              		 <dsp:oparam name="output">
                  		<dsp:droplet name="/atg/dynamo/droplet/ForEach">   
							<dsp:param name="array" param="imageMap"/>  
							<dsp:oparam name="output">
						    		<dsp:param name="storeImageVO" param="element" />
				  				  	<dsp:getvalueof var="imageLocation" param="storeImageVO.legendFileLocation"></dsp:getvalueof>
				  				  	<dsp:getvalueof var="imageAlt" param="storeImageVO.legendAltText"></dsp:getvalueof>
				  				  	<div class="benefitsItem textCenter">
						 			  	<img src="${imageLocation}" alt="${imageAlt}" />
						 			</div>
						    </dsp:oparam>
				        </dsp:droplet>
			        </dsp:oparam>
			     </dsp:droplet>	
	        </div>
        </div>
    </div>

<div id="findAStoreMap" title="Store Location">
    <div id="mapAddress"></div>
    <div id="map"></div>
</div>

<div id="directionsDialogWrapper" class="clearfix hidden" title="Driving Directions">
	<div id="directionsForm" class="directionsForm clearfix">
		<div class="width_3 fl marLeft_10 marRight_10">
		<%-- 
			<h3>Start</h3>
			<p>Please enter the address, city, state where you will be starting.</p>
			--%>
			<div id="startLocationFormWrapper">
				<form method="post" action="/_ajax/get_directions.jsp" name="startLocationForm" id="startLocationForm">
					<div class="inputField">
						<%-- 
					<div class="label">
							<label id="lbltxtStreet" for="txtStreet">Street</label>
						</div>
						--%>
						<div class="text">
							<div class="width_3">
								<input type="text" name="dirStartStreetName" id="txtStreet" aria-required="false" aria-labelledby="lbltxtStreet errortxtStreet" />
							</div>
						</div>
					</div>

					<div class="inputField">
					   <%--
						<div class="label">
							<label id="lbltxtCity" for="txtCity">City</label>
						</div>
						 --%>
						<div class="text">
							<div class="width_3">
								<input type="text" name="dirStartCityName" id="txtCity" aria-required="false" aria-labelledby="lbltxtCity errortxtCity" />
							</div>
						</div>
					</div>

					<div class="inputField">
					    <%--
						<div class="label">
							<label id="lblselStateStart" for="selStateStart">State <span class="required">*</span></label>
						</div>
						 --%>
						<div class="select width_3">
							<select id="selStateStart" name="dirStartStateName" class="uniform" aria-required="true" aria-labelledby="lblselStateStart errorselStateStart" >
								<option value="GA">Georgia</option>
							</select>
							<label id="errorselStateStart" for="selStateStart" generated="true" class="error"></label>
						</div>
					</div>

					<div class="inputField">
					    <%--
						<div class="label">
							<label id="lbltxtZip" for="txtZip">Zip <span class="required">*</span></label>
						</div>
						--%>
						<div class="text">
							<div class="width_1">
								<input type="text" name="dirStartZipName" id="txtZip" aria-required="false" aria-labelledby="lbltxtZip errortxtZip" />
							</div>
							<label id="errortxtZip" for="txtZip" generated="true" class="error"></label>
						</div>
					</div>

					<div class="radioItem input clearfix">
						<div class="radio">
							<input id="dirWithoutMap" type="radio" name="dirShowMap" value="false" checked="checked" aria-checked="true" aria-labelledby="lbldirWithoutMap" />
						</div>
						 <%--
						<div class="label">
							<label id="lbldirWithoutMap" for="dirWithoutMap">Show Directions without Maps</label>
						</div>
						--%>
					</div>
					<div class="radioItem input clearfix">
						<div class="radio">
							<input id="dirWithMap" type="radio" name="dirShowMap" value="true" aria-checked="true" aria-labelledby="lbldirWithMap" />
						</div>
						<%--
						<div class="label">
							<label id="lbldirWithMap" for="dirWithMap">show Directions With Maps</label>
						</div>
						 --%>
					</div>


					<input type="hidden" name="dialogDestStreet" data-dest-class="street" value="" />
					<input type="hidden" name="dialogDestCity" data-dest-class="city" value="" />
					<input type="hidden" name="dialogDestState" data-dest-class="state" value="" />
					<input type="hidden" name="dialogDestZip" data-dest-class="zip" value="" />
					<input type="submit" class="hidden" value=""/>
				</form>
			</div>
			<div id="startLocationSet">

			</div>
		</div>
		<div class="width_3 fl prefix_1 marLeft_10">
			 <%--
			<h3>Destination</h3>
			 --%>
			<div id="destinationLocation">

			</div>
		</div>
	</div>
	<div id="directionResultWrapper">

	</div>
</div>
			
			
		</jsp:body>
<jsp:attribute name="footerContent">		
<script type="text/javascript">
$(function () {
	rkg_micropixel("BedBathCA","storeloc");
});

	if (typeof s !== 'undefined') {
		s.pageName ='My Account>Find A Store';
		s.channel = 'My Account';
		s.prop1 = 'My Account';
		s.prop2 = 'My Account';
		s.prop3 = 'My Account';
		var s_code = s.t();
		if (s_code)
			document.write(s_code);
		}
</script>	
</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>