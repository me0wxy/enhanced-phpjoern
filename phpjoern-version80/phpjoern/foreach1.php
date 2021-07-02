<?php
/**
 * Testcase for issue:
 * foreach($var as $value)
 */
$a = array(1,2,3,4);
$sum = 0;
foreach($a as $value){
  echo $value;
  $sum += $value;
}
