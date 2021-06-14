<?php

// MiNote3 SamsangS8 iPhone X
// they all have facial recognition system
trait GeneralFunction {
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