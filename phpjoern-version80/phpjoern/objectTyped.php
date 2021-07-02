<?php

class Foo {
}
 
class Bar {
    public function foo(Foo $object_foo_Bar) : object {
        return $object_foo_Bar;
    }
}
 
class Baz extends Bar {
    public function foo(object $object_foo_Baz) : object {
        return $object_foo_Baz;    
    }
}
