import { HttpClient,HttpHeaders } from '@angular/common/http';
import {Injectable, EventEmitter}from '@angular/core';
import 'rxjs/add/operator/toPromise';
import {Observable} from 'rxjs/Observable';
import {Subscription} from 'rxjs/Subscription';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import { TreeModel } from 'ng2-tree';

declare var oboe: any;
@Injectable()
export class CrawlerService {
private versionURL = 'https://stanoq.herokuapp.com/version';
private crawlerURL = 'http://localhost:9000/stream';

data: TreeModel = {
value: 'Programming languages by programming paradigm',
children: [
{
value: 'Object-oriented programming',
children: [
{value: 'Java'},
{value: 'C++'},
{value: 'C#'}
]
},
{
value: 'Prototype-based programming',
children: [
{value: 'JavaScript'},
{value: 'CoffeeScript'},
{value: 'Lua'}
]
}
]
};

dataProvider = new BehaviorSubject(this.data);
dataSub: Subscription;
dataProviderObservable = this.dataProvider.asObservable();
callbackObservable = this.dataProvider.asObservable();
private spinnerProvider = new BehaviorSubject(false);
spinnerProviderObservable = this.spinnerProvider.asObservable();


constructor(private http: HttpClient) {
    this.dataSub = this.dataProviderObservable.subscribe(data => this.data = data)
}


  getVersion() {
    return this.http.get(this.versionURL).toPromise()
      .catch(this.handleError);
    }

    postRequest(url:String, depth:number) {
        const headers = new HttpHeaders({ "Content-Type": "application/json; charset=UTF-8" });
        console.log('posting:: '+url)
        const data = {
                      "url": url,
                      "depthLimit": depth,
                      "timeout": 5,
                      "exclusions": ["test1","test2"]
                    }
        return this.http.post(this.crawlerURL, JSON.stringify(data), { headers: headers }).toPromise().catch(this.handleError);
  }

    getSiteTree(url: String) {
        var config = {
            'url': this.crawlerURL,
            'method': "POST",
            'body': '',
            'cached': false,
            'withCredentials': false
        }
        var emitter = new EventEmitter();

        this.oboeService = oboe(config);
        this.data = this.oboeService
            .on('start', (status, headers) =>{
                console.log('Start', status, headers);
            })
            .node('!.*', function(el){
              console.log(el);
              console.log(this.data);
              emitter.emit(el);
            })
            .done(function(things){
                console.log("Done", things);
                console.log("Last", things[things.length-1]);
                return things[things.length-1];
            })
            .fail(function(errorReport) {
                console.log(errorReport.thrown, errorReport.statusCode, errorReport.body, errorReport.jsonBody);
    });
        emitter.subscribe(el => this.dataProvider.next(el));


        //this.spinnerProvider.next(true);
        //return this.postRequest(url, 3).then(data => {this.spinnerProvider.next(false);console.log(data);this.dataProvider.next(data);}).catch(error => {console.error(error);});

        return emitter;
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }

 private oboeService: any;

}