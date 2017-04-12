<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <dsp:page>
        <dsp:getvalueof var="checkListVO" param="checkListVO"/>

            <div class="checklistWrapper">
                <div class="viewport">
                    <div class="overview">
                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                            <dsp:param value="${checkListVO.categoryListVO}" name="array" />
                            <dsp:param name="elementName" value="category" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof param="category.displayName" var="categoryDisplayname" />

                                <div style="display:block;width:100%;height:auto;">
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
                                                    <tbody>
                                                        <tr style="text-align:left">
                                                            <td class="leftContent">
                                                                <table>
                                                                    <tbody>
                                                                    <tr class="checklistCategoryHeader">
                                                                                <td class="checklistCategoryName">
                                                                                </td>
                                                                                <td class="checklistCategoryCount recommendedCount"><bbbl:label key='lbl_checklist_full_view_recommended' language="${pageContext.request.locale.language}" /></td>
                                                                            </tr>
                                                                        <c:forEach var="c3CategoryList" items="${c2Category.childCategoryVO}" begin="0" step="2">

                                                                            <c:set var="c3Category" value="${c3CategoryList}" />

                                                                            <tr class="checklistCategoryList">
                                                                                <td class="checklistCategoryName">
                                                                                    <div>${c3Category.displayName}</div>
                                                                                </td>
                                                                                <td class="checklistCategoryCount" style="">${c3Category.suggestedQuantity}</td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </td>
                                                            <td class="rightContent">
                                                                <table>
                                                                    <tbody>
                                                                    <c:if test="${numberOfC3 > 1}">
                                                                    <tr class="checklistCategoryHeader">
                                                                                <td class="checklistCategoryName">
                                                                                </td>
                                                                                <td class="checklistCategoryCount recommendedCount"><bbbl:label key='lbl_checklist_full_view_recommended' language="${pageContext.request.locale.language}" /></td>
                                                                            </tr>
                                                                            </c:if>
                                                                        <c:forEach var="c3CategoryList" items="${c2Category.childCategoryVO}" begin="1" step="2">
                                                                            <c:set var="c3Category" value="${c3CategoryList}" />

                                                                            <tr class="checklistCategoryList">
                                                                                <td class="checklistCategoryName">
                                                                                    <div>${c3Category.displayName}</div>
                                                                                </td>
                                                                                <td class="checklistCategoryCount">${c3Category.suggestedQuantity}</td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                </table>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="c2Count" value="${fn:length(c2childcategories)}" />
                                            <div class="checklistCategory">
                                                <span class="checklistC1Text">${categoryDisplayname}&nbsp;</span>
                                            </div>
                                            <table class="checklistCategoryContent" style="width:100%;margin:0;">
                                                <tbody>
                                                    <tr style="text-align:left">
                                                        <td class="leftContent">
                                                            <table>
                                                                <tbody>
                                                                <tr class="checklistCategoryHeader">
                                                                                <td class="checklistCategoryName">
                                                                                </td>
                                                                                <td class="checklistCategoryCount recommendedCount"><bbbl:label key='lbl_checklist_full_view_recommended' language="${pageContext.request.locale.language}" /></td>
                                                                            </tr>
                                                                    <c:forEach var="c2CategoryList" items="${c2childcategories}" begin="0" step="2">

                                                                        <c:set var="c2Category" value="${c2CategoryList}" />

                                                                        <tr class="checklistCategoryList">
                                                                            <td class="checklistCategoryName">
                                                                                <div>${c2Category.displayName}</div>
                                                                            </td>
                                                                            <td class="checklistCategoryCount" >${c2Category.suggestedQuantity}</td>
                                                                        </tr>
                                                                    </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </td>
                                                        <td class="rightContent">
                                                            <table>
                                                                <tbody>
                                                                <c:if test="${c2Count > 1}">
                                                                <tr class="checklistCategoryHeader">
                                                                                <td class="checklistCategoryName">
                                                                                </td>
                                                                                <td class="checklistCategoryCount recommendedCount"><bbbl:label key='lbl_checklist_full_view_recommended' language="${pageContext.request.locale.language}" /></td>
                                                                  </tr>
                                                                 </c:if>
                                                                    <c:forEach var="c2CategoryList" items="${c2childcategories}" begin="1" step="2">

                                                                        <c:set var="c2Category" value="${c2CategoryList}" />

                                                                        <tr class="checklistCategoryList">
                                                                            <td class="checklistCategoryName">
                                                                                <div>${c2Category.displayName}</div>
                                                                            </td>
                                                                            <td class="checklistCategoryCount" >${c2Category.suggestedQuantity}</td>
                                                                        </tr>
                                                                    </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </td>
                                                    </tr>
                                            </table>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </dsp:oparam>
                        </dsp:droplet>
                    </div>
                </div>
            </div>
        <!-- </section> -->
    </dsp:page>
