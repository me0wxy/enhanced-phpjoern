<?php

namespace aaa\bbb\ccc;

class methodTest
{
	/**
	 * This is methodTest_A function
	 */
	public static function methodTest_A():?int
	{
		$a = 1;
		return $a;
	}

	/**
	 * This is methodTest_B function
	 */
	private function methodTest_B():?string
	{
		$b = 'bbb';
		return $b;
	}
	
}

class A extends methodTest
{
	// ...
}

methodTest::methodTest_A();
