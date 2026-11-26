import { ComponentFixture, TestBed } from '@angular/core/testing'

import { ScoreFormFieldComponent } from './score-form-field.component'
import {queryElement} from '../../helper/test.helper'

describe('ScoreFormFieldComponent', () => {
  let component: ScoreFormFieldComponent
  let fixture: ComponentFixture<ScoreFormFieldComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScoreFormFieldComponent]
    })
      .compileComponents()

    fixture = TestBed.createComponent(ScoreFormFieldComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should increment', () => {
    expect(component.value()).toBe(null)
    component.increment()
    expect(component.value()).toBe(1)
  })

  it('should decrement', () => {
    component.value.set(1)
    component.decrement()
    expect(component.value()).toBe(0)
  })

  it('should not decrement if value is null', () => {
    expect(component.value()).toBe(null)
    component.decrement()
    expect(component.value()).toBe(null)
  })

  it('should not decrement if value is 0', () => {
    component.value.set(0)
    component.decrement()
    expect(component.value()).toBe(0)
  })

  it('should not increment if value is 999', () => {
    component.value.set(999)
    component.increment()
    expect(component.value()).toBe(999)
  })

  it('should change value on input', () => {
    const input = queryElement(fixture, 'input', HTMLInputElement)
    input.value = '5'
    input.dispatchEvent(new Event('input'))
    expect(component.value()).toBe(5)
  })

  it('should allow deletion of value', () => {
    component.value.set(5)
    const input = queryElement(fixture, 'input', HTMLInputElement)
    input.value = ''
    input.dispatchEvent(new Event('input'))
    expect(component.value()).toBeNull()
  })

  it('should change input value on value change', () => {
    const input = queryElement(fixture, 'input', HTMLInputElement)
    expect(input.value).toBe('')
    component.writeValue(5)
    fixture.detectChanges()
    expect(input.value).toBe('5')
  })

  it('should hide buttons if disabled', () => {
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('button').length).toBe(2)
    component.setDisabledState(true)
    fixture.detectChanges()
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('button').length).toBe(0)
  })
})
