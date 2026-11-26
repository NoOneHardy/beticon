import {AnimationCallbackEvent, Component, input, signal} from '@angular/core'
import {animateClose, animateOpen} from '../../helper/height-animation'

const COLLAPSED: Keyframe = {height: '0px', paddingTop: '0px'}

@Component({
  selector: 'bet-expandable',
  standalone: true,
  templateUrl: './expandable.component.html',
  styleUrl: './expandable.component.scss',
  host: {
    '[class.-large]': 'large()'
  }
})
export class ExpandableComponent {
  readonly name = input.required<string>()
  readonly large = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })

  readonly expanded = signal(false)
  private toggled = false

  toggle(): void {
    this.toggled = true
    this.expanded.update(expanded => !expanded)
  }

  expand(event: AnimationCallbackEvent): void {
    // The preview is rendered on load and must not animate in then.
    if (this.toggled) {
      animateOpen(event, COLLAPSED)
    } else {
      event.animationComplete()
    }
  }

  collapse(event: AnimationCallbackEvent): void {
    animateClose(event, COLLAPSED)
  }
}
