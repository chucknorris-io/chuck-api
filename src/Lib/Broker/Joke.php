<?php

/**
 * Joke.php - created Mar 6, 2016 2:03:34 PM
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
namespace Chuck\Broker;

use \Chuck\Entity as Entity;

/**
 *
 * Joke
 *
 * @package Chuck
 *
 */
class Joke
{
    /**
     *
     * @var \Chuck\Database
     */
    protected $database;

    /**
     *
     * @var Entity\Factory
     */
    protected $entityFactory;

    /**
     *
     * @return void
     */
    public function __construct(\Chuck\Database  $database, Entity\Factory $entityFactory)
    {
        $this->database      = $database;
        $this->entityFactory = $entityFactory;
    }

    /**
     *
     * @return integer
     */
    public function count()
    {
        return $this->database->fetchColumn('SELECT count_jokes ();');
    }

    /**
     *
     * @param  array|string $jokeId
     * @throws \InvalidArgumentException
     * @return Entity\JokeCollection|Entity\Joke
     */
    public function get($jokeId)
    {
        if (is_array($jokeId)) {
            return $this->entityFactory->fromJson(
                Entity\JokeCollection::class,
                $this->database->fetchColumn('SELECT get_jokes_by_ids(:ids);', [ 'ids' => json_encode($jokeId) ])
            );
        }

        if (is_string($jokeId)) {
            return $this->entityFactory->fromJson(
                Entity\Joke::class,
                $this->database->fetchColumn('SELECT get_joke(:joke_id);', [ 'joke_id' => $jokeId ])
            );
        }

        throw new \InvalidArgumentException(
            sprintf('Unsupported data type ("%s") given, only string or array allowed.', gettype($jokeId))
        );
    }

    /**
     *
     * @return array
     */
    public function getCategories()
    {
         return json_decode($this->database->fetchColumn('SELECT find_categories()'), true);
    }

    /**
     *
     * @param  Entity\Joke $joke
     * @return Entity\Joke
     */
    public function insert(Entity\Joke $joke)
    {
        return $this->entityFactory->fromJson(
            Entity\Joke::class,
            $this->database->fetchColumn(
                'SELECT insert_joke (:joke_id, :categories, :value)',
                [
                    'joke_id'    => $joke->getId(),
                    'categories' => ! empty($joke->getCategories()) ? json_encode($joke->getCategories()) : null,
                    'value'      => $joke->getValue()
                ]
            )
        );
    }

    /**
     *
     * @param  integer $limit
     * @param  integer $offest
     * @param  string  $category
     * @return \Chunk\Entity\JokeCollection
     */
    public function iterate($limit = null, $offest = 0, $category = null)
    {
        $response = $this->database->fetchColumn(
            'SELECT paginate_jokes(:size, :start, :category);',
            [
                'size'     => $limit,
                'start'    => $offest,
                'category' => $category
            ]
        );

        return $this->entityFactory->fromJson(Entity\JokeCollection::class, $response);
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
        return $this->entityFactory->fromJson(
            Entity\Joke::class,
            $this->database->fetchColumn(
                'SELECT personalize_joke_random (:replace_term, :parental_control)',
                [
                    'replace_term'     => $replaceTerm,
                    'parental_control' => [ $parentalControl, \PDO::PARAM_BOOL ]
                ]
            )
        );
    }

    /**
     * Get a random chuck norris joke
     *
     * @param  string  $category
     * @param  boolean $parentalControl
     * @return Entity\Joke
     */
    public function random($category = null, $parentalControl = false)
    {
        $response = $this->database->fetchColumn(
            'SELECT get_joke_random (:category, :parental_control)',
            [
                'category'         => $category,
                'parental_control' => [ $parentalControl, \PDO::PARAM_BOOL ]
            ]
        );

        return $this->entityFactory->fromJson(Entity\Joke::class, $response);
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
        $response = json_decode($this->database->fetchColumn(
            'SELECT find_jokes_by_query(:query, :options);',
            [
                'query'   => $query,
                'options' => json_encode([ 'limit'  => $limit, 'offset' => $offset ])
            ]
        ), true);

        if ($response['result']) {
            foreach ($response['result'] as $index => $row) {
                $response['result'][$index] = $this->entityFactory->fromArray(
                    Entity\Joke::class,
                    $row
                );
            }
        }

        return [
            'total'  => $response['total'],
            'result' => $response['result']
        ];
    }

    /**
     *
     * @param  Entity\Joke $joke
     * @return Entity\Joke
     */
    public function update(Entity\Joke $joke)
    {
        $response = $this->database->fetchColumn(
            'SELECT update_joke (:joke_id, :categories, :value)',
            [
                'joke_id'    => $joke->getId(),
                'categories' => ! empty($joke->getCategories()) ? json_encode($joke->getCategories()) : null,
                'value'      => $joke->getValue()
            ]
        );

        return $this->entityFactory->fromJson(
            Entity\Joke::class,
            $response
        );
    }

    /**
     * Get a joke by a given id including the previous and next joke
     *
     * @param  string $jokeId
     * @return null|\Chunk\Entity\JokeWindow
     */
    public function window($jokeId)
    {
        return $this->entityFactory->fromJson(
            Entity\JokeWindow::class,
            $this->database->fetchColumn('SELECT get_joke_window_by_joke_id(:id);', [ 'id' => $jokeId ])
        );
    }
}
