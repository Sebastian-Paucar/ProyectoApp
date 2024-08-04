import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from "./auth.service";
import {inject} from "@angular/core";



export const authGuard: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);
console.log(authService.isLoggedIn());
  if (authService.isLoggedIn()) {
    return true;
  } else {
    await router.navigate(['/login']);
    return false;
  }
};
