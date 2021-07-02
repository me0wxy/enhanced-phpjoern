<?php

class Money
{
    public function __construct(
        public Currency $currency,
        public int $amount,
    ) {}
}
