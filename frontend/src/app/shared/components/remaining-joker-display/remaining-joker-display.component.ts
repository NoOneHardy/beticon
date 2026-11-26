import {Component, input} from '@angular/core'

@Component({
  selector: 'bet-remaining-joker-display',
  imports: [],
  templateUrl: './remaining-joker-display.component.html',
  styleUrl: './remaining-joker-display.component.css'
})
export class RemainingJokerDisplayComponent {
  readonly remainingJokers = input.required<number>()
  readonly jokerLimit = input.required<number>()
  readonly jokerMultiplier = input.required<number>()

  get jokerArray(): number[] {
    return Array.from({length: this.jokerMultiplier()}, (_, i) => i)
  }
}
