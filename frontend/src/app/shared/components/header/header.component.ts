import {AnimationCallbackEvent, Component, computed, ElementRef, inject, OnInit, signal} from '@angular/core'
import {NavItemComponent} from './components/nav-item/nav-item.component'
import {UserMenuComponent} from './components/user-menu/user-menu.component'
import {EventType, Router, RouterLink} from '@angular/router'
import {UserButtonComponent} from './components/user-button/user-button.component'
import {Store} from '@ngrx/store'
import {selectIsAdmin, selectUser} from '../../../user-management/store/user.feature'

import {logout} from '../../../user-management/store/user.actions'
import {ViewportService} from '../../services/viewport/viewport.service'
import {NotificationsComponent} from './components/notifications/notifications.component'
import {animateClose, animateOpen} from '../../helper/height-animation'

const MENU_COLLAPSED: Keyframe = {height: '0px', paddingTop: '0px', paddingBottom: '0px', opacity: 0}

@Component({
  selector: 'bet-header',
  host: {
    '(document:click)': 'closeMenuOnBlur($event)',
    '(document:keydown.escape)': 'closeMenu()'
  },
  imports: [
    NavItemComponent,
    UserMenuComponent,
    RouterLink,
    UserButtonComponent,
    NotificationsComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  private store = inject(Store)
  private el = inject<ElementRef<HTMLElement>>(ElementRef)
  private viewportService = inject(ViewportService)
  private router = inject(Router)

  readonly user = this.store.selectSignal(selectUser)
  readonly isAdmin = this.store.selectSignal(selectIsAdmin)

  readonly isVpL = computed(() => !this.viewportService.isVPMorSmaller())
  readonly isExpanded = signal(false)

  ngOnInit(): void {
    this.router.events.subscribe(($event) => {
      if ($event.type === EventType.NavigationEnd) {
        this.closeMenu()
      }
    })
  }

  logout(): void {
    this.store.dispatch(logout())
    this.closeMenu()
  }

  toggleMenu(): void {
    this.isExpanded.update(isExpanded => !isExpanded)
  }

  closeMenuOnBlur(e: MouseEvent): void {
    if (!(e.target instanceof Node) || !this.el.nativeElement.contains(e.target)) {
      this.isExpanded.set(false)
    }
  }

  closeMenu(): void {
    this.isExpanded.set(false)
  }

  expand(event: AnimationCallbackEvent): void {
    animateOpen(event, MENU_COLLAPSED)
  }

  collapse(event: AnimationCallbackEvent): void {
    animateClose(event, MENU_COLLAPSED)
  }
}
