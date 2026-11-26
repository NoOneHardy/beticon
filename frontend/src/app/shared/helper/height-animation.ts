import {AnimationCallbackEvent} from '@angular/core'

const DURATION_MS = 200

// The open height is measured because CSS cannot animate to `height: auto`.
export function animateOpen(event: AnimationCallbackEvent, collapsed: Keyframe): void {
  const open = {height: getComputedStyle(event.target).height}
  event.target.animate([collapsed, open], DURATION_MS)
    .finished.then(event.animationComplete, event.animationComplete)
}

export function animateClose(event: AnimationCallbackEvent, collapsed: Keyframe): void {
  const open = {height: getComputedStyle(event.target).height}
  event.target.animate([open, collapsed], {duration: DURATION_MS, fill: 'forwards'})
    .finished.then(event.animationComplete, event.animationComplete)
}
