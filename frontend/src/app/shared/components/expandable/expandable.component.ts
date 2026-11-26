import {Component, input} from '@angular/core'
import {animate, state, style, transition, trigger} from '@angular/animations'

@Component({
  selector: 'bet-expandable',
  standalone: true,
  templateUrl: './expandable.component.html',
  styleUrl: './expandable.component.scss',
  host: {
    '[class.-large]': 'large()'
  },
  animations: [
    trigger('expandable', [
      state('collapsed', style({
        'height': '0px',
        'padding-top': '0px'
      })),
      state('expanded', style({
        'height': '*',
        'padding-top': '*'
      })),
      transition('collapsed <=> expanded', [
        animate(200)
      ])
    ])
  ]
})
export class ExpandableComponent {
  readonly name = input.required<string>()
  readonly large = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })

  expanded = false
}
