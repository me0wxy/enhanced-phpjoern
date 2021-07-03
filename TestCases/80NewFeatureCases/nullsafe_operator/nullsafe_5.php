<?php

class Foo {
    public function bar() {
        echo "bar\n";
    }
}

$foo = new Foo();

echo $foo?->bar();