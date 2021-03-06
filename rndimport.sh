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

rndseed=1
numNumericTypes=5
numCrops=10
numCropDisorders=10
numCDRs=5000
numDisorderAssociations=20
numericDescriptorPercentage=0.7
stddevBetween=3.0
stddevWithin=1.0

do_run cp import/target/isacrodi-import.jar ./
do_run java -jar isacrodi-import.jar -r ${rndseed} ${numNumericTypes} ${numCrops} ${numCropDisorders} ${numCDRs} ${numDisorderAssociations} ${numericDescriptorPercentage} ${stddevBetween} ${stddevWithin}

