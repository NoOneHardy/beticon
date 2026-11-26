import {Component, input, output} from '@angular/core'

import {RouterLink, RouterLinkActive} from '@angular/router'
import {MatRipple} from '@angular/material/core'

@Component({
  selector: 'bet-nav-item',
  imports: [
    RouterLink,
    RouterLinkActive,
    MatRipple
  ],
  templateUrl: './nav-item.component.html',
  styleUrl: './nav-item.component.scss'
})
export class NavItemComponent {
  readonly icon = input<string | null>(null)
  readonly displayName = input<string | null>(null)
  readonly route = input<string | null>('/')
  readonly exact = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v
  })
  readonly gradient = input<boolean, boolean | ''>(false, {
    transform: v => v === '' || v
  })
  readonly clickEvent = output()
}
