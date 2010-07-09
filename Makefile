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

prod : isacrodi_production.tgz

doc :
	mvn -o install
	mvn -o javadoc:javadoc
	$(MAKE) -C docs

deploy : package
	cp ear/target/isacrodi.ear $(JBOSS_DEPLOYDIR)

undeploy :
	rm -f isacrodi.ear $(JBOSS_DEPLOYDIR)

tgz : clean
	rm -f isacrodi_trunk.tgz
	cd ../.. ; tar -zcvf isacrodi_trunk.tgz isacrodi/trunk
	mv ../../isacrodi_trunk.tgz .

isacrodi_production.tgz : package
	$(MAKE) -C pack production_ears
	tar --no-wildcards --exclude .svn -zcvf isacrodi_production.tgz *.sh pack/*.ear sampledata import/target/isacrodi-import.jar

clean :
	mvn clean
	$(MAKE) -C docs clean

.PHONY : package deploy undeploy tgz doc clean prod

