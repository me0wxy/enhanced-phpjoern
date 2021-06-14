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

namespace aaa\bbb\ccc\Student {

	interface UserInterface {
	    function getStudentId();
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

namespace ddd\eee\fff\Teacher {

	interface UserInterface {
		function getTeacherId();
	}

}

namespace Test {

	use ddd\eee\fff\Amy as AmyConnection, aaa\bbb\ccc\Student;

	class TestConnection extends AmyConnection\Connection implements Student\UserInterface {
		private $age = "18";

		function setAge($age) {
			$this->age = $age;
		}

		function getAge() {
			return $this->age;
		}

		function getStudentId() {
			return "1111";
		}
	}


	$tconn = new TestConnection();
	echo $tconn->getName();		// Amy
	echo $tconn->getStudentId(); 	// 1111
}