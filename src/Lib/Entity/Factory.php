<?php

/**
 * Factory.php - created 8 Aug 2016 01:34:50
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
 * Factory
 *
 * @package Chuck\Lib
 *
 */
class Factory
{

    /**
     *
     * @var \JMS\Serializer\Serializer
     */
    private static $serializer;

    /**
     *
     * @internal Need to trim leading slashes from the namespace, because JMS
     *           serializer can't handle them.
     *
     * @param  string $entityClassName
     * @return string
     */
    private static function getCollectionClassName($entityClassName)
    {
        return sprintf('array<%s>', ltrim($entityClassName, '\\'));
    }

    /**
     *
     * @param  string  $entityClassName
     * @param  array[] $array
     * @return null|object[]
     */
    public static function collectionFromArray($entityClassName, array $array)
    {
        return ! (empty($array))
            ? self::getSerializer()->fromArray($array, self::getCollectionClassName($entityClassName))
            : null;
    }

    /**
     *
     * @param  string $entityClassName
     * @param  string $json
     * @return null|object[]
     */
    public static function collectionFromJson($entityClassName, $json)
    {
        return ! empty($json)
            ? self::getSerializer()->deserialize(
                $json,
                self::getCollectionClassName($entityClassName),
                'json',
                \JMS\Serializer\DeserializationContext::create()->setSerializeNull(true)
            )
            : null;
    }

    /**
     *
     * @param  string $entityClassName
     * @param  array  $array
     * @return null|object
     */
    public static function fromArray($entityClassName, array $array)
    {
        return ! empty($array) ? self::getSerializer()->fromArray($array, $entityClassName) : null;
    }

    /**
     *
     * @param  string $entityClassName
     * @param  string $json
     * @return null|object
     */
    public static function fromJson($entityClassName, $json)
    {
        return ! empty($json)
            ? self::getSerializer()->deserialize(
                $json,
                $entityClassName,
                'json',
                \JMS\Serializer\DeserializationContext::create()->setSerializeNull(true)
            )
            : null;
    }

    /**
     *
     * @return \JMS\Serializer\Serializer
     */
    private static function getSerializer()
    {
        if (null === self::$serializer) {
            self::setSerializer();
        }

        return self::$serializer;
    }

    /**
     *
     * @return void
     */
    private static function setSerializer()
    {
        \Doctrine\Common\Annotations\AnnotationRegistry::registerLoader('class_exists');

        $builder = \JMS\Serializer\SerializerBuilder::create();
        $builder->addDefaultHandlers();

        self::$serializer = $builder->build();
    }

    /**
     *
     * @param  object|object[] $object
     * @return array|array[]
     */
    public static function toArray($object)
    {
        return self::getSerializer()->toArray($object);
    }

    /**
     *
     * @param  object|object[] $object
     * @return string
     */
    public static function toJson($object)
    {
        return self::getSerializer()->serialize(
            $object,
            'json',
            \JMS\Serializer\SerializationContext::create()->setSerializeNull(true)
        );
    }
}
