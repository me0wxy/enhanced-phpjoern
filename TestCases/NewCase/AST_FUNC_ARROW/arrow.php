<?php
// This only works in PHP 7.4 and above
$str = "Hello World";
$my_function = fn($a) => $str . $a;
echo $my_function("!");
?>
