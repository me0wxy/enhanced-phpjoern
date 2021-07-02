<?php

namespace common;

use common\Messageble\GeneralFunction as GeneralFunction;

// iPhone X
class iPhone {
	// import Message Trait
	use GeneralFunction;

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

$iphone = new iPhone();
echo $iphone->getMessage();

