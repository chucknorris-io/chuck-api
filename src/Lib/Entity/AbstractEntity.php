<?php

/**
 * AbstractEntity.php - created 8 Aug 2016 01:34:50
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
namespace Chuck\Entity;

/**
 *
 * AbstractEntity
 *
 * @package Chuck\Lib
 *
 */
abstract class AbstractEntity
{

    /**
     * @return void
     */
    final public function __construct()
    {
    }

    /**
     *
     * @param  string $name
     * @param  array  $arguments
     * @return mixed
     */
    final public function __call($name, array $arguments = [])
    {
        if ('get' === substr($name, 0, 3)) {
            return $this->__get(lcfirst(substr($name, 3)));
        }

        if ('has' === substr($name, 0, 3)) {
            return $this->__get(lcfirst($name) ?: false);
        }

        if ('is' === substr($name, 0, 2)) {
            return $this->__get(lcfirst(substr($name, 2)) ?: false);
        }
    }

    /**
     *
     * @param  string $name
     * @return mixed
     */
    final public function __get($name)
    {
        return $this->$name;
    }

    /**
     *
     * @return void
     */
    final public function __set($name, $value)
    {
    }
}
