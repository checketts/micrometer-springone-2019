import {Component} from '@angular/core';
import {AnalyticsService} from './analytics.service';
import {MatTabChangeEvent} from '@angular/material';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'metrics-app';

  constructor(private analytics: AnalyticsService) {
    this.analytics.subscribeToRouterEventsAndPublishMetrics(5000);
  }

  recordChange($event: MatTabChangeEvent) {
    console.log('record', $event.tab);
    this.analytics.increment($event.tab.textLabel);
  }
}
