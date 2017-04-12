<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />	


	<dsp:getvalueof var="lrgImgUrl" param="lrgImgUrl" />
	<dsp:getvalueof var="tAndc" param="tAndc" />
	<dsp:getvalueof var="offerId" param="offerId" />
	<dsp:getvalueof var="expDate" param="expDate" />

	<c:choose>
		<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
			<c:set var="bodyClass" value="baby" />
		</c:when>
		<c:otherwise>
			<c:set var="bodyClass" value="bbby" />
		</c:otherwise>
	</c:choose>

	<div id="content" class="container_12 clearfix ${bodyClass}">
		<%-- BP - not sure if concept logo comes from current site, or coupon data --%>
		<div class="header">
			<div class="innerHeader">

				<%-- turn this logo into bcc text area --%>
				<bbbt:textArea key="txt_printCoupon_Logo" language ="${pageContext.request.locale.language}"/>

				<%--
				<c:choose>
		          <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
		              <img class="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb_bb.png" height="50" width="121" alt="Buy Buy Baby" />
		          </c:when>
		          <c:otherwise>
		              <img class="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond" />
		          </c:otherwise>
		      </c:choose>
		      --%>
		      <h1>		      	
		      	<bbbl:label key="lbl_instore_savingscertificate" language ="${pageContext.request.locale.language}"/>
		      </h1>
		      <div class="cpnImg">
		            <c:choose>
		      		<c:when test="${not empty lrgImgUrl}">
							<img src="${lrgImgUrl}" />
					</c:when>
					<c:otherwise>
							<img src="/_assets/global/images/couponWallet/sampleCoupon.JPG" />
					</c:otherwise>
					</c:choose>
		      </div>
			</div>
		</div>

		<div class="content">
			<div class="innerContent">
				<div class="promoDesription">					
					<%-- replace description exp date with url value--
					${fn:replace(promoDesription,"${expDate}" ,expDate)}
					--%>

					<p>
						<bbbl:label key="lbl_printCoupon_expiration_msg" language ="${pageContext.request.locale.language}"/> ${expDate} 
						<br />

						<bbbl:label key="lbl_printCoupon_rule" language ="${pageContext.request.locale.language}"/> 
					</p>
				</div>
				<div class="tNc">
					${tAndc}
				</div>
				<div class="coupon">
					<div class="couponImageWrap">

					</div>
				</div>
			</div>
		</div>

		<div class="footer">
			<div class="innerFooter">
				<p>
				<bbbl:label key="lbl_printCoupon_copyright" language ="${pageContext.request.locale.language}"/>
					
				</p>
			</div>
		</div>
		<div class="grid_12">
			<h1 class="account fl"></h1>
		</div>
	</div>


	<script src="/_assets/global/js/thirdparty/coupon/bcmath-min.js"></script>
	<script src="/_assets/global/js/thirdparty/coupon/pdf417-min.js"></script>

	<script>
		var $couponImageWrap = document.getElementsByClassName('couponImageWrap')[0];

		generate_pdf('${offerId}', $couponImageWrap);

		function generate_pdf(barcode, target)
		{
			var
				canvas = document.createElement('canvas'),
				textNode = document.createTextNode(barcode),
				bw = 3, bh = 3, y = 0, r = 0, c = 0,
				barcode, ctx;

			PDF417.init(barcode);
			barcode = PDF417.getBarcodeArray();

			canvas.width = bw * barcode['num_cols'];
			canvas.height = bh * barcode['num_rows'];
			ctx = canvas.getContext('2d');

			for ( r = 0; r < barcode['num_rows']; ++r )
			{
				var x = 0;
				for ( c = 0; c < barcode['num_cols']; ++c )
				{
				  if (barcode['bcode'][r][c] == 1)
				  {
				    ctx.fillRect(x, y, bw, bh);
				  }
				  x += bw;
				}
				y += bh;
			}
			//target.appendChild(canvas);
			var image = new Image(310,100);
			image.src = canvas.toDataURL("image/png");
			target.appendChild(image);
			target.appendChild(textNode);
		}
	</script>


	<link rel="stylesheet" type="text/css" href="/_assets/global/css/print_coupon.css">

</dsp:page>	