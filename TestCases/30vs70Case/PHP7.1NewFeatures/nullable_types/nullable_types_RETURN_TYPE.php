<?php

function sayHello(?string $name) 
{
    echo "Hello " . $name . PHP_EOL;
}

sayHello(null); // Hello
sayHello("John"); //Hello John