<?php

/**
 * Class SurveysGroups
 * @inheritdoc
 * Used for Permission on survey inside group :
 *
 */
class SurveysInGroup extends SurveysGroups implements PermissionInterface
{
    use PermissionTrait;

    // ...
}