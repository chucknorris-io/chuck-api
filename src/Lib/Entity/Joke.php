<?php

/**
 * Joke.php - created Mar 6, 2016 12:09:49 PM
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
 * Joke
 *
 * @package Chuck\Lib
 *
 * @method array  getCategories
 * @method string getCreatedAt
 * @method string getId
 * @method string getUpdatedAt
 * @method string getValue
 *
 */
class Joke extends AbstractEntity
{

    /**
     *
     * @SerializeType("array") @SerializeName("categories")
     * @var array
     */
    protected $categories;

    /**
     *
     * @SerializeType("string") @SerializeName("createdAt")
     * @var string
     */
    protected $createdAt;

    /**
     *
     * @SerializeType("string") @SerializeName("id")
     * @var string
     */
    protected $id;

    /**
     *
     * @SerializeType("string") @SerializeName("updatedAt")
     * @var string
     */
    protected $updatedAt;

    /**
     *
     * @SerializeType("string") @SerializeName("value")
     * @var string
     */
    protected $value;
}
