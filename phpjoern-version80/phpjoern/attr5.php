<?php

use \Support\Attributes\ListensTo;

class ProductSubscriber
{
    #[ListensTo(ProductCreated::class)]
    public function onProductCreated(ProductCreated $event) { /* … */ }

    #[ListensTo(ProductDeleted::class)]
    public function onProductDeleted(ProductDeleted $event) { /* … */ }
}
