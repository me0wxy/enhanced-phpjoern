<?php

class A {}
class B extends A {}

class Test {
    public A|B $prop;
}
class Test2 extends Test {
    public A $prop;
}
