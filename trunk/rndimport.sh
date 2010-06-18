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


do_run cp import/target/isacrodi-import.jar ./
do_run java -jar isacrodi-import.jar -r 1 5 10 10 100 20 0.7 3.0 1.0
