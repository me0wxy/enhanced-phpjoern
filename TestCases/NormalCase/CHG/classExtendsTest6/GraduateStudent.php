<?php
include "User1.php";
include "User2.php";

use aaa\bbb\ccc\user2\User;

// extends from User and implements TeacherInterface meanwhile
class GraduateStudent extends User{
    private $teacher ;
}

$graduateStudent = new GraduateStudent();
echo $graduateStudent->getName();