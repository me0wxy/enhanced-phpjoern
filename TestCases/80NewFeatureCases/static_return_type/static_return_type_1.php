<?php

/**
  * type=AST_TYPE, flags=TYPE_STATIC
  */
class Test {
     public function create(): static {
          return new static();
     }
}