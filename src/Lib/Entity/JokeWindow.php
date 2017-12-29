<?php

/**
 * JokeWindow.php - created Mar 13, 2016 2:39:29 PM
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling.com>
 *
 * For the full copyright and license information, please view the LICENSE file
 * that was distributed with this source code.
 *
 */
namespace Chuck\Entity;

use \JMS\Serializer\Annotation\Type as SerializeType;
use \JMS\Serializer\Annotation\SerializedName as SerializeName;

/**
 *
 * JokeWindow
 *
 * @package Chuck\Lib
 *
 * @method \Chuck\Entity\Joke getCurrent
 * @method \Chuck\Entity\Joke getNext
 * @method \Chuck\Entity\Joke getPrevious
 *
 */
class JokeWindow extends AbstractEntity
{

    /**
     *
     * @SerializeType("Chuck\Entity\Joke") @SerializeName("current")
     * @var \Chuck\Entity\Joke
     */
    protected $current;

    /**
     /**
     *
     * @SerializeType("Chuck\Entity\Joke") @SerializeName("next")
     * @var \Chuck\Entity\Joke
     */
    protected $next;

    /**
     *
     * @SerializeType("Chuck\Entity\Joke") @SerializeName("previous")
     * @var \Chuck\Entity\Joke
     */
    protected $previous;
}
