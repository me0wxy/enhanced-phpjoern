<?php
    
class Money
{
    public Currency $currency;

    public int $amount;

    public function __construct(
        Currency $currency,
        int $amount,
    ) {
        $this->currency = $currency;
        $this->amount = $amount;
    }
}

$money = new Money(new Currency(), 1);
