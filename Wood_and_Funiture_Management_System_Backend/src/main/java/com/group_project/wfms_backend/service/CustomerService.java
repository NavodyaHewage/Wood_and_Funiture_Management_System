package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.Customer;
import com.group_project.wfms_backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer.getNic() != null && !customer.getNic().isEmpty()
                && customerRepository.existsByNic(customer.getNic())) {
            throw new RuntimeException("Customer with NIC " + customer.getNic() + " already exists");
        }
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Integer id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        customer.setCusName(customerDetails.getCusName());
        customer.setMobile(customerDetails.getMobile());
        customer.setAddress(customerDetails.getAddress());
        customer.setEmail(customerDetails.getEmail());
        customer.setNic(customerDetails.getNic());
        customer.setIsActive(customerDetails.getIsActive());
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
}
