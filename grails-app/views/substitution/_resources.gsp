<div class="row-fluid">
    <div class="span4"><legend>Ressources</legend></div>
    <div class="span1"><span class="badge badge-important" id="resourcesPercentage">0 %</span></div>
    <div class="span2"><a id="runSubResourcesButton" class="btn btn-info"><i class="icon-play icon-white"></i> LANCER</a></div>
    <div class="span1" id="resourcesLoader" style="display: none; float : right;"><g:img dir="images/substitution" file="loader.gif" width="30" height="30"/></div>
</div>

<div id="subResourcesAlertContainer">
</div>

<table id="resourceTable" class="table table-striped">
    <thead>
    <tr class="upper">
        <th style="text-align: center;">#</th>
        <th>code</th>
        <th>tags</th>
        <th>comment</th>
        <th>propriétaire</th>
        <th>nom</th>
        <th style="text-align: center;">
            A RELANCER <input id="restartResourceAll" type="checkbox" disabled="true" style="float: right;">
        </th>
    </tr>
    </thead>
    <tbody>
    <g:each status="i" in="${resourceList}" var="resource">
        <tr id="res${resource.id}_plot${resource.plotId}">
            <!-- # -->
            <td style="text-align: center;">${i + 1}</td>
            <!-- Code - modal button -->
            <td><a href="#modalRes${i + 1}" role="button" class="btn" data-toggle="modal" disabled="true">RES-${resource.id.encodeAsHTML()}_${resource.plotId.encodeAsHTML()}</a></td>
            <!-- Tags -->
            <td>
                <ul class="unstyled">
                    <g:each status="j" in="${resource.tagList}" var="tag">
                        <li><strong class="cap">${tag.value.encodeAsHTML()}</strong>
                            (<span class="cap">${tag.family.encodeAsHTML()}</span> / ${tag.weight.encodeAsHTML()})</li>
                    </g:each>
                </ul>
            </td>
            <!-- Comment -->
            <td>${resource.comment.encodeAsHTML()}</td>
            <!-- Character owner -->
            <td class="upper">
				<g:if test="${resource.character != null}">
                CHAR - ${resource.character.id}
            </g:if> <g:else>
                Info indisponible
            </g:else>
			</td>
            <!-- Resource -->
            <td class="resource">
                <select class="bold" disabled="true" isEmpty="true">
                </select>
                <a class="btn unban" title="Débannir" disabled="true"><i class="icon-arrow-left"></i></a>
            </td>
            <!-- Restart resource -->
            <td class="restartResource" style="text-align: center;">
                <input type="checkbox" name="option" value="unlock" disabled="true">
            </td>
        </tr>
    </g:each>
    <tbody>
</table>

<!-- Modal Views -->
<!--g:render template="modalViewResources" /-->

<g:javascript src="substitution/subResources.js" />

<script type="text/javascript">
    $(document).ready(function() {
        // ResourcesJSON
        resourcesJSON = initResourcesJSON();

        isSubResourcesRunning = false;

        initResourcesEvents("${g.createLink(controller:'substitution', action:'getSubResources')}")
    });

    function initResourcesJSON() {
        var jsonObject = new Object();
        // Universe
        jsonObject.universe = "${gnInfo.universe}";

        // BEGIN Resources LOOP
        var resourceArray = new Array();
        <g:each status="i" in="${resourceList}" var="resource">
        var resource = new Object();
        // Gn id
        resource.gnId = "${resource.id}"
        // Gn plot id
        resource.gnPlotId = "${resource.plotId}"
        // HTML id
        resource.htmlId = "res${resource.id}_plot${resource.plotId}"
        // Code
        resource.code = "${resource.code}"
        // BEGIN Tags LOOP
        var tagArray = new Array();
        <g:each status="j" in="${resource.tagList}" var="tag">
        var tag = new Object();
        tag.value = "${tag.value}";
        tag.family = "${tag.family}";
        tag.weight = "${tag.weight}";
        tagArray.push(tag);
        </g:each>
        // END Tags LOOP
        if (tagArray.length > 0) {resource.tags = tagArray;}
        resourceArray.push(resource);
        </g:each>
        // END Resources LOOP

        jsonObject.resources = resourceArray;
        return jsonObject;
    }
</script>

