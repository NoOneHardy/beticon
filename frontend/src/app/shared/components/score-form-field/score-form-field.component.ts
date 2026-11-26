import {Component, input, signal} from '@angular/core'
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from '@angular/forms'
import {EMPTY_METHOD, OnChangeFn, OnTouchFn} from '../../helper/control-value-accessor'
import {FormFieldComponent} from '../form-field/form-field.component'


@Component({
  selector: 'bet-score-form-field',
  imports: [
    FormFieldComponent,
    FormsModule
  ],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: ScoreFormFieldComponent,
    multi: true
  }],
  templateUrl: './score-form-field.component.html',
  styleUrl: './score-form-field.component.scss'
})
export class ScoreFormFieldComponent implements ControlValueAccessor {
  onChange: OnChangeFn<number | null> = EMPTY_METHOD
  onTouch: OnTouchFn = EMPTY_METHOD

  readonly isLarge = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v,
    alias: 'large'
  })

  readonly value = signal<number | null>(null)
  readonly isDisabled = signal(false)

  registerOnChange(fn: OnChangeFn<number | null>): void {
    this.onChange = fn
  }

  registerOnTouched(fn: OnTouchFn): void {
    this.onTouch = fn
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled.set(isDisabled)
  }

  writeValue(value: number | null): void {
    this.value.set(value)
    this.onChange(value)
  }

  increment(): void {
    const value = this.value()
    if (!value || value < 999) this.writeValue((value ?? 0) + 1)
  }

  decrement(): void {
    const value = this.value()
    if (value && value > 0) this.writeValue(value - 1)
  }

  onInput(value: string | number): void {
    if (value === '') return this.writeValue(null)
    this.writeValue(Number(value))
  }
}
