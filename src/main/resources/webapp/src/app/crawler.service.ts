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
private host = 'https://stanoq.herokuapp.com'
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

dataProvider = new BehaviorSubject(this.data);
dataSub: Subscription;
dataProviderObservable = this.dataProvider.asObservable();
private oboeService: any;


constructor(private http: HttpClient) {
    this.dataSub = this.dataProviderObservable.subscribe(data => this.data = data)
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
        this.oboeService = oboe(this.getOboeConfig(url, 3));
        this.data = this.oboeService
            .node('!.*', function(el){
                emitter.emit(el);
            })
            .fail(function(errorReport) {
                console.log(errorReport);
        });
        emitter.subscribe(el => this.dataProvider.next(el));
        return emitter;
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }
}