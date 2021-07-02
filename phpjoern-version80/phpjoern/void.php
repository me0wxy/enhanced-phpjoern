<?php

class Cookie
{

    protected $jar;

    public function set(string $key, $value) : void
    {
        $this->jar[$key] = $value;
    }
}
