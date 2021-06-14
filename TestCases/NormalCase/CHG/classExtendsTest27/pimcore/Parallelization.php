<?php

namespace Pimcore\Console\Traits;

use Symfony\Component\Console\Command\LockableTrait;
use Webmozarts\Console\Parallelization\Parallelization as WebmozartParallelization;

trait Parallelization
{
    use LockableTrait;

    use WebmozartParallelization
    {
        WebmozartParallelization::configureParallelization as parentConfigureParallelization;
    }

}