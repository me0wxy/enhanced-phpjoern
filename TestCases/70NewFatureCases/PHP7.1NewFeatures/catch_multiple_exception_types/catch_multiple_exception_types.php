<?php
/**
  * Catching Multiple Exception Types
  */
  
try {
  	// something
  	$a = 1;
} catch (MissingParameterException | IllegalOptionException $e) {
	throw new \InvalidArgumentException($e->getMessage());
}