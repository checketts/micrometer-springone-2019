import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivationStart, Router } from '@angular/router';
import {interval, Observable} from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  componentMetrics = new Map<string, number>();

  constructor(private httpClient: HttpClient, private router: Router) {}

  subscribeToRouterEventsAndPublishMetrics(publishInterval: number) {
    console.log('sub!')
    this.router.events.subscribe(e => {
      if (e instanceof ActivationStart) {
        const component = e.snapshot.routeConfig.path;

        this.increment(component);
      }
    });

    console.log('sub! 2')
    interval(publishInterval)
      .pipe(
        map(() => {
          console.log('boink');
          const currentMetrics = this.componentMetrics;
          this.componentMetrics = new Map();
          return currentMetrics;
        })
      )
      .subscribe(metrics => {
        console.log('sub! 3', metrics)
        if (metrics.size > 0) {
          const metricsObject = {};
          metrics.forEach((value, key) => (metricsObject[key] = value));
          this.sendAnalytics(metricsObject);
        }
      });
  }

  increment(name: string) {
    const currentCount = this.componentMetrics.get(name);
    if (currentCount) {
      this.componentMetrics.set(name, currentCount + 1);
    } else {
      this.componentMetrics.set(name, 1);
    }
  }

  sendAnalytics(data: any) {
    this.httpClient.post('/api/analytics/metrics', data).subscribe();
  }
}
