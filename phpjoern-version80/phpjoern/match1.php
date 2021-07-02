<?php

match ($pressedKey) {
    Key::RETURN_ => save(),
    Key::DELETE => delete(),
};
