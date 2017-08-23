import { HttpClient,HttpHeaders } from '@angular/common/http';
import {Injectable, EventEmitter}from '@angular/core';
import 'rxjs/add/operator/toPromise';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import { TreeModel } from 'ng2-tree';

@Injectable()
export class CrawlerService {
private versionURL = 'http://localhost:9000/version';
private crawlerURL = 'http://localhost:9000/crawler';
oldData = {
    pages:
        [ {id: 'start', label: 'start', title: 'test' },
        { id: '1', label: 'Query ThreatConnect' },
        { id: '2', label: 'Query XForce' },
        { id: '3', label: 'Format Results' },
        { id: '4', label: 'Search Splunk' },
        { id: '5', label: 'Block LDAP' },
        { id: '6', label: 'Email Results' } ],
    links:
        [ { source: 'start', target: '1', label: 'links to' },
        { source: 'start', target: '2' },
        { source: '1', target: '3', label: 'related to' },
        { source: '2', target: '4' },
        { source: '2', target: '6' },
        { source: '3', target: '5' }]}

public data: TreeModel = {
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

    postRequest() {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        const data = {
                      "url": "https://www.websocket.org/echo.html",
                      "depthLimit": 3,
                      "timeout": 5,
                      "exclusions": ["test1","test2"]
                    }
        return this.http.post(this.crawlerURL, JSON.stringify(data), { headers: headers }).toPromise().catch(this.handleError);
  }

    getSiteTree(json: String) {
        this.spinnerProvider.next(true);
        return this.postRequest().then(data => {this.spinnerProvider.next(false);console.log(data);this.dataProvider.next(data);}).catch(error => {console.error(error);});
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }
}