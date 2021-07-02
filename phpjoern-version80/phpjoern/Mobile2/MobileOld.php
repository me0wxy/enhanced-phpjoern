<?php

/**
  * IncludeMap:MobileOld.php
  */


class Mobile {
    protected $message;
    
    public function __construct($message)
    {
        $this->message = $message;
    }

    public function getMessage()
    {
        return $this->message;
    }

    // ...
}

