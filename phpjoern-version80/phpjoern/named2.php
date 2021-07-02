<?php

class CustomerData
{
    public function __construct(
        public string $name,
        public string $email,
        public int $age,
    ) {}
}

$data = new CustomerData(
    name: $input['name'],
    email: $input['email'],
    age: $input['age'],
);
