<s:url var="cropdisorderurl" action="crud"><s:param name="entityClassName" value="%{'CropDisorder'}"/><s:param name="entityId" value="%{id}"/></s:url><s:a href="%{cropdisorderurl}"><s:property value="%{name}"/> (<span class="scientificname"><s:property value="%{scientificName}"/></span>)</s:a>