<?php

/**
 * JokeCollection.php - created Mar 13, 2016 2:39:29 PM
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
namespace Chuck\Entity;

use \JMS\Serializer\Annotation\Type as SerializeType;
use \JMS\Serializer\Annotation\SerializedName as SerializeName;

/**
 *
 * JokeCollection
 *
 * @package Chuck\Lib
 *
 * @method integer              getCount
 * @method \Chuck\Entity\Joke[] getItems
 * @method integer              getTotal
 *
 */
class JokeCollection extends AbstractEntity implements \Countable, \IteratorAggregate
{

    /**
     *
     * @SerializeType("integer") @SerializeName("count")
     * @var integer
     */
    protected $count;

    /**
     *
     * @SerializeType("array<Chuck\Entity\Joke>") @SerializeName("items")
     * @var \Chuck\Entity\Joke[]
     */
    protected $items;

    /**
     *
     * @SerializeType("integer") @SerializeName("total")
     * @var integer
     */
    protected $total;

    /**
     *
     * @return integer
     */
    public function count()
    {
        return count($this->items);
    }

    /**
     *
     * @return \ArrayIterator
     */
    public function getIterator()
    {
        return new \ArrayIterator($this->items);
    }
}
