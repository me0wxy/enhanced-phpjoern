<?php
    
class ElePHPant
{
    private $name, $colour, $age, $cuteness;
 
    public function __construct(array $attributes) {
        list(
            "name" => $this->name,
            "colour" => $this->colour,
            "age" => $this->age,
            "cuteness" => $this->cuteness
        ) = $attributes;
    }
}