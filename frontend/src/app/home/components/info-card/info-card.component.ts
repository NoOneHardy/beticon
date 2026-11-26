import {Component, input} from '@angular/core'

@Component({
  selector: 'bet-info-card',
  imports: [],
  templateUrl: './info-card.component.html',
  styleUrl: './info-card.component.css',
  host: {
    '[class.-accent]': 'accent()'
  }
})
export class InfoCardComponent {
  readonly icon = input.required<string>()
  readonly name = input.required<string>()
  readonly accent = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })
}
