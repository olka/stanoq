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
       emiters load browser! MEMORY LEAK on emiter
   crawler4j or self-written crawler?
3) Extract modules (UI, backend?)
4) VueJS
5) remove font awesome
6) Mock tests
7) remove overhead payload in stream endpoint
8) UI performance optimization
9) mongo DB + caching
10) Session management and https://jwt.io
11) performance metrics
12) Crawling process as akka actor
13) UI improvements (overflow, css, layouts)
14) integrate gatling into build process
15) compress payload


