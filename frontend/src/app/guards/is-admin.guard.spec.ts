import {TestBed} from '@angular/core/testing'
import {GuardResult, provideRouter, Router} from '@angular/router'

import {isAdminGuard} from './is-admin.guard'
import {MockStore, provideMockStore} from '@ngrx/store/testing'
import {selectIsAdmin, selectIsUserLoading} from '../user-management/store/user.feature'
import {runGuard} from '../shared/helper/test.helper'

describe('isAdminGuard', () => {
  let store: MockStore
  let result: GuardResult | null

  const subscribeToGuard = () => {
    runGuard(isAdminGuard).subscribe((guardResult) => {
      result = guardResult
    })
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideMockStore()]
    })

    result = null
    store = TestBed.inject<MockStore>(MockStore)
    store.overrideSelector(selectIsUserLoading, false)
  })

  it('should return true if user is admin', () => {
    store.overrideSelector(selectIsAdmin, true)
    store.refreshState()
    subscribeToGuard()

    expect(result).toBe(true)
  })

  it('should return a url tree for "/" if user is not admin', () => {
    store.overrideSelector(selectIsAdmin, false)
    store.refreshState()
    subscribeToGuard()
    expect(result).toEqual(TestBed.inject(Router).createUrlTree(['/']))
  })

  it('should wait until loading is done and redirect', () => {
    store.overrideSelector(selectIsAdmin, false)
    store.overrideSelector(selectIsUserLoading, true)
    store.refreshState()
    subscribeToGuard()
    expect(result).toBeNull()

    store.overrideSelector(selectIsUserLoading, false)
    store.refreshState()
    expect(result).toEqual(TestBed.inject(Router).createUrlTree(['/']))
  })

  it('should wait until loading is done and allow', () => {
    store.overrideSelector(selectIsAdmin, false)
    store.overrideSelector(selectIsUserLoading, true)
    store.refreshState()
    subscribeToGuard()
    expect(result).toBeNull()

    store.overrideSelector(selectIsUserLoading, false)
    store.overrideSelector(selectIsAdmin, true)
    store.refreshState()
    expect(result).toBe(true)
  })
})
