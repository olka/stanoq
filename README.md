[![Coverage Status](https://coveralls.io/repos/github/olka/stanoq/badge.svg?branch=master)](https://coveralls.io/github/olka/stanoq?branch=master)
[![Build Status](https://travis-ci.org/olka/stanoq.svg?branch=master)](https://travis-ci.org/olka/stanoq)

STANoQ
https://olka.github.io/stanoq-ui/

TODO:

1) performance improvement
       recursion - stack size
       getdocument 100-400ms
       parselinks - 100 ms
2) Mock tests
3) Session management and https://jwt.io
4) Crawling process as akka actor (Start/Stop)
5) mobile UI and cross-browser support
6) integrate gatling into build process and track performance improvement

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

sbt test:run - execute gatling test