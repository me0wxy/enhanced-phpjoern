<?php
namespace MobileNew;

// iPhone 12
class iPhone {
	// import facial recognition Trait and Message Trait
	use \System\Traits\Faceable;

	// unique Wireless Charging
	protected $charge_time;

	public function __construct() {
		$this->wirelessCharging();
	}

	private function wirelessCharging() {
		return $this->charge_time;
	}

	// ...
}

 // Huawei Nova5
 class Huawei {
 	// import message Trait
 	use \System\Traits\Faceable;

 	// unique SuperCharge
 	protected $super_charge;

 	public function __construct() {
 		$this->SuperCharge();
 	}

 	// Multi-Screen collaboration
 	private function SuperCharge() {
 		// ...
 		echo "Welcome to experience the SuperCharge function";
 	}
 }