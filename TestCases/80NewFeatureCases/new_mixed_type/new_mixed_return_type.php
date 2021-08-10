<?php

// Valid example
 
class A
{
    public function bar(): mixed {}
}
 
class B extends A
{
    // return type was narrowed from mixed to int, this is allowed
    public function bar(): int {}
}