<dsp:page>
 <%--This JSP would load in cases of Static Checklist. Story : BBBI-1071

Input Params:

1.Registry Type

--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof bean="SessionBean.activateGuideInRegistryRibbon" var="activateGuideInRegistryRibbon" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/StaticCheckListDroplet" />
<dsp:getvalueof var="checklistType" param="checklistType" />
<dsp:getvalueof bean="SessionBean" var="bean"/> 

<c:set var="interactiveChecklist"><bbbl:label key="lbl_interactive_checklist" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="fullView"><bbbl:label key="lbl_interactive_full_view" language="${pageContext.request.locale.language}"/></c:set>
 <dsp:droplet name="StaticCheckListDroplet">
	<dsp:param name="checklistType" value="${checklistType}" />
	<dsp:oparam name="output">
	<dsp:getvalueof var="staticChecklistVO" param="staticChecklistVO" />
	<dsp:getvalueof var="registryType" param="registryType" />
	<dsp:getvalueof var="guideType" param="guideType" />
	</dsp:oparam>
 </dsp:droplet>

 <c:set var="registryStaticCheckList" value="${registryType}CheckListProgress"/>

<c:set var="progressBarAvailable"><bbbc:config key="${registryStaticCheckList}" configName="ContentCatalogKeys" /></c:set>

<c:if test="${fn:containsIgnoreCase(registryType, 'Commitment')}">
<c:set var="progressBarAvailable"><bbbc:config key="CommitmentCheckListProgress" configName="ContentCatalogKeys" /></c:set>
</c:if>

<c:if test="${fn:containsIgnoreCase(registryType, 'College') || fn:containsIgnoreCase(registryType, 'University')}">
<c:set var="progressBarAvailable"><bbbc:config key="CollegeCheckListProgress" configName="ContentCatalogKeys" /></c:set>
</c:if>
  <c:choose>
	<c:when test="${fn:containsIgnoreCase(progressBarAvailable,'off')}">
	<c:set var="itemCount"><bbbl:label key="lbl_checklist_full_view_recommended" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="loadingProgress"><bbbl:label key="lbl_interactive_progress_unavailable" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="showItemsRecommended">true</c:set>
	<c:set var="progressLoader">progressUnavailable</c:set>
	</c:when>
	<c:when test="${fn:containsIgnoreCase(progressBarAvailable,'notApplicable')}">
	<c:set var="itemCount"><bbbl:label key="lbl_checklist_full_view_recommended" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="showItemsRecommended">true</c:set>
	</c:when>
	<c:when test="${fn:containsIgnoreCase(progressBarAvailable,'on')}">
	<c:set var="loadingProgress"><bbbl:label key="lbl_interactive_progress_loading" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="progressLoader">progressLoader</c:set>
	<c:set var="showItemsRecommended">false</c:set>
	<c:set var="itemCount"><bbbl:label key="lbl_interactive_your_items" language="${pageContext.request.locale.language}"/></c:set>
	</c:when>
	</c:choose>
	<c:if test="${activateGuideInRegistryRibbon}">
	<c:set var="progressLoader">progressUnavailable</c:set>
	<c:set var="itemCount"><bbbl:label key="lbl_checklist_full_view_recommended" language="${pageContext.request.locale.language}"/></c:set>
	</c:if>
<c:set var="clickToView" scope="request"><bbbl:label key='lbl_click_tap_open' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="subCategories" scope="request"><bbbl:label key='lbl_subcategories' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="categoryTxt" scope="request"><bbbl:label key='lbl_category' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="navigateToPLP" scope="request"><bbbl:label key='lbl_navigate_to_plp' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="progressStatus" scope="request"><bbbl:label key='lbl_progress_status' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="notAvailable" scope="request"><bbbl:label key='lbl_not_Available' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="notStarted" scope="request"><bbbl:label key='lbl_not_started' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="itemsRecommended" scope="request"><bbbl:label key='lbl_items_Recommended_for' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="go_back"><bbbl:label key='lbl_go_back_to' language="${pageContext.request.locale.language}"/></c:set>
<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues" />
<dsp:getvalueof value="${sessionMapValues.guideVoList}" var="guideVOList" />
<c:if test="${not empty guideVOList and activateGuideInRegistryRibbon}">
              	<dsp:getvalueof var="selectedGuideVO" value="${guideVOList[0]}" />
              </c:if>
<section id="C1-Slider" class="grid_12 clearfix interactive-flyout ${progressLoader} <c:if test="${empty staticChecklistVO}">hidden</c:if>">
<div class="overall-progress-bar ib fl">
<div class="left-container">
 <c:if test="${not empty staticChecklistVO && not empty staticChecklistVO.categoryListVO}">
    <a id="openChecklistFullView" href="javascript:void(0);" aria-label="${clickToView} the ${fullView} of <c:choose><c:when test='${!activateGuideInRegistryRibbon}'>${registryType} ${interactiveChecklist}</c:when><c:otherwise>${selectedGuideVO.guideDisplayName}</c:otherwise></c:choose>">${fullView} </a>
    <div  class="registry-checklist-name">
    	<c:choose>
    		<c:when test="${not empty guideType and not empty staticChecklistVO and activateGuideInRegistryRibbon}">
    			${staticChecklistVO.displayName}
    		</c:when>
    		<c:otherwise>
    			${registryType} 
    		</c:otherwise>
    	</c:choose>
    	
    	<bbbl:label key="lbl_interactive_checklist" language="${pageContext.request.locale.language}"/>
    </div>
     </c:if>
     <c:if test="${not empty loadingProgress and not activateGuideInRegistryRibbon}">
    <div class="complete-status"><span></span><span class="<c:if test="${fn:containsIgnoreCase(progressBarAvailable,'off') || fn:containsIgnoreCase(progressBarAvailable,'notApplicable')}">noProgress</c:if>">&nbsp;${loadingProgress}</span>
    
    <c:if test="${fn:containsIgnoreCase(progressBarAvailable,'off') and not activateGuideInRegistryRibbon}">
      <a href="javascript:void(0);" id="progressUnvTip" aria-hidden="false" aria-label='<bbbl:label key="lbl_progress_not_available" language="${pageContext.request.locale.language}"/>'> <i aria-hidden='true' class="info icon icon-question-circle"></i></a>
    </c:if>
    </div>
    <div class="progress-bar" aria-hidden="true">
        <div class="progressWrapper span12">
            <div class="inner-progress-bar" data-progress-complete="0" style="width: 0%;">
            </div>
        </div>
    </div>
      </c:if>
</div>
</div>
<div class="separator-bar fl ib"></div>

 <c:set var="emptyC3s" value="${true}"/>
 <c:if test="${not empty staticChecklistVO && not empty staticChecklistVO.categoryListVO}">
 
  <c:forEach items="${staticChecklistVO.categoryListVO}" var="c1Category">
     <c:forEach items="${c1Category.childCategoryVO}" var="c2Category">
          <c:forEach items="${c2Category.childCategoryVO}" var="c3Category">
             <c:set var="emptyC3s" value="${false}"/>
         </c:forEach>
       </c:forEach>
  </c:forEach>
</c:if>
 <input type="hidden" id="isEmptyC3s" value="${emptyC3s}"/>
 
        <c:choose>
        <c:when test="${!emptyC3s}">
        <div class="category-one-list ib">
    <div class="back-icon ib" aria-hidden="true"></div>
    <div class="C1-Scrollable-list ib">
        <ul class="C1-list-items">
         <c:forEach items="${staticChecklistVO.categoryListVO}" var="categoryListVO" varStatus="countC1">
          <li id="${categoryListVO.categoryId}_c1">
            <c:set var="cat1Url">javascript:void(0);</c:set>
            <!-- <c:if test="${not empty categoryListVO.categoryURL}">
               <c:set var="cat1Url">${contextPath}${categoryListVO.categoryURL}</c:set>
            </c:if> -->
              <a href="${cat1Url}" data-target="#${categoryListVO.categoryId}" aria-hidden="false" aria-labelledby='c1Name_${countC1.count}  <c:if test='${progressLoader eq "progressUnavailable"}'> progressStatusMsg_${countC1.count}  </c:if>viewSubCategories_${countC1.count}' data-count='${countC1.count}' data-target="#${categoryListVO.categoryId}" >
              <div class="category-img" aria-hidden='true'><img class="c1Img" src="${categoryListVO.imageURL}&wid=38"/></div>
              <div class="C1-name cb"  aria-hidden='true'>${categoryListVO.displayName}</div>
                <c:if test="${not empty loadingProgress}">
              <div class="progress-bar" aria-hidden="true">
                    <div class="progressWrapper span12">
                        <div class="inner-progress-bar" data-progress-complete="0" style="width:0%;">
                        </div>
                    </div>
              </div>
              </c:if>
            </a>
            <span style='display:none' id="c1Name_${countC1.count}">${categoryListVO.displayName}. </span>
            <span style='display:none' id="viewSubCategories_${countC1.count}">${clickToView} ${subCategories} ${categoryListVO.displayName} ${categoryTxt}</span>

            <c:if test='${progressLoader eq "progressUnavailable"}'>
                 <span style='display:none' id="progressStatusMsg_${countC1.count}">${progressStatus} ${notAvailable}</span>
            </c:if>
          </li>
		</c:forEach>
        </ul>
    </div>
    <div class="forward-icon ib" aria-hidden="true"></div>
</div>
        <div class="c2-category-list clearfix hidden">
        <div class="active-category-arrow"></div>
        <a href="#" class="prev-c2-category" aria-hidden="true">
            <span class="icon-leftarrow"></span>
        </a>
          <div class="c2-category-wrapper">
        <c:forEach items="${staticChecklistVO.categoryListVO}" var="c1Category">
        <ul id="${c1Category.categoryId}">
         <li class="noHeight"><a class="goToC1 visuallyhidden" href="javascript:void(0);" data-ref-id="${c1Category.categoryId}_c1" aria-label="${go_back} ${c1Category.displayName}">close</a></li>
         <c:forEach items="${c1Category.childCategoryVO}" var="c2Category">
            <li class="c2-category">
            <c:set var="cat2Url">javascript:void(0);</c:set>
       
                <a href="${cat2Url}" aria-label="${c2Category.displayName}. ${clickToView} ${subCategories} ${c2Category.displayName} ${categoryTxt}"<c:if test="${not empty c2Category.childCategoryVO}">class="c2-category-link"</c:if> data-menu-content="#${c2Category.categoryId}">${c2Category.displayName}</a>
              
                <div id="${c2Category.categoryId}" class="hidden">
                    <section class="c3-categories-container">
                        <div class="viewport clearfix registry-checklist-container">
                           <table class="overview registry-checklist-items ${progressLoader}">
                                <thead>
                                    <tr class="checklist-header">
                                       <th class="checklist-status"></th>
                                       <th class="checklist-name"><bbbl:label key="lbl_interactive_browse" language="${pageContext.request.locale.language}"/></th>
                                       <th class="checklist-quantity">${itemCount}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${c2Category.childCategoryVO}" var="c3Category">
                                       <c:set var="cat3Url">javascript:void(0);</c:set>
                                        <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c3Url" value="${c3Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC3" value="${c3Category.usOverriddenURL}"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c3Url" value="${c3Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC3" value="${c3Category.babyOverriddenURL}"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c3Url" value="${c3Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC3" value="${c3Category.caOverriddenURL}"/>
											</c:otherwise>
										</c:choose>
                                        <c:choose>
                                            <c:when test="${not empty c3Url}">
                                            <c:set var="cat3Url"><c:if test="${!isOverriddenURLC3}">${contextPath}</c:if>${c3Url}</c:set>
                                           
                                                <c:set var="cat3Element"><a data-c1name="${c1Category.displayName}" data-c2name="${c2Category.displayName}" data-c3name="${c3Category.displayName}" href="${cat3Url}" aria-label="${c3Category.displayName}. ${navigateToPLP}">${c3Category.displayName}</a></c:set>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="cat3Element"><span class="textOnly">${c3Category.displayName}</span></c:set>
                                            </c:otherwise>
                                        </c:choose>
                                    <tr class="checklist-items">
                                       <td class="checklist-status">
                                       </td>
                                       <td class="checklist-name">${cat3Element}</td>
                                       <c:if test="${showItemsRecommended || activateGuideInRegistryRibbon}">
                                        <td class="checklist-quantity text-right">
                                            <span class="textOnly" aria-hidden="true">${c3Category.suggestedQuantity}</span>
                                            <span class="visuallyhidden" aria-hidden="false">${c3Category.suggestedQuantity} ${itemsRecommended} ${c3Category.displayName}</span>
                                        </td>
                                       </c:if>
                                    </tr>
                                    </c:forEach>
                                     <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c2Category.usOverriddenURL}"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c2Category.babyOverriddenURL}"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" value="${c2Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c2Category.caOverriddenURL}"/>
											</c:otherwise>
										</c:choose>
                                     <tr>
<%--                                      <c:if test="${not activateGuideInRegistryRibbon}"> --%>
    							<td colspan="3"><a  data-c1name="${c1Category.displayName}" data-c2name="${c2Category.displayName}" href="<c:if test="${!isOverriddenURLC2}">${contextPath}</c:if>${c2Url}" role="button" class="button-Med btnSecondary checklistShowAll" >
                          <span> SHOP ALL ${c2Category.displayName} </span></a></td>
<%--                           </c:if> --%>
  								</tr> 
                                </tbody>
                             </table>
                         </div>
                    </section>
                </div>

            </li>
            </c:forEach>
            <li class="noHeight"><a class="goToC1 visuallyhidden" href="javascript:void(0);" data-ref-id="${c1Category.categoryId}_c1" aria-label="${go_back} ${c1Category.displayName}">close</a></li>
        </ul>
    </c:forEach>
    </div>
      <a href="#" class="next-c2-category" aria-hidden="true">
        <span class="icon-rightarrow"></span>
    </a>
    </div>
        </c:when>
        <c:otherwise>
         <div class="category-one-list ib">
    <div class="back-icon ib" aria-hidden="true"></div>
    <div class="C1-Scrollable-list ib">
        <ul class="C1-list-items">
         <c:forEach items="${staticChecklistVO.categoryListVO}" var="categoryListVO" varStatus="countC1">
          <li id="${categoryListVO.categoryId}_c1">
            <c:set var="cat1Url">javascript:void(0);</c:set>
           <!--  <c:if test="${not empty categoryListVO.categoryURL}">
               <c:set var="cat1Url">${contextPath}${categoryListVO.categoryURL}</c:set>
            </c:if> -->

              <a href="${cat1Url}" data-target="#${categoryListVO.categoryId}" data-menu-content="#${categoryListVO.categoryId}" aria-hidden="false" aria-labelledby='c1Name_${countC1.count} viewSubCategories_${countC1.count}' data-count='${countC1.count}' data-target="#${categoryListVO.categoryId}">
              <div class="category-img" aria-hidden='true'><img class="c1Img" src="${categoryListVO.imageURL}&wid=38"/></div>
              <div class="C1-name cb"  aria-hidden="true">${categoryListVO.displayName}</div>
                <c:if test="${not empty loadingProgress}">
              <div class="progress-bar" aria-hidden="true">
                    <div class="progressWrapper span12">
                        <div class="inner-progress-bar" data-progress-complete="0" style="width:0%;">
                        </div>
                    </div>
              </div>
              </c:if>
            </a>
            <c:choose>
                <c:when test="${empty progressLoader}">
                    <c:set var="cat1labelmsg">${cat1labelmsg}</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="cat1labelmsg">${notAvailable}</c:set>
                </c:otherwise>
            </c:choose>
            <span style='display:none' id="c1Name_${countC1.count}">${categoryListVO.displayName}. </span>
            <span style='display:none' id="viewSubCategories_${countC1.count}">${clickToView} ${subCategories} ${categoryListVO.displayName} ${categoryTxt}</span>
          </li>
		</c:forEach>
        </ul>
    </div>
    <div class="forward-icon ib" aria-hidden="true"></div>
</div>
        <c:set var="noOfC1" value="${fn:length(staticChecklistVO.categoryListVO)}"/>
        <c:forEach items="${staticChecklistVO.categoryListVO}" var="c1Category">
                <div id="${c1Category.categoryId}" class=" hidden hiddenInteractive">
                    <section class="c3-categories-container">
                        <div class="viewport clearfix registry-checklist-container">
                           <table class="overview registry-checklist-items ${progressLoader}">
                                <thead>
                                    <tr class="checklist-header">
                                       <th class="checklist-status"></th>
                                       <th class="checklist-name"><bbbl:label key="lbl_interactive_browse" language="${pageContext.request.locale.language}"/></th>
                                       <th class="checklist-quantity">${itemCount}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${c1Category.childCategoryVO}" var="c2Category">
                                        <c:set var="cat2Url">javascript:void(0);</c:set>
                                        <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c2Category.usOverriddenURL}"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c2Category.babyOverriddenURL}"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" value="${c2Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c2Category.caOverriddenURL}"/>
											</c:otherwise>
										</c:choose>
                                        <c:choose>
                                            <c:when test="${not empty c2Url}">
                                            
                                                 <c:set var="cat2Url"><c:if test="${!isOverriddenURLC2}">${contextPath}</c:if>${c2Url}</c:set>
                                                <c:set var="cat2Element"><a data-c1name="${c1Category.displayName}" data-c2name="${c2Category.displayName}" href="${cat2Url}">${c2Category.displayName}</a></c:set>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="cat2Element"><span class="textOnly">${c2Category.displayName}</span></c:set>
                                            </c:otherwise>
                                        </c:choose>
                                    <tr class="checklist-items">
                                       <td class="checklist-status">
                                       </td>
                                       <td class="checklist-name">${cat2Element}</td>
                                       <c:if test="${showItemsRecommended || activateGuideInRegistryRibbon}">
                                        <td class="checklist-quantity text-right">
                                            <span class="textOnly" aria-hidden="true">${c2Category.suggestedQuantity}</span>
                                            <span class="visuallyhidden" aria-hidden="false">${c2Category.suggestedQuantity} ${itemsRecommended} ${c2Category.displayName}</span>
                                       </c:if>
                                    </tr>
                                    </c:forEach>
                                      <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" value="${c1Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c1Category.usOverriddenURL}"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" value="${c1Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c1Category.babyOverriddenURL}"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" value="${c1Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value="${c1Category.caOverriddenURL}"/>
											</c:otherwise>
										</c:choose>
                                     <tr>
<%--                                      <c:if test="${not activateGuideInRegistryRibbon}"> --%>
    							<td colspan="3"><a data-c1name="${c1Category.displayName}" href="<c:if test="${!isOverriddenURLC2}">${contextPath}</c:if>${c2Url}" role="button" class="button-Med btnSecondary checklistShowAll" >
                          <span> SHOP ALL ${c1Category.displayName} </span></a></td>
<%--                           </c:if> --%>
  								</tr> 
                                </tbody>
                             </table>
                         </div>
                    </section>
                </div>
            </c:forEach>
        </c:otherwise>
        </c:choose>
</section>
</dsp:page>