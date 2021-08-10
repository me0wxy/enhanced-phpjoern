<?php

echo $twig->render(
    'index.html',
    compact(
        'title',
        'body',
        'comments',
    )
);