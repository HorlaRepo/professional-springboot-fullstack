import {inject} from '@angular/core';
import { Router } from '@angular/router';

import {AuthenticationService} from '../authentication/authentication.service';
import {AuthenticationResponse} from "../../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";

export const authGuard = () => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      const token = authResponse.token;
      if (token) {
        const jwtHelper = new JwtHelperService();
        const isTokenNonExpired = !jwtHelper.isTokenExpired(token);
        if (isTokenNonExpired) {
          return true;
        }
      }
  }

  // Redirect to the login page
  return router.parseUrl('/login');
};
