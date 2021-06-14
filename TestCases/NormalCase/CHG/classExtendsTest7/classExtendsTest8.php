<?php
namespace aaa\bbb\ccc\Tom {

	class Connection {
		private $name = "Tom";

		function setName($name) {
			$this->name = $name;
		}

		function getName() {
			return $this->name;
		}
	}
}

namespace ddd\eee\fff\Amy {

	class Connection {
		private $name = "Amy";
		private $gender = "F";

		function setGender($gender) {
			$this->gender = $gender;
		}

		function getGender() {
			return $this->gender;
		}

		function setName($name) {
			$this->name = $name;
		} 

		function getName() {
			return $this->name;
		}
	}
}

namespace Test {

	use ddd\eee\fff\Amy\Connection;

	class TestConnection extends Connection {
		private $age = "18";

		function setAge($age) {
			$this->age = $age;
		}

		function getAge() {
			return $this->age;
		}
	}


	$tconn = new TestConnection();
	echo $tconn->getName();		// Amy
}