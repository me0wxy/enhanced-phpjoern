<?php

trait PermissionTrait
{

    /**
     * Get the owner id of this record
     * Used for Permission, to be extendable for each model with owner
     * @return integer|null
     */
    public function getOwnerId()
    {
        return null;
    }

}