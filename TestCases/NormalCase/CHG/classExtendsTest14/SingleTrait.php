<?php

// MiNote3 SamsangS8 iPhone X
// they all have facial recognition system
trait Faceable {
	protected $face_id = 0;

	public function getFaceId() {
		// ...
		return $this->face_id;
	}

	public function setFaceId($face_id) {
		// ...
		$this->face_id = $face_id;
	}
}

// MiNote3
class MiNote3 {
	// import facial recognition Trait
	use Faceable;

	// unique MIUI
	protected $miui;

	// initialize MIUI
	public function __construct($miui) {
		$this->miui = $miui;
		$this->bootUI();
	}

	private function bootUI() {
		return $this->miui;
	}

	// ...
}

// Samsang Galaxy S8
class SamsangS8 {
	// import facial recognition Trait
	use Faceable;

	// unique Bixby
	protected $bixby;

	public function __construct() {
		$this->sayHello();
	}

	private function sayHello() {
		echo "Hi, I am Bixby!";
	}

	// ...
}

// iPhone X
class iPhoneX {
	// import facial recognition Trait
	use Faceable;

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

$iphonex = new iPhoneX();
$iPhoneX_face_id = 10;
$iphonex->setFaceId($iPhoneX_face_id);
echo $iphonex->getFaceId();