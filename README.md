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
15) compress payload
16) measure performance on PI


==============================Version===========================================
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


