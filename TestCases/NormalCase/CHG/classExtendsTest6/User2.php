<?php
namespace aaa\bbb\ccc\user2;

// declare interface UserInterface
interface UserInterface {
    function getname();
}

// implement interface UserInterface
class User implements UserInterface {
    private $name = "jerry";
    private $age = "18";
    public function getName(){
        return $this->name;
    }
}