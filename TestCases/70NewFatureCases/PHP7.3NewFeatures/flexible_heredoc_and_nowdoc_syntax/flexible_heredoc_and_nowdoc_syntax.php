object(ast\Node)#1 (4) {
  ["kind"]=>
  int(132)
  ["flags"]=>
  int(0)
  ["lineno"]=>
  int(1)
  ["children"]=>
  array(3) {
    [0]=>
    object(ast\Node)#2 (5) {
      ["kind"]=>
      int(70)
      ["flags"]=>
      int(0)
      ["lineno"]=>
      int(2)
      ["children"]=>
      array(7) {
        ["name"]=>
        string(1) "A"
        ["docComment"]=>
        NULL
        ["extends"]=>
        NULL
        ["implements"]=>
        NULL
        ["stmts"]=>
        object(ast\Node)#3 (4) {
          ["kind"]=>
          int(132)
          ["flags"]=>
          int(0)
          ["lineno"]=>
          int(2)
          ["children"]=>
          array(1) {
            [0]=>
            object(ast\Node)#4 (5) {
              ["kind"]=>
              int(69)
              ["flags"]=>
              int(1)
              ["lineno"]=>
              int(3)
              ["children"]=>
              array(7) {
                ["name"]=>
                string(6) "method"
                ["docComment"]=>
                NULL
                ["params"]=>
                object(ast\Node)#5 (4) {
                  ["kind"]=>
                  int(136)
                  ["flags"]=>
                  int(0)
                  ["lineno"]=>
                  int(3)
                  ["children"]=>
                  array(3) {
                    [0]=>
                    object(ast\Node)#6 (4) {
                      ["kind"]=>
                      int(1280)
                      ["flags"]=>
                      int(0)
                      ["lineno"]=>
                      int(3)
                      ["children"]=>
                      array(5) {
                        ["type"]=>
                        object(ast\Node)#7 (4) {
                          ["kind"]=>
                          int(1)
                          ["flags"]=>
                          int(4)
                          ["lineno"]=>
                          int(3)
                          ["children"]=>
                          array(0) {
                          }
                        }
                        ["name"]=>
                        string(4) "many"
                        ["default"]=>
                        NULL
                        ["attributes"]=>
                        NULL
                        ["docComment"]=>
                        NULL
                      }
                    }
                    [1]=>
                    object(ast\Node)#8 (4) {
                      ["kind"]=>
                      int(1280)
                      ["flags"]=>
                      int(0)
                      ["lineno"]=>
                      int(3)
                      ["children"]=>
                      array(5) {
                        ["type"]=>
                        object(ast\Node)#9 (4) {
                          ["kind"]=>
                          int(1)
                          ["flags"]=>
                          int(6)
                          ["lineno"]=>
                          int(3)
                          ["children"]=>
                          array(0) {
                          }
                        }
                        ["name"]=>
                        string(10) "parameters"
                        ["default"]=>
                        NULL
                        ["attributes"]=>
                        NULL
                        ["docComment"]=>
                        NULL
                      }
                    }
                    [2]=>
                    object(ast\Node)#10 (4) {
                      ["kind"]=>
                      int(1280)
                      ["flags"]=>
                      int(0)
                      ["lineno"]=>
                      int(3)
                      ["children"]=>
                      array(5) {
                        ["type"]=>
                        NULL
                        ["name"]=>
                        string(4) "here"
                        ["default"]=>
                        NULL
                        ["attributes"]=>
                        NULL
                        ["docComment"]=>
                        NULL
                      }
                    }
                  }
                }
                ["stmts"]=>
                object(ast\Node)#11 (4) {
                  ["kind"]=>
                  int(132)
                  ["flags"]=>
                  int(0)
                  ["lineno"]=>
                  int(3)
                  ["children"]=>
                  array(0) {
                  }
                }
                ["returnType"]=>
                NULL
                ["attributes"]=>
                NULL
                ["__declId"]=>
                int(0)
              }
              ["endLineno"]=>
              int(3)
            }
          }
        }
        ["attributes"]=>
        NULL
        ["__declId"]=>
        int(1)
      }
      ["endLineno"]=>
      int(4)
    }
    [1]=>
    object(ast\Node)#12 (5) {
      ["kind"]=>
      int(70)
      ["flags"]=>
      int(0)
      ["lineno"]=>
      int(5)
      ["children"]=>
      array(7) {
        ["name"]=>
        string(1) "B"
        ["docComment"]=>
        NULL
        ["extends"]=>
        object(ast\Node)#13 (4) {
          ["kind"]=>
          int(2048)
          ["flags"]=>
          int(1)
          ["lineno"]=>
          int(5)
          ["children"]=>
          array(1) {
            ["name"]=>
            string(1) "A"
          }
        }
        ["implements"]=>
        NULL
        ["stmts"]=>
        object(ast\Node)#14 (4) {
          ["kind"]=>
          int(132)
          ["flags"]=>
          int(0)
          ["lineno"]=>
          int(5)
          ["children"]=>
          array(1) {
            [0]=>
            object(ast\Node)#15 (5) {
              ["kind"]=>
              int(69)
              ["flags"]=>
              int(1)
              ["lineno"]=>
              int(6)
              ["children"]=>
              array(7) {
                ["name"]=>
                string(6) "method"
                ["docComment"]=>
                NULL
                ["params"]=>
                object(ast\Node)#16 (4) {
                  ["kind"]=>
                  int(136)
                  ["flags"]=>
                  int(0)
                  ["lineno"]=>
                  int(6)
                  ["children"]=>
                  array(1) {
                    [0]=>
                    object(ast\Node)#17 (4) {
                      ["kind"]=>
                      int(1280)
                      ["flags"]=>
                      int(16)
                      ["lineno"]=>
                      int(6)
                      ["children"]=>
                      array(5) {
                        ["type"]=>
                        NULL
                        ["name"]=>
                        string(10) "everything"
                        ["default"]=>
                        NULL
                        ["attributes"]=>
                        NULL
                        ["docComment"]=>
                        NULL
                      }
                    }
                  }
                }
                ["stmts"]=>
                object(ast\Node)#18 (4) {
                  ["kind"]=>
                  int(132)
                  ["flags"]=>
                  int(0)
                  ["lineno"]=>
                  int(6)
                  ["children"]=>
                  array(0) {
                  }
                }
                ["returnType"]=>
                NULL
                ["attributes"]=>
                NULL
                ["__declId"]=>
                int(2)
              }
              ["endLineno"]=>
              int(6)
            }
          }
        }
        ["attributes"]=>
        NULL
        ["__declId"]=>
        int(3)
      }
      ["endLineno"]=>
      int(7)
    }
    [2]=>
    object(ast\Node)#19 (4) {
      ["kind"]=>
      int(283)
      ["flags"]=>
      int(0)
      ["lineno"]=>
      int(9)
      ["children"]=>
      array(1) {
        ["expr"]=>
        string(1) "
"
      }
    }
  }
}