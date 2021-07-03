<?php

$result = match($input) {
    0 => "hello",
    '1', '2', '3' => "world",
};