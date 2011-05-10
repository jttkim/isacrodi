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

function testclassifier()
{
  for (( i=0; i<=${maxmissingvalue}; i++ ))
  do
    mvp=`echo "${i} * 0.01" | bc -l`
    tname=`printf '%s_p%03d.txt' ${outputfilename} ${i} `
    sed -e 's/'${missingprob}'/0'${mvp}'\t/g' -e 's/'${outputfilename}'/'${tname}'\t/g' ${input_file} > dummy.txt
    do_run java -jar isacrodi-cmdtool.jar ${task_name} dummy.txt
  done
}

function compressfile()
{
  tar czvf ${outputfilename}'.tar' ${outputfilename}*.txt
}


input_file="test_all.txt"
task_name="testsvm"
maxmissingvalue=0
missingprob=$(grep -w "missingDescriptorProbability" ${input_file} | awk '{print$2}')
outputfilename=`grep testResultFile ${input_file} | sed -e 's/testResultFile: *//' -e 's/\.*//'`

copyjar
testclassifier
compressfile
