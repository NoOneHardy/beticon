import { Component , ChangeDetectionStrategy} from '@angular/core'

@Component({
  changeDetection: ChangeDetectionStrategy.Eager,
  selector: 'bet-payout-board',
  imports: [],
  templateUrl: './payout-board.component.html',
  styleUrl: './payout-board.component.css'
})
export class PayoutBoardComponent {
  positions: {
    class?: string
    percentage: number
  }[] = [
      {class: '-first', percentage: 40},
      {class: '-second', percentage: 25},
      {percentage: 20},
      {percentage: 10},
      {percentage: 5}
    ]
}
