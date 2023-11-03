import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerService} from "../../services/customer/customer.service";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationResponse} from "../../models/authentication-response";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

    constructor(private router: Router,
                private customerService: CustomerService,
                private authenticationService: AuthenticationService) {
    }
    errorMsg = '';
    customer: CustomerRegistrationRequest = {};

    login() {
        this.router.navigate(['login']);
    }

    createAccount() {
        this.customerService.registerCustomer(this.customer)
            .subscribe({
                next: () => {
                    const authenticationRequest: AuthenticationRequest = {
                        username: this.customer.email,
                        password: this.customer.password
                    }
                    this.authenticationService.login(authenticationRequest)
                        .subscribe({
                            next: (authResponse) => {
                                localStorage.setItem('user', JSON.stringify(authResponse));
                                this.router.navigate(['customers']);
                            },
                            error: err => {
                                if (err.error.statusCode === 401) {
                                    this.errorMsg = 'Email and / or password is incorrect';
                                }
                            }
                        })
                }
            })
    }
}
