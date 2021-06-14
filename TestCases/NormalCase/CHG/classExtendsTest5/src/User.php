<?php
include "UserInterface.php";

// implement interface UserInterface
class User implements UserInterface {
    private $name = "tom";
    public function getName(){
        return $this->name;
    }
}
