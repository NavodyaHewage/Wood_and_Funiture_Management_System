package com.group_project.wfms_backend.service;


import com.group_project.wfms_backend.model.CustomerOrder;
import com.group_project.wfms_backend.model.CustomerOrderDetails;
import com.group_project.wfms_backend.repository.CustomerOrderDetailsRepository;
import com.group_project.wfms_backend.repository.CustomerRepository;
import com.group_project.wfms_backend.repository.CustomerorderRepository;
import com.group_project.wfms_backend.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CustomerOrderService {
    @Autowired
    private CustomerOrderDetailsRepository detailsRepo;

    @Autowired
    private CustomerorderRepository  orderRepo;


    // method ekk hdla api gannwa ekata adla details okkom order add krnn den order add krnn oni
    public CustomerOrder createOrder(
       CustomerOrder  order,
       List<CustomerOrderDetails> detailsList){

        CustomerOrder savedOrder = orderRepo.save(order);

        for(CustomerOrderDetails d : detailsList) {
            d.setorder(savedOrder);
            detailsRepo.save(d);
        }



         return savedOrder;
        }
    }



