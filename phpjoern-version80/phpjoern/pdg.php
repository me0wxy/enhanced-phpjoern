<?php
/**
 * Testcase for issue:
 * dynamic call
 */
class A{
  public $a;
  public function func(){
    return 'func in class A';
  }
}
class B{
  public $b;
  public function func($a){
    return 'func in class B';
  }
}
function testfunc($x, $y){
  $res = $x->func();
  echo $y->func("a");
  return $res;
}
$m = new A;
$n = new B;
echo testfunc($m, $n);
