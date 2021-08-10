<?php

function bar(): iterable {
    return [1, 2, 3];
}

function foo(iterable $iterable) {
    foreach ($iterable as $value) {
        echo $value . "\n";
    }
}

foo(bar());
// 1
// 2
// 3