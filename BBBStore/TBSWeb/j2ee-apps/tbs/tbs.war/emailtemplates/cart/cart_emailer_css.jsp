<dsp:page>
<dsp:getvalueof var="siteId" param="siteId" />
<dsp:getvalueof var="emailType" param="emailType" />

<c:choose>
    <c:when test="${siteId eq 'BedBathUS'}">
    	<style type="text/css">
			/* Client-specific Styles */
			<!--#outlook a{padding:0;}--> /* Force Outlook to provide a "view in browser" button. */
			<!--#outlook p.MsoNormal, #outlook .MsoNormal { line-height: 0px; font-family: arial;}-->
			<!--body{width:100% !important;}--> 
			<!--.ReadMsgBody{width:100%;}--> /* Force Hotmail to display emails at full width */
			<!--.ExternalClass{width:100%;}--> /* Force Hotmail to display emails at full width */
			<!--body{-webkit-text-size-adjust:none;}--> /* Prevent Webkit platforms from changing default text sizes. */

			/* Reset Styles */
			<!--body{margin:0; padding:0;}-->
			<!--img{border:0; height:auto; line-height:100%; outline:none; text-decoration:none; display: block;}-->
			<!--table td{border-collapse:collapse;}-->
			<!--#backgroundTable{height:100% !important; margin:0; padding:0; width:100% !important;}-->

			/* Template Styles */

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: COMMON PAGE ELEMENTS /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Page
			* @section background color
			* @tip Set the background color for your email. You may want to choose one that matches your company's branding.
			* @theme page
			*/
			<!--body, #backgroundTable{	/*@editable*/ background-color:#FFFFFF;}-->

			/**
			* @tab Page
			* @section email border
			* @tip Set the border for your email.
			*/
			<!--#templateContainer{	/*@editable*/ border:0;}-->

			/**
			* @tab Page
			* @section heading 1
			* @tip Set the styling for all first-level headings in your emails. These should be the largest of your headings.
			* @style heading 1
			*/
			<!--h1, .h1{ /*@editable*/ color:#666666; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:40px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%;	margin-left:0; /*@editable*/ text-align:left;}-->

			/**
			* @tab Page
			* @section heading 2
			* @tip Set the styling for all second-level headings in your emails.
			* @style heading 2
			*/
			<!--h2, .h2{ /*@editable*/ color:#666666; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:18px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%; margin-left:0; /*@editable*/ text-align:left;}-->

			/**
			* @tab Page
			* @section heading 3
			* @tip Set the styling for all third-level headings in your emails.
			* @style heading 3
			*/
			<!--h3, .h3{ /*@editable*/ color:#606060; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:16px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%; margin-left:0; /*@editable*/ text-align:left;}-->

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: HEADER /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Header
			* @section header style
			* @tip Set the background color and border for your email's header area.
			* @theme header
			*/
			<!--#templateHeader{ /*@editable*/ background-color:#FFFFFF; }-->
			
			<!--p.MsoNormal, .MsoNormal { line-height: 0px; font-family: arial;}-->
			
			/**
			* @tab Header
			* @section header text
			* @tip Set the styling for your email's header text. Choose a size and color that is easy to read.
			*/
			<!--.headerContent{ /*@editable*/ color:#000000; /*@editable*/ font-family:Arial; /*@editable*/ font-size:12px; /*@editable*/ font-weight:normal; /*@editable*/ line-height:100%; /*@editable*/ padding-top: 15px; padding-bottom: 15px; /*@editable*/ text-align:left; /*@editable*/ vertical-align:middle; border-bottom: 3px solid #283794;}-->

			/**
			* @tab Header
			* @section header link
			* @tip Set the styling for your email's header links. Choose a color that helps them stand out from your text.
			*/
			<!--.headerContent a:link, .headerContent a:visited, /* Yahoo! Mail Override */ .headerContent a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->
			
			<!--.headerContent a span {color: #273691;}-->
			
			<!--.headerContent a:hover, /* Yahoo! Mail Override */ .headerContent a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:none;}-->
			
			<!--#headerImage{ height:auto; max-width:173px !important;}-->
			
			/**
			* @tab Header
			* @section header Table
			* @theme header
			*/
			<!--#templateHeaderNav { /*@editable*/ background-color:#3245B9; /*@editable*/ border-bottom: 3px solid #00AEF0; font-family: arial,helvetica,sans-serif}-->
			
			/**
			* @tab Header
			* @section header Navigation
			*/
			<!--#templateHeaderNav .headerNavigation td { border-right: 1px solid #6C78C2; border-left: 1px solid #000000; width: 91px; text-align: center; vertical-align:middle; padding-top: 10px; padding-bottom: 10px; padding-left: 10px; padding-right: 10px; font-size: 12px;}-->
			
			/**
			* @tab Header
			* @section header Navigation Links
			*/
			<!--#templateHeaderNav .headerNavigation td a:link, #templateHeaderNav .headerNavigation td a:visited, /* Yahoo! Mail Override */ #templateHeaderNav .headerNavigation td a .yshortcuts /* Yahoo! Mail Override */ { font-weight: bold; text-transform: lowercase; color: #FFFFFF; text-decoration: none; }-->
			
			<!--#templateHeaderNav .headerNavigation td u {display: none;}-->
			
			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: MAIN BODY /\/\/\/\/\/\/\/\/\/\ */
			
			/**
			* @tab Body
			* @section body Common Styles for Left and Right column.
			*/
			<!--#templateBody { font-family: arial; font-size: 12px; text-align: left;}-->
			
			<!--#templateBody ul{ margin-top: 5px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding: 0px; font-family: arial; font-size: 12px; text-align: left;}-->
			
			<!--#templateBody ul li{ list-style-type: circle; list-style-position: inside; line-height: 150%; padding: 0px;}-->
			
			<!--#templateBody .small{ font-size: 11px; color: #999999;}-->
			
			<!--#templateBody .textCenter, #templateFooter .textCenter{ text-align: center;}-->
			<!--#templateBody a:link, #templateBody a:visited, /* Yahoo! Mail Override */ #templateBody a .yshortcuts /* Yahoo! Mail Override */{ color: #273691; text-decoration: none;}-->
			
			<!--#templateBody a:hover, /* Yahoo! Mail Override */ #templateBody a:hover .yshortcuts /* Yahoo! Mail Override */{ color: #273691; text-decoration: underline;}-->
			
			/**
			* @tab Body
			* @section body style
			* @tip Set the background color for your email's body area.
			*/
			<!--#templateContainer, .bodyContent{ /*@editable*/ background-color:#FFFFFF;}-->
			
			<!--.bodyContent{ /*@editable*/ padding-top: 15px;}-->

			/**
			* @tab Body
			* @section body text
			* @tip Set the styling for your email's main content text. Choose a size and color that is easy to read.
			* @theme main
			*/
			
			<!--.bodyContent td { padding-top: 0px; padding-right: 10px; padding-bottom: 0px; padding-left: 10px;}-->
			
			<!--.bodyContent td table td { padding-top: 10px; padding-right: 10px; padding-bottom: 10px; padding-left: 10px;}-->
			
			<!--.bodyContent td table th { padding-top: 10px; padding-right: 10px; padding-bottom: 0px; padding-left: 10px; border-top: 5px solid #E8E8E8; text-align: left; font-family: arial; font-size: 12px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper td { padding-top: 0px; padding-right: 20px; padding-bottom: 3px; padding-left: 20px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { padding-top: 5px; padding-right: 10px; padding-bottom: 5px; padding-left: 0px; font-size: 12px; font-family: arial;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable th, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable th { padding-top: 10px; padding-right: 10px; padding-bottom: 0px; padding-left: 0px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable th { padding-top: 7px; padding-right: 10px; padding-bottom: 7px; padding-left: 0px; background-color: #F9F9F9; color: #222222; border-top: 1px solid #E8E8E8;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td { border-bottom: 1px solid #E8E8E8; padding-top: 10px; padding-bottom: 10px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td table td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { border-top: none;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { padding-top: 3px; padding-right: 10px; padding-bottom: 0px; padding-left: 0px;}-->
			
			<!--.bodyContent div{ /*@editable*/ color:#222222; /*@editable*/ font-family:Arial; /*@editable*/ font-size:12px; /*@editable*/ line-height:150%; /*@editable*/ text-align:justify;}-->
			
			<!--.bodyContent p{ margin-top: 10px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; font-family: arial; font-size: 12px; }-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p { margin-top: 2px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px;}-->
			
			<!--.bodyContent p.productTitle{ color: #273691; font-weight: bold;}-->
			
			<!--.bodyContent .smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.smallText { font-size: 11px;}-->
			
			<!--.bodyContent p.highlightRed{ color: #FF0000;}-->
			
			<!--.bodyContent p.highlightYellow{ color: #FFAB27;}-->
			
			<!--.bodyContent p.upperCase{ text-transform: uppercase;}-->
			
			<!--.bodyContent .marTop_10, .bodyContent p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.marTop_10 { margin-top: 10px;}-->
			
			<!--.bodyContent .marTop_20, .bodyContent p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.marTop_20 { margin-top: 20px;}-->
			
			/**
			* @tab Body
			* @section body link
			* @tip Set the styling for your email's main content links. Choose a color that helps them stand out from your text.
			*/
			<!--.bodyContent div a:link, .bodyContent div a:visited, /* Yahoo! Mail Override */ .bodyContent div a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->

			<!--.bodyContent img{ display:block; height:auto;}-->
			
			<!--.bodyContent .listTypeTable td{ padding-top: 5px; padding-right: 5px; padding-bottom: 1px; padding-left: 5px; font-family: arial; font-size: 12px;}-->
			
			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: SIDEBAR /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Sidebar
			* @section sidebar style
			* @tip Set the background color and border for your email's sidebar area.
			*/
			<!--#templateSidebar{ /*@editable*/ background-color:#FFFFFF;}-->

			/**
			* @tab Sidebar
			* @section sidebar text
			* @tip Set the styling for your email's sidebar text. Choose a size and color that is easy to read.
			*/
			<!--.sidebarContent div{ /*@editable*/ color:#222222; /*@editable*/ font-family:Arial; /*@editable*/ font-size:10px; /*@editable*/ line-height:150%; /*@editable*/ text-align:left;}-->

			/**
			* @tab Sidebar
			* @section sidebar link
			* @tip Set the styling for your email's sidebar links. Choose a color that helps them stand out from your text.
			*/
			<!--.sidebarContent div a:link, .sidebarContent div a:visited, /* Yahoo! Mail Override */ .sidebarContent div a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->

			<!--.sidebarContent img{ display:block; height:auto;}-->

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: FOOTER /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Footer
			* @section footer style
			* @tip Set the background color and top border for your email's footer area.
			* @theme footer
			*/
			<!--#templateFooter{ }-->
			
			<!--#templateFooter #templateFooterBody { /*@editable*/ background-color:#F2F2F2;}-->
			
			/**
			* @tab Footer
			* @section footer text
			* @tip Set the styling for your email's footer text. Choose a size and color that is easy to read.
			* @theme footer
			*/
			<!--#templateFooter .footerContentImageBorder { padding-top: 20px;}-->
			
			<!--.footerContent {padding-top: 20px;} -->
			
			<!--.footerContent div{ /*@editable*/ color:#707070; /*@editable*/ font-family:Arial; /*@editable*/ font-size:11px; /*@editable*/ line-height:125%; /*@editable*/ text-align:left;}-->

			/**
			* @tab Footer
			* @section footer link
			* @tip Set the styling for your email's footer links. Choose a color that helps them stand out from your text.
			*/
			<!--.footerContent  a, .footerContent  a:link, .footerContent a:visited, /* Yahoo! Mail Override */ .footerContent a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration: none; padding-left: 10px; padding-right: 10px;}-->
			
			<!--.footerContent  a:hover, /* Yahoo! Mail Override */ .footerContent a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration: underline;}-->
			
			<!--.footerContent  #footerLinks a:link, .footerContent #footerLinks a:visited, /* Yahoo! Mail Override */ .footerContent #footerLinks a .yshortcuts /* Yahoo! Mail Override */{ font-weight:bold; font-size: 13px;}-->
			
			<!--.footerContent #footerLinks  a:hover, /* Yahoo! Mail Override */ .footerContent #footerLinks a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ font-weight:bold;}-->

			<!--.footerContent img{ display:block;}-->

			/**
			* @tab Footer
			* @section utility bar style
			* @tip Set the background color and border for your email's footer utility bar.
			* @theme footer
			*/
			<!--#utility{ /*@editable*/ background-color:#273691; /*@editable*/ border-top:0;}-->

			/**
			* @tab Footer
			* @section utility bar style
			* @tip Set the background color and border for your email's footer utility bar.
			*/
			<!--#utility div{ /*@editable*/ text-align: center; color: #FFFFFF;}-->

			<!--#monkeyRewards img{max-width:170px !important;}-->
		</style>
    </c:when>
    <c:when test="${siteId eq 'BedBathCanada'}">
    	<style type="text/css">
			/* Client-specific Styles */
			<!--#outlook a{padding:0;}--> /* Force Outlook to provide a "view in browser" button. */
			<!--#outlook p.MsoNormal, #outlook .MsoNormal { line-height: 0px; font-family: arial;}-->
			<!--body{width:100% !important;}--> 
			<!--.ReadMsgBody{width:100%;}--> /* Force Hotmail to display emails at full width */
			<!--.ExternalClass{width:100%;}--> /* Force Hotmail to display emails at full width */
			<!--body{-webkit-text-size-adjust:none;}--> /* Prevent Webkit platforms from changing default text sizes. */

			/* Reset Styles */
			<!--body{margin:0; padding:0;}-->
			<!--img{border:0; height:auto; line-height:100%; outline:none; text-decoration:none; display: block;}-->
			<!--table td{border-collapse:collapse;}-->
			<!--#backgroundTable{height:100% !important; margin:0; padding:0; width:100% !important;}-->

			/* Template Styles */

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: COMMON PAGE ELEMENTS /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Page
			* @section background color
			* @tip Set the background color for your email. You may want to choose one that matches your company's branding.
			* @theme page
			*/
			<!--body, #backgroundTable{	/*@editable*/ background-color:#FFFFFF;}-->

			/**
			* @tab Page
			* @section email border
			* @tip Set the border for your email.
			*/
			<!--#templateContainer{	/*@editable*/ border:0;}-->

			/**
			* @tab Page
			* @section heading 1
			* @tip Set the styling for all first-level headings in your emails. These should be the largest of your headings.
			* @style heading 1
			*/
			<!--h1, .h1{ /*@editable*/ color:#666666; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:40px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%;	margin-left:0; /*@editable*/ text-align:left;}-->

			/**
			* @tab Page
			* @section heading 2
			* @tip Set the styling for all second-level headings in your emails.
			* @style heading 2
			*/
			<!--h2, .h2{ /*@editable*/ color:#666666; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:18px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%; margin-left:0; /*@editable*/ text-align:left;}-->

			/**
			* @tab Page
			* @section heading 3
			* @tip Set the styling for all third-level headings in your emails.
			* @style heading 3
			*/
			<!--h3, .h3{ /*@editable*/ color:#606060; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:16px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%; margin-left:0; /*@editable*/ text-align:left;}-->

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: HEADER /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Header
			* @section header style
			* @tip Set the background color and border for your email's header area.
			* @theme header
			*/
			<!--#templateHeader{ /*@editable*/ background-color:#FFFFFF; }-->
			
			<!--p.MsoNormal, .MsoNormal { line-height: 0px; font-family: arial;}-->
			
			/**
			* @tab Header
			* @section header text
			* @tip Set the styling for your email's header text. Choose a size and color that is easy to read.
			*/
			<!--.headerContent{ /*@editable*/ color:#000000; /*@editable*/ font-family:Arial; /*@editable*/ font-size:12px; /*@editable*/ font-weight:normal; /*@editable*/ line-height:100%; /*@editable*/ padding-top: 15px; padding-bottom: 15px; /*@editable*/ text-align:left; /*@editable*/ vertical-align:middle; border-bottom: 3px solid #283794;}-->

			/**
			* @tab Header
			* @section header link
			* @tip Set the styling for your email's header links. Choose a color that helps them stand out from your text.
			*/
			<!--.headerContent a:link, .headerContent a:visited, /* Yahoo! Mail Override */ .headerContent a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->
			
			<!--.headerContent a span {color: #273691;}-->
			
			<!--.headerContent a:hover, /* Yahoo! Mail Override */ .headerContent a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:none;}-->
			
			<!--#headerImage{ height:auto; max-width:173px !important;}-->
			
			/**
			* @tab Header
			* @section header Table
			* @theme header
			*/
			<!--#templateHeaderNav { /*@editable*/ background-color:#3245B9; /*@editable*/ border-bottom: 3px solid #00AEF0; font-family: arial,helvetica,sans-serif}-->
			
			/**
			* @tab Header
			* @section header Navigation
			*/
			<!--#templateHeaderNav .headerNavigation td { border-right: 1px solid #6C78C2; border-left: 1px solid #000000; width: 91px; text-align: center; vertical-align:middle; padding-top: 10px; padding-bottom: 10px; padding-left: 10px; padding-right: 10px; font-size: 12px;}-->
			
			/**
			* @tab Header
			* @section header Navigation Links
			*/
			<!--#templateHeaderNav .headerNavigation td a:link, #templateHeaderNav .headerNavigation td a:visited, /* Yahoo! Mail Override */ #templateHeaderNav .headerNavigation td a .yshortcuts /* Yahoo! Mail Override */ { font-weight: bold; text-transform: lowercase; color: #FFFFFF; text-decoration: none; }-->
			
			<!--#templateHeaderNav .headerNavigation td u {display: none;}-->
			
			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: MAIN BODY /\/\/\/\/\/\/\/\/\/\ */
			
			/**
			* @tab Body
			* @section body Common Styles for Left and Right column.
			*/
			<!--#templateBody { font-family: arial; font-size: 12px; text-align: left;}-->
			
			<!--#templateBody ul{ margin-top: 5px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding: 0px; font-family: arial; font-size: 12px; text-align: left;}-->
			
			<!--#templateBody ul li{ list-style-type: circle; list-style-position: inside; line-height: 150%; padding: 0px;}-->
			
			<!--#templateBody .small{ font-size: 11px; color: #999999;}-->
			
			<!--#templateBody .textCenter, #templateFooter .textCenter{ text-align: center;}-->
			<!--#templateBody a:link, #templateBody a:visited, /* Yahoo! Mail Override */ #templateBody a .yshortcuts /* Yahoo! Mail Override */{ color: #273691; text-decoration: none;}-->
			
			<!--#templateBody a:hover, /* Yahoo! Mail Override */ #templateBody a:hover .yshortcuts /* Yahoo! Mail Override */{ color: #273691; text-decoration: underline;}-->
			
			/**
			* @tab Body
			* @section body style
			* @tip Set the background color for your email's body area.
			*/
			<!--#templateContainer, .bodyContent{ /*@editable*/ background-color:#FFFFFF;}-->
			
			<!--.bodyContent{ /*@editable*/ padding-top: 15px;}-->

			/**
			* @tab Body
			* @section body text
			* @tip Set the styling for your email's main content text. Choose a size and color that is easy to read.
			* @theme main
			*/
			
			<!--.bodyContent td { padding-top: 0px; padding-right: 10px; padding-bottom: 0px; padding-left: 10px;}-->
			
			<!--.bodyContent td table td { padding-top: 10px; padding-right: 10px; padding-bottom: 10px; padding-left: 10px;}-->
			
			<!--.bodyContent td table th { padding-top: 10px; padding-right: 10px; padding-bottom: 0px; padding-left: 10px; border-top: 5px solid #E8E8E8; text-align: left; font-family: arial; font-size: 12px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper td { padding-top: 0px; padding-right: 20px; padding-bottom: 3px; padding-left: 20px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { padding-top: 5px; padding-right: 10px; padding-bottom: 5px; padding-left: 0px; font-size: 12px; font-family: arial;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable th, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable th { padding-top: 10px; padding-right: 10px; padding-bottom: 0px; padding-left: 0px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable th { padding-top: 7px; padding-right: 10px; padding-bottom: 7px; padding-left: 0px; background-color: #F9F9F9; color: #222222; border-top: 1px solid #E8E8E8;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td { border-bottom: 1px solid #E8E8E8; padding-top: 10px; padding-bottom: 10px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td table td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { border-top: none;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { padding-top: 3px; padding-right: 10px; padding-bottom: 0px; padding-left: 0px;}-->
			
			<!--.bodyContent div{ /*@editable*/ color:#222222; /*@editable*/ font-family:Arial; /*@editable*/ font-size:12px; /*@editable*/ line-height:150%; /*@editable*/ text-align:justify;}-->
			
			<!--.bodyContent p{ margin-top: 10px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; font-family: arial; font-size: 12px; }-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p { margin-top: 2px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px;}-->
			
			<!--.bodyContent p.productTitle{ color: #273691; font-weight: bold;}-->
			
			<!--.bodyContent .smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.smallText { font-size: 11px;}-->
			
			<!--.bodyContent p.highlightRed{ color: #FF0000;}-->
			
			<!--.bodyContent p.highlightYellow{ color: #FFAB27;}-->
			
			<!--.bodyContent p.upperCase{ text-transform: uppercase;}-->
			
			<!--.bodyContent .marTop_10, .bodyContent p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.marTop_10 { margin-top: 10px;}-->
			
			<!--.bodyContent .marTop_20, .bodyContent p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.marTop_20 { margin-top: 20px;}-->
			
			/**
			* @tab Body
			* @section body link
			* @tip Set the styling for your email's main content links. Choose a color that helps them stand out from your text.
			*/
			<!--.bodyContent div a:link, .bodyContent div a:visited, /* Yahoo! Mail Override */ .bodyContent div a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->

			<!--.bodyContent img{ display:block; height:auto;}-->
			
			<!--.bodyContent .listTypeTable td{ padding-top: 5px; padding-right: 5px; padding-bottom: 1px; padding-left: 5px; font-family: arial; font-size: 12px;}-->
			
			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: SIDEBAR /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Sidebar
			* @section sidebar style
			* @tip Set the background color and border for your email's sidebar area.
			*/
			<!--#templateSidebar{ /*@editable*/ background-color:#FFFFFF;}-->

			/**
			* @tab Sidebar
			* @section sidebar text
			* @tip Set the styling for your email's sidebar text. Choose a size and color that is easy to read.
			*/
			<!--.sidebarContent div{ /*@editable*/ color:#222222; /*@editable*/ font-family:Arial; /*@editable*/ font-size:10px; /*@editable*/ line-height:150%; /*@editable*/ text-align:left;}-->

			/**
			* @tab Sidebar
			* @section sidebar link
			* @tip Set the styling for your email's sidebar links. Choose a color that helps them stand out from your text.
			*/
			<!--.sidebarContent div a:link, .sidebarContent div a:visited, /* Yahoo! Mail Override */ .sidebarContent div a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->

			<!--.sidebarContent img{ display:block; height:auto;}-->

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: FOOTER /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Footer
			* @section footer style
			* @tip Set the background color and top border for your email's footer area.
			* @theme footer
			*/
			<!--#templateFooter{ }-->
			
			<!--#templateFooter #templateFooterBody { /*@editable*/ background-color:#F2F2F2;}-->
			
			/**
			* @tab Footer
			* @section footer text
			* @tip Set the styling for your email's footer text. Choose a size and color that is easy to read.
			* @theme footer
			*/
			<!--#templateFooter .footerContentImageBorder { padding-top: 20px;}-->
			
			<!--.footerContent {padding-top: 20px;} -->
			
			<!--.footerContent div{ /*@editable*/ color:#707070; /*@editable*/ font-family:Arial; /*@editable*/ font-size:11px; /*@editable*/ line-height:125%; /*@editable*/ text-align:left;}-->

			/**
			* @tab Footer
			* @section footer link
			* @tip Set the styling for your email's footer links. Choose a color that helps them stand out from your text.
			*/
			<!--.footerContent  a, .footerContent  a:link, .footerContent a:visited, /* Yahoo! Mail Override */ .footerContent a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration: none; padding-left: 10px; padding-right: 10px;}-->
			
			<!--.footerContent  a:hover, /* Yahoo! Mail Override */ .footerContent a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#273691; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration: underline;}-->
			
			<!--.footerContent  #footerLinks a:link, .footerContent #footerLinks a:visited, /* Yahoo! Mail Override */ .footerContent #footerLinks a .yshortcuts /* Yahoo! Mail Override */{ font-weight:bold; font-size: 13px;}-->
			
			<!--.footerContent #footerLinks  a:hover, /* Yahoo! Mail Override */ .footerContent #footerLinks a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ font-weight:bold;}-->

			<!--.footerContent img{ display:block;}-->

			/**
			* @tab Footer
			* @section utility bar style
			* @tip Set the background color and border for your email's footer utility bar.
			* @theme footer
			*/
			<!--#utility{ /*@editable*/ background-color:#273691; /*@editable*/ border-top:0;}-->

			/**
			* @tab Footer
			* @section utility bar style
			* @tip Set the background color and border for your email's footer utility bar.
			*/
			<!--#utility div{ /*@editable*/ text-align: center; color: #FFFFFF;}-->

			<!--#monkeyRewards img{max-width:170px !important;}-->
		</style>
    </c:when>
    <c:otherwise>
    	<style type="text/css">
			/* Client-specific Styles */
			<!--#outlook a{padding:0;}--> /* Force Outlook to provide a "view in browser" button. */
			<!--#outlook p.MsoNormal, #outlook .MsoNormal { line-height: 0px; font-family: arial;}-->
			<!--body{width:100% !important;}--> 
			<!--.ReadMsgBody{width:100%;}--> /* Force Hotmail to display emails at full width */
			<!--.ExternalClass{width:100%;}--> /* Force Hotmail to display emails at full width */
			<!--body{-webkit-text-size-adjust:none;}--> /* Prevent Webkit platforms from changing default text sizes. */

			/* Reset Styles */
			<!--body{margin:0; padding:0;}-->
			<!--img{border:0; height:auto; line-height:100%; outline:none; text-decoration:none; display: block;}-->
			<!--table td{border-collapse:collapse;}-->
			<!--#backgroundTable{height:100% !important; margin:0; padding:0; width:100% !important;}-->

			/* Template Styles */

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: COMMON PAGE ELEMENTS /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Page
			* @section background color
			* @tip Set the background color for your email. You may want to choose one that matches your company's branding.
			* @theme page
			*/
			<!--body, #backgroundTable{	/*@editable*/ background-color:#FFFFFF;}-->

			/**
			* @tab Page
			* @section email border
			* @tip Set the border for your email.
			*/
			<!--#templateContainer{	/*@editable*/ border:0;}-->

			/**
			* @tab Page
			* @section heading 1
			* @tip Set the styling for all first-level headings in your emails. These should be the largest of your headings.
			* @style heading 1
			*/
			<!--h1, .h1{ /*@editable*/ color:#666666; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:40px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%;	margin-left:0; /*@editable*/ text-align:left;}-->

			/**
			* @tab Page
			* @section heading 2
			* @tip Set the styling for all second-level headings in your emails.
			* @style heading 2
			*/
			<!--h2, .h2{ /*@editable*/ color:#666666; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:18px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%; margin-left:0; /*@editable*/ text-align:left;}-->

			/**
			* @tab Page
			* @section heading 3
			* @tip Set the styling for all third-level headings in your emails.
			* @style heading 3
			*/
			<!--h3, .h3{ /*@editable*/ color:#606060; display:block; /*@editable*/ font-family:Arial; /*@editable*/ font-size:16px; /*@editable*/ font-weight:bold; /*@editable*/ line-height:100%; margin-top:2%; margin-right:0; margin-bottom:1%; margin-left:0; /*@editable*/ text-align:left;}-->

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: HEADER /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Header
			* @section header style
			* @tip Set the background color and border for your email's header area.
			* @theme header
			*/
			<!--#templateHeader{ /*@editable*/ background-color:#DFF4FF; }-->
			
			<!--p.MsoNormal, .MsoNormal { line-height: 0px; font-family: arial;}-->
			
			/**
			* @tab Header
			* @section header text
			* @tip Set the styling for your email's header text. Choose a size and color that is easy to read.
			*/
			<!--.headerContent{ /*@editable*/ color:##26ACE4; /*@editable*/ font-family:Arial; /*@editable*/ font-size:12px; /*@editable*/ font-weight:normal; /*@editable*/ line-height:100%; /*@editable*/ padding:0px; /*@editable*/ text-align:left; /*@editable*/ vertical-align:middle;}-->

			/**
			* @tab Header
			* @section header link
			* @tip Set the styling for your email's header links. Choose a color that helps them stand out from your text.
			*/
			<!--.headerContent a:link, .headerContent a:visited, /* Yahoo! Mail Override */ .headerContent a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#336699; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->
			
			<!--.headerContent a span {color: #336699;}-->
			
			<!--.headerContent a:hover, /* Yahoo! Mail Override */ .headerContent a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#336699; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:none;}-->
			
			<!--#headerImage{ height:auto; max-width:127px !important;}-->
			
			/**
			* @tab Header
			* @section header Table
			* @theme header
			*/
			<!--#templateHeaderNav { /*@editable*/ background-color:#009FE1; /*@editable*/ border-top: 3px solid #4ABBEA; font-family: arial,helvetica,sans-serif}-->
			
			/**
			* @tab Header
			* @section header Navigation
			*/
			<!--#templateHeaderNav .headerNavigation td { border-right: 1px solid #0F86BC; border-left: 1px solid #0F86BC; width: 91px; text-align: center; vertical-align:middle; padding-top: 10px; padding-bottom: 10px; padding-left: 10px; padding-right: 10px; font-size: 12px;}-->
			
			/**
			* @tab Header
			* @section header Navigation Links
			*/
			<!--#templateHeaderNav .headerNavigation td a:link, #templateHeaderNav .headerNavigation td a:visited, /* Yahoo! Mail Override */ #templateHeaderNav .headerNavigation td a .yshortcuts /* Yahoo! Mail Override */ { font-weight: bold; text-transform: lowercase; color: #FFFFFF; text-decoration: none; }-->
			
			<!--#templateHeaderNav .headerNavigation td u {display: none;}-->
			
			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: MAIN BODY /\/\/\/\/\/\/\/\/\/\ */
			
			/**
			* @tab Body
			* @section body Common Styles for Left and Right column.
			*/
			<!--#templateBody { font-family: arial; font-size: 12px; text-align: left;}-->
			
			<!--#templateBody ul{ margin-top: 5px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding: 0px; font-family: arial; font-size: 12px; text-align: left;}-->
			
			<!--#templateBody ul li{ list-style-type: circle; list-style-position: inside; line-height: 150%; padding: 0px;}-->
			
			<!--#templateBody .small{ font-size: 11px; color: #999999;}-->
			
			<!--#templateBody .textCenter, #templateFooter .textCenter{ text-align: center;}-->
			<!--#templateBody a:link, #templateBody a:visited, /* Yahoo! Mail Override */ #templateBody a .yshortcuts /* Yahoo! Mail Override */{ color: #2791CD; text-decoration: none;}-->
			
			<!--#templateBody a:hover, /* Yahoo! Mail Override */ #templateBody a:hover .yshortcuts /* Yahoo! Mail Override */{ color: #2791CD; text-decoration: underline;}-->
			
			/**
			* @tab Body
			* @section body style
			* @tip Set the background color for your email's body area.
			*/
			<!--#templateContainer, .bodyContent{ /*@editable*/ background-color:#FFFFFF;}-->
			
			<!--.bodyContent{ /*@editable*/ padding-top: 15px;}-->

			/**
			* @tab Body
			* @section body text
			* @tip Set the styling for your email's main content text. Choose a size and color that is easy to read.
			* @theme main
			*/
			
			<!--.bodyContent td { padding-top: 0px; padding-right: 10px; padding-bottom: 0px; padding-left: 10px;}-->
			
			<!--.bodyContent td table td { padding-top: 10px; padding-right: 10px; padding-bottom: 10px; padding-left: 10px;}-->
			
			<!--.bodyContent td table th { padding-top: 10px; padding-right: 10px; padding-bottom: 0px; padding-left: 10px; border-top: 5px solid #E8E8E8; text-align: left; font-family: arial; font-size: 12px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper td { padding-top: 0px; padding-right: 20px; padding-bottom: 3px; padding-left: 20px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { padding-top: 5px; padding-right: 10px; padding-bottom: 5px; padding-left: 0px; font-size: 12px; font-family: arial;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable th, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable th { padding-top: 10px; padding-right: 10px; padding-bottom: 0px; padding-left: 0px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable th { padding-top: 7px; padding-right: 10px; padding-bottom: 7px; padding-left: 0px; background-color: #FEFAFC; color: #F12D81; border-top: 1px solid #B2DBF3;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td { border-bottom: 1px solid #B2DBF3; padding-top: 10px; padding-bottom: 10px;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td table td, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { border-top: none;}-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td { padding-top: 3px; padding-right: 10px; padding-bottom: 0px; padding-left: 0px;}-->
			
			<!--.bodyContent div{ /*@editable*/ color:#222222; /*@editable*/ font-family:Arial; /*@editable*/ font-size:12px; /*@editable*/ line-height:150%; /*@editable*/ text-align:justify;}-->
			
			<!--.bodyContent p{ margin-top: 10px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; font-family: arial; font-size: 12px; }-->
			
			<!--.bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p { margin-top: 2px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px;}-->
			
			<!--.bodyContent p.productTitle{ color: #334F92; font-weight: bold;}-->
			
			<!--.bodyContent .smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.smallText, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.smallText { font-size: 11px;}-->
			
			<!--.bodyContent p.highlightRed{ color: #FF0000;}-->
			
			<!--.bodyContent p.highlightYellow{ color: #FFAB27;}-->
			
			<!--.bodyContent p.upperCase{ text-transform: uppercase;}-->
			
			<!--.bodyContent .marTop_10, .bodyContent p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.marTop_10, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.marTop_10 { margin-top: 10px;}-->
			
			<!--.bodyContent .marTop_20, .bodyContent p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmTable td p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTable td p.marTop_20, .bodyContent table.orderConfirmationWrapper table.orderConfirmationDataTotalTable td p.marTop_20 { margin-top: 20px;}-->
			
			/**
			* @tab Body
			* @section body link
			* @tip Set the styling for your email's main content links. Choose a color that helps them stand out from your text.
			*/
			<!--.bodyContent div a:link, .bodyContent div a:visited, /* Yahoo! Mail Override */ .bodyContent div a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#336699; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->

			<!--.bodyContent img{ display:block; height:auto;}-->
			
			<!--.bodyContent .listTypeTable td{ padding-top: 5px; padding-right: 5px; padding-bottom: 1px; padding-left: 5px; font-family: arial; font-size: 12px;}-->
			
			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: SIDEBAR /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Sidebar
			* @section sidebar style
			* @tip Set the background color and border for your email's sidebar area.
			*/
			<!--#templateSidebar{ /*@editable*/ background-color:#FFFFFF;}-->

			/**
			* @tab Sidebar
			* @section sidebar text
			* @tip Set the styling for your email's sidebar text. Choose a size and color that is easy to read.
			*/
			<!--.sidebarContent div{ /*@editable*/ color:#222222; /*@editable*/ font-family:Arial; /*@editable*/ font-size:10px; /*@editable*/ line-height:150%; /*@editable*/ text-align:left;}-->

			/**
			* @tab Sidebar
			* @section sidebar link
			* @tip Set the styling for your email's sidebar links. Choose a color that helps them stand out from your text.
			*/
			<!--.sidebarContent div a:link, .sidebarContent div a:visited, /* Yahoo! Mail Override */ .sidebarContent div a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#336699; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration:underline;}-->

			<!--.sidebarContent img{ display:block; height:auto;}-->

			/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: FOOTER /\/\/\/\/\/\/\/\/\/\ */

			/**
			* @tab Footer
			* @section footer style
			* @tip Set the background color and top border for your email's footer area.
			* @theme footer
			*/
			<!--#templateFooter{ }-->
			
			<!--#templateFooter #templateFooterBody { /*@editable*/ background-color:#FEFAFC;}-->
			
			/**
			* @tab Footer
			* @section footer text
			* @tip Set the styling for your email's footer text. Choose a size and color that is easy to read.
			* @theme footer
			*/
			<!--#templateFooter .footerContentImageBorder { padding-top: 20px;}-->
			
			<!--.footerContent div{ /*@editable*/ color:#707070; /*@editable*/ font-family:Arial; /*@editable*/ font-size:11px; /*@editable*/ line-height:125%; /*@editable*/ text-align:left;}-->

			/**
			* @tab Footer
			* @section footer link
			* @tip Set the styling for your email's footer links. Choose a color that helps them stand out from your text.
			*/
			<!--.footerContent  a, .footerContent  a:link, .footerContent a:visited, /* Yahoo! Mail Override */ .footerContent a .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#F12D81; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration: none; padding-left: 10px; padding-right: 10px;}-->
			
			<!--.footerContent  a:hover, /* Yahoo! Mail Override */ .footerContent a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ color:#F12D81; /*@editable*/ font-weight:normal; /*@editable*/ text-decoration: underline;}-->
			
			<!--.footerContent  #footerLinks a:link, .footerContent #footerLinks a:visited, /* Yahoo! Mail Override */ .footerContent #footerLinks a .yshortcuts /* Yahoo! Mail Override */{ font-weight:bold; font-size: 13px;}-->
			
			<!--.footerContent #footerLinks  a:hover, /* Yahoo! Mail Override */ .footerContent #footerLinks a:hover .yshortcuts /* Yahoo! Mail Override */{ /*@editable*/ font-weight:bold;}-->

			<!--.footerContent img{ display:block;}-->

			/**
			* @tab Footer
			* @section utility bar style
			* @tip Set the background color and border for your email's footer utility bar.
			* @theme footer
			*/
			<!--#utility{ /*@editable*/ background-color:#F16C7D; /*@editable*/ border-top:0;}-->

			/**
			* @tab Footer
			* @section utility bar style
			* @tip Set the background color and border for your email's footer utility bar.
			*/
			<!--#utility div{ /*@editable*/ text-align: center; color: #FFFFFF;}-->

			<!--#monkeyRewards img{max-width:170px !important;}-->
		</style>
    </c:otherwise>
</c:choose>
</dsp:page>