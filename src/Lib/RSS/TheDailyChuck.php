<?php

/**
 * TheDailyChuck.php - created Fri 29 Dec 13:50:13 GMT 2017
 *
 * @copyright Copyright (c) Mathias Schilling <m@matchilling>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
namespace Chuck\RSS;

use Aws\S3\S3Client;
use DateTime;
use Chuck\JokeFacade;

/**
 *
 * TheDailyChuck
 *
 * @package Chuck\Lib
 *         
 */
class TheDailyChuck
{

    /**
     *
     * @var string
     */
    protected $bucket;

    /**
     *
     * @var JokeFacade
     */
    protected $jokeFacade;

    /**
     *
     * @var string
     */
    protected $key;

    /**
     *
     * @var S3Client
     */
    protected $s3Client;

    /**
     *
     * @param array $config
     * @param S3Client $s3Client
     * @param JokeFacade $jokeFacade
     */
    public function __construct(array $config, S3Client $s3Client, JokeFacade $jokeFacade)
    {
        $this->bucket = $config['bucket'];
        $this->jokeFacade = $jokeFacade;
        $this->key = $config['key'];
        $this->s3Client = $s3Client;
    }

    /**
     *
     * @return array
     */
    private function fetchIssueList()
    {
        if (! $this->s3Client->doesObjectExist($this->bucket, $this->key)) {
            return [];
        }
        
        $result = $this->s3Client->getObject([
            'Bucket' => $this->bucket,
            'Key' => $this->key
        ]);
        
        return json_decode($result['Body'], true);
    }

    /**
     *
     * @return array
     */
    public function getCurrentIssue()
    {
        $today = new DateTime();
        $issueList = $this->fetchIssueList();

        $key = $today->format('Y-m-d');
        if (array_key_exists($key, $issueList)) {
            $joke = $this->jokeFacade->get($issueList[$key]['joke_id']);
            
            return [
                'issue' => count($issueList),
                'joke' => $joke
            ];
        }
        
        $retries = 33;
        $doesExist = function ($jokeId) use ($issueList) {
            foreach ($issueList as $issue) {
                if ($jokeId === $issue['joke_id']) {
                    return true;
                }
            }
            
            return false;
        };
        
        do {
            $joke = $this->jokeFacade->random(null, true);
            $retries -= 1;
        } while ($doesExist($joke->getId()) || $retries > 0);
        
        $issueList[$key] = [
            'joke_id' => $joke->getId()
        ];
        $this->updateIssueList($issueList);
        
        return [
            'issue' => count($issueList),
            'joke' => $joke
        ];
    }

    /**
     *
     * @param array $issueList
     * @return \Aws\ResultInterface
     */
    private function updateIssueList($issueList = [])
    {
        return $this->s3Client->upload($this->bucket, $this->key, json_encode($issueList));
    }
}
