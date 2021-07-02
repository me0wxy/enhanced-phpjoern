<?php
/**
 * Testcase for issue:
 * edges between array uses of different dimensions
 */
function sink($a){
	foreach ($a as $key => $value) {
		readfile($value);
	}
}
$_POST['dir']['file']['name'] = '1.php';
$_POST['dir']['file'] = '';
if(isset($_POST['op'])){
	$_POST['dir']['file']['name'] = '/etc/passwd';
}
sink($_POST['dir']['file']);


