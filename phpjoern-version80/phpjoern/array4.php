<?php
/**
 * Testcase for issue:
 * dynamic index(variables)
 */
$a = array(1,2,3);
$i = 1;
$a[$i] = 4;
for($i=0; $i<3; $i++){
	echo $a[$i];
}

