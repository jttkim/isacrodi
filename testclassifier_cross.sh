#!/bin/sh

function do_run ()
{
  echo $*
  if $* ; then
    true
  else
    echo "*** error"
    exit 1
  fi
}


function copyjar()
{
  do_run cp cmdtool/target/isacrodi-cmdtool.jar .
}


function makebasename ()
{
  local prefix=$1
  local mdpInt=`echo "$2 * 100" | bc -l`
  local crmInt=`echo "$3 * 10" | bc -l`
  local cepInt=`echo "$4 * 100" | bc -l`
  printf '%s_mdp%03.0f_crm%03.0f_cep%03.0f' $1 $mdpInt $crmInt $cepInt
}


function testclassifier ()
{
  local baseprefix=$1
  local missingDescriptorProbability=$2
  local cauchyRangeMagnifier=$3
  local categoricalErrorProbability=$4
  missingDescriptorProbability=`printf '%5.3f' $missingDescriptorProbability`
  cauchyRangeMagnifier=`printf '%5.3f' $cauchyRangeMagnifier`
  categoricalErrorProbability=`printf '%5.3f' $categoricalErrorProbability`
  basename=`makebasename $baseprefix $missingDescriptorProbability $cauchyRangeMagnifier $categoricalErrorProbability`
  configfile=${basename}.txt
  cdrtablefile="${basename}_cdrtable.txt"
  trainingcdrfile="${basename}_cdrs.txt"
  sed -e "/missingDescriptorProbability/s/:.*/: ${missingDescriptorProbability}/" -e "/cauchyRangeMagnifier/s/:.*/: ${cauchyRangeMagnifier}/" -e "/categoricalErrorProbability/s/:.*/: ${categoricalErrorProbability}/" -e "/testResultFile/s/:.*/: ${cdrtablefile}/" -e "/trainingCdrFile/s/:.*/: ${trainingcdrfile}/" $input_file > ${configfile}
  do_run java -jar isacrodi-cmdtool.jar testsvm ${configfile}
  echo ${configfile} finished `date`
}


function mdpsweep ()
{
  i=0
  while test $i -le 100 ; do
    mdp=`echo "$i * 0.01" | bc -l`
    testclassifier $baseprefix $mdp $crmdefault $cepdefault
    i=`expr $i + 10`
  done
}


function crmsweep ()
{
  for crm in 0.1 0.2 0.3 0.5 0.7 1.0 1.3 1.6 2.0 ; do
    testclassifier $baseprefix $mdpdefault $crm $cepdefault
  done
}


function cepsweep ()
{
  i=0
  while test $i -le 100 ; do
    cep=`echo "$i * 0.01" | bc -l`
    testclassifier $baseprefix $mdpdefault $crmdefault $cep
    i=`expr $i + 10`
  done
}


function compressfile()
{
  tar czvf ${baseprefix}.tgz ${baseprefix}*.txt
}
 

input_file="test_all.txt"
baseprefix=dummy
mdpdefault=0.5
crmdefault=0.5
cepdefault=0.2
copyjar
mdpsweep
crmsweep
cepsweep
compressfile

