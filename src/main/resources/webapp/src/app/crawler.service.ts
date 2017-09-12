import{HttpClient,HttpHeaders}from '@angular/common/http';
import {Injectable, EventEmitter}from '@angular/core';
import 'rxjs/add/operator/toPromise';
import {Observable}from 'rxjs/Observable';
import {Subscription}from 'rxjs/Subscription';
import {BehaviorSubject}from 'rxjs/BehaviorSubject';
import {TreeModel} from 'ng2-tree';

declare var oboe: any;

@Injectable()
export class CrawlerService {
private host = 'http://localhost:9000'//'https://stanoq.herokuapp.com'
private versionURL = this.host + '/version';
private crawlerURL = this.host + '/crawlerStream';

data: TreeModel = {
value: 'Programming languages by programming paradigm',
children: [
{
value: 'Object-oriented programming',
children: [
{value: 'Java'
},
{value: 'C++'
},
{value: 'C#'
}
]
},
{
value: 'Prototype-based programming',
children: [
{value: 'JavaScript'
},
{value: 'CoffeeScript'
},
{value: 'Lua'
}
]
}
]
};
options: any = this.getOptions(
{
"nodes": [{
"name": "1", "value": "10" }, {
"name": '2', "value": 15}, {
"name": "3", "value": "20"
},{
"name": "4", "value": "20"
}],
"links": [{
"source":"1", "target":"2"
},{"source":"3", "target":"4"
},{
"source":"4", "target":"1"
}]
}
)

dataProvider = new BehaviorSubject(this.data);
graphProvider = new BehaviorSubject(this.options);
dataSub: Subscription;
graphSub: Subscription;
dataProviderObservable = this.dataProvider.asObservable();
graphObservable = this.graphProvider.asObservable();
private oboeService: any;


constructor(private http: HttpClient) {
    this.dataSub = this.dataProviderObservable.subscribe(data => this.data = data)
    this.graphSub = this.dataProviderObservable.subscribe(opts => this.options = opts)
}

  getVersion() {
    return this.http.get(this.versionURL).toPromise()
      .catch(this.handleError);
    }

    getOboeConfig(url:String, depth:number) {
        const rawData = {
              "url": String(url),
              "depthLimit": depth,
              "timeout": 5,
              "exclusions": ["test1","test2"]
        }

        const data = JSON.stringify(rawData)

        var config = {
            'url': this.crawlerURL,
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: data,
            cached: false,
            withCredentials: false
        }
        return config;
  }

    getSiteTree(url: String) {
        var echartEmitter = new EventEmitter();
        var treeEmitter = new EventEmitter();
        this.oboeService = oboe(this.getOboeConfig(url, 1));
        this.data = this.oboeService
            .node('echart', function(el){
                echartEmitter.emit(el);
                return oboe.drop;
            })
            .node('node', function(el){
                treeEmitter.emit(el);
                return oboe.drop;
            })
            .fail(function(errorReport) {
                console.log(errorReport);
        });
        treeEmitter.subscribe(el => this.dataProvider.next(el));
        echartEmitter.subscribe(el => this.graphProvider.next(this.getOptions(el)));
        return null;
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }

    getOptions(data: any){
        return {
            series: [{
                type: 'graph',
                layout: 'circular',
                focusNodeAdjacency: true,
                legendHoverLink: true,
                hoverAnimation:true,
                animation: true,
                data: data.nodes,
                edges: data.links,
                roam: true,
                categories: [{}],
                label: {
                    emphasis: {
                        position: 'right',
                        show: true
                    },
                    normal: {
                        position: 'right',
                        formatter: '{b}'
                    }
                },
                lineStyle: {
                    normal: {
                        color: 'source',
                        curveness: 0.01
                    }
                }
        }]
        };
    }
}