<?php
/**
 * Testcase for issue:
 * dynamic call
 */
class ClassOne{
  public function foo(){
    echo "foo in ClassOne";
  }
}
class ClassTwo{
  public function foo($b){
    echo "foo in ClassTwo with ".$b;
  }
}
$classone = new ClassOne();
$classtwo = new ClassTwo();
$classone->foo();
$classtwo->foo('bar');