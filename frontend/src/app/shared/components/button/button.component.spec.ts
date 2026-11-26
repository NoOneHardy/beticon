import { ComponentFixture, TestBed } from '@angular/core/testing'

import { ButtonComponent } from './button.component'
import {findElement, queryElement} from '../../helper/test.helper'

describe('ButtonComponent', () => {
  let component: ButtonComponent
  let fixture: ComponentFixture<ButtonComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent]
    })
      .compileComponents()

    fixture = TestBed.createComponent(ButtonComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should have toggle for secondary design', () => {
    expect(component.secondary).toBeTruthy()
  })

  it('should map \'\' to true', () => {
    expect(component.secondary()).toBe(false)
    fixture.componentRef.setInput('secondary', '')
    expect(component.secondary()).toBe(true)
  })

  it('should map false to false', () => {
    fixture.componentRef.setInput('secondary', false)
    expect(component.secondary()).toBe(false)
  })

  it('should map true to true', () => {
    fixture.componentRef.setInput('secondary', true)
    expect(component.secondary()).toBe(true)
  })

  it('should have output for click event', () => {
    expect(component.clickEvent).toBeTruthy()
  })

  it('should contain a button', () => {
    expect(findElement(fixture, 'button')).toBeTruthy()
  })

  it('should emit click', () => {
    const spy = vi.spyOn(component.clickEvent, 'emit').mockReturnValue(undefined)
    component.click(new Event('click'))
    expect(spy).toHaveBeenCalled()
  })

  it('should emit click event on button click', () => {
    const spy = vi.spyOn(component.clickEvent, 'emit').mockReturnValue(undefined)
    queryElement(fixture, 'button', HTMLElement).click()
    expect(spy).toHaveBeenCalled()
  })

  it('should have an input for tab index', () => {
    expect(component.tabindex).toBeTruthy()
  })

  it('should have default tab index of 0', () => {
    expect(component.tabindex()).toBe(0)
    expect(queryElement(fixture, 'button', HTMLElement).tabIndex).toBe(0)
  })

  it('should set tab index to 1', () => {
    fixture.componentRef.setInput('tabindex', 1)
    fixture.detectChanges()
    expect(component.tabindex()).toBe(1)
    expect(queryElement(fixture, 'button', HTMLElement).tabIndex).toBe(1)
  })

  it('should stop propagation on click', () => {
    const spy = vi.spyOn(Event.prototype, 'stopPropagation').mockReturnValue(undefined)
    component.click(new Event('click'))
    expect(spy).toHaveBeenCalled()
  })

  it('should prevent default on click', () => {
    const spy = vi.spyOn(Event.prototype, 'preventDefault').mockReturnValue(undefined)
    component.click(new Event('click'))
    expect(spy).toHaveBeenCalled()
  })

  it('should have a default primary design', () => {
    expect(queryElement(fixture, 'button', HTMLElement).classList).not.toContain('secondary')
  })

  it('should have a secondary design', () => {
    fixture.componentRef.setInput('secondary', true)
    fixture.detectChanges()
    expect(queryElement(fixture, 'button', HTMLElement).classList).toContain('secondary')
  })

  it('should have an input for disabled', () => {
    expect(component.disabled).toBeTruthy()
  })

  it('should map \'\' to true', () => {
    expect(component.disabled()).toBe(false)
    fixture.componentRef.setInput('disabled', '')
    expect(component.disabled()).toBe(true)
  })

  it('should map false to false', () => {
    fixture.componentRef.setInput('disabled', false)
    expect(component.disabled()).toBe(false)
  })

  it('should map true to true', () => {
    fixture.componentRef.setInput('disabled', true)
    expect(component.disabled()).toBe(true)
  })

  it('should have a default enabled state', () => {
    expect(queryElement(fixture, 'button', HTMLButtonElement).disabled).toBe(false)
  })

  it('should be disabled', () => {
    fixture.componentRef.setInput('disabled', true)
    fixture.detectChanges()
    expect(queryElement(fixture, 'button', HTMLButtonElement).disabled).toBe(true)
  })
})
