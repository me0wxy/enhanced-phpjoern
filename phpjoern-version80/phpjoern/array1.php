<?php
/**
 * Testcase for issue:
 * edges between array definitions of different dimensions
 */
$a = array('a','b');
$a[0] = array('x','m','z');
$a[0][1] = 'y';
$a[1] = 'c';
