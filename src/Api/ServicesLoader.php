<?php

/**
 * ServicesLoader.php - created 23 Oct 2016 10:28:25
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
namespace Chuck\App\Api;

use \Aws\S3\S3Client;

/**
 *
 * ServicesLoader
 *
 * @package \Chuck\App\Api
 *
 */
class ServicesLoader implements \Silex\ServiceProviderInterface
{
    /**
     *
     * @var \Silex\Application
     */
    protected $app;

    /**
     * Bootstraps the application.
     *
     * This method is called after all services are registered
     * and should be used for "dynamic" configuration (whenever
     * a service must be requested).
     *
     * {@inheritDoc}
     * @see \Silex\ServiceProviderInterface::boot()
     */
    public function boot(\Silex\Application $app)
    {
    }

    /**
     * Registers services on the given app.
     *
     * This method should only be used to configure services and parameters.
     * It should not get services.
     *
     * {@inheritDoc}
     * @see \Silex\ServiceProviderInterface::register()
     */
    public function register(\Silex\Application $app)
    {
        $this->app = $app;

        $this->app['acl'] = $this->app->share(
            function () {
                return [
                    'write' => getenv('ACL_CAN_WRITE')
                        ? json_decode(getenv('ACL_CAN_WRITE'), true)
                        : []
                ];
            }
        );

        $this->app['config'] = $this->app->share(
            function () {
                return [
                    'aws_region'                  => getenv('AWS_DEFAULT_REGION')          ? : null, 
                    'alexa_skill_id'              => getenv('ALEXA_SKILL_ID')              ? : null,
                    'application_env'             => getenv('APPLICATION_ENV')             ? : null,
                    'blackfire_server_id'         => getenv('BLACKFIRE_SERVER_ID')         ? : null,
                    'blackfire_server_token'      => getenv('BLACKFIRE_SERVER_TOKEN')      ? : null,
                    'database_url'                => getenv('DATABASE_URL')                ? : null,
                    'facebook_app_id'             => getenv('FACEBOOK_APP_ID')             ? : null,
                    'facebook_app_page_id'        => getenv('FACEBOOK_APP_PAGE_ID')        ? : null,
                    'facebook_app_secret'         => getenv('FACEBOOK_APP_SECRET')         ? : null,
                    'facebook_jssdk_version'      => getenv('FACEBOOK_JSSDK_VERSION')      ? : null,
                    'facebook_page_access_token'  => getenv('FACEBOOK_PAGE_ACCESS_TOKEN')  ? : null,
                    'facebook_verification_token' => getenv('FACEBOOK_VERIFICATION_TOKEN') ? : null,
                    'logzio_api_key'              => getenv('LOGZIO_API_KEY')              ? : null,
                    'mongodb_uri'                 => getenv('MONGODB_URI')                 ? : null,
                    'papertrail_api_token'        => getenv('PAPERTRAIL_API_TOKEN')        ? : null,
                    'redis_url'                   => getenv('REDIS_URL')                   ? : null,
                    'slack_auth'                  => getenv('SLACK_AUTH')
                        ? json_decode(getenv('SLACK_AUTH'), true)
                        : null,
                    'slack_verification_token'    => getenv('SLACK_VERIFICATION_TOKEN')    ? : null
                ];
            }
        );
        
        $this->app['cache_service'] = $this->app->share(
            function () {
                return new \Chuck\App\Api\Service\CacheService(
                    new \Predis\Client($this->app['config']['redis_url']),
                    $this->app['chuck.entity_factory'],
                    $this->app['chuck.joke']
                );
            }
        );

        $this->app['chuck.joke'] = $this->app->share(
            function () {
                return new \Chuck\JokeFacade(
                    new \Chuck\Broker\Joke(
                        new \Chuck\Database('fromEnv'),
                        new \Chuck\Entity\Factory()
                    )
                );
            }
        );

        $this->app['chuck.entity_factory'] = $this->app->share(
            function () {
                return new \Chuck\Entity\Factory();
            }
        );

        $this->app['aws.s3'] = $this->app->share(
            function () {
                return S3Client::factory([
                    'region'  => $this->app['config']['aws_region'],
                    'version' => '2006-03-01'
                ]);
            }
        );
        
        $this->app['service.the_daily_chuck'] = $this->app->share(
            function () {
                return new \Chuck\RSS\TheDailyChuck(
                    [
                        'bucket' => 'io.chucknorris.api',
                        'key'    => sprintf(
                            'newsletter/%s/dailychuck/issues.json',
                            $this->app['application_env']
                         )
                    ],
                    $this->app['aws.s3'],
                    $this->app['chuck.joke']
                );
            }
        );
    }
}
