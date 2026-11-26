import {Component, computed, input} from '@angular/core'
import {Ranking} from '../../../model/leaderboard/ranking'
import { NgOptimizedImage } from '@angular/common'

@Component({
  selector: 'bet-position',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './position.component.html',
  styleUrl: './position.component.scss'
})
export class PositionComponent {
  readonly ranking = input.required<Ranking>()
  readonly highlight = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v
  })

  readonly movementIcon = computed(() => {
    const ranking = this.ranking()
    const movement = ranking.prevRanking - ranking.ranking

    if (movement >= 5) return 'keyboard_double_arrow_up'
    if (movement <= -5) return 'keyboard_double_arrow_down'
    if (movement > 0) return 'keyboard_arrow_up'
    if (movement < 0) return 'keyboard_arrow_down'
    return 'equal'
  })
}
