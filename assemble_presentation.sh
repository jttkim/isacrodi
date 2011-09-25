#!/bin/sh

rm -rf presentation-assembly
mkdir presentation-assembly
cd presentation
jar xf ../ear/target/isacrodi.ear
mkdir web
cd web
jar xf ../isacrodi-web.war 
mv css/site-presentation.css css/site.css 
sed -e '/<param-value>development/s/development/presentation/' WEB-INF/web.xml > web.xml
mv web.xml WEB-INF/
sed -e '/struts.devMode/s/"true"/"false"/' WEB-INF/classes/struts.xml > struts.xml
mv struts.xml WEB-INF/classes/
jar cf ../isacrodi-web.war .
cd ..
rm -rf web
jar cf ../isacrodi-presentation.ear .
cd ..
rm -rf presentation-assembly

