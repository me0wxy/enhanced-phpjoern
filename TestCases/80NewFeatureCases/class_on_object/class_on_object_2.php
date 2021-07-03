<?php

$object = new stdClass;
var_dump($object::class); // "stdClass"
 
$object = null;
var_dump($object::class); // TypeError