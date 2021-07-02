<?php
// foo.php
include "bar.php";

function foo()
{
	$a = $_GET['a'];
	$b = $_GET['b'];
	bar( $a, $b);
}

foo();
