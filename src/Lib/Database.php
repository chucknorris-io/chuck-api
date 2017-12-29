<?php

/**
 * Database.php - created Mar 6, 2016 1:48:04 PM
 *
 * @copyright Copyright (c) pinkbigmacmedia
 *
 */
namespace Chuck;

/**
 *
 * Database
 *
 * @package Chuck
 *
 */
class Database
{

    /**
     *
     * @var \Doctrine\DBAL\Connection
     */
    protected $connection;

    /**
     * Fetch type map
     *
     * @var aray
     */
    protected static $fetchTypeMap = [
        self::FETCH_TYPE_COLUMN,
        self::FETCH_TYPE_LIST,
        self::FETCH_TYPE_RECORD
    ];

    // Database fetch type constants
    const FETCH_TYPE_COLUMN = 'column';
    const FETCH_TYPE_LIST   = 'list';
    const FETCH_TYPE_RECORD = 'record';

    /**
     *
     * @param  mixed $connectionConfig
     * @return void
     */
    public function __construct($connectionConfig)
    {
        $this->setConnection($connectionConfig);
    }

    /**
     * Starts a transaction by suspending auto-commit mode.
     *
     * @return void
     */
    public function beginTransaction()
    {
        $this->getConnection()->beginTransaction();
    }

    /**
     * Commits the current transaction.
     *
     * @return void
     *
     * @throws \Doctrine\DBAL\ConnectionException If the commit failed due to no active transaction or
     *                                            because the transaction was marked for rollback only.
     */
    public function commit()
    {
        $this->getConnection()->commit();
    }

    /**
     * Executes a query with the optional parameters
     *
     * @param  string    $sql        The raw SQL query to execute
     * @param  array     $bindings   The parameters to bind to the query, if any.
     * @param  string    $fetchType  Available fetch types are:
     *                                - column           returns a single column from the next row of a result,
     *                                - list   (default) returns an array containing all of the result set rows,
     *                                - record           returns the next row of a result set.
     * @param  callable  $callback   An optional callback
     *
     * @throws \InvalidArgumentException
     * @return boolean|string|boolean|mixed
     */
    private function executeQuery(
        $sql,
        array $bindings = [],
        $fetchType = self::FETCH_TYPE_LIST,
        callable $callback = null
    ) {
        $conn = $this->getConnection();
        $stmt = $conn->prepare($sql);
        if (! empty($bindings)) {
            $this->setBindings($stmt, $bindings);
        }
        $response = $stmt->execute();

        switch ($fetchType) {
            case self::FETCH_TYPE_COLUMN:
                return null !== $callback ? $callback($stmt->fetchColumn()) : $stmt->fetchColumn();

            case self::FETCH_TYPE_LIST:
                return null !== $callback ? $callback($stmt->fetchAll()) : $stmt->fetchAll();

            case self::FETCH_TYPE_RECORD:
                return null !== $callback ? $callback($stmt->fetch()) : $stmt->fetch();

            default:
                throw new \InvalidArgumentException(
                    sprintf(
                        'Invalid fetch type ("%s") given. Supported types are "%s".',
                        $fetchType,
                        implode(', ', self::$fetchTypeMap)
                    )
                );
        }
    }

    /**
     * Fetch column
     *
     * @param  string   $sql
     * @param  array    $bindings
     * @param  callable $callback
     * @return mixed
     */
    public function fetchColumn($sql, array $bindings = [], callable $callback = null)
    {
        return $this->executeQuery($sql, $bindings, self::FETCH_TYPE_COLUMN, $callback);
    }

    /**
     * Fetch list
     *
     * @param  string   $sql
     * @param  array    $bindings
     * @param  callable $callback
     * @return mixed
     */
    public function fetchList($sql, array $bindings = [], callable $callback = null)
    {
        return $this->executeQuery($sql, $bindings, self::FETCH_TYPE_LIST, $callback);
    }

    /**
     * Fetch record
     *
     * @param  string   $sql
     * @param  array    $bindings
     * @param  callable $callback
     * @return mixed
     */
    public function fetchRecord($sql, array $bindings = [], callable $callback = null)
    {
        return $this->executeQuery($sql, $bindings, self::FETCH_TYPE_RECORD, $callback);
    }

    /**
     * Returns the connection object
     *
     * @internal Method's scope is set to protected in order to force
     *           the class' consumer to use the standarized interfaces
     *           like e.g. executeQuery()
     *
     * @return   \Doctrine\DBAL\Connection
     */
    protected function getConnection()
    {
        return $this->connection;
    }

    /**
     * Cancels any database changes done during the current transaction.
     *
     * This method can be listened with onPreTransactionRollback and onTransactionRollback
     * eventlistener methods.
     *
     * @throws \Doctrine\DBAL\ConnectionException If the rollback operation failed.
     */
    public function rollBack()
    {
        $this->getConnection()->rollBack();
    }

    /**
     * Binds an array of parameter to a by reference passed statement.
     *
     * Example format for the bindings array:
     *  - Named parameters:
     *    - [ 'id' => [ $id, \PDO::PARAM_INT, 32 ], ... ]
     *    - [ 'id' => [ $id, \PDO::PARAM_INT ] ... ]
     *    - [ 'id' => $id, ... ]
     *
     * @param  \Doctrine\DBAL\Driver\Statement $stmt
     * @param  array                           $bindings
     * @return void
     */
    private function setBindings(\Doctrine\DBAL\Driver\Statement &$stmt, array $bindings)
    {
        foreach ($bindings as $name => $args) {
            $args = ! is_array($args) ? [ $args ] : $args;

            $stmt->bindValue(
                $name,
                $args[0],
                isset($args[1]) ? $args[1] : null,
                isset($args[2]) ? $args[2] : null
            );
        }
    }

    /**
     * Set the connection object
     *
     * Set the connection by using one of following provided methods:
     *   - Pass an object which implements the \Doctrine\DBAL\Driver\Connection interface
     *   - Pass an array following the doctrine configuration definitions
     *   - Pass the connection setting as an url
     *   - Pass 'fromEnv' to retrieve connection settings from an environment variable 'DATABASE_URL' (url format)
     *
     * @example URL: 'schema://user:pass@domain:port/dbname?query'
     * @see     http://doctrine-orm.readthedocs.org/projects/doctrine-dbal/en/latest/reference/configuration.html
     *
     * @param  mixed $config
     * @throws \InvalidArgumentException
     * @return \Chuck\Database
     */
    protected function setConnection($config)
    {
        if ($config instanceof \Doctrine\DBAL\Driver\Connection) {
            $this->connection = $config;

        } elseif (is_array($config)) {
            $this->connection = \Doctrine\DBAL\DriverManager::getConnection(
                $config,
                new \Doctrine\DBAL\Configuration()
            );

        } elseif (is_string($config)) {
            $this->connection = \Doctrine\DBAL\DriverManager::getConnection(
                [
                    'url' => 'fromEnv' === $config ? \Chuck\Util::getEnvOrDefault('DATABASE_URL') : $config
                ],
                new \Doctrine\DBAL\Configuration()
            );

        } else {
            throw new \InvalidArgumentException('No valid connection configuration found.');
        }

        return $this;
    }
}
