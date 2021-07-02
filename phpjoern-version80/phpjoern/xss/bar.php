<?php
// bar.php
    
function bar ($a, $b): ?int
{
	$c = $_GET['c'];
	return $a + $c;
}
