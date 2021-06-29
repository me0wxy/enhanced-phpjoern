<?php

/**
  * IncludeMap: MobileNew.php
  */

class Mobile {
    protected $face_id;
    
    public function __construct($face_id)
    {
        $this->face_id = $face_id;
    }

    public function getFaceId()
    {
        return $this->face_id;
    }

}