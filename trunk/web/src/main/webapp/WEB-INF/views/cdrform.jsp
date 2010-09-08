<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

  <head>
    <title>Isacrodi CDRForm</title>
    <link rel="stylesheet" type="text/css" href="<s:url value="/css/site.css"/>"/>
  </head>
  <body>
 <%@include file="/WEB-INF/views/components/navbar.jsp" %>

	<div class="content">
        <h1>CDR Form</h1>
	<p><s:actionerror/></p>
	<s:form action="cdrform">
	<s:if test="cropdisorderrecordid">
	<tr><td>editing crop disorder record</td><td><code>${cropdisorderrecordid}</td></tr>
	<s:hidden name="cropdisorderrecordid"/>
	<tr><td>reporting user</td><td><s:property value="%{isacrodiuser.firstname}"/> <s:property value="%{isacrodiuser.lastname}"/></tr>
	</s:if>
	<%-- <s:textfield label="crop (scientific name)" name="cropScientificName" value="%{cropScientificName}"/> --%>
        <s:select label="crop (scientific name)"
          name="cropScientificName"
          list="%{cropList}"
          listKey="scientificName"
          listValue="scientificName"
          emptyOption="true"
          value="%{cropScientificName}"
        />
	</td>
	</tr>
	<tr><td colspan="2"><h3>Average climate conditions</h3></td></tr>
	<s:textfield label="monthly precipitation (%{monthlyprecipitationType.unit})" name="monthlyprecipitation" value="%{monthlyprecipitation}" rows="1" cols="10"/>
	<s:textfield label="monthly average humidity (%{monthlyaveragehumidityType.unit})" name="monthlyaveragehumidity" value="%{monthlyaveragehumidity}" rows="1" cols="10"/>
	<s:textfield label="monthly average temperature (%{monthlyaveragetemperatureType.unit})" name="monthlyaveragetemperature" value="%{monthlyaveragetemperature}" rows="1" cols="10"/>
	<s:textfield label="altitude (%{altitudeType.unit})" name="altitude" value="%{altitude}"/>

	<tr><td colspan="2"><h3>Crop management</h3></td></tr>
	<s:textfield label="cultivated area (%{cultivatedareaType.unit})" name="cultivatedarea" value="%{cultivatedarea}" rows="1" cols="10"/>
	<s:textfield label="crop age (%{cropageType.unit})" name="cropage" value="%{cropage}" rows="1" cols="10"/>
	<s:textfield label="relative affected area (%{relativeaffectedareaType.unit})" name="relativeaffectedarea" value="%{relativeaffectedarea}" rows="1" cols="10"/>
	<s:textfield label="pH (%{phType.unit})" name="ph" value="%{ph}" rows="1" cols="10"/>
	<s:textfield label="pest density (%{pestdensityType.unit})" name="pestdensity" value="%{pestdensity}" rows="1" cols="10"/>

	<s:select label="irrigation system"
	  emptyOption="true"
 	  name="irrigationsystem"
          multiple="false"
	  list="%{irrigationsystemValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>
	<s:textfield label="irrigation - amount (%{irrigationamountType.unit})" name="irrigationamount" value="%{irrigationamount}" rows="1" cols="10"/>
	<s:textfield label="irrigation frequency (%{irrigationfrequencyType.unit})" name="irrigationfrequency" value="%{irrigationfrequency}" rows="1" cols="10"/>
	<s:select label="irrigation origin"
 	  name="irrigationorigin"
          multiple="false"
	  list="%{irrigationoriginValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>
	<s:select label="soil type"
          value="%{soil}"
 	  name="soil"
          multiple="false"
	  list="%{soilValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="symptom"
 	  name="symptom"
          multiple="true"
	  list="%{symptomValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
          size="8"
	/>

	<s:select label="First symptom crop stage"
	  name="firstsymptomcropstage"
          multiple="false"
	  list="%{firstsymptomcropstageValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

        <s:select label="overall appearance"
	  emptyOption="true"
 	  name="overallappearance"
          multiple="false"
	  list="%{overallappearanceValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:checkboxlist label="leafdiscoloration"
 	  name="leafdiscoloration"
	  list="%{leafdiscolorationValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="leafappearance"
 	  name="leafappearance"
	  list="%{leafappearanceValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	
	<s:checkboxlist label="leafsymptom"
 	  name="leafsymptom"
	  list="%{leafsymptomValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="seedlingsymptom"
 	  name="seedlingsymptom"
	  list="%{seedlingsymptomValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	
	<s:checkboxlist label="rootsymptom"
 	  name="rootsymptom"
	  list="%{rootsymptomValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="lesioncolour"
 	  name="lesioncolour"
	  list="%{lesioncolourValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="lesionshape"
 	  name="lesionshape"
	  list="%{lesionshapeValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="lesionappearance"
 	  name="lesionappearance"
	  list="%{lesionappearanceValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="odour"
 	  name="odour"
	  list="%{odourValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="lesionlocation"
 	  name="lesionlocation"
	  list="%{lesionlocationValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:checkboxlist label="steminternal"
 	  name="steminternal"
	  list="%{steminternalValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="drainage"
 	  name="drainage"
	  list="%{drainageValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:checkboxlist label="affected part of the plant"
 	  name="affectedpart"
	  list="%{affectedpartValueList}"
          listKey="valueType"
          listValue="valueType"
	/>


	<s:radio label="crop stage"
 	  name="cropstage"
	  list="%{cropstageValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:checkboxlist label="Pest type"
 	  name="pesttype"
	  list="%{pesttypeValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:radio label="Disease field distribution"
 	  name="diseasefielddistribution"
	  list="%{diseasefielddistributionValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:radio label="Origin of seedlings"
 	  name="seedlingorigin"
	  list="%{seedlingoriginValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:submit/>
	</s:form>

  </body>
</html>
