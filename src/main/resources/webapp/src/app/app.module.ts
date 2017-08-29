import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { CrawlerService } from './crawler.service';
import { HttpClientModule } from '@angular/common/http';
import { TreeModule } from 'ng2-tree';
import { AngularEchartsModule } from 'ngx-echarts';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule, HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    TreeModule,
    AngularEchartsModule
  ],
  providers: [CrawlerService],
  bootstrap: [AppComponent]
})
export class AppModule { }
