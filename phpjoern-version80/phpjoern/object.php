<?php
class Model {
    public $table = 'users';
    public function __construct($table = null){
        if($table){
            $this->table = $table;
        }
    }
    public function getTable(){
        return $this->table;
    }
}
$myclass = new Model;
function test(Model $arg) : Object {
    return $arg;
}
echo test($myclass)->getTable();
