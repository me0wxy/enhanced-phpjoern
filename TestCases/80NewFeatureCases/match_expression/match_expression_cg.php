<?php

/**
 * Test Call Graph
 */

function save()
{
    echo "Saved";
}

function delete()
{
    echo "deleted";
}
    
match ($pressedKey) {
    Key::RETURN_ => save(),
    Key::DELETE => delete(),
};