[![Coverage Status](https://coveralls.io/repos/github/olka/stanoq/badge.svg?branch=master)](https://coveralls.io/github/olka/stanoq?branch=master)
[![Build Status](https://travis-ci.org/olka/stanoq.svg?branch=master)](https://travis-ci.org/olka/stanoq)

STANoQ
http://stanoq.herokuapp.com

ng build --prod --aot --build-optimizer

TODO:

2) performance improvement
       recursion - stack size
       getdocument 100-400ms
       parselinks - 100 ms
3) Main purpose: analyze site/pages loading speed
4) VueJS
5) Mock tests
6) mongo DB => caching with pre-defined expiration timeout (12 hours?)
7) Session management and https://jwt.io
8) performance metrics
9) Crawling process as akka actor
10) UI improvements (overflow, css, layouts, mobile UI)
11) integrate gatling into build process ?
12) improve visualization
13) add different topology layout
14) depth level control

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