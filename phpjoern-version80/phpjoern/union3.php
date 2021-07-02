<?php

function show(?string $a){
	var_dump($a);
}
show(null);


function show(string|null $a){
	var_dump($a);
}
show(null);
