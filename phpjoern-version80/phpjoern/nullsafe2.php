<?php

class Person{

	public $user;
	public $country;

	public function __construct(){
		$this->user=$this;
		$this->country='UK';
	}

	public function getAddress(){
		return $this;
	}
}

$session=new Person();

$country = $session?->user?->getAddress()?->country;

var_dump($country);
