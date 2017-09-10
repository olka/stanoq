[![Coverage Status](https://coveralls.io/repos/github/olka/stanoq/badge.svg?branch=master)](https://coveralls.io/github/olka/stanoq?branch=master)
[![Build Status](https://travis-ci.org/olka/stanoq.svg?branch=master)](https://travis-ci.org/olka/stanoq)

STANoQ
http://stanoq.herokuapp.com

ng build --prod --aot --build-optimizer

TODO:
1) Deployment process https://github.com/angular-buch/angular-cli-ghpages

    npm install --save-dev gh-pages
    then, in your package.json:

    "scripts": {
        "deploy": "ng build -prod -sm -ec -bh /reponame/ && gh-pages -d dist"
    }
2) performance improvement
       recursion - stack size
       getdocument 100-400ms
       parselinks - 100 ms
   crawler4j or self-written crawler?
3) Extract modules (UI, backend?)
4) VueJS
6) Mock tests
7) remove overhead payload in stream endpoint
9) mongo DB + caching
10) Session management and https://jwt.io
11) performance metrics
12) Crawling process as akka actor
13) UI improvements (overflow, css, layouts, mobile UI)
14) integrate gatling into build process

==============================Heroku============================================
---- Global Information --------------------------------------------------------
> request count                                       9000 (OK=9000   KO=0     )
> min response time                                     97 (OK=97     KO=-     )
> max response time                                    453 (OK=453    KO=-     )
> mean response time                                   124 (OK=124    KO=-     )
> std deviation                                         35 (OK=35     KO=-     )
> response time 50th percentile                        113 (OK=113    KO=-     )
> response time 75th percentile                        122 (OK=122    KO=-     )
> response time 90th percentile                        157 (OK=157    KO=-     )
> response time 99th percentile                        265 (OK=265    KO=-     )
> mean requests/sec                                    200 (OK=200    KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 400 ms                                          8997 (100%)
> 400 ms < t < 800 ms                                    3 (  0%)
> t > 800 ms                                             0 (  0%)
> failed                                                 0 (  0%)

---- Global Information --------------------------------------------------------
> request count                                       9000 (OK=9000   KO=0     )
> min response time                                    102 (OK=102    KO=-     )
> max response time                                   5205 (OK=5205   KO=-     )
> mean response time                                   318 (OK=318    KO=-     )
> std deviation                                        315 (OK=315    KO=-     )
> response time 50th percentile                        240 (OK=240    KO=-     )
> response time 75th percentile                        342 (OK=342    KO=-     )
> response time 90th percentile                        485 (OK=485    KO=-     )
> response time 99th percentile                       1651 (OK=1651   KO=-     )
> mean requests/sec                                  187.5 (OK=187.5  KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 400 ms                                          7353 ( 82%)
> 400 ms < t < 800 ms                                 1268 ( 14%)
> t > 800 ms                                           379 (  4%)
> failed                                                 0 (  0%)
================================================================================


===============================PI===============================================
---- Global Information --------------------------------------------------------
> request count                                       9000 (OK=9000   KO=0     )
> min response time                                     11 (OK=11     KO=-     )
> max response time                                  29098 (OK=29098  KO=-     )
> mean response time                                  2303 (OK=2303   KO=-     )
> std deviation                                       4211 (OK=4211   KO=-     )
> response time 50th percentile                        534 (OK=534    KO=-     )
> response time 75th percentile                       1753 (OK=1753   KO=-     )
> response time 90th percentile                       9248 (OK=9248   KO=-     )
> response time 99th percentile                      17800 (OK=17800  KO=-     )
> mean requests/sec                                147.541 (OK=147.541 KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 400 ms                                          3935 ( 44%)
> 400 ms < t < 800 ms                                 1349 ( 15%)
> t > 800 ms                                          3716 ( 41%)
> failed                                                 0 (  0%)
================================================================================

vcgencmd measure_temp



