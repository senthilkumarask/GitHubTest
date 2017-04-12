<dsp:page>
<dsp:importbean bean="com/bbb/account/OrderHistoryDroplet" />
<dsp:importbean bean="/com/bbb/account/BBBOrderTrackingFormHandler"/>
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:attribute name="pageWrapper">orderDetailWrapper myAccount</jsp:attribute>
    <jsp:body>
    
    	<style type="text/css">
    	#ordersSummaryTabs .ui-tabs-panel {padding: 3px 0;}
    	.orderHeader {
    		margin-bottom: 1px;
    		background-color:#f7f7f7;
    		
    	}
    	
    	/* table header, don't need this really */
    	.orderHeader table {margin: 0;}
    	.orderHeader table p {margin: 0 !important;}
    	
    	
    	.orderHeader div {
			font-family: futuraStdHeavy;
			font-size:18px;
			color:#444;
			padding:5px 12px 5px 0;
			background-color:#f7f7f7;
		}
		
		.orderContent {margin-top: 14px;}
		
		a.collapseLink, a.expandLink{
			font-size: 28px;
			line-height: 18px;
			padding: 0 10px;
			color:#444;
		}
		
		.ordersHeader table {margin-bottom: 1px;}
		
    	</style>
    
   		<%@ page import="com.bbb.constants.BBBAccountConstants"%>
		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12">
				<h1 class="account fl">
					<bbbl:label key="lbl_personalinfo_myaccount"
						language="${pageContext.request.locale.language}" />
				</h1>
				<h3 class="subtitle fl"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></h3>
                <div class="clear"></div>
			</div>
			<div class="grid_2">
				<c:import url="/account/left_nav.jsp">
				  <c:param name="currentPage"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
			</div>
			<div class="grid_10 ordersSummary trackOrderDetail">
				<div class="clearfix prefix_1">
				
					<div id="ordersSummaryTabs" class="alpha omega grid_9 clearfix">
						<ul class="categoryProductTabsLinks noprint"> 
							<li><a href="#onlineOrders">Online</a></li>
							<li><a href="#instoreOrders">In-Store</a></li>
						</ul>
						<div id="onlineOrders">

							<div class="grid_9 alpha omega clearfix ordersHeader"><table title="Track Order Search Results"> <thead> <tr> <th class="width_3" scope="col">Order Date</th> <th class="width_3" scope="col">Order Number</th> <th class="width_1" scope="col">Total</th> <th class="width_2 talign-rt" scope="col">Order Status</th> </tr> </thead></table></div>
						
													
							<div id="onlineOrdersContent">
							
								<div class="grid_9 alpha omega clearfix orderHeader"> 
									<div class="grid_3 alpha omega " >
										<a class="collapseLink" href="#">-</a>
										November 12, 2013
									</div> 
									<div class="grid_3 alpha omega " >BBB2050681051</div> 
									<div class="grid_1 alpha omega " >$76.97</div> 
									<div class="grid_2 alpha omega talign-rt noMarRight" >Submitted</div> 
								</div>
								<div class="grid_9 alpha omega clearfix orderContent "> <div class="grid_9 alpha omega clearfix"> <div class="grid_3 alpha"> <h4>Shipment&nbsp;1</h4> <div class="detail"> <dl class="clearfix"> <dt class="fl">Shipping Method:&nbsp;</dt> <dd class="fl">Standard</dd> </dl> <dl class="clearfix"> <dt class="fl">Gift Options:&nbsp;</dt> <dd class="fl">None</dd> </dl> </div> <div class="detail"> <dl> <dt>Shipped To:</dt> <dd>Dhanashree Waghmare</dd> <dd></dd><dd> </dd><dd>Wedgewood Manor 41 Madison Ave</dd> <dd>Apt 1C</dd> <dd>Madison, NJ 07940-1453</dd> </dl> </div> </div> <div class="grid_6 omega"> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="Waterpik® Cordless Plus Water Flosser®" href="/store/product/waterpik-reg-cordless-plus-water-flosser-reg/119004?skuId=14660976"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/10024514660976p?wid=63&amp;hei=63&amp;" alt="Waterpik® Cordless Plus Water Flosser®" title="Waterpik® Cordless Plus Water Flosser®"> </a> </div> <div class="grid_3 product-descript"> <a title="Waterpik® Cordless Plus Water Flosser®" href="/store/product/waterpik-reg-cordless-plus-water-flosser-reg/119004?skuId=14660976">Waterpik® Cordless Plus Water Flosser®</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="119004" value="Write a review"> </div> </div> </div> </div> </div> <div class="grid_9 alpha omega shipmentSummary"> <div class="grid_3 alpha"> <h4>Shipment&nbsp;2</h4> <div class="detail"> <dl class="clearfix"> <dt class="fl">Shipping Method:&nbsp;</dt> <dd class="fl">Standard</dd> </dl> <dl class="clearfix"> <dt class="fl">Gift Options:&nbsp;</dt> <dd class="fl">None</dd> </dl> </div> <div class="detail"> <dl> <dt>Shipped To:</dt> <dd>sdf dfgd</dd> <dd></dd><dd> </dd><dd>650 liberty avenue</dd> <dd></dd> <dd>union, NJ 07083</dd> </dl> </div> </div> <div class="grid_6 omega"> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers" href="/store/product/k-cup-reg-18-count-green-mountain-coffee-reg-breakfast-blend-coffee-for-keurig-reg-brewers/1014560351?skuId=14560351"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/16625014560351p?wid=63&amp;hei=63&amp;" alt="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers" title="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers"> </a> </div> <div class="grid_3 product-descript"> <a title="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers" href="/store/product/k-cup-reg-18-count-green-mountain-coffee-reg-breakfast-blend-coffee-for-keurig-reg-brewers/1014560351?skuId=14560351">K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1014560351" value="Write a review"> </div> </div> </div> </div> </div> </div>
													
							
								<div class="grid_9 alpha omega clearfix orderHeader"> 
									<div class="grid_3 alpha omega " >
										<a class="collapseLink" href="#">-</a>
										January 3, 2014
									</div> 
									<div class="grid_3 alpha omega " >BBB5050755014</div> 
									<div class="grid_1 alpha omega " >$215.94</div> 
									<div class="grid_2 alpha omega talign-rt noMarRight" >Submitted</div> 
								</div>								
								<div class="grid_9 alpha omega clearfix orderContent"> <div class="grid_9 alpha omega clearfix"> <div class="grid_3 alpha"> <div class="detail"> <dl class="clearfix"> <dt class="fl">Shipping Method:&nbsp;</dt> <dd class="fl">Standard</dd> </dl> <dl class="clearfix"> <dt class="fl">Gift Options:&nbsp;</dt> <dd class="fl">None</dd> </dl> </div> <div class="detail"> <dl> <dt>Shipped To:</dt> <dd>Dhanashree Waghmare</dd> <dd></dd><dd> </dd><dd>Wedgewood Manor 41 Madison Ave</dd> <dd>Apt 1C</dd> <dd>Madison, NJ 07940-1453</dd> </dl> </div> </div> <div class="grid_6 omega"> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-18-inch-square-toss-pillow/1018574314?skuId=18574314"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/18331818574314p?wid=63&amp;hei=63&amp;" alt="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow" title="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow"> </a> </div> <div class="grid_3 product-descript"> <a title="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-18-inch-square-toss-pillow/1018574314?skuId=18574314">J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1018574314" value="Write a review"> </div> </div> </div> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-20-inch-square-toss-pillow/1018574322?skuId=18574322"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/18331918574322p?wid=63&amp;hei=63&amp;" alt="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow" title="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow"> </a> </div> <div class="grid_3 product-descript"> <a title="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-20-inch-square-toss-pillow/1018574322?skuId=18574322">J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1018574322" value="Write a review"> </div> </div> </div> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="J. Queen New York™&nbsp;Alicante Boudoir Pillow" href="/store/product/j-queen-new-yorkalicante-boudoir-pillow/1018574349?skuId=18574349"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/18332018574349p?wid=63&amp;hei=63&amp;" alt="J. Queen New York™&nbsp;Alicante Boudoir Pillow" title="J. Queen New York™&nbsp;Alicante Boudoir Pillow"> </a> </div> <div class="grid_3 product-descript"> <a title="J. Queen New York™&nbsp;Alicante Boudoir Pillow" href="/store/product/j-queen-new-yorkalicante-boudoir-pillow/1018574349?skuId=18574349">J. Queen New York™&nbsp;Alicante Boudoir Pillow</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1018574349" value="Write a review"> </div> </div> </div> </div> </div> </div>
						
						
							</div>
						</div>
						<div id="instoreOrders">
							
								<div class="grid_9 alpha omega clearfix ordersHeader"><table title="Track Order Search Results"> <thead> <tr> <th class="width_3" scope="col">Order Date</th> <th class="width_3" scope="col">Order Number</th> <th class="width_1" scope="col">Total</th> <th class="width_2 talign-rt" scope="col">Order Status</th> </tr> </thead></table></div>
						
								<div class="grid_9 alpha omega clearfix orderHeader"> 
									<div class="grid_3 alpha omega " >
										<a class="collapseLink" href="#">-</a>
										January 3, 2014
									</div> 
									<div class="grid_3 alpha omega " >BBB5050755014</div> 
									<div class="grid_1 alpha omega " >$215.94</div> 
									<div class="grid_2 alpha omega talign-rt noMarRight" >Submitted</div> 
								</div>								
								<div class="grid_9 alpha omega clearfix orderContent"> <div class="grid_9 alpha omega clearfix"> <div class="grid_3 alpha"> <div class="detail"> <dl class="clearfix"> <dt class="fl">Shipping Method:&nbsp;</dt> <dd class="fl">Standard</dd> </dl> <dl class="clearfix"> <dt class="fl">Gift Options:&nbsp;</dt> <dd class="fl">None</dd> </dl> </div> <div class="detail"> <dl> <dt>Shipped To:</dt> <dd>Dhanashree Waghmare</dd> <dd></dd><dd> </dd><dd>Wedgewood Manor 41 Madison Ave</dd> <dd>Apt 1C</dd> <dd>Madison, NJ 07940-1453</dd> </dl> </div> </div> <div class="grid_6 omega"> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-18-inch-square-toss-pillow/1018574314?skuId=18574314"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/18331818574314p?wid=63&amp;hei=63&amp;" alt="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow" title="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow"> </a> </div> <div class="grid_3 product-descript"> <a title="J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-18-inch-square-toss-pillow/1018574314?skuId=18574314">J. Queen New York™&nbsp;Alicante 18-Inch Square Toss Pillow</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1018574314" value="Write a review"> </div> </div> </div> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-20-inch-square-toss-pillow/1018574322?skuId=18574322"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/18331918574322p?wid=63&amp;hei=63&amp;" alt="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow" title="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow"> </a> </div> <div class="grid_3 product-descript"> <a title="J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow" href="/store/product/j-queen-new-yorkalicante-20-inch-square-toss-pillow/1018574322?skuId=18574322">J. Queen New York™&nbsp;Alicante 20-Inch Square Toss Pillow</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1018574322" value="Write a review"> </div> </div> </div> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="J. Queen New York™&nbsp;Alicante Boudoir Pillow" href="/store/product/j-queen-new-yorkalicante-boudoir-pillow/1018574349?skuId=18574349"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/18332018574349p?wid=63&amp;hei=63&amp;" alt="J. Queen New York™&nbsp;Alicante Boudoir Pillow" title="J. Queen New York™&nbsp;Alicante Boudoir Pillow"> </a> </div> <div class="grid_3 product-descript"> <a title="J. Queen New York™&nbsp;Alicante Boudoir Pillow" href="/store/product/j-queen-new-yorkalicante-boudoir-pillow/1018574349?skuId=18574349">J. Queen New York™&nbsp;Alicante Boudoir Pillow</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1018574349" value="Write a review"> </div> </div> </div> </div> </div> </div>
						
								<div class="grid_9 alpha omega clearfix orderHeader"> 
									<div class="grid_3 alpha omega " >
										<a class="collapseLink" href="#">-</a>
										November 12, 2013
									</div> 
									<div class="grid_3 alpha omega " >BBB2050681051</div> 
									<div class="grid_1 alpha omega " >$76.97</div> 
									<div class="grid_2 alpha omega talign-rt noMarRight" >Submitted</div> 
								</div>
								<div class="grid_9 alpha omega clearfix orderContent "> <div class="grid_9 alpha omega clearfix"> <div class="grid_3 alpha"> <h4>Shipment&nbsp;1</h4> <div class="detail"> <dl class="clearfix"> <dt class="fl">Shipping Method:&nbsp;</dt> <dd class="fl">Standard</dd> </dl> <dl class="clearfix"> <dt class="fl">Gift Options:&nbsp;</dt> <dd class="fl">None</dd> </dl> </div> <div class="detail"> <dl> <dt>Shipped To:</dt> <dd>Dhanashree Waghmare</dd> <dd></dd><dd> </dd><dd>Wedgewood Manor 41 Madison Ave</dd> <dd>Apt 1C</dd> <dd>Madison, NJ 07940-1453</dd> </dl> </div> </div> <div class="grid_6 omega"> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="Waterpik® Cordless Plus Water Flosser®" href="/store/product/waterpik-reg-cordless-plus-water-flosser-reg/119004?skuId=14660976"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/10024514660976p?wid=63&amp;hei=63&amp;" alt="Waterpik® Cordless Plus Water Flosser®" title="Waterpik® Cordless Plus Water Flosser®"> </a> </div> <div class="grid_3 product-descript"> <a title="Waterpik® Cordless Plus Water Flosser®" href="/store/product/waterpik-reg-cordless-plus-water-flosser-reg/119004?skuId=14660976">Waterpik® Cordless Plus Water Flosser®</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="119004" value="Write a review"> </div> </div> </div> </div> </div> <div class="grid_9 alpha omega shipmentSummary"> <div class="grid_3 alpha"> <h4>Shipment&nbsp;2</h4> <div class="detail"> <dl class="clearfix"> <dt class="fl">Shipping Method:&nbsp;</dt> <dd class="fl">Standard</dd> </dl> <dl class="clearfix"> <dt class="fl">Gift Options:&nbsp;</dt> <dd class="fl">None</dd> </dl> </div> <div class="detail"> <dl> <dt>Shipped To:</dt> <dd>sdf dfgd</dd> <dd></dd><dd> </dd><dd>650 liberty avenue</dd> <dd></dd> <dd>union, NJ 07083</dd> </dl> </div> </div> <div class="grid_6 omega"> <div class="registry-product clearfix"> <div class="grid_1 alpha"> <a title="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers" href="/store/product/k-cup-reg-18-count-green-mountain-coffee-reg-breakfast-blend-coffee-for-keurig-reg-brewers/1014560351?skuId=14560351"> <img src="//s7d9.scene7.com/is/image/BedBathandBeyond/16625014560351p?wid=63&amp;hei=63&amp;" alt="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers" title="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers"> </a> </div> <div class="grid_3 product-descript"> <a title="K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers" href="/store/product/k-cup-reg-18-count-green-mountain-coffee-reg-breakfast-blend-coffee-for-keurig-reg-brewers/1014560351?skuId=14560351">K-Cup® 18-Count Green Mountain Coffee® Breakfast Blend Coffee for Keurig® Brewers</a> </div> <div class="grid_2 omega"> <div class="button fr clearfix bvSubmitReviewButtonContainer"> <input type="button" onclick="javascript:customLinkTracking('write a review – track order');" class="triggerBVsubmitReview" data-bvproductid="1014560351" value="Write a review"> </div> </div> </div> </div> </div> </div>
						
						</div>
					</div>
				</div>
			</div>
		</div>
		</jsp:body>
		<jsp:attribute name="footerContent">
			<script type="text/javascript">
			
			 if(($(".ordersSummary")[0]))
				{
		        	$("#ordersSummaryTabs").tabs({
						select: function( event, ui ) {
							window.location.hash = '#t=' + ui.tab.hash.substr(1);							
						}
		            
					});
		        	
		        	//$('#onlineOrdersContent').accordion({ header: "div.orderHeader" });
		        	$('.expandLink, .collapseLink').on('click',function(ev){
		        		ev.preventDefault;		        		
		        		$(this).closest('.orderHeader').next('.orderContent').slideToggle('slow');
		        		
		        		$(this).toggleClass("expandLink").toggleClass("collapseLink");
		        		
		        		if ($(this).html() == '-') {
		        			$(this).html('+');
		        		}
		        		else if ($(this).html() == '+') {
		        			$(this).html('-');
		        		}
		        		
		        		
		        		//if($(this).hasClass('expandLink')) {$(this).removeClass('expandLink').addClass('collapseLink').html('-')};
		        		//if($(this).hasClass('collapseLink')) {$(this).removeClass('collapseLink').addClass('expandLink').html('+')};
		        		
		        	    return false;
		        	});
				}
			
			           if(typeof s !=='undefined') {
			        	s.pageName='My Account > My Orders';
			        	s.channel='My Account';
			        	s.prop1='My Account';
			        	s.prop2='My Account';
			        	s.prop3='My Account';
			                
			            var s_code=s.t();
			            if(s_code)document.write(s_code);           
			           }
			           function omnitureExternalLinks(data){
			             	if (typeof s !== 'undefined') {
			             	externalLinks(data);
			             	}
			             }
			</script>
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>