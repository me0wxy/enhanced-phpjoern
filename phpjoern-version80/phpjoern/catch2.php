<?php

try {
    // Something goes wrong
} catch (MySpecialException $exception) {
    Log::error("Something went wrong");
}
