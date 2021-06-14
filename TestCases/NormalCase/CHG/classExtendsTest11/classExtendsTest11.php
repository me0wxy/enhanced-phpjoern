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

	use ddd\eee\fff\Amy as AmyConnection, aaa\bbb\ccc\Student\UserInterface, ddd\eee\fff\Teacher;

	class TestConnection extends AmyConnection\Connection implements UserInterface{
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

	class TryConnection extends AmyConnection\Connection implements Teacher\UserInterface {
		private $major = "CS";

		function getTeacherId() {
			return "2222";
		}
	}

	$tconn = new TestConnection();
	echo $tconn->getName();		// Amy
	echo $tconn->getStudentId(); 	// 1111

	$tryconn = new TryConnection();
	echo $tryconn->getTeacherId();	// 2222
}