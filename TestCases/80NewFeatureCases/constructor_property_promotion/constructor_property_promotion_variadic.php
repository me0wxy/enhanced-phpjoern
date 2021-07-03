<?php

class Test {
    public function __construct(public string ...$strings) {}
}

$test = new Test();