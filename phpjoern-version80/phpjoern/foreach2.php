<?php
/**
 * Testcase for issue:
 * foreach($var as $key=>$value)
 */
$a = array('a'=>'aaa','b'=>'bbb','c'=>'ccc');
foreach($a as $key=>$value){
	var_dump($key);
	echo $value;
}
