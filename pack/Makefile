production_ears : isacrodi-create.ear isacrodi-validate.ear

isacrodi-create.ear : ../ear/target/isacrodi.ear do_unpack do_ear
	./do_unpack
	sed -e '/hibernate.hbm2ddl.auto/s/value="[^"]*"/value="create"/' ejbjar/META-INF/persistence.xml > persistence-create.xml
	cp persistence-create.xml ejbjar/META-INF/persistence.xml
	sed -e '/struts.devMode/s/true/false/' war/WEB-INF/classes/struts.xml > struts-create.xml
	cp struts-create.xml war/WEB-INF/classes/struts.xml
	./do_ear isacrodi-create.ear


isacrodi-validate.ear : ../ear/target/isacrodi.ear do_unpack do_ear
	./do_unpack
	sed -e '/hibernate.hbm2ddl.auto/s/value="[a-zA-Z-]*"/value="validate"/' ejbjar/META-INF/persistence.xml > persistence-validate.xml
	cp persistence-validate.xml ejbjar/META-INF/persistence.xml
	sed -e '/struts.devMode/s/true/false/' war/WEB-INF/classes/struts.xml > struts-validate.xml
	cp struts-validate.xml war/WEB-INF/classes/struts.xml
	./do_ear isacrodi-validate.ear

.PHONY : production_ears

