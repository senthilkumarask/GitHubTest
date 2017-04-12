<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
<dsp:page>
<html>
<head><title>Categories</title>
<!--  Need to update the field and button IDs in qas.js -->
<script src="/qas/jquery/js/jquery-1.4.2.min.js"></script>
<script src="/qas/jquery/js/jquery-ui-1.8.6.custom.min.js"></script>
<link rel="stylesheet" href="/qas/jquery/css/qas/jquery-ui-1.8.6.custom.css"/>
</head>
<body>
<dsp:getvalueof id="currentSiteId" bean="CSRFormHandler.currentSite" />

<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
<dsp:param name="value" param="path"/>
<dsp:oparam name="false">
	<dsp:getvalueof var="pathLink" value="${param.path},${param.categoryId}" />
</dsp:oparam>
<dsp:oparam name="true">
	<dsp:getvalueof var="pathLink" value="${param.categoryId}" />
</dsp:oparam>
</dsp:droplet>

<dsp:param name="pathArray" value="${fn:split(param.path, ',')}" />

<!--SiteId:${currentSiteId}: PathLink ${pathLink}-->
		<%-- featured Categories Display --%>
		<div class="row">
			<div class="small-12 columns no-padding">
				<h2 class="divider">Categories</h2>
			</div>
		</div>
		<dsp:form id="catInfo" iclass="clearfix"
			action="displayChildCategories.jsp?catalogId=${param.catalogId}&categoryId=${param.categoryId}&path=${pathLink}&site=${param.site}" method="post">
			<dsp:input type="hidden" bean="CSRFormHandler.rank" maxlength="40" name="rank" id="rank" />
			<dsp:input type="hidden" bean="CSRFormHandler.category" maxlength="40" name="category" id="category" />
			<dsp:input type="hidden" bean="CSRFormHandler.currentSite" value="${currentSiteId}" maxlength="40" name="currentSite" id="currentSite" />
			<dsp:input type="hidden" bean="CSRFormHandler.changeRankSuccessURL" maxlength="40" value="displayChildCategories.jsp?catalogId=${param.catalogId}&categoryId=${param.categoryId}&path=${pathLink}" />
			<dsp:input bean="CSRFormHandler.changeRank" id="cacheInfoBtn" style="display:none" type="Submit" value="Submit" />
		</dsp:form>
		<div class="row">
			<div class="small-12 columns no-padding">
				<h3 class="divider">
					<dsp:a href="/qas/displayCatDimension.jsp">
						Home
					</dsp:a>
					<dsp:a href="displayChildCategories.jsp?catalogId=${param.catalogId}&site=${param.site}">
						> ${param.site}
					</dsp:a>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="pathArray"/>
						<dsp:oparam name="output">
							
							<dsp:droplet name="/com/bbb/dimension/droplet/CategoryListDroplet">
							<dsp:param name="categoryId" param="element" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="repId" param="element.repositoryId"/>
								 > 
								 <dsp:a href='displayChildCategories.jsp?catalogId=${param.catalogId}&categoryId=${repId}&site=${param.site}'>
								 <dsp:valueof param="element.displayName"/>
								 </dsp:a>
							</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							<dsp:droplet name="/com/bbb/dimension/droplet/CategoryListDroplet">
							<dsp:param name="categoryId" value="${param.categoryId}" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="repId" param="element.repositoryId"/>
								 > 
								 <dsp:valueof param="element.displayName"/>
							</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
						</dsp:droplet>
				</h2>
			</div>
		</div>
		<div style="margin-bottom:10px;">Drag Item to Position</div>
		<table id="caltbl">
		<tbody>
		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" param="categoryId"/>
				<dsp:oparam name="true">
					<!--Display L1 Categories -->
						<dsp:droplet name="/atg/targeting/RepositoryLookup">
						<dsp:param bean="/atg/commerce/catalog/ProductCatalog" name="repository"/>
						<dsp:param name="itemDescriptor" param="catalog"/>
						<dsp:param name="id" param="catalogId"/>
						<dsp:oparam name="output">
							<dsp:param name="catalogRepItem" param="element" />
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="catalogRepItem.allRootCategories" name="array"/>
							<dsp:oparam name="output">
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
								<dsp:param name="value" param="element.childCategories"/>
								<dsp:oparam name="false">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									 <dsp:param param="element.childCategories" name="array"/>
									 <dsp:oparam name="output">
									 <dsp:getvalueof var="catName" param="element.displayName"/>
									 <dsp:getvalueof var="catId" param="element.repositoryId"/>
									 <dsp:getvalueof var="catalogId" param="catalogId"/>
									 <dsp:getvalueof var="index" param="index"/>
									 <dsp:getvalueof var="count" param="count"/>
									 <dsp:getvalueof var="catName" param="element.displayName"/>
									 <dsp:getvalueof var="rank" value=""/>
										<tr>
											<td>
												<dsp:droplet name="/atg/targeting/RepositoryLookup">
												<dsp:param bean="/com/bbb/commerce/catalog/repository/SkuFeatureFacetRepository" name="repository"/>
												<dsp:param name="itemDescriptor" value="dimOrder"/>
												<dsp:param name="id" value="${catId}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="rank" param="element.rank"/>
												</dsp:oparam>
												</dsp:droplet>
												<input type="hidden" maxlength="5" name="rank" id="rank_${index}" value="${rank}"/>
												<input type="hidden" id="category_${index}" value="${catId}"/>
												
											</td>
											<td>
											<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
												<dsp:param name="value" param="element.childCategories"/>
													<dsp:oparam name="false">
														<dsp:a href="/qas/displayChildCategories.jsp?categoryId=${catId}&catalogId=${param.catalogId}&path=${pathLink}&site=${param.site}">
															${catName}
														</dsp:a>
													</dsp:oparam>
													 <dsp:oparam name="true">
															${catName}
													  </dsp:oparam>
												</dsp:droplet>
											</td>
										</tr>
											  </dsp:oparam>
										   </dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
						</dsp:droplet>
				</dsp:oparam>
				 <dsp:oparam name="false">
				 <!--Category Id <dsp:valueof param="categoryId" />-->
				 <!--catalog Id <dsp:valueof param="catalogId" />-->
								 <!--Display L2 or L3 Categories -->
										<dsp:droplet name="/com/bbb/dimension/droplet/CategoryListDroplet">
										<dsp:param name="categoryId" param="categoryId" />
									<dsp:oparam name="output">
						<!--ChildCats:<dsp:valueof param="element.fixedChildCategories" />-->
									 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
										 <dsp:param param="element.fixedChildCategories" name="array"/>
										 <dsp:oparam name="output">
										 <dsp:getvalueof var="catName" param="element.displayName"/>
										 <dsp:getvalueof var="catId" param="element.repositoryId"/>
										<dsp:getvalueof var="index" param="index"/>
									    <dsp:getvalueof var="count" param="count"/>
										<dsp:getvalueof var="catName" param="element.displayName"/>
										<dsp:getvalueof var="rank" value=""/>
										<tr>
											<td>
												<dsp:droplet name="/atg/targeting/RepositoryLookup">
												<dsp:param bean="/com/bbb/commerce/catalog/repository/SkuFeatureFacetRepository" name="repository"/>
												<dsp:param name="itemDescriptor" value="dimOrder"/>
												<dsp:param name="id" value="${catId}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="rank" param="element.rank"/>
												</dsp:oparam>
												</dsp:droplet>
												<input type="hidden" maxlength="5" name="rank" id="rank_${index}" value="${rank}"/>
												<input type="hidden" id="category_${index}" value="${catId}"/>
											</td>
											<td>
											<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
												<dsp:param name="value" param="element.childCategories"/>
													<dsp:oparam name="false">
														<dsp:a href="/qas/displayChildCategories.jsp?categoryId=${catId}&catalogId=${param.catalogId}&path=${pathLink}&site=${param.site}">
															${catName}
														</dsp:a>
													</dsp:oparam>
													 <dsp:oparam name="true">
														<dsp:droplet name="/com/bbb/dimension/droplet/CategoryListDroplet">
														<dsp:param name="categoryId" param="element.id" />
										 				<dsp:oparam name="output">
										 				<dsp:valueof param="element.displayName"/>
														</dsp:oparam>
														</dsp:droplet>
													  </dsp:oparam>
												</dsp:droplet>
											</td>
										</tr>
										</dsp:oparam>
								   </dsp:droplet>
								  </dsp:oparam>
							</dsp:droplet>
				  </dsp:oparam>
			</dsp:droplet>

<tbody>
</table>
<table>
<tr>
<td>
	<button onclick="javascript:loadForm();">Submit</button>
</td>
<td>
	<button onclick="javascript:reLoadPage();">Reset</button>
</td>
</tr>
</table>

		<script  type="text/javascript">
		//catCount = ${count};
		var tbl = document.getElementById("caltbl").tBodies[0];
		function sortTable() {
			var store = [];
			for(var i=0, len=tbl.rows.length; i<len; i++){
				var row = tbl.rows[i];
				var sortnr =  parseFloat(document.getElementById('rank_'+i).value);
				if(!isNaN(sortnr)) store.push([sortnr, row]);
			}
			store.sort(function(x,y){
				return x[0] - y[0];
			});
			for(var i=store.length-1; i>=0; i--){
				tbl.insertBefore(store[i][1], tbl.rows[0]);
			}
			store = null;

			$("tbody").sortable({
    			items: "> tr",
    			appendTo: "parent",
    			start: function(event, ui){
    				ui.item.startPos = ui.item.index();
    			},
    			stop: function(event, ui){
    				var column_rank;

    				for(var i=0, len=tbl.rows.length; i<len; i++){
							column_rank = tbl.rows[i].childNodes[1].childNodes[1];
							column_rank.setAttribute('value', i+1);
					}
					
    			  }
			}).disableSelection();
		}

		function loadForm() {
			//alert(catCount);
			rank = "";
			categories = "";
			for (var i=0, len=tbl.rows.length; i<len; i++)
			{
				rank += tbl.rows[i].childNodes[1].childNodes[1].value+",";
				categories += tbl.rows[i].childNodes[1].childNodes[3].value+",";
			}
	
			document.getElementById('rank').value = rank.substring(0,rank.length-1);
			document.getElementById('category').value = categories.substring(0,categories.length-1);
			//alert(document.getElementById('rank').value);
			//alert(document.getElementById('category').value);
			document.getElementById('cacheInfoBtn').click();
		}

		function reLoadPage() {
			location.reload(true); 
		}

		$(document).ready(function () {
			sortTable();
		});
		</script>
</body>
</html>
</dsp:page>
