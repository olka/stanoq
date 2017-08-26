import { HttpClient,HttpHeaders } from '@angular/common/http';
import {Injectable, EventEmitter}from '@angular/core';
import 'rxjs/add/operator/toPromise';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import { TreeModel } from 'ng2-tree';

@Injectable()
export class CrawlerService {
private versionURL = 'https://stanoq.herokuapp.com/version';
private crawlerURL = 'https://stanoq.herokuapp.com/crawler';

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
private dataProvider = new BehaviorSubject(this.data);
dataProviderObservable = this.dataProvider.asObservable();
private spinnerProvider = new BehaviorSubject(false);
spinnerProviderObservable = this.spinnerProvider.asObservable();


constructor(private http: HttpClient) { }
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
        this.spinnerProvider.next(true);
        return this.postRequest(url, 3).then(data => {this.spinnerProvider.next(false);console.log(data);this.dataProvider.next(data);}).catch(error => {console.error(error);});
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }
}