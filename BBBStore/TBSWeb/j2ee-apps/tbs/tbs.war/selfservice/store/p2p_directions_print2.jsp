<%-- ====================== Description===================
/**
* This page is used to display directions to reach out to store from inputed starting address on printer friendly page 
* @author Babi
**/
--%>
<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/P2PDirectionsFormHandler" />
	<c:set var="section" value="browse" scope="request" />
	<c:set var="pageWrapper" value="useStoreLocator noStoreLocatorCss" scope="request" />

<bbb:pageContainer titleKey="lbl_find_store_print_title" index="false" follow="false">

	<jsp:attribute name="section">${section}</jsp:attribute>
	<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

	<jsp:body>		
	<%--Jsp body starts from here--%>

<div id="content" class="container_12 clearfix" role="main">


	<style type="text/css">
    	.print {display:none;}
    	#storeLocatorTurnByTurnDirections table {width: 100%; }
   		#storeLocatorTurnByTurnDirections table td {border-bottom: 1px solid #ccc; padding: 5px 0;}
   		#storeLocatorTurnByTurnRouteInfo {font-weight: bold;}
   		#storeLocatorMapResults {margin-top: 10px; height: 600px; page-break-inside:avoid;}
   		
   		#topNavHeader,
		#navBottomNotes,
		#menuWrapper,
		#mainHeader .mainContent,
		#headerInformation #promoArea,
		#collegeBridalArea,
		#searchWrapper,
		#footer,
		.breadcrumbs,
		.noprint,
		div.share,
		#menuWrapper,
		.fb-like {display: none !important;}
    
    </style>
	
    <script type="text/javascript">
    
    $.urlParam = function(name){
    	var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    	if (results==null){
    		return null;
    	} else {
    		return results[1] || 0;
    	}	
    };
    
 	$(document).ready(function() {
    	
    	var origin, destination, options;
    	origin = decodeURIComponent($.urlParam('origin'));
    	destination = decodeURIComponent($.urlParam('destination'));    	
    	
        options = {
				conceptID: '${currentSiteId}',
                debug:false    
        };
            
        BBB.BBBStoreLocator.init(options);
        BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
        BBB.BBBStoreLocator.loadMapDirections(origin, destination,{});
        
      	//only load static map and open the print dialog after the map directions have finished loading
        $('body').on('storeLocator.directionsLoaded', 
    			function(){    				
    				$('#storeLocatorMapResults').hide();
    				$('#storeLocatorStaticMapResults').attr('src', BBB.BBBStoreLocator.getStaticMapUrl);
    				window.print();    				
    			}
    	);
        
        
    });
    
    </script> 


	<div id="storeLocatorPrintAddresses" class="container_12 " >
	    <div id="storeLocatorPrintAddressesOrigin" class="grid_6 alpha "></div>                    
	    <div id="storeLocatorPrintAddressesDestination" class="grid_6 omega "></div>
	</div>    
              
    <div class="clear"></div>        
              
   	<div id="storeLocatorDirections" class="grid_12 alpha omega "> 
    	<div id="storeLocatorTurnByTurnDirections"></div>
        <div id="storeLocatorTurnByTurnRouteInfo"></div>                    
	</div>
              
    <div class="clear"></div>        
                  
    <div id="storeLocatorResults" class="container_12" >
    	<div id="storeLocatorMapResults" class="grid_12 alpha omega " ></div>
    	<img id="storeLocatorStaticMapResults" src="" />
	</div>       

</div>
	<%--Jsp body Ends here--%>	
	</jsp:body>
	</bbb:pageContainer>
</dsp:page>