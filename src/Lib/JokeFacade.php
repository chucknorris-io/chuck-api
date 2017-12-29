<?php

/**
 * JokeFacade.php - created Mar 6, 2016 1:48:04 PM
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
namespace Chuck;

use \Chuck\Entity as Entity;

/**
 *
 * JokeFacade
 *
 * @package Chuck
 *
 */
class JokeFacade
{

    /**
     *
     * @var \Chuck\Broker\Joke
     */
    protected $jokeBroker;

    /**
     *
     * @param  \Chuck\Broker\Joke $jokeBroker
     * @return void
     */
    public function __construct(\Chuck\Broker\Joke $jokeBroker)
    {
        $this->jokeBroker = $jokeBroker;
    }

    /**
     * Get total number of jokes
     *
     * @return integer
     */
    public function count()
    {
        return $this->jokeBroker->count();
    }

    /**
     * Get a joke by a given joke id
     *
     * @param  string $jokeId
     * @return null|\Chunk\Entity\Joke
     */
    public function get($jokeId)
    {
        return $this->jokeBroker->get($jokeId);
    }

    /**
     * Get joke categories with joke count
     *
     * @return array
     */
    public function getCategories()
    {
        return $this->jokeBroker->getCategories();
    }


    /**
     * Insert a joke and return the new record
     *
     * @param  Entity\Joke $joke
     * @return Entity\Joke
     */
    public function insert(Entity\Joke $joke)
    {
        return $this->jokeBroker->insert($joke);
    }

    /**
     *
     * @param  integer $limit
     * @param  integer $offest
     * @param  string  $category
     * @return \Chunk\Entity\Joke[]
     */
    public function iterate($limit = null, $offest = 0, $category = null)
    {
        return $this->jokeBroker->iterate($limit, $offest, $category);
    }

    /**
     * Get a random personalized joke.
     *
     * @param  string  $replaceTerm
     * @param  boolean $parentalControl
     * @return Entity\Joke
     */
    public function personalizeRandom($replaceTerm, $parentalControl = false)
    {
        return $this->jokeBroker->personalizeRandom($replaceTerm, $parentalControl);
    }

    /**
     * Return a random chuck norris joke
     *
     * @param  string $category
     * @param  boolean $parentalControl
     * @return Entity\Joke
     */
    public function random($category = null, $parentalControl = false)
    {
        return $this->jokeBroker->random($category, $parentalControl);
    }

    /**
     * Search jokes by a given query
     *
     * @param  string  $query
     * @param  integer $limit
     * @param  integer $offset
     * @return NULL|Entity\Joke[]
     */
    public function searchByQuery($query, $limit = null, $offset = 0)
    {
        return $this->jokeBroker->searchByQuery($query, $limit, $offset);
    }

    /**
     *
     * @param  Entity\Joke $joke
     * @return Entity\Joke
     */
    public function update(Entity\Joke $joke)
    {
        return $this->jokeBroker->update($joke);
    }

    /**
     * Get a joke by a given id including the previous and next joke
     *
     * @param  string $jokeId
     * @return null|\Chunk\Entity\JokeWindow
     */
    public function window($jokeId)
    {
        return $this->jokeBroker->window($jokeId);
    }
}
