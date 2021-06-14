<?php
class ClassOne{
  public function funcone(){
    echo 'funcone in ClassOne';
  }
  public function foo(){
    $this->funcone();
  }
}
class ClassTwo{
  public function functwo($a){
    echo 'functwo in ClassTwo with '.$a;
  }
  public function foo($b){
    $this->functwo($b);
  }
}
$classone = new ClassOne();
$classtwo = new ClassTwo();
$classone->funcone();
$classtwo->functwo('bar');