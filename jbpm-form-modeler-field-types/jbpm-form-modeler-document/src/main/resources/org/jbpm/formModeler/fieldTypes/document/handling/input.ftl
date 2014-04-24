<div>
    <input type="hidden" name="${inputId}_delete" id="${inputId}_delete" value="false"/>
    <#if showLink = true>
        <div id="${inputId}_head" style="padding:0 0 5px 0; display:table;">
            <div style="display: table-row;">
                <div style="padding:0 5px 0 0; display: table-cell; vertical-align: bottom;">
                    <img src="${fileIcon}" border="0">
                </div>
                <div style="display: table-cell; vertical-align: bottom; white-space: nowrap">
                    <a href="${downloadLink}" target="_blank">${fileName} (${fileSize})</a>
                </div>
        <#if showInput = true>
                <div style="display: table-cell; vertical-align: bottom; padding:0 0 0 5px;">
                    <a href="#" onclick="$('#${inputId}_head').hide(); $('#${inputId}_delete').val('true'); return false;"><img src="${dropIcon}" border="0"/></a>
                </div>
        </#if>
            </div>
        </div>
    </#if>
    <#if showInput = true>
        <div>
            <input type="file" name="${inputId}" id="${inputId}"/>
        </div>
    </#if>
</div>