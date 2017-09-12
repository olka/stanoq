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

1.1) Extract modules (UI, backend?)

2) performance improvement
       recursion - stack size
       getdocument 100-400ms
       parselinks - 100 ms
3) Main purpose: analyze site/pages loading speed
4) VueJS
6) Mock tests
7) remove overhead payload in stream endpoint
9) mongo DB + caching
10) Session management and https://jwt.io
11) performance metrics
12) Crawling process as akka actor
13) UI improvements (overflow, css, layouts, mobile UI)
14) integrate gatling into build process ?
15) improve visualization
16) add different topology layout
17) mongo DB controls
18) depth level control

==============================Heroku============================================
> t < 400 ms                                          7353 ( 82%)
> 400 ms < t < 800 ms                                 1268 ( 14%)
> t > 800 ms                                           379 (  4%)
> failed                                                 0 (  0%)
================================================================================


===============================PI===============================================
> t < 400 ms                                          3935 ( 44%)
> 400 ms < t < 800 ms                                 1349 ( 15%)
> t > 800 ms                                          3716 ( 41%)
> failed                                                 0 (  0%)
================================================================================

vcgencmd measure_temp



