import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter, RouterOutlet, withViewTransitions} from '@angular/router';

import { routes } from './app.routes';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientModule} from "@angular/common/http";
import {ReactiveFormsModule} from "@angular/forms";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes,withViewTransitions()),importProvidersFrom(BrowserAnimationsModule,
    HttpClientModule,
    BrowserModule,
    RouterOutlet,
    ReactiveFormsModule,
  ), provideClientHydration(), provideAnimationsAsync()]
};
