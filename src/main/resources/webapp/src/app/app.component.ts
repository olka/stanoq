import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {CrawlerService}from './crawler.service';
import {Subscription} from 'rxjs/Subscription';
import { TreeModel, NodeEvent, NodeRenamedEvent } from 'ng2-tree';

declare const alertify: any;
declare var window: Window;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Site tree generator';
  version:any;
  isSpinnerVisible:any;
  dataSub:Subscription;
  data: TreeModel;
  domain: string;
  error = {};

  constructor(private service: CrawlerService) {
        console.log('constructor')
    }

    ngOnInit() {
        this.dataSub = this.service.dataProviderObservable.subscribe(data => this.data = data)
    }

    ngOnDestroy() {
        this.dataSub.unsubscribe();
    }

    getVersion(){
        this.service.getVersion().then(data => this.version = data.version).catch(error => this.error = error);
    }

    getSiteTree(domain:string){
        console.log(domain)
        this.service.getSiteTree(String(domain))
    }

   public logEvent(e: NodeEvent): void {
     //window.open(String(e.node.node.id));
     alertify.message(`${e.node.value}`);
  }

  public renamedEvent(e: NodeRenamedEvent): void {
     this.data.value = e.newValue;
     alertify.message(`${e.node.value}`);
  }

}
