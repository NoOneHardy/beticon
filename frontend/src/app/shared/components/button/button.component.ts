import {Component, input, output} from '@angular/core'


@Component({
  selector: 'bet-button',
  imports: [],
  host: {
    '[style]': 'getWidth()'
  },
  templateUrl: './button.component.html',
  styleUrl: './button.component.scss'
})
export class ButtonComponent {
  readonly tabindex = input<number>(0)
  readonly secondary = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v
  })
  readonly disabled = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v
  })
  readonly submit = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v
  })
  readonly fullWidth = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v,
    alias: 'full-width'
  })
  readonly icon = input<string | null>(null)

  clickEvent = output()

  click(event: Event): void {
    event.stopPropagation()
    event.preventDefault()
    this.clickEvent.emit()
  }

  getWidth(): string {
    return this.fullWidth() ? 'width: 100%' : ''
  }
}
