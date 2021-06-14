<?php
include "User.php";
include "Teacher.php";

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

$graduateStudent = new GraduateStudent();