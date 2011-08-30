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


function makebasename ()
{
  local prefix=$1
  local mdpInt=`echo "$2 * 100" | bc -l`
  local nrmInt=`echo "$3 * 10" | bc -l`
  local cepInt=`echo "$4 * 100" | bc -l`
  printf '%s_mdp%03.0f_nrm%03.0f_cep%03.0f' $1 $mdpInt $nrmInt $cepInt
}


function testclassifier ()
{
  local rtest_templatefile=${1}
  local baseprefix=${2}
  local missingDescriptorProbability=${3}
  local numericRangeMagnifier=${4}
  local categoricalErrorProbability=${5}
  missingDescriptorProbability=`printf '%5.3f' $missingDescriptorProbability`
  numericRangeMagnifier=`printf '%5.3f' $numericRangeMagnifier`
  categoricalErrorProbability=`printf '%5.3f' $categoricalErrorProbability`
  basename=`makebasename $baseprefix $missingDescriptorProbability $numericRangeMagnifier $categoricalErrorProbability`
  rtest_file=${basename}.txt
  cdrtablefile="${basename}_cdrtable.txt"
  sed -e "/testName:/s/:.*/: ${basename}/" \
          -e "/missingNumericDescriptorProbability/s/:.*/: ${missingDescriptorProbability}/" \
	  -e "/numericRangeMagnifier/s/:.*/: ${numericRangeMagnifier}/" \
	  -e "/missingCategoricalDescriptorProbability/s/:.*/: ${missingDescriptorProbability}/" \
	  -e "/categoricalErrorProbability/s/:.*/: ${categoricalErrorProbability}/" \
	  -e "/testResultFile/s/:.*/: ${cdrtablefile}/" \
	  ${rtest_templatefile} > ${rtest_file}
  if grep -q replaceme ${rtest_file} ; then
    echo replaceme placeholder remains in ${rtest_file} -- exiting
    exit 1
  fi
  do_run java -jar isacrodi-cmdtool.jar rtest ${rtest_file}
  echo ${rtest_file} finished `date`
}


function grid ()
{
  local rtest_templatefile=${1}
  local i=0
  while test $i -le 100 ; do
    local mdp=x
    mdp=`echo "$i * 0.01" | bc -l`
    for nrm in 0.1 0.2 0.3 0.5 0.7 1.0 1.3 1.6 2.0 ; do
      local j=0
      while test $j -le 100 ; do
	cep=`echo "$j * 0.01" | bc -l`
	testclassifier ${rtest_templatefile} ${baseprefix} $mdp $nrm $cep
	j=`expr $j + 10`
      done
    done
    i=`expr $i + 10`
  done
}


if test "$#" -lt 3 ; then
  echo "usage: $0 <rtrain file> <rtest template file> <prefix>"
  exit 1
fi

rtrain_file=$1
rtest_templatefile=$2
baseprefix=$3

# train classifier
do_run java -jar isacrodi-cmdtool.jar rtrain ${rtrain_file}

grid ${rtest_templatefile}

