import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {MessageService} from "primeng/api";
import {CustomerService} from "../../services/customer/customer.service";

@Component({
  selector: 'app-customer-card',
  templateUrl: './customer-card.component.html',
  styleUrls: ['./customer-card.component.scss'],
  providers: [MessageService]
})
export class CustomerCardComponent {

  constructor(private customerService: CustomerService) {
  }

  @Input()
  customer: CustomerDTO = {};

  @Input()
  customerIndex = 0;

  @Output()
  delete: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  @Output()
  update: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  get customerImage(): string {
    const gender = this.customer.gender === 'MALE' ? 'men' : 'women';
    return `https://randomuser.me/api/portraits/${gender}/${this.customer.id}.jpg`
  }

  deleteCustomer(){
    this.customerService.deleteCustomer(this.customer.id)
        .subscribe({
          next: ()=> {

          }
        })
  }

  onDelete() {
    this.delete.emit(this.customer);
  }

  onUpdate() {
    this.update.emit(this.customer);
  }
}
