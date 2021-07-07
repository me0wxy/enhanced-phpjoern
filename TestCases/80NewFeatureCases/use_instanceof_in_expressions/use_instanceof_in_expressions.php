<?php
 
class NumberGenerator {}

$gen = new NumberGenerator();
 
$test1 = $gen instanceof ('NumberGenerator');      // true
 
$test2 = $gen instanceof (NumberGenerator::class); // true
 
$prefix = 'Number';
$service = 'Generator';
$test3a = $gen instanceof ($prefix.$service);      // true
$test3b = $gen instanceof ('Number'.$service);     // true