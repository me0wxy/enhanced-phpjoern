<?php

class CustomerData
{
    public function __construct(
        public string $name,
        public string $email,
        public int $age,
    ) {}
}

/**
  * Named arguments also support array spreading
  */ 
$data = new CustomerData(...$customerRequest->validated());
