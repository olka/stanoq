import { HttpClient,HttpHeaders } from '@angular/common/http';
import {Injectable, EventEmitter}from '@angular/core';
import 'rxjs/add/operator/toPromise';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import { TreeModel } from 'ng2-tree';

@Injectable()
export class CrawlerService {
private versionURL = 'http://localhost:9000/version';
private crawlerURL = 'http://localhost:9000/crawler';

data: TreeModel = {value: 'Set website name by pressing right button', id: ''};
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