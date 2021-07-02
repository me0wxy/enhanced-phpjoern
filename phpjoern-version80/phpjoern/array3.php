<?php

$arrayA = [1, 2, 3];

$arrayB = [4, 5];

$result = [0, ...$arrayA, ...$arrayB, 6 ,7];

// [0, 1, 2, 3, 4, 5, 6, 7]
