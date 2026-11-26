import {Component, input} from '@angular/core'
import {Score} from '../../../../model/game/score'

@Component({
  selector: 'bet-result',
  standalone: true,
  templateUrl: './result.component.html',
  styleUrl: './result.component.scss',
  host: {
    '[class.-large]': 'large()'
  }
})
export class ResultComponent {
  readonly result = input<Score | null>(null)

  readonly large = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })
}
