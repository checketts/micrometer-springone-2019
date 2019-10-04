import {Component} from '@angular/core';
import {AnalyticsService} from './analytics.service';

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
}
