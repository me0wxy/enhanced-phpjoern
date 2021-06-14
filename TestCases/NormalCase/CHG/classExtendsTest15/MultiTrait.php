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

// MiNote3, SamsangS8, iPhone X, Huawei Nova5
// they all have message sending function
trait Messageable {
	protected $message_content = "This is a message";

	public function getMessage() {
		// ...
		return $this->message_content;
	}

	public function setMessage($message_content) {
		// ...
		$this->message_content = $message_content;
	}
}

// MiNote3
class MiNote3 {
	// import facial recognition Trait and Message Trait
	use Faceable, Messageable;

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
	// import facial recognition Trait and Message Trait
	use Faceable, Messageable;

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
	// import facial recognition Trait and Message Trait
	use Faceable, Messageable;

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
 class HuaweiNova5 {
 	// import message Trait
 	use Messageable;

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

$iphonex = new iPhoneX();
$iPhoneX_face_id = 10;
$iphonex->setFaceId($iPhoneX_face_id);
echo $iphonex->getFaceId();

$nove5 = new HuaweiNova5();
echo $nove5->getMessage();