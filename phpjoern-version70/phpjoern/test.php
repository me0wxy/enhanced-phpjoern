<?php

/*
 * This file is part of the Symfony package.
 *
 * (c) Fabien Potencier <fabien@symfony.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 * Gives access to currency-related ICU data.
 *
 * @author Bernhard Schussek <bschussek@gmail.com>
 * @author Roland Franssen <franssen.roland@gmail.com>
 */
final class Currencies extends ResourceBundle
{
    private const INDEX_SYMBOL = 0;
    private const INDEX_NAME = 1;
    private const INDEX_FRACTION_DIGITS = 0;
    private const INDEX_ROUNDING_INCREMENT = 1;

}
