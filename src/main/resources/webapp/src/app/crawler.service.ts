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
private echartcrawlerURL = this.host + '/crawlerStreamEchart';
private crawlerURL = this.echartcrawlerURL//this.host + '/crawlerStream';

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
        {"nodes": [{"name": "555", "value": "10" }, { "name": '777', "value": 15}, { "name": "3", "value": "20"},{ "name": "8", "value": "20"}],
        "links": [{"source":"555", "target":"777"},{"source":"3", "target":"8"},{"source":"8", "target":"555"}]}
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
            headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': data.length},
            body: data,
            cached: false,
            withCredentials: false
        }
        return config;
  }

    getSiteTree(url: String) {
        var emitter = new EventEmitter();
        this.oboeService = oboe(this.getOboeConfig(url, 5));
        this.data = this.oboeService
            .node('!.*', function(el){
                console.log(el);
                emitter.emit(el);
            })
            .fail(function(errorReport) {
                console.log(errorReport);
        });
        //emitter.subscribe(el => this.dataProvider.next(el));
        emitter.subscribe(el => this.graphProvider.next(this.getOptions(el)));
        return emitter;
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }

    getOptions(data: any){
                console.log("getOptions "+data)
                console.log(data.nodes)
                console.log(data.links)
        return {
            series: [{
                type: 'graph',
                layout: 'circular',
                animation: true,
                data: data.nodes,
                categories: [{}],
                edges: data.links
        }]
        };
    }
}