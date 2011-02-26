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
        <s:if test="cropDisorderRecordId">
        <tr><td>Editing crop disorder record</td><td><code>${cropDisorderRecordId}</td></tr>
        <s:hidden name="cropDisorderRecordId"/>
        <tr><td>Reporting user</td><td><s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/></tr>
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
	<s:textfield label="monthly precipitation (%{monthlyprecipitationType.unit})" name="monthlyprecipitation" value="%{monthlyprecipitation}" rows="1" cols="10" class="texta"/>
	<s:textfield label="monthly average humidity (%{monthlyaveragehumidityType.unit})" name="monthlyaveragehumidity" value="%{monthlyaveragehumidity}" rows="1" cols="10"/>
	<s:textfield label="monthly average temperature (%{monthlyaveragetemperatureType.unit})" name="monthlyaveragetemperature" value="%{monthlyaveragetemperature}" rows="1" cols="10"/>
	<s:textfield label="altitude (%{altitudeType.unit})" name="altitude" value="%{altitude}"/>

	<tr><td colspan="2"><h3>Crop management</h3></td></tr>
	<s:textfield label="cultivated area (%{cultivatedareaType.unit})" name="cultivatedarea" value="%{cultivatedarea}" rows="1" cols="10"/>
	<s:textfield label="crop age (%{cropageType.unit})" name="cropage" value="%{cropage}" rows="1" cols="10"/>
	<s:textfield label="relative affected area (%{relativeaffectedareaType.unit})" name="relativeaffectedarea" value="%{relativeaffectedarea}" rows="1" cols="10"/>
	<s:textfield label="pH (%{phType.unit})" name="pH" value="%{pH}" rows="1" cols="10"/>
	<s:textfield label="pest density (%{pestdensityType.unit})" name="pestdensity" value="%{pestdensity}" rows="1" cols="10"/>

	<s:select label="Irrigation system"
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
	<s:select label="Irrigation origin"
 	  name="irrigationorigin"
          multiple="false"
	  list="%{irrigationoriginValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>
	<s:select label="Soil type"
          value="%{soil}"
 	  name="soil"
          multiple="false"
	  list="%{soilValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Symptom"
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

        <s:select label="Overall appearance"
	  emptyOption="true"
 	  name="overallappearance"
          multiple="false"
	  list="%{overallappearanceValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Discoloration of affected leaves"
	  emptyOption="true"
 	  name="leafdiscoloration"
          multiple="false"
	  list="%{leafdiscolorationValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Appearance of leaves"
	  emptyOption="true"
 	  name="leafappearance"
          multiple="true"
	  list="%{leafappearanceValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>
	
	<s:select label="Main symptoms affecting the leaves"
	  emptyOption="true"
 	  name="leafsymptom"
          multiple="true"
	  list="%{leafsymptomValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Main symptoms affecting seedlings"
	  emptyOption="true"
 	  name="seedlingsymptom"
          multiple="true"
	  list="%{seedlingsymptomValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>
	
	<s:select label="Main symptoms affecting roots"
	  emptyOption="true"
 	  name="rootsymptom"
          multiple="true"
	  list="%{rootsymptomValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Colour of lesions"
	  emptyOption="true"
 	  name="lesioncolour"
          multiple="false"
	  list="%{lesioncolourValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Shape of lesions"
	  emptyOption="true"
 	  name="lesionshape"
          multiple="false"
	  list="%{lesionshapeValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Appearance of lesions"
	  emptyOption="true"
 	  name="lesionappearance"
          multiple="false"
	  list="%{lesionappearanceValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Odour"
	  emptyOption="true"
 	  name="odour"
          multiple="false"
	  list="%{odourValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Location of lesion"
	  emptyOption="true"
 	  name="lesionlocation"
          multiple="true"
	  list="%{lesionlocationValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>
	<s:select label="Condition inside the stem"
	  emptyOption="true"
 	  name="steminternal"
          multiple="true"
	  list="%{steminternalValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Drainage"
	  emptyOption="true"
 	  name="drainage"
          multiple="false"
	  list="%{drainageValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="Affected part of the plant"
	  emptyOption="true"
 	  name="affectedpart"
          multiple="true"
	  list="%{affectedpartValueList}"
          listKey="valueType"
          listValue="valueType"
          emptyOption="true"
	/>

	<s:select label="crop stage"
	  emptyOption="true"
 	  name="cropstage"
          multiple="false"
	  list="%{cropstageValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:select label="Pest type"
	  emptyOption="true"
 	  name="pesttype"
          multiple="false"
	  list="%{pesttypeValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:select label="Pest stage"
	  emptyOption="true"
 	  name="peststage"
          multiple="false"
	  list="%{peststageValueList}"
          listKey="valueType"
          listValue="valueType"
	/>

	<s:textfield label="plant affected by pest (%{plantaffectedType.unit})" name="plantaffected" value="%{plantaffected}" rows="1" cols="10"/>
	<s:select label="Disease field distribution"
	  emptyOption="true"
 	  name="diseasefielddistribution"
          multiple="false"
	  list="%{diseasefielddistributionValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:select label="Origin of seedlings"
	  emptyOption="true"
 	  name="seedlingorigin"
          multiple="false"
	  list="%{seedlingoriginValueList}"
          listKey="valueType"
          listValue="valueType"
	/>
	<s:submit/>
	</s:form>

  </body>
</html>
