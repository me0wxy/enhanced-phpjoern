<?php
namespace aaa\bbb\ccc\user1;

// declare interface UserInterface
interface UserInterface {
    function getname();
}

// implement interface UserInterface
class User implements UserInterface {
    private $name = "tom";
    public function getName(){
        return $this->name;
    }
}
