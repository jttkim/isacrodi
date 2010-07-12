PRODUCTIONTGZ	= isacrodi_production.tgz
ifeq ($(JBOSS_HOME),)
JBOSS_HOME	= $(HOME)/hacking/java/jboss/jboss-current
export JBOSS_HOME
endif

ifneq ($(wildcard make.local),)
include make.local
endif

ifeq ($(JBOSS_DEPLOYDIR),)
JBOSS_DEPLOYDIR	= $(JBOSS_HOME)/server/default/deploy
endif

package :
	mvn -o clean
	mvn -o package

prod : $(PRODUCTIONTGZ)

doc :
	mvn -o install
	mvn -o javadoc:javadoc
	$(MAKE) -C docs

deploy : package
	cp ear/target/isacrodi.ear $(JBOSS_DEPLOYDIR)

undeploy :
	rm -f isacrodi.ear $(JBOSS_DEPLOYDIR)

tgz : clean
	cd ../.. ; tar -zcvf isacrodi_trunk.tgz isacrodi/trunk
	mv ../../isacrodi_trunk.tgz .

$(PRODUCTIONTGZ) : package
	$(MAKE) -C pack production_ears
	tar --no-wildcards --exclude .svn -zcvf $(PRODUCTIONTGZ) *.sh pack/*.ear sampledata import/target/isacrodi-import.jar

clean :
	mvn clean
	$(MAKE) -C docs clean
	$(MAKE) -C pack clean
	rm -f $(PRODUCTIONTGZ) isacrodi_trunk.tgz

.PHONY : package deploy undeploy tgz doc clean prod

