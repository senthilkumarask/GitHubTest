<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean var="storeConfig" bean="/atg/store/StoreConfiguration"/>  	
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/PrintAtHomeDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="registryId" param="registryId"/>
	<dsp:getvalueof var="templateId" param="cardId"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
        <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:when test="${not empty eventType && eventType eq 'Baby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="pageVariation" value="br" scope="request" />
        </c:otherwise>
    </c:choose>
	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">printCardsAtHomeEditor</jsp:attribute>
	<jsp:body>
		<dsp:droplet name="PrintAtHomeDroplet">
					<dsp:param name="siteId" value="${appid}"/>
				    <dsp:oparam name="output">
					        	<dsp:droplet name="ForEach">
						<dsp:param name="array" param="repoItems" />
						<dsp:param name="elementName" value="heroPrintCard"/>
						<dsp:oparam name="output">	
						    <dsp:getvalueof var="announcementCards" param="heroPrintCard.announcementCards"/>
							 <dsp:getvalueof var="promoContent" param="heroPrintCard.promoContent"/>
						  </dsp:oparam>
						      </dsp:droplet>
			  </dsp:oparam>
	</dsp:droplet>
	 <c:if test="${( not empty registryId )  }">
		<dsp:droplet name="RegistryInfoDisplayDroplet">
			<dsp:param value="${registryId}" name="registryId" />
			
			<dsp:oparam name="output">

    			<dsp:getvalueof var="eventTypeParam" param="eventType"/>
			    <dsp:getvalueof var="eventTypeVar" param="registrySummaryVO.eventType"/>
    			<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
    			<dsp:getvalueof var="primaryFirstName" param="registrySummaryVO.primaryRegistrantFirstName"/>
    			<dsp:getvalueof var="primaryLastName" param="registrySummaryVO.primaryRegistrantLastName"/>
				<dsp:getvalueof var="coFirstName"param="registrySummaryVO.coRegistrantFirstName"/>
				<dsp:getvalueof var="coLastName"param="registrySummaryVO.coRegistrantLastName"/>
				
				
    		</dsp:oparam>
    	</dsp:droplet>
    </c:if>
	<div class="container_12 printCardsAtHomeContent">
	
		<div class="grid_12">
		
			<div class="grid_8 alpha">
				<bbbl:label key="lbl_card_design_editor_detail_card_editor" language="${pageContext.request.locale.language}" />
				<p><bbbl:textArea key="lbl_card_design_editor_detail_short_description" language="${pageContext.request.locale.language}" /></p>
			</div>
			<div class="grid_4 omega">
				<%-- <bbbl:label key="lbl_card_design_editor_detail_chat" language="${pageContext.request.locale.language}" /> --%>
				<%-- Commenting Click to Chat as part of 34473 
				<div id="chatModal" class="fr marTop_20">
					<div id="chatModalDialogs"></div>
	                <dsp:include page="/common/click2chatlink.jsp">
	                	<dsp:param name="pageId" value="2" />
	                </dsp:include>
				</div>
				--%>
			</div>
		</div>
		<div class="grid_12">
		
			<div class="grid_4 alpha">
				<bbbl:label key="lbl_card_design_editor_detail_customize_card" language="${pageContext.request.locale.language}" />
				
				<p><bbbl:textArea key="lbl_card_design_editor_detail_long_description" language="${pageContext.request.locale.language}" /></p>
				
				<p id="printHomeAvery">				
					<bbbl:label key="lbl_card_design_editor_landing_gettheaverycardprintersheets" language="${pageContext.request.locale.language}" />
				</p>
				<div>
					<div class="grid_4 alpha">
						<div class="button button_active button_active_orange">
							<input type="button" class="printPDF" value="Print Your Cards" onclick= "javascript:customLinkTracking('Print at Home Announcement Cards')";/>
						</div>
					</div>
					
					<div class="grid_4 alpha marTop_10 marBottom_20">
						<div class="button clearfix">
							<input type="button" class="downloadPDF" value="Download PDF" onclick= "javascript:customLinkTracking('Download at Home Announcement Cards')";/>
						</div>
					</div>
					
					<div class="grid_4 alpha omega">
					 	${promoContent}
					</div>	
				</div>
			</div>
			<div class="grid_8 omega">							
				
	            <div id="cardWrapper" class="clearfix">
					<div class="cardContainer" ></div>					
					
					<input type="text" class='nameField' style="display:none;" name="editNameOne" id="editNameOne" value=""  />						
					<input type="text" class='nameField' style="display:none;" name="editNameTwo" id="editNameTwo" value="" />
					
					<%-- 
					<span id="regId"></span>							
					--%>
				</div>
				<div id="editors">	
				
					<%-- 					
					<bbbl:textArea key="lbl_card_design_editor_detail_htmlfonts1" language="${pageContext.request.locale.language}" />
					<bbbl:textArea key="lbl_card_design_editor_detail_htmlfonts2" language="${pageContext.request.locale.language}" />
					--%>
					
					<div class="textEditor" id="textEditorOne" >
						<div class="leaflet-popup-tip-container">
							<div class="leaflet-popup-tip"></div>
						</div>
						<div class="textEditorFieldsWrapper">
							<label>Select a Font:</label>
							<div class="select">
							<select class="selectFont uniform" id="fontFamilyOne">
								<option style="font-family:'Libre Baskerville'" value="Libre Baskerville">Baskerville</option>
								<option style="font-family: 'Tangerine';">Tangerine</option>								
								<option style="font-family: 'Playball';">Playball</option>
								<option style="font-family: 'AmericanTypewriter';" value="AmericanTypewriter">Typewriter</option>
								<option style="font-family: 'Open Sans Condensed';">Open Sans Condensed</option>
								<option style="font-family: 'Julius Sans One'" value="Julius Sans One">Julius</option>
								<option style="font-family: 'Raleway';">Raleway</option>								
							</select>
							</div>
							<div id="fontSizeContainer">
								<label>Font Size:</label>
								<div class="select">
									<select id="nameOneSize" class="fontSize uniform" >
										<option value="14">smaller</option>
										<option value="16">small</option>
										<option value="17">medium</option>
										<option value="18">large</option>
										<option value="20">larger</option>
									</select>
								</div>
							</div>
						
							<div id="fontColorContainer">
								<label>Font Color:</label>
								<ul class="fontColors" id="fontColorsOne">
									<li><a class="fontColor red" href="#" data-color="#b43232">red</a></li>
									<li><a class="fontColor pink" href="#" data-color="#c30f7a">pink</a></li>
									<li><a class="fontColor blue" href="#" data-color="#273691">blue</a></li>
									<li><a class="fontColor purple" href="#" data-color="#49176e">purple</a></li>
									<li><a class="fontColor green" href="#" data-color="#508801">green</a></li>
								</ul>
							</div>
							
							<div  class="clearfix">
								<div class="button button_active button_active_orange">
									<input type="button" value="Done" class="editorClose" />
								</div>
							</div>
														
						</div>
					</div>
					
					<div class="textEditor" id="textEditorTwo" >
						
						<div class="leaflet-popup-tip-container">
							<div class="leaflet-popup-tip"></div>
						</div>
						
						<div class="textEditorFieldsWrapper">
						
							<label>Select a Font:</label>
							<div class="select">
								<select  class="selectFont uniform" id="fontFamilyTwo">							
									<option style="font-family:'Libre Baskerville'" value="Libre Baskerville">Baskerville</option>
									<option style="font-family: 'Tangerine';">Tangerine</option>									
									<option style="font-family: 'Playball';">Playball</option>
									<option style="font-family: 'AmericanTypewriter';"  value="AmericanTypewriter">Typewriter</option>
									<option style="font-family: 'Open Sans Condensed';">Open Sans Condensed</option>
									<option style="font-family: 'Julius Sans One'" value="Julius Sans One">Julius</option>
									<option style="font-family: 'Raleway';">Raleway</option>								
								</select>
							</div>
						
						
							<div id="fontSizeContainer">
								<label>Font Size:</label>
								<div class="select">
									<select id="nameTwoSize" class="fontSize uniform" >
										<option value="14">smaller</option>
										<option value="16">small</option>
										<option value="17">medium</option>
										<option value="18">large</option>
										<option value="20">larger</option>
									</select>
								</div>
							</div>
						
							<div id="fontColorContainer">
								<label>Font Color:</label>
								<ul class="fontColors" id="fontColorsTwo">
									<li><a class="fontColor red" href="#" data-color="#b43232">red</a></li>
									<li><a class="fontColor pink" href="#" data-color="#c30f7a">pink</a></li>
									<li><a class="fontColor blue" href="#" data-color="#273691">blue</a></li>
									<li><a class="fontColor purple" href="#" data-color="#49176e">purple</a></li>
									<li><a class="fontColor green" href="#" data-color="#508801">green</a></li>
								</ul>
							</div>
							
							<div  class="clearfix">
								<div class="button button_active button_active_orange">
									<input type="button" value="Done" class="editorClose" />
								</div>
							</div>
														
						</div>
					</div>
					
				</div>				
				<div id="cardThumbnailsContainer" class="grid_8 alpha omega clearfix">
					<h3 id="cardThumbnailsHeader" class="marTop_10">Switch Templates</h3>
					<%-- <bbbl:label key="lbl_card_design_editor_detail_switch_template" language="${pageContext.request.locale.language}" /> --%>
								
					<%-- this is where you would put each card template thumbnail - same as from the landing page --%>
			    	<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${announcementCards}" />	
					 	<dsp:oparam name="outputStart">		
		                	<dsp:getvalueof var="cardsCount" param="count"/>		
					   		<ul id="cardThumbnails">		
		             	</dsp:oparam>
					 	<dsp:oparam name="output">	
					 		<dsp:getvalueof var="cardIndex" param="index"/>	
					 		<dsp:getvalueof var="cardId" param="element.Id"/>
					 		
					 			<c:choose>
			                		<c:when test="${cardIndex % 4 == 0}">
			                			<li class="grid_2 alpha">
			                			
			                			<c:if test="${cardIndex == 0}">
								 			<dsp:getvalueof var="defaultCardId" param="element.Id"/>									
										</c:if>
			                			
			                		</c:when>
			                		<c:when test="${cardIndex % 4 == 3}">
			                			<li class="grid_2 omega">
			                		</c:when>
	               					<c:otherwise>
	               						<li class="grid_2">
	               					</c:otherwise>
	              				</c:choose>
							
									<a class="cardThumbnail" data-templateId="${cardId}"  href="<dsp:valueof param="element.thumbnailImage" />" >
										<img src="<dsp:valueof param="element.thumbnailImage" />" alt="Switch This Design"/>								
									</a>							
								</li>
						</dsp:oparam>				
		               	<dsp:oparam name="outputEnd">
		                  	</ul>
		               </dsp:oparam>
				   </dsp:droplet>
				</div>
				
				<%-- 
					<div id="cardTemplates" style="display:none;">
			        	<div data-templateId="1" class="cardContainer cardTemplate" data-font="Libre Baskerville" data-fontsize="20" data-fontcolor="#49176e" >			        						
			        		<img height='288'  width='378' class="cardBackground" 
			        			src="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-1-unfilled-full.jpg" 
			        			data-printscr="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-1-unfilled-print.jpg"
			        			style="position:absolute; left:0; top: 0;" ></img>
							<span class="cardNameOne cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 115px; text-align: center;">Name One</span>						
							<span class="cardNameTwo cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 150px; text-align: center;">Name Two</span>					
							<span class="regId" style="color:#000; font-size:12px; position:absolute; left:59px; top: 193px; text-align: left;"></span>
						</div>
						
						<div data-templateId="2" class="cardContainer cardTemplate"  data-font="Open Sans Condensed" data-fontsize="20" data-fontcolor="#49176e" >			        						
			        		<img height='288'  width='378' class="cardBackground" 
			        			data-printscr="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-1-unfilled-print.jpg"
			        			src="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-2-unfilled-full.jpg" 
			        			style="position:absolute; left:0; top: 0;" ></img>
							<span class="cardNameOne cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 115px; text-align: center;">Name One</span>						
							<span class="cardNameTwo cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 150px; text-align: center;">Name Two</span>					
							<span class="regId" style="color:#000; font-size:12px; position:absolute; left:59px; top: 193px; text-align: left;"></span>
						</div>
						
						<div data-templateId="3" class="cardContainer cardTemplate"  data-font="Tangerine" data-fontsize="20" data-fontcolor="#49176e">			        						
			        		<img height='288'  width='378' class="cardBackground" 
			        		data-printscr="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-3-unfilled-print.jpg"
			        		src="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-3-unfilled-full.jpg" style="position:absolute; left:0; top: 0;" ></img>
							<span class="cardNameOne cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 115px; text-align: center;">Name One</span>						
							<span class="cardNameTwo cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 150px; text-align: center;">Name Two</span>					
							<span class="regId" style="color:#000; font-size:12px; position:absolute; left:59px; top: 193px; text-align: left;"></span>
						</div>
						
						<div data-templateId="4" class="cardContainer cardTemplate"  data-font="Libre Baskerville" data-fontsize="20" data-fontcolor="#49176e">			        						
			        		<img height='288'  width='378' class="cardBackground" 
			        		data-printscr="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-4-unfilled-print.jpg"
			        		src="http://192.168.14.167:7007/_assets/global/images/printCards/wedding-4-unfilled-full.jpg" style="position:absolute; left:0; top: 0;" ></img>
							<span class="cardNameOne cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 115px; text-align: center;">Name One</span>						
							<span class="cardNameTwo cardText" style="color:black; font-size:20pt; position:absolute; left:10px; width:350px; top: 150px; text-align: center;">Name Two</span>					
							<span class="regId" style="color:#000; font-size:12px; position:absolute; left:59px; top: 193px; text-align: left;"></span>
						</div>
						
					</div>    
				--%> 	
				
				<c:set var="serverURL"><bbbc:config key="PrintAtHomeImageServerIP" configName="GiftRegistryConfig" /></c:set>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${announcementCards}" />	
					<dsp:oparam name="outputStart">		
		            	<dsp:getvalueof var="cardsCount" param="count"/>	
			               <div id="cardTemplates" style="display:none;">
							<%-- this is where you would put each card template, need the card ID --%>		
		             </dsp:oparam>
					 <dsp:oparam name="output">	
					 	<dsp:getvalueof var="cardContent" param="element.cardContent"  />
					 	<dsp:getvalueof var="cardId" param="element.Id"/>
                                         <c:set var="filterCardContent">${fn:replace(cardContent, "${cardId}", cardId)}</c:set>
					      ${fn:replace(filterCardContent,"${imageServer}" ,serverURL)}
					</dsp:oparam>				
		            <dsp:oparam name="outputEnd">
					      	</div>
		            </dsp:oparam>
				</dsp:droplet>	
			
			</div>
		</div>
		<div style="display: none;">
		<dsp:form id="frmRowItemRemove" method="post" >
			<dsp:textarea bean="GiftRegistryFormHandler.htmlMessage" id="cardAreaFull"   name="htmlMessage" rows="40" iclass=""  ></dsp:textarea>
			
  <textarea id="cardHeaderHtml"><html>
  <head>
    <style>
    @page {  size: 8.5in 11in; }
    @page {  margin: 0in; }
    
    body {
        margin: 0px;
        padding: 0px;
    }
     
	.cardContainer {				
		display: block;
		width: 378px;
		height: 288px;
		overflow: hidden;		
		position: absolute;
	}
	
	#cardContainer {top:93px;left:28px}
	#cardContainer2 {top:381px;left:28px}
	#cardContainer3 {top:669px;left:28px}
	#cardContainer4 {top:94px;left:415px}
	#cardContainer5 {top:382px;left:415px}
	#cardContainer6 {top:670px;left:415px}
	

	@font-face {
        font-family: "Tangerine";
        src: url("${serverURL}/_assets/global/images/printCards/Tangerine_Regular.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	@font-face {
        font-family: "Playball";
        src: url("${serverURL}/_assets/global/images/printCards/Playball-Regular.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	@font-face {
        font-family: "Libre Baskerville";
        src: url("${serverURL}/_assets/global/images/printCards/LibreBaskerville-Regular.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	@font-face {
        font-family: "Open Sans Condensed";
        src: url("${serverURL}/_assets/global/images/printCards/OpenSans-CondLight.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	@font-face {
        font-family: "Julius Sans One";
        src: url("${serverURL}/_assets/global/images/printCards/JuliusSansOne-Regular.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	@font-face {
        font-family: "Raleway";
        src: url("${serverURL}/_assets/global/images/printCards/Raleway-Regular.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	@font-face {
        font-family: "AmericanTypewriter";
        src: url("${serverURL}/_assets/global/images/printCards/AMERICAT.ttf");
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H; 
	}
	
	.cardText{
		color: #000;
		font-size: 40px;
		font-family: 'Tangerine';
	}
	
	
</style>
</head>
<body>
	$html
</body>
</html></textarea>
			 
			<%-- <dsp:getvalueof var="successUrlVar" value="${contextPath}/printCards.pdf" /> --%>
				
			<%-- <dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="${successUrlVar}"/>        
			<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${successUrlVar}" /> --%>
			        <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="printCards" />
			<dsp:input bean="GiftRegistryFormHandler.downloadFlag" type="hidden" id="downloadFlag" value="false" />			

			<dsp:input bean="GiftRegistryFormHandler.printInvitationCards" type="submit" id="submitPrintCard" value="Submit"/>
			
			<%-- make sure there are no spaces before the html text in the textarea - will break JS, very IMPORTANT!! --%>
			<textarea  id="cardAreaHtml"  name="" rows="40" ><div id="cardWrapper"><div id="cardContainer" class="cardContainer">
				</div>

				<div id="cardContainer2" class="cardContainer">					
				</div>
				
				<div id="cardContainer3" class="cardContainer">
				</div>
				
				<div id="cardContainer4" class="cardContainer">
				</div>
				
				<div id="cardContainer5" class="cardContainer">
				</div>
				
				<div id="cardContainer6" class="cardContainer">
				</div>				
			</div></textarea>  
		</dsp:form>
		</div>
	</div>
	
	
	 <script defer="defer">
	
	
	//*******************************************************
	$( document ).ready(function() {	  
		
		var nameOne = '${primaryFirstName}'  + ' ' + '${primaryLastName}';
		var nameTwo = '${coFirstName}' + ' ' + '${coLastName}';
		var templateId = ('${templateId}' == '') ? '${defaultCardId}' : '${templateId}';
		
				
		if (nameTwo.length < 2 && nameOne.length < 2){
			nameTwo = 'Co-Registrant Name';			
		} 
		else if (nameTwo.length < 2 && nameOne.length > 1){
			nameTwo = '';			
		}
		
		if (nameOne.length < 2){nameOne = 'Registrant Name';}
		if (nameTwo.length > 2){ nameOne = nameOne + ' &';}
		
		
		
		BBB.printAtHome.init({
            nameOneVal : nameOne,
            nameTwoVal  : nameTwo,
            templateId : templateId,
            regId: ('${registryId}' == '')? '':'Registry # ${registryId}'
    	})
	});	  
	
		
	
var BBB = BBB || {};

BBB.printAtHome = (function($) { 		
	 	var defaults = {
	            nameOneVal : 'Name One',
	            nameTwoVal  : 'Name Two',
	            templateId : 1,
	            regId: ''
	    };    
	 	var settings = {};
		var $cardWrapper = $('#cardWrapper'),
		 	$cardTemplates =  $('#cardTemplates'),
			$editNameOne = $('#editNameOne'),
			$editNameTwo = $('#editNameTwo'),
			$editorNameOne = $('#textEditorOne'),
			$editorNameTwo = $('#textEditorTwo');
		var $cardContainer , $nameOne , $nameTwo ;
		var cardAmp = '';
		var assetsUrl = 'http://192.168.14.167:7007/_assets/global/images/printCards/';
		var serverUrl = 'http://192.168.14.167:7007';
		var	$cardAreaFull = $('#cardAreaFull'),
			$cardAreaHtml = $('#cardAreaHtml'),
			//console.log($cardAreaHtml.val());
			objArea = $('<div/>').html($cardAreaHtml.val()).contents();
		var fontFactor = 1.4;
		var scale = 1.7;
		
		$cardAreaFull.hide();
			
		this.init = function(options){
			var $template,font, fontsize,fontcolor;
			
			settings = $.extend({}, defaults, options);
			$editNameOne.val(settings.nameOneVal);
			$cardTemplates.find('.cardNameOne').text(settings.nameOneVal); 
			$editNameTwo.val(settings.nameTwoVal);
			$cardTemplates.find('.cardNameTwo').text(settings.nameTwoVal);			
			$cardWrapper.find('#regId').text(settings.regId);
			$cardTemplates.find('.regId').text(settings.regId); 
			
			//load default font, color, size based upon initial template
			$template = $cardTemplates.find('.cardTemplate[data-templateId="' + settings.templateId +'"]');
			font = $template.data('font');
			fontsize = $template.data('fontsize');
			fontcolor = $template.data('fontcolor');
			
			//update all templates with the default settings for the loaded template
			$cardTemplates.find('.cardNameOne').css('font-family', font);
			$cardTemplates.find('.cardNameTwo').css('font-family', font);			
			$cardTemplates.find('.cardNameOne').css('font-size', fontsize+'pt');
			$cardTemplates.find('.cardNameTwo').css('font-size', fontsize+'pt');			
			$cardTemplates.find('.cardNameOne').css('color', fontcolor);
			$cardTemplates.find('.cardNameTwo').css('color', fontcolor);
			
			$editNameOne.css('font-family', font);
			$editNameOne.css('font-size', (fontsize*fontFactor)+'px');
			$editNameOne.css('color', fontcolor);
			$editNameTwo.css('font-family', font);
			$editNameTwo.css('font-size', (fontsize*fontFactor)+'px');
			$editNameTwo.css('color', fontcolor);
			
			
			//preselect fields on editors
			$('#fontFamilyTwo').val(font);
			$('#fontFamilyOne').val(font);
			$('#nameOneSize').val((fontsize));
			$('#nameTwoSize').val((fontsize));
			
			//quickly cylce all the templates to fix chrome blurry bug
			$cardTemplates.find('.cardTemplate').each(function( index ) {
				switchTemplate($(this).data('templateid'));
				switchTemplate($(this).data('templateid'));
			});
			/*
			switchTemplate(1);
			switchTemplate(1);
			switchTemplate(2);
			switchTemplate(2);
			switchTemplate(3);
			switchTemplate(3);
			switchTemplate(4);
			switchTemplate(4);
			*/
			
			switchTemplate(settings.templateId);
		};
		
		var switchTemplateRefresh = function(){
			$cardContainer = $cardWrapper.find('.cardContainer');
			$nameOne = $cardWrapper.find('.cardNameOne');
			$nameTwo = $cardWrapper.find('.cardNameTwo');
		};
		
		
		this.switchTemplate = function(templateId){
			var src, printSrc, html, $template, $background;
			
			//templateId = $(this).data('templateid');
			//console.log(templateId );
			
			$template = $cardTemplates.find('.cardTemplate[data-templateId="' + templateId +'"]');
			
			if($template)[0]
			{
				html = $('<div>').append($template.clone()).remove().html();
				//console.log($template);
				$cardWrapper.find('.cardContainer').replaceWith(html);	
				$cardWrapper.find('.cardContainer').css({
					'width': '644px',
					height: 'auto'
				});
				
				//make image the background
				$cardWrapper.find('.cardBackground').css({
					//'width': '644px',
					height: 'auto'
				});
			
			
				switchTemplateRefresh();

				
				$nameOne.hide();
				$nameTwo.hide();
				
				//in the future, we can position the edit fields where the name fields are
				//based on the name span positions
				
				$editNameOne.css({
					position: 'absolute',
					'left': $nameOne.css('left'),
					'width': $nameOne.css('width'),
					'top': $nameOne.css('top'),
					'text-align' : $nameOne.css('text-align'),
					'display': 'block'
				});
				
				$editNameTwo.css({
					position: 'absolute',
					'left': $nameTwo.css('left'),
					'width': $nameTwo.css('width'),
					'top': $nameTwo.css('top'),
					'text-align' : $nameTwo.css('text-align'),
					'display': 'block'
				});
				
				//console.log($editNameOne.position());
				//console.log($editNameTwo.position());
				
				setEditorPosition($editorNameOne, $editNameOne);
				setEditorPosition($editorNameTwo, $editNameTwo);
				
				
			}
			
			
			$background = $template.find('.cardBackground')
			if($background[0])
			{
			
				printSrc = $background.data('printscr');
				if(printSrc != '') $background.attr('src',printSrc);
			}
			objArea.find('.cardContainer').html($template.html());
			//objArea.find('.cardContainer').css('background-image', "url("+serverUrl  + printSrc+")" );
			updateCardAreaHtml();
			
		};
		
		var setEditorPosition = function($editor, $nameField) {
			var namePosition = {}, 
				nameTop=0, 
				nameBottom=0, 
				nameLeft=0, 
				nameRight=0,
				editorLeft=0;
			
			namePosition = $nameField.position();
			//console.log(namePosition);
			nameTop = namePosition.top;
			nameLeft = namePosition.left; 

			//the bottom of the name input field, times the transform scale
			nameBottom = nameTop +  ($nameField.outerHeight(true)*scale);
			//console.log({nameTop :nameTop ,nameLeft:nameLeft, nameBottom :nameBottom });
			
			//want to align editor with center of input field
			//to get left position, get width of input field, minus width of editor, divide by 2
			if(($nameField.outerWidth()*scale) > $editor.width())
			{
				editorLeft = ($nameField.outerWidth()*scale - $editor.width()) / 2;
			}
			//console.log(editorLeft);
			//$editor.offset({ top: nameBottom, left: nameLeft });
			
			$editor.css({				
				'left': editorLeft,				
				'top': nameBottom +5				
			});
			
			
			
		};
		  
		$('.cardThumbnail').click(function(e){
			e.preventDefault();			
			var src, printSrc, templateId, html, $template;
			
			templateId = $(this).data('templateid');
			//console.log(templateId );
			
			switchTemplate(templateId);
		});

		$('.printPDF').click(function(){

			$('#downloadFlag').val('false');
			$('#frmRowItemRemove').prop("target", "_blank");
			$('#submitPrintCard').trigger('click');
		});
		
		$('.downloadPDF').click(function(){


			$('#downloadFlag').val('true');
			$('#frmRowItemRemove').removeAttr("target");
			$('#submitPrintCard').trigger('click');
		});
		
		$('#editNameOne').keyup( function(){
			//console.log($(this).val());		
			//$nameOne.text(this.value);
			$cardTemplates.find('.cardNameOne').text(this.value);
			
			objArea.find('.cardNameOne').text(this.value);
			updateCardAreaHtml();			
		});
		
		$('#editNameTwo').keyup( function(){		
			$nameTwo.text($(this).val());		
			cardAmp = ($(this).val() == '') ? '' :  '&amp;' ;
			
			
			
			
			//update all templates
			$cardTemplates.find('.cardNameTwo').text(this.value);
			
			//update textarea
			objArea.find('.cardNameTwo').text(this.value);			
			
			($(this).val() == '') ? objArea.find('.cardAmp').hide() : objArea.find('.cardAmp').show();
			updateCardAreaHtml();			
			
		});
		
		$('#editNameOne').focus(function(){
			$('#textEditorTwo').hide();
			$('#textEditorOne').fadeIn(200);
		});
		
		$('#editNameTwo').focus(function(){
			$('#textEditorOne').hide();
			$('#textEditorTwo').fadeIn(200);			
		});
		
		$('.editorClose').click(function(){
			$(this).parents('.textEditor').fadeOut(200);
		});
		
		
		$('.fontColor').click(function(e){
			e.preventDefault();
			var $parent
			var parentID = '';
			var color = $(this).data('color'); 
			
			$parent = $(this).parents('ul.fontColors');			
			if ($parent.attr('id') == 'fontColorsOne') {
				$editNameOne.css('color', color); 
				objArea.find('.cardNameOne').css('color', color);
				$cardTemplates.find('.cardNameOne').css('color', color);			
			}
			else {
				$editNameTwo.css('color', color);
				objArea.find('.cardNameTwo').css('color', color);
				$cardTemplates.find('.cardNameTwo').css('color', color);
			}	
			
			updateCardAreaHtml();	
		});
		  
		$('#nameOneSize').on("input change", function(){			
			$editNameOne.css('font-size',($(this).val()*fontFactor)+'px');
			objArea.find('.cardNameOne').css('font-size', $(this).val()+'pt');
			$cardTemplates.find('.cardNameOne').css('font-size', $(this).val()+'pt');
			updateCardAreaHtml();	
			
			/*
			<select id="nameOneSize" class="fontSize uniform" style="opacity: 0;"> 
				<option value="14">smaller</option> 
				<option value="16">small</option> 
				<option value="17">medium</option> 
				<option value="18">large</option> 
				<option value="20">larger</option> 
			</select>
			*/
			
		}); 
		
		$('#nameTwoSize').on("input change", function(){						
			$editNameTwo.css('font-size',($(this).val()*fontFactor)+'px');
			objArea.find('.cardNameTwo').css('font-size', $(this).val()+'pt');
			$cardTemplates.find('.cardNameTwo').css('font-size', $(this).val()+'pt');
			updateCardAreaHtml();	
		});
		
		
		$('#fontFamilyOne').change(function(){
			var font = $(this).val();			
			$editNameOne.css('font-family', font);
			objArea.find('.cardNameOne').css('font-family', font);
			$cardTemplates.find('.cardNameOne').css('font-family', font);
			updateCardAreaHtml();	
		});
		
		$('#fontFamilyTwo').change(function(){
			var font = $(this).val();			
			$editNameTwo.css('font-family', font);
			objArea.find('.cardNameTwo').css('font-family', font);
			$cardTemplates.find('.cardNameTwo').css('font-family', font);
			updateCardAreaHtml();	
		});
		
		var updateCardAreaHtml = function(){
			$cardAreaHtml.val(objArea.html());
		}
		
		$('#submitPrintCard').click(function(e){
			//e.preventDefault();
			var pdfHtml;
			
			pdfHtml = $cardAreaHtml.val();
			
			//add closing image tags for pdf parser
			pdfHtml = pdfHtml.replace(/<img[^>]+>/g, '$&' +"</img>");
			
			//add closing input tags for pdf parser
			pdfHtml = pdfHtml.replace(/<input[^>]+>/g, '$&' +"</input>");
			//console.log(pdfHtml);
			$cardAreaFull.val($('#cardHeaderHtml').val().replace("$html",pdfHtml));
			
			//need to add selected font face
			
		});
		
		 return {
             init: init           
         };
		
}( jQuery ));
</script>,
   
</jsp:body>
<jsp:attribute name="footerContent">
    <script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.pageName = 'Registry Card Designer';
			s.channel = 'Registry';
			s.prop1 = 'Registry';
			s.prop2 = 'Registry';
			s.prop3 = 'Registry';
			s.prop4 = '';
			s.prop5 = '';		
			var s_code = s.t();
			if (s_code)
				document.write(s_code);
		}
	</script>
    </jsp:attribute>
</bbb:pageContainer>	
</dsp:page> 