<?php

namespace NS;

#[\SomeAttribute()]
class X {
    #[Attr1]
    #[
        Attr2(true), # Line comment on an attribute
    ]
    public $prop;
    
    #[Attr3]
    public const CONST_WITH_ATTRIBUTE = 123;
    
    #[Attr4, Attr5()]
    public static function hasAttribute(
        #[ThisIsAnAttribute, \AnotherAttribute] $parameter
    ) {}
}

#[Deprecated]
function myGlobal() {}