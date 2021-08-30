import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Subject, timer } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';

@Injectable()
export class ExecutorService {
  private backends$ = new BehaviorSubject<string[]>([]);
  private unsubscriber$ = new Subject();

  constructor(private httpClient: HttpClient) {}

  initialize() {
    timer(0, 3_000)
      .pipe(
        takeUntil(this.unsubscriber$),
        tap(() => this.fetchBackends())
      )
      .subscribe();
  }

  destroy() {
    this.unsubscriber$.next(true);
  }

  fetchBackends() {
    return this.httpClient
      .get<string[]>('/api/sessions')
      .pipe(tap((backends) => this.backends$.next(backends)))
      .subscribe();
  }

  get backends() {
    return this.backends$.asObservable();
  }
}
