import {Component, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss'],
    providers: [MessageService]
})
export class CustomerComponent implements OnInit{

    display: boolean = false;
    operation: 'create' | 'update' = 'create'

    customers: CustomerDTO[] = [];
    customer: CustomerRegistrationRequest = {};

    constructor(private customerService: CustomerService,
                private messageService: MessageService,
                private confirmationService: ConfirmationService) {
    }

    ngOnInit(): void {
        this.findAllCustomers();
    }

    private findAllCustomers() {
        this.customerService.findAll()
            .subscribe({
                next: data => {
                    this.customers = data;
                    console.log(data);
                }
            })
    }

    save(customer: CustomerRegistrationRequest) {
        if (customer) {
            if(this.operation === 'create') {
                this.customerService.registerCustomer(customer)
                    .subscribe({
                        next: () => {
                            this.findAllCustomers();
                            this.display = false;
                            this.customer = {};
                            this.showToast(
                                'success',
                                'Customer saved',
                                `Customer ${customer.name} was successfully saved`
                            )
                        }
                    });
            } else if (this.operation === 'update') {
                this.customerService.updateCustomer(customer.id, customer)
                    .subscribe({
                        next: () => {
                            this.findAllCustomers();
                            this.display = false;
                            this.customer = {};
                            this.showToast(
                                'success',
                                'Customer updated',
                                `Customer ${customer.name} was successfully updated`
                            )
                        }
                    })
            }

        }

    }

    deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm({
            header: 'Delete customer',
            message: `Are you sure you want to delete ${customer.name}? You can\'t undo this action afterwards`,
            accept: () => {
                this.customerService.deleteCustomer(customer.id)
                    .subscribe({
                        next: () => {
                            this.findAllCustomers();
                            this.showToast(
                                'success',
                                'Customer deleted',
                                `Customer ${customer.name} was successfully deleted`
                            )
                        }
                    })
            }
        })

    }

    private showToast(severity: string, summary: string, detail: string): void{
        this.messageService.add(
            {
                severity: severity,
                summary: summary,
                detail: detail
            }
        )
    }

    updateCustomer(customerDTO: CustomerDTO) {
        this.display = true;
        this.customer = customerDTO;
        this.operation = 'update';
    }

    createCustomer() {
        this.display = true;
        this.customer = {};
        this.operation = 'create';
    }

    cancel() {
        this.display = false;
        this.customer = {};
        this.operation = 'create';
    }
}
