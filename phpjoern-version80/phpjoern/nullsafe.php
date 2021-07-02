<?php

class User
{
	private string $username;
	private string $address;

	public function getAddress(): ?string
	{
		return "Abbey Road";
	}
}

$user = new User();

$country = $session?->user?->getAddress()?->country;
 
// do something with $country
