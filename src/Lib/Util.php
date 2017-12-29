<?php

/**
 * Util.php - created Mar 6, 2016 12:50:43 PM
 *
 * @copyright Copyright (c) pinkbigmacmedia
 *
 */
namespace Chuck;

/**
 *
 * Util
 *
 * @package Chuck
 */
class Util
{

    /**
     * Encode a given string into an url safe representation
     *
     * @param  string $str
     * @return string
     */
    public static function base64UrlsafeEncode($str)
    {
        $data = base64_encode($str);
        $data = preg_replace('/[\=]+\z/', '', $data);
        $data = strtr($data, '+/=', '-_');

        return $data;
    }

    /**
     * Create an url safe uuid with a fixed length of 22 characters

     * @return string
     */
    public static function createSlugUuid()
    {
        $uuid = \Ramsey\Uuid\Uuid::uuid4();

        return self::base64UrlsafeEncode($uuid->getBytes());
    }

    /**
     * Gets the value of an environment variable or an optional default value
     *
     * @param  string $name
     * @param  string $default
     * @return mixed
     */
    public static function getEnvOrDefault($name, $default = null)
    {
        return getenv($name) ?: (null !== $default ? $default : null);
    }

    /**
     * Get the value of a given variable or return null
     *
     * @param  string $variable
     * @return mixed
     */
    public static function getOrNull(&$variable)
    {
        return isset($variable) ? $variable : null;
    }

    /**
     * Get the value of a given variable or an optional default value
     *
     * @param  string $variable
     * @param  string $default
     * @return mixed
     */
    public static function getOrDefault(&$variable, $default = null)
    {
        return isset($variable) ? $variable : $default;
    }
}
