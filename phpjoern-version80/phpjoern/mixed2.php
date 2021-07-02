<?php

// Valid example

class A
{
    public function foo(int $value) {}
}

class B extends A
{
    // Parameter type was widened from int to mixed, this is allowed
    public function foo(mixed $value) {}
}
