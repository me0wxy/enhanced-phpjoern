<?php

class Currency
{
	// ...
}

class Money 
{
    public function __construct(
        public Currency $currency,
        public int $amount,
    ) {}
}

$money = new Money(new Currency(), 1);
