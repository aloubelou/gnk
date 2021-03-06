<%@ page import="org.gnk.resplacetime.Resource" %>
<%@ page import="org.gnk.resplacetime.GenericResource" %>

<div id="create-resource" class="content scaffold-create" role="main">
	<legend>Création d'une nouvelle ressource</legend>


	<g:hasErrors bean="${resourceInstance}">
	<ul class="errors" role="alert">
		<g:eachError bean="${resourceInstance}" var="error">
		<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
		</g:eachError>
	</ul>
	</g:hasErrors>
	
	<g:form action="save" >
		<form class="form-inline">
			<div class="row">
     			<div class="span3">Nom : <g:textField name="name" maxlength="45" value="${resourceInstance?.name}"/></div>
     			<div class="span3.5">Description : <g:textField name="desc" maxlength="100" value="${resourceInstance?.description}"/></div>
      			<div class="span2.5">
      			<g:select
	              name="gender_select"
	              from="${Resource.genders}"
	              noSelection="['':'-Choix du genre-']"/>
              </div>
                <div class="span3">
                    <g:select
                            name="genericResource_select"
                            optionKey="id"
                            optionValue="code"
                            from="${GenericResource.list()}"
                            noSelection="['':'-Ressource générique-']"/>
                </div>
   			</div>
  			<g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.add')}" />
		</form>
	</g:form>
</div>