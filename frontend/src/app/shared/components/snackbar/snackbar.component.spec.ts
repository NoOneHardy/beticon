import { ComponentFixture, TestBed } from '@angular/core/testing'

import { SnackbarComponent } from './snackbar.component'
import {provideMockStore} from '@ngrx/store/testing'
import {queryElement} from '../../helper/test.helper'

describe('SnackbarComponent', () => {
  let component: SnackbarComponent
  let fixture: ComponentFixture<SnackbarComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SnackbarComponent],
      providers: [provideMockStore()]
    }).compileComponents()

    fixture = TestBed.createComponent(SnackbarComponent)
    fixture.componentRef.setInput('message', {
      message: 'Test message'
    })
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should use success style as default', () => {
    expect(component.message().type).toBe('success')
  })

  it('should add error class on error type', () => {
    fixture.componentRef.setInput('message', {
      message: 'Test message',
      type: 'error'
    })
    fixture.detectChanges()
    expect(component.message().type).toBe('error')
    expect(queryElement(fixture, '.snackbar', HTMLElement).classList).toContain('-error')
  })

  it('should change icon based on type', () => {
    const icon = queryElement(fixture, '.icon', HTMLElement)
    expect(icon.innerHTML).toBe('check_circle')

    fixture.componentRef.setInput('message', {
      message: 'Test message',
      type: 'error'
    })
    fixture.detectChanges()
    expect(icon.innerHTML).toBe('cancel')
  })
})
