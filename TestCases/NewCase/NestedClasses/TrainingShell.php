<?php
/*
 * Verify whether phpjoern corretly parses [classid] when nested classes exists.
 * In this case, the nested class id is anonymous class.
 */
class TrainingShell extends AppShell {

    private function __queryRemoteMISP($options, $returnFullResponse = false)
    {
        
            $response = new class{};
           
    }

    public function getOptionParser($a)
    {
        return $a;
    }
}