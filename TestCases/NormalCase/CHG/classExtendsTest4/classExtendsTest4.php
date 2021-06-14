<?php
// declare interface UserInterface
interface UserInterface {
    function getname();
}

// declare interface TeacherInterface
interface TeacherInterface {
    function getLengthOfService();
}

// implement interface UserInterface
class User implements UserInterface {
    private $name = "tom";
    public function getName(){
        return $this->name;
    }
}

// implement interface TeacherInterface
class Teacher implements TeacherInterface {
    private $lengthOfService = 5; 
    public function getLengthOfService(){
        return $this->lengthOfService;
    }
}

// extends from User and implements TeacherInterface meanwhile
class GraduateStudent extends User implements TeacherInterface {
    private $teacher ;
    public function __construct(){
        $this->teacher = new Teacher();     
    }   
    public function getLengthOfService(){
        return $this->teacher->getLengthOfService();
    }
}