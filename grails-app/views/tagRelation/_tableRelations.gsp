<%@ page import="org.gnk.tag.TagRelation" %>
<%@ page import="org.gnk.tag.Tag" %>
<table class="table table-bordered">
	<thead>
		<tr>
			<th><g:message code="tagRelation.tag1.label" default="Tag1" /></th>
			<th><g:message code="tagRelation.tag2.label" default="Tag2" /></th>
			<g:sortableColumn property="isBijective" title="${message(code: 'tagRelation.isBijective.label', default: 'Is Bijective')}" />		
			<g:sortableColumn property="weight" title="${message(code: 'tagRelation.weight.label', default: 'Weight')}" />	
			<g:sortableColumn property="delete" title="${message(code: 'tagRelation.weight.label', default: ' ')}" />	
		</tr>
	</thead>
	<tbody>
	<g:each in="${TagRelation.list()}" status="i" var="tagRelationInstance">
		<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
			<td><a href="#modal${tagRelationInstance.tag1.id}" role="button" class="btn" data-toggle="modal">${fieldValue(bean: tagRelationInstance, field: "tag1")}</a></td>
			<td><a href="#modal${tagRelationInstance.tag2.id}" role="button" class="btn" data-toggle="modal">${fieldValue(bean: tagRelationInstance, field: "tag2")}</a></td>
			<td><g:formatBoolean boolean="${tagRelationInstance.isBijective}" /></td>	
			<td>${fieldValue(bean: tagRelationInstance, field: "weight")}</td>	
			<td>
                <g:form>
                    <fieldset class="buttons">
                        <g:hiddenField name="idRelation" value="${tagRelationInstance?.id}" />
                        <g:actionSubmit class="btn btn-warning" action="deleteRelation" value="${message(code: 'default.delete')}" onclick="return confirm('${message(code: 'adminRef.tagRelation.deleteRelation')}');" />
                    </fieldset>
                </g:form>
            </td>
		</tr>
	</g:each>
	</tbody>
</table>

<!-- Modal Views -->
<g:render template="../tag/modalViewTags" />