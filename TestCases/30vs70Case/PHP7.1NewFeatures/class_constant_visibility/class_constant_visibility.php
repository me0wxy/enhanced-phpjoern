<?php
/**
  * Class Constant Visibility
  */
  
class PostValidator
{
    protected const MAX_LENGTH = 100;

    public function validateTitle($title)
    {
        if(strlen($title) > self::MAX_LENGTH) {
            throw new \Exception("Title is too long");
        }
        return true;
    }  
}