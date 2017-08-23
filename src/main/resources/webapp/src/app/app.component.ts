import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {CrawlerService}from './crawler.service';
import {Subscription} from 'rxjs/Subscription';
import { TreeModel, NodeEvent } from 'ng2-tree';

declare const alertify: any;

declare var window: Window;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'stanoq';
  version:any;
  spinnerSub:Subscription;
  isSpinnerVisible:any;
  dataSub:Subscription;
  data: TreeModel;
  error = {};

  constructor(private service: CrawlerService) {
        console.log('constructor')
    }

    ngOnInit() {
        console.log('init')
        this.spinnerSub = this.service.spinnerProviderObservable.subscribe(spinner => this.isSpinnerVisible = spinner)
        this.dataSub = this.service.dataProviderObservable.subscribe(data => this.data = data)
    }

    ngOnDestroy() {
        this.spinnerSub.unsubscribe();
        this.dataSub.unsubscribe();
    }

    getVersion(){
        this.service.getVersion().then(data => this.version = data.version).catch(error => this.error = error);
    }

    getSiteTree(){
        this.service.getSiteTree("")
    }

   public logEvent(e: NodeEvent): void {
     window.open(String(e.node.node.id));
     alertify.message(`${e.node.value}`);
  }

}
