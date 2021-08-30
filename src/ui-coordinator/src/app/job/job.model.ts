export enum RequestState {
  INIT = 'INIT',
  PROCESSING = 'PROCESSING',
  FAIL = 'FAIL',
  COMPLETE = 'COMPLETE',
}

export interface JobReport {
  resolved: boolean;
  createdAt: number;
  requestId: string;
  query: string;
  response: string;
}
