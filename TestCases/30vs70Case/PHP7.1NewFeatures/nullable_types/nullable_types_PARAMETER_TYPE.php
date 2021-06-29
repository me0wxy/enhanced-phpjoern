<?php

class Cookie
{

    protected $jar;

    public function get(string $key) : ?string
    {
        if(isset($this->jar[$key])) {
            return $this->jar[$key];
        }
        return null;
    }

}