[![Coverage Status](https://coveralls.io/repos/github/olka/stanoq/badge.svg?branch=master)](https://coveralls.io/github/olka/stanoq?branch=master)
[![Build Status](https://travis-ci.org/olka/stanoq.svg?branch=master)](https://travis-ci.org/olka/stanoq)

STANoQ
http://stanoq.herokuapp.com

ng build --prod --aot --build-optimizer

TODO:
1) Deployment process
2) performance improvement
       recursion - stack size
       getdocument 100-400ms
       parselinks - 100 ms
   crawler4j or self-written crawler?
3) Extract modules (UI, backend?)
4) ReactJS to support plugin system