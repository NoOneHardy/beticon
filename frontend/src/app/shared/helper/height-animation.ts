import {AnimationCallbackEvent} from '@angular/core'

const DURATION_MS = 200

export function animateOpen(event: AnimationCallbackEvent, collapsed: Keyframe): void {
  animateHeight(event, open => [collapsed, open], {duration: DURATION_MS})
}

export function animateClose(event: AnimationCallbackEvent, collapsed: Keyframe): void {
  animateHeight(event, open => [open, collapsed], {duration: DURATION_MS, fill: 'forwards'})
}

// The open height is measured because CSS cannot animate to `height: auto`.
function animateHeight(
  event: AnimationCallbackEvent,
  keyframes: (open: Keyframe) => Keyframe[],
  options: KeyframeAnimationOptions
): void {
  const open = {height: getComputedStyle(event.target).height}
  event.target.animate(keyframes(open), options)
    .finished.then(event.animationComplete, event.animationComplete)
}
