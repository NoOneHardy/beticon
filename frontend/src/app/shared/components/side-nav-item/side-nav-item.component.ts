import {Component, input, output} from '@angular/core'
import {RouterLink, RouterLinkActive} from '@angular/router'

@Component({
  selector: 'bet-side-nav-item',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './side-nav-item.component.html',
  styleUrl: './side-nav-item.component.css'
})
export class SideNavItemComponent {
  readonly route = input<string | null>(null)
  readonly icon = input<string | null>(null)
  clickAction = output<Event>()
}
