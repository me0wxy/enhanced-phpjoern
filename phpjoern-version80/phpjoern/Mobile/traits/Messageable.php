<?php

namespace common\Messageble;

// MiNote3, SamsangS8, iPhone X, Huawei Nova5
// they all have message sending function
trait GeneralFunction {
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
