import {ComponentFixture} from '@angular/core/testing'

import {FormFieldComponent} from './form-field.component'
import {configureTestHost, TestHostComponent} from '../test-host/test-host.component'
import {queryElement} from '../../helper/test.helper'

describe('FormFieldComponent', () => {
  let component: TestHostComponent
  let fixture: ComponentFixture<TestHostComponent>

  beforeEach(async () => {
    [fixture, component] = await configureTestHost({
      imports: [FormFieldComponent],
      template: '<bet-form-field><input></bet-form-field>'
    })
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should set placeholder to ""', () => {
    const input = queryElement(fixture, 'input', HTMLInputElement)
    expect(input.placeholder).toBe('')
  })

  it('should set isPassword to true', async () => {
    [fixture, component] = await configureTestHost({
      imports: [FormFieldComponent],
      template: '<bet-form-field><input type="password"></bet-form-field>'
    })

    expect((fixture.nativeElement as HTMLElement).querySelector('button.show-password-icon')).toBeTruthy()
  })

  it('should toggle password visibility', async () => {
    [fixture, component] = await configureTestHost({
      imports: [FormFieldComponent],
      template: '<bet-form-field><input type="password"></bet-form-field>'
    })

    expect((fixture.nativeElement as HTMLElement).querySelector('button.show-password-icon')).toBeTruthy()
    queryElement(fixture, 'button.show-password-icon', HTMLElement).click()
    fixture.detectChanges()
    expect(queryElement(fixture, 'input', HTMLInputElement).type).toBe('text')
  })
})
