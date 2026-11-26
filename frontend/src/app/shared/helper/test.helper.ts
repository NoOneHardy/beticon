import {GlobalData} from '../services/base-http/global-data'
import {ServiceError} from '../../model/error'
import {TestRequest} from '@angular/common/http/testing'
import {ComponentFixture, TestBed} from '@angular/core/testing'
import {ActivatedRoute, CanActivateFn, GuardResult, Router} from '@angular/router'
import {Observable} from 'rxjs'

export function flushApiResponse(req: TestRequest, data: unknown, globalData?: GlobalData): void {
  req.flush({
    data: data,
    globalData: globalData ?? {
      id: 'request-id',
      notifications: []
    }
  })
}

export interface ApiErrorResponseMock {
  status?: number,
  message?: string,
  displayMessage?: string
}

function mapErrorMockToServiceError(error?: ApiErrorResponseMock): ServiceError {
  return {
    status: error?.status ?? 500,
    message: error?.message ?? 'Internal Server Error',
    displayMessage: error?.displayMessage ?? 'Ein Fehler ist aufgetreten.',
    timestamp: new Date('2025-08-08T10:57:00')
  }
}

export function flushApiErrorResponse(req: TestRequest, error?: ApiErrorResponseMock): void {
  const response: ServiceError = mapErrorMockToServiceError(error)

  req.flush(response, {
    status: response.status,
    statusText: response.message
  })
}

export function findElement(fixture: ComponentFixture<unknown>, selector: string): Element | null {
  return (fixture.nativeElement as HTMLElement).querySelector(selector)
}

export function queryElement<T extends Element>(fixture: ComponentFixture<unknown>, selector: string, type: new () => T): T {
  const element = findElement(fixture, selector)
  if (!(element instanceof type)) throw new Error(`No ${type.name} matches '${selector}'`)
  return element
}

export function runGuard(guard: CanActivateFn): Observable<GuardResult> {
  const route = TestBed.inject(ActivatedRoute).snapshot
  const state = TestBed.inject(Router).routerState.snapshot
  return TestBed.runInInjectionContext(() => guard(route, state)) as Observable<GuardResult>
}
