<?php

$foo = $bar['offset'] ?? throw new OffsetDoesNotExist('offset');
