import { Injectable } from '@angular/core';
import { BehaviorSubject, of, Subject, timer } from 'rxjs';
import {
  catchError,
  concatMap,
  switchMap,
  takeUntil,
  takeWhile,
  tap,
} from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { JobReport, RequestState } from './job.model';

@Injectable()
export class JobService {
  private state$ = new BehaviorSubject<RequestState>(RequestState.INIT);
  private requestId$ = new Subject<string>();
  private response$ = new BehaviorSubject<string>('');
  private unsubscriber$ = new Subject<boolean>();

  constructor(private httpClient: HttpClient) {}

  initialize() {
    this.requestId$
      .pipe(
        takeUntil(this.unsubscriber$),
        switchMap((requestId: string) => this.fetchResponse(requestId))
      )
      .subscribe();
  }

  destroy() {
    this.unsubscriber$.next(true);
  }

  get state() {
    return this.state$.asObservable();
  }

  get response() {
    return this.response$.asObservable();
  }

  submitJob(backend: string, query: string) {
    const options: any = {
      responseType: 'text',
    };

    this.httpClient
      .post<string>(`/api/sessions/${backend}/execute`, query, options)
      .pipe(tap((requestId) => this.requestId$.next(String(requestId))))
      .subscribe();
  }

  private fetchResponse(requestId: string) {
    this.state$.next(RequestState.PROCESSING);

    return timer(0, 1_000).pipe(
      takeUntil(this.unsubscriber$),
      takeWhile(() => this.state$.value === RequestState.PROCESSING),
      concatMap(() =>
        this.httpClient.get<JobReport>(`/api/jobs/${requestId}`).pipe(
          catchError((e) => {
            console.log(e);
            this.state$.next(RequestState.FAIL);
            return of(null);
          })
        )
      ),
      tap((response) => {
        if (response && response.resolved) {
          this.response$.next(response.response);
          this.state$.next(RequestState.COMPLETE);
        }
      })
    );
  }
}
