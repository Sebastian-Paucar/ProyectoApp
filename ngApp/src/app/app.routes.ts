import { Routes } from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";

import {RegisterComponent} from "./pages/register/register.component";
import {DashboardComponent} from "./pages/dashboard/dashboard.component";
import {authGuard} from "./services/auth.guard";


export const routes: Routes = [
  {
    path:"login",
    component:LoginComponent,
    title:"Login",
    children:[]
  },
  {
    path:"register",
    component:RegisterComponent,
    title:"Register",

  },
  {
    path:"dashboard",
    component:DashboardComponent,
    title:"Dashboard",
    canActivate:[authGuard]

  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
