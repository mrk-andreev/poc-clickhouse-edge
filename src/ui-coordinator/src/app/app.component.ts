import { Component, OnDestroy, OnInit } from '@angular/core';
import { ExecutorService } from './executor/executor.service';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { JobService } from './job/job.service';
import { RequestState } from './job/job.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [ExecutorService, JobService],
})
export class AppComponent implements OnInit, OnDestroy {
  RequestState = RequestState;

  form: FormGroup = this.fb.group({
    backend: this.fb.control(null, Validators.required),
    query: this.fb.control('', Validators.required),
  });

  constructor(
    public service: ExecutorService,
    public requestService: JobService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.service.initialize();
    this.requestService.initialize();
  }

  ngOnDestroy(): void {
    this.service.destroy();
    this.requestService.destroy();
  }

  submit({ backend, query }: { backend: string; query: string }): void {
    this.requestService.submitJob(backend, query);
  }
}
