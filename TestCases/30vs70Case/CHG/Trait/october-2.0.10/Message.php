<?php

namespace Illuminate\Mail;

use Illuminate\Support\Traits\ForwardsCalls;

/**
 * @mixin \Swift_Message
 */
class Message
{
    use ForwardsCalls;

    /**
     * Dynamically pass missing methods to the Swift instance.
     *
     * @param  string  $method
     * @param  array  $parameters
     * @return mixed
     */
    public function __call($method, $parameters)
    {
        return $this->forwardCallTo($this->swift, $method, $parameters);
    }
}
