<?php

/**
 * Test Case from kanboard-1.2.19
 * app/Api/Procedure/SubtaskProcedure.php
 */

$values = array(
    'id' => $id,
);

foreach ($values as $key => $value) {
    if (is_null($value)) {
        unset($values[$key]);
    }
}

