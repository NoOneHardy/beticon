import {AbstractControl} from '@angular/forms'
import {ErrorStateMatcher} from '@angular/material/core'

export function hasError(control?: AbstractControl | null, error?: string): boolean {
  if (!control || control.untouched || !control.errors) return false

  if (error) {
    const index = Object.keys(control.errors).indexOf(error)
    if (index !== 0) return false
  }
  return control.invalid
}

export function errorStateMatcher(isErrorState: ErrorStateMatcher['isErrorState']): ErrorStateMatcher {
  const matcher = new ErrorStateMatcher()
  matcher.isErrorState = isErrorState
  return matcher
}
