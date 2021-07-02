<?php

$longArgs_longVars = function (
    $longArgument,
    $longerArgument,
    $muchLongerArgument,  // Trailing commas were allowed in parameter lists in PHP 8.0
) use (
    $longVar1,
    $longerVar2,
    $muchLongerVar3
) {
   // body
};
$longArgs_longVars(
    $longArgumentValue,
    $obj->longMethodCall(),
    $obj->longPropertyName ?? $longDefault,
);
