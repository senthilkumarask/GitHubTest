<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:page>
            <dsp:getvalueof var="checkListVO" bean="SessionBean.checklistVO" />

            <div class="checklistWrapper">
                <div class="viewport">
                    <div class="overview">
                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                            <dsp:param value="${checkListVO.categoryListVO}" name="array" />
                            <dsp:param name="elementName" value="category" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof param="category.displayName" var="categoryDisplayname" />
                                <dsp:getvalueof param="category.childCategoryVO" var="c2childcategories" />
                                <c:forEach var="c2childcategory" items="${c2childcategories}" begin="1" end="1">
                                    <dsp:getvalueof value="${c2childcategory}" var="c2Category" />
                                    <c:set var="c2CategoryLength" value="${fn:length(c2Category.childCategoryVO)}" />
                                    <c:set var="c3Available" value="false" />
                                    <c:if test="${null ne c2Category.childCategoryVO and c2CategoryLength > 1}">
                                        <c:set var="c3Available" value="true" />
                                    </c:if>
                                </c:forEach>

                                <c:choose>
                                    <c:when test="${c3Available}">
                                        <c:forEach var="c2childcategory" items="${c2childcategories}">
                                            <dsp:getvalueof value="${c2childcategory}" var="c2Category" />
                                            <c:set var="numberOfC3" value="${fn:length(c2Category.childCategoryVO)}"></c:set>
                                            <div class="checklistCategory">
                                                <span class="checklistC1Text">${categoryDisplayname}&nbsp; | &nbsp;</span>
                                                <span class="checklistC2Text">${c2childcategory.displayName}</span>
                                            </div>
                                            <table class="checklistCategoryContent" style="width:100%;margin:0;">
                                                <tr style="text-align:left">
                                                    <td class="leftContent" style="">
                                                        <table>
                                                            <tr class="checklistCategoryHeader">
                                                                    <td  class="checklistStatus">
                                                                    </td>
                                                                    <td class="checklistCategoryName">

                                                                    </td>
                                                                        <td class="checklistCategoryCount">
																		<bbbl:label key='lbl_checklist_my_items_full_view' language="${pageContext.request.locale.language}" />
    																	</td>
                                                                </tr>
                                                            <c:forEach var="c3CategoryList" items="${c2Category.childCategoryVO}" varStatus="iterator" begin="0" step="2">

                                                                <c:set var="c3Category" value="${c3CategoryList}" />
                                                                <tr class="checklistCategoryList">
                                                                    <td class="checklistStatus" style="">
                                                                        <c:choose>
                                                                            <c:when test="${c3Category.c3AutoComplete or c3Category.c3ManualComplete}">
                                                                                <span class="checked <c:if test="${c3Category.c3ManualComplete}">manualChecked</c:if>">
																					<img style="" src="/_assets/global/images/RegistryChecklist/green_check_icon.png" alt="checked" />
																				</span>
                                                                            </c:when>
                                                                            <c:when test="${!(c3Category.c3AutoComplete or c3Category.c3ManualComplete)}">
                                                                                <span class="unchecked"></span>
                                                                            </c:when>

                                                                        </c:choose>
                                                                    </td>
																	<td class="checklistCategoryName" style="">
                                                                        <div class="<c:if test="${c3Category.c3ManualComplete}">manualChecked</c:if>">${c3Category.displayName}</div>
                                                                    </td>
                                                                        	<td class="checklistCategoryCount <c:if test="${c3Category.c3ManualComplete}">manualChecked</c:if>">${c3Category.addedQuantity}
                                                                            <bbbl:label key='lbl_pagination_header_2' language="${pageContext.request.locale.language}" /> ${c3Category.suggestedQuantity}</td>
                                                                    	
                                                                </tr>
                                                            </c:forEach>
                                                        </table>
                                                    </td>
                                                    <td class="rightContent" >
                                                        <table>
                                                       <c:if test="${numberOfC3 > 1}">
                                                        <tr class="checklistCategoryHeader">
                                                                    <td class="checklistStatus"> </td>
                                                                    <td class="checklistCategoryName"> </td>

                                                                            <td class="checklistCategoryCount">
                                                                            <bbbl:label key='lbl_checklist_my_items_full_view' language="${pageContext.request.locale.language}" />
                                                                            </td>
                                                                       
                                                                </tr>
                                                                </c:if>
                                                            <c:forEach var="c3CategoryList" items="${c2Category.childCategoryVO}" varStatus="iterator" begin="1" step="2">
                                                                <c:set var="c3Category" value="${c3CategoryList}" />
                                                                <tr class="checklistCategoryList">
                                                                    <td class="checklistStatus">
                                                                        <c:choose>
                                                                            <c:when test="${c3Category.c3AutoComplete or c3Category.c3ManualComplete}">
                                                                                <span class="checked <c:if test="${c3Category.c3ManualComplete}">manualChecked</c:if>">
																					<img style="" src="/_assets/global/images/RegistryChecklist/green_check_icon.png" alt="checked" />
																				</span>
                                                                            </c:when>
                                                                            <c:when test="${!(c3Category.c3AutoComplete or c3Category.c3ManualComplete)}">
                                                                                <span class="unchecked"></span>
                                                                            </c:when>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td class="checklistCategoryName <c:if test="${c3Category.c3ManualComplete}">manualChecked</c:if>">
                                                                        <div>${c3Category.displayName}</div>
                                                                    </td>
                                                                        	<td class="checklistCategoryCount <c:if test="${c3Category.c3ManualComplete}">manualChecked</c:if>">${c3Category.addedQuantity}
                                                                            <bbbl:label key='lbl_pagination_header_2' language="${pageContext.request.locale.language}" /> ${c3Category.suggestedQuantity}</td>
                                                                    	
                                                                </tr>
                                                            </c:forEach>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="c2Count" value="${fn:length(c2childcategories)}" />
                                        <div class="checklistCategory">
                                            <span class="checklistC1Text">${categoryDisplayname}&nbsp; </span>
                                        </div>
                                        <table  class="checklistCategoryContent">
                                            <tr>
                                                <td class="leftContent">
                                                    <table>

                                                    <tr class="checklistCategoryHeader">
                                                                    <td class="checklistStatus"></td>
                                                                    <td class="checklistCategoryName"></td>
                                                                            <td class="checklistCategoryCount">
                                                                                <bbbl:label key='lbl_checklist_my_items_full_view' language="${pageContext.request.locale.language}" />
                                                                            </td>

                                                                </tr>

                                                        <c:forEach var="c2CategoryList" items="${c2childcategories}" varStatus="iterator" begin="0" step="2">

                                                            <c:set var="c2Category" value="${c2CategoryList}" />

                                                            <tr class="checklistCategoryList">
                                                                <td class="checklistStatus">
                                                                    <c:choose>
                                                                        <c:when test="${c2Category.c3AutoComplete or c2Category.c3ManualComplete}">
                                                                            <span class="checked <c:if test="${c2Category.c3ManualComplete}">manualChecked</c:if>">  <%-- style="font-family:'BedBathIcons';background-color:#5cb801;color:#fff;padding-left:2px;line-height:19px;display:block;display:block;width:20px; height:20px;border:1px solid #5cb801;border-radius:50%;box-sizing:border-box;" --%>
																				<img style="" src="/_assets/global/images/RegistryChecklist/green_check_icon.png" alt="checked" />
																			</span>
                                                                        </c:when>
                                                                        <c:when test="${!(c2Category.c3AutoComplete or c2Category.c3ManualComplete)}">
                                                                            <span class="unchecked"></span>
                                                                        </c:when>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="checklistCategoryName">
                                                                    <div class="<c:if test="${c2Category.c3ManualComplete}">manualChecked</c:if>">${c2Category.displayName}</div>
                                                                </td>
                                                                    	<td class="checklistCategoryCount <c:if test="${c2Category.c3ManualComplete}">manualChecked</c:if>">${c2Category.addedQuantity}
                                                                        	 <bbbl:label key='lbl_pagination_header_2' language="${pageContext.request.locale.language}" /> ${c2Category.suggestedQuantity}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </table>
                                                </td>
                                                <td class="rightContent">
                                                    <table>
													<c:if test="${c2Count > 1}">
                                                    <tr class="checklistCategoryHeader">
                                                                    <td class="checklistStatus"></td>
                                                                    <td class="checklistCategoryName"></td>
                                                                            <td class="checklistCategoryCount">
                                                                                <bbbl:label key='lbl_checklist_my_items_full_view' language="${pageContext.request.locale.language}" />
                                                                            </td>
                                                                </tr>
                                                       </c:if>
                                                        <c:forEach var="c2CategoryList" items="${c2childcategories}" varStatus="iterator" begin="1" step="2">

                                                            <c:set var="c2Category" value="${c2CategoryList}" />

                                                            <tr class="checklistCategoryList">
                                                                <td class="checklistStatus">
                                                                    <c:choose>
                                                                        <c:when test="${c2Category.c3AutoComplete or c2Category.c3ManualComplete}">
                                                                            <span class="checked <c:if test="${c2Category.c3ManualComplete}">manualChecked</c:if>"> <%-- style="font-family:'BedBathIcons';background-color:#5cb801;color:#fff;padding-left:2px;line-height:19px;display:block;display:block;width:20px; height:20px;border:1px solid #5cb801;border-radius:50%;box-sizing:border-box;" --%>
																			<img src="/_assets/global/images/RegistryChecklist/green_check_icon.png" alt="checked" />
																			</span>
                                                                        </c:when>
                                                                        <c:when test="${!(c2Category.c3AutoComplete or c2Category.c3ManualComplete)}">
                                                                            <span class="unchecked"></span>
                                                                        </c:when>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="checklistCategoryName">
                                                                    <div style="<c:if test="${c2Category.c3ManualComplete}">manualChecked</c:if>">${c2Category.displayName}</div>
                                                                </td>
                                                                    	<td class="checklistCategoryCount <c:if test="${c2Category.c3ManualComplete}">manualChecked</c:if>">${c2Category.addedQuantity}
                                                                	    <bbbl:label key='lbl_pagination_header_2' language="${pageContext.request.locale.language}" /> ${c2Category.suggestedQuantity}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </c:otherwise>
                                </c:choose>

                            </dsp:oparam>
                        </dsp:droplet>
                    </div>
                </div>
            </div>
    </dsp:page>
