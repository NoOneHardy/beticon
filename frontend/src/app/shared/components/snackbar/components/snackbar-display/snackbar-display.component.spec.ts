import {ComponentFixture, TestBed} from '@angular/core/testing'

import {SnackbarDisplayComponent} from './snackbar-display.component'
import {provideMockStore} from '@ngrx/store/testing'
import {SnackbarService} from '../../../../services/snackbar/snackbar.service'
import {queryElement} from '../../../../helper/test.helper'

describe('SnackbarDisplayComponent', () => {
  let component: SnackbarDisplayComponent
  let fixture: ComponentFixture<SnackbarDisplayComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SnackbarDisplayComponent],
      providers: [provideMockStore()]
    }).compileComponents()

    fixture = TestBed.createComponent(SnackbarDisplayComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should take signal from snackbar service', () => {
    const service = TestBed.inject(SnackbarService)
    expect(component.messages).toEqual(service.messages)
    expect(component.messages()).toEqual([])

    service.addMessage({message: 'test'})
    fixture.detectChanges()
    expect(component.messages()).toEqual([{message: 'test'}])
  })

  it('should display message', () => {
    const service = TestBed.inject(SnackbarService)
    service.addMessage({message: 'test'})
    fixture.detectChanges()

    const snackbar = queryElement(fixture, 'bet-snackbar', HTMLElement)
    expect(snackbar).toBeTruthy()
    expect(snackbar.textContent).toContain('test')
  })

  it('should display multiple messages', () => {
    const service = TestBed.inject(SnackbarService)
    service.addMessage({message: 'test'})
    fixture.detectChanges()

    const snackbar = queryElement(fixture, 'bet-snackbar', HTMLElement)
    expect(snackbar).toBeTruthy()
    expect(snackbar.textContent).toContain('test')

    service.addMessage({message: 'test2'})
    fixture.detectChanges()

    const snackbars = (fixture.nativeElement as HTMLElement).querySelectorAll('bet-snackbar')
    expect(snackbars.length).toEqual(2)
    expect(snackbars[1].textContent).toContain('test2')
  })
})
