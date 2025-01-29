import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "./auth.service";

export const unauthGuard: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  console.log(!authService.isLoggedIn());
  if (!authService.isLoggedIn()) {

    return true;
  } else {
    await router.navigate(['/dashboard']);
    return false;
  }
};
