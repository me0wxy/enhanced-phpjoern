<?php
namespace MobileOld;

// iPhone X
class iPhone {
	// import facial recognition Trait and Message Trait
	use \System\Traits\Messageable;

	// unique TrueDepth
	protected $true_depth;

	public function __construct() {
		$this->openCamera();
	}

	private function openCamera() {
		return $this->true_depth;
	}

	// ...
}

 // Huawei Nova5
 class Huawei {
 	// import message Trait
 	use \System\Traits\Messageable;

 	// unique EMUI
 	protected $emui;

 	public function __construct() {
 		$this->MultiScreen();
 	}

 	// Multi-Screen collaboration
 	private function MultiScreen() {
 		// ...
 		echo "Welcome to experience the multi-screen collaboration function";
 	}
 }