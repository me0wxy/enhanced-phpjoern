<?php
include "TeacherInterface.php";

// implement interface TeacherInterface
class Teacher implements TeacherInterface {
    private $lengthOfService = 5; 
    public function getLengthOfService(){
        return $this->lengthOfService;
    }
}