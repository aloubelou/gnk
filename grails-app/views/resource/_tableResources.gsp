<%@ page import="org.gnk.resplacetime.Resource" %>
<%@ page import="org.gnk.tag.Tag" %>

<legend>Administration des ressources existantes</legend>

<table class="table table-bordered">
	<thead>
		<tr>
			<g:sortableColumn property="name" title="${message(code: 'resource.name.label', default: 'Nom')}" />
			<g:sortableColumn property="description" title="${message(code: 'resource.description.label', default: 'Description')}" />
			<g:sortableColumn property="gender" title="${message(code: 'resource.gender.label', default: 'Genre')}" />
			<g:sortableColumn property="tags" title="${message(code: 'resource.dateCreated.label', default: 'Tags')}" />
			<g:sortableColumn property="univers" title="${message(code: 'resource.lastUpdated.label', default: 'Univers')}" />
            <th><g:message code="default.delete"/></th>
		</tr>
	</thead>
	<tbody>
	<g:each in="${resourceInstanceList}" status="i" var="resourceInstance">
		<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
		
			<td><g:link action="show" id="${resourceInstance.id}">${fieldValue(bean: resourceInstance, field: "name")}</g:link></td>
			<td>${fieldValue(bean: resourceInstance, field: "description")}</td>
			<td>${fieldValue(bean: resourceInstance, field: "gender")}</td>
		
			<td>
				<ul class="inline">
					<g:each in="${resourceInstance.extTags.toArray()}" status="j" var="resourceHasResourceTags">
                        <li class="badge badge-info">
                            <g:form class="form-small">
                                <g:hiddenField name="id" value="${resourceHasResourceTags.id}" />

                                ${resourceHasResourceTags.tag.name} ${resourceHasResourceTags.weight}%

                                <g:actionSubmit class="icon-remove remove-action" controller="ressource" action="deleteTag" value=" " onclick="return confirm('${message(code: 'adminRef.resource.deleteTag')}');" />
                            </g:form>
                        </li>
					</g:each>
				</ul>
			</td>
		
			<td>
				<ul class="inline">
					<g:each in="${resourceInstance.resourceHasUniverses.toArray()}" status="j" var="resourceHasUniverses">
						<span class="badge badge-info">
							<li class="label label-info">
								<g:form class="form-small">
									<g:hiddenField name="id" value="${resourceHasUniverses.id}" />
									${resourceHasUniverses.univers.name} ${resourceHasUniverses.weight}%
									<g:actionSubmit class="icon-remove remove-action" controller="ressource" action="deleteUnivers" value=" " onclick="return confirm('${message(code: 'adminRef.resource.deleteUnivers')}');" />
								</g:form>
							</li>
						</span>
					</g:each>
				</ul>
			</td>

			<td>
				<g:form>
					<fieldset class="buttons">
						<g:hiddenField name="id" value="${resourceInstance?.id}" />
						<g:actionSubmit class="btn btn-warning" action="deleteResource" value="${message(code: 'default.delete')}" onclick="return confirm('Supprimer la ressource ?');" />
					</fieldset>
				</g:form>
			</td>
		</tr>
	</g:each>
	</tbody>
</table>