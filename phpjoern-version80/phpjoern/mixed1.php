<?php

// Invalid example
 
class A
{
    public function foo(mixed $value) {}
}
 
class B extends A
{
    // Parameter type cannot be narrowed from mixed to int
    // Fatal error thrown
    public function foo(int $value) {}
}
